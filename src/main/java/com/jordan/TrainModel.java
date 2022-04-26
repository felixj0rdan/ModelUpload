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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
//import org.apache.tomcat.util.http.fileupload.FileItem;

/**
 * Servlet implementation class TrainModel
 */

// a class to train the model
public class TrainModel extends HttpServlet {
	
	// a post method to get training data set
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
		
		// filename will have the path where the trained model will be stored
		String filename = "D:\\java-web\\ModelUpload\\MLmodel\\model.model";
		File file = new File(filename); // a new file is created at that location to store the data
		
		
		// try with resource block for writing the object to a file
		try(FileOutputStream fos = new FileOutputStream(file); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			
			// now we read all the training files we get and store them in a list
			ServletFileUpload sf = new ServletFileUpload(new DiskFileItemFactory());
			List<FileItem> multiFile = sf.parseRequest(new ServletRequestContext(request));
			// a new model is created to be trained
			TestSmile model = new TestSmile();
			
			// now the list of files is iterated and is used to train the model
			for (FileItem item : multiFile) {
				DataFrame train = null;
				try {
					
					// we write and read the file
					item.write(new File("D://java-web/ModelUpload/storage/" + item.getName()));
					train = Read.csv("D://java-web/ModelUpload/storage/" + item.getName());
					
					// now with that file we train the model and write it to a .model file
					model.trainModel(train);
					oos.writeObject(model);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
			
			// to show model metrics
			System.out.println(model.metrics);	
			
//			// to return the metrics
		    try(PrintWriter out = response.getWriter();){
		    	response.setContentType("text");
				 out.print(model.metrics);
				 out.flush();
		    }
		} 

		}
}
