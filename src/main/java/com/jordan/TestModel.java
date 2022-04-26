package com.jordan;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.PathParam;
import smile.data.DataFrame;
import smile.io.Read;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

import com.google.gson.Gson;

import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;


// a class to test the model
public class TestModel extends HttpServlet {
	
	protected static TestSmile model1;
	private static Logger logger = Logger.getLogger("");
	
	// a post method to get all the testing data set and trest the model
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// to get the root working directory
		String localDir = System.getProperty("user.dir");
		System.out.println(localDir);
		
		// now to load the trained model to a TestSmile object we first locate the .model file 
		String filename = localDir+"\\ModelUpload\\MLmodel\\model.model";
	    File file = new File(filename);
	    
	    // new model is created to store the read model
	    TestSmile model = null;
	    int[] res = null;
	    // try with resources block to read the .model file to an object
	    try(FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois =  new  ObjectInputStream(fis)){
	    	
	    	
	    	// now we will read all the testing file we receive and store them in a list
	    	ServletFileUpload sf = new ServletFileUpload(new DiskFileItemFactory());
			List<FileItem> multiFile = sf.parseRequest(new ServletRequestContext(request));
			List<int[]> result = new ArrayList<>();
			// the read object is stored in model object
			model = (TestSmile) ois.readObject();
			
			
			// now the list of files is iterated and the testing is carried on
			for (FileItem item : multiFile) {
				DataFrame test = null;
				
				try {
					
					// we write and read the file
					String filePath = localDir+"\\ModelUpload\\src\\main\\java\\storage" + item.getName().toString();
					item.write(new File(filePath));
					test = Read.csv(filePath);
					
					// and we test the model with that file
					res = model.testModel(test);
					
					// without reading an object from file
					//res = model1.testModel(test);
				} catch (Exception e) {
					e.printStackTrace();
				}		
				
			}
	    } catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} 
	    
	    // to return the prediction
	    try(PrintWriter out = response.getWriter();){
	    	
	    	ReturnModel mod = new ReturnModel(res, model.metrics.accuracy*100, model.metrics.fitTime);
	    	String modelJsonString = new Gson().toJson(mod);
	    	response.setContentType("application/json");
			out.print(modelJsonString);
			out.flush();
	    }	 
	    catch (Exception e) {
			// TODO: handle exception
	    	System.out.println(e);
	    	logger.warning(e.toString());	
		}
	}   
	
}
