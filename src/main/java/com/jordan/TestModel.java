package com.jordan;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import jakarta.websocket.server.PathParam;
import smile.data.DataFrame;
import smile.io.Read;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
@MultipartConfig
public class TestModel extends HttpServlet {
	
	private static Logger logger = Logger.getLogger("");
	
	// a post method to get all the testing data set and rest the model
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// to get the root working directory
		String localDir = System.getProperty("user.dir");
   
		// get model to be used
    	String modelName = request.getParameter("modelName");
		
		// now to load the trained model to a TestSmile object we first locate the .model file 
		String filename = localDir+"\\ModelUpload\\MLmodel\\"+modelName+".model";
	    File file = new File(filename);
	    
	    // new model is created to store the read model
	    TestSmile model = null;
	    int[] res = null;
	    
	    // try with resources block to read the .model file to an object
	    try(FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois =  new  ObjectInputStream(fis)){		
			
			List<int[]> result = new ArrayList<>();
			// the read object is stored in model object
			model = (TestSmile) ois.readObject();
			
			
			// now the testing is carried on with the testing file
			DataFrame test = null;
			
			try {
				Part part = request.getPart("file");
				String filePath = localDir+"\\ModelUpload\\src\\main\\java\\storage\\" + part.getSubmittedFileName();
				part.write(filePath);
				test = Read.csv(filePath);
				
				// and we test the model with that file
				res = model.testModel(test);
				
			} catch (Exception e) {
				e.printStackTrace();
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
