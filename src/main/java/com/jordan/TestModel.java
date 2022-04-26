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

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;


// a class to test the model
public class TestModel extends HttpServlet {
	
	static TestSmile model1;
	
	// a post method to get all the testing data set and trest the model
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// now to load the trainded model to a TestSmile object we first locate the .model file 
		String filename = "D:\\java-web\\ModelUpload\\MLmodel\\model.model";
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
					item.write(new File("D://java-web/ModelUpload/storage/" + item.getName()));
					test = Read.csv("D://java-web/ModelUpload/storage/" + item.getName());
					
					// and we test the model with that file
					res = model.testModel(test);
					
					// without raeding an object from file
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
	    	response.setContentType("application/json");
			 out.print("{ TestResults:"+Arrays.toString(res)+"}");
			 out.flush();
	    }
	    System.out.println(a);
	 
	}   
	
}
