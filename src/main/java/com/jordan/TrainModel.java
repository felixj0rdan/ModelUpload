package com.jordan;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.PathParam;
import smile.data.DataFrame;
import smile.io.Read;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
//import java.lang.System.Logger.Level;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.swing.filechooser.FileSystemView;

import java.util.logging.Level;

//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

import com.google.gson.Gson;

import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
//import org.apache.tomcat.util.http.fileupload.FileItem;

/**
 * Servlet implementation class TrainModel
 */

// a class to train the model
public class TrainModel extends HttpServlet {
	
	private static Logger logger = Logger.getLogger("");
	
	// a post method to get training data set
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
		
		// to get the root working directory
		String localDir = System.getProperty("user.dir");
		System.out.println(localDir);		
		
		// create a new model file name
		String modelsListFile = localDir+"\\ModelUpload\\MLmodel\\ListOfModels.txt";
		Random random = new Random();
		String newFile = "Model_"+String.format("%04d", random.nextInt(10000));	
		
		ArrayList<String> modelList = new ArrayList<>(Files.readAllLines(Paths.get(modelsListFile)));
		System.out.println(modelList);
		
		while(modelList.contains(newFile)) {
			newFile = "Model_"+String.format("%04d", random.nextInt(10000));
		}
		
		System.out.println(newFile);
		modelList.add(newFile);
			
		// filename will have the path where the trained model will be stored
		String filename = localDir+"/ModelUpload/MLmodel/" + newFile +".model";
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
					String filePath = localDir+"\\ModelUpload\\src\\main\\java\\storage\\" + item.getName();
					item.write(new File(filePath));
					train = Read.csv(filePath); 
					
					// now with that file we train the model and write it to a .model file
					model.trainModel(train);
					oos.writeObject(model);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
			
			// to log the model metrics	
			logger.log(Level.INFO, model.metrics.toString());		
			
			// to return the metrics
		    try(PrintWriter out = response.getWriter();){
		    	ReturnTrained rt = new ReturnTrained(model.metrics.fitTime,model.metrics.scoreTime, model.metrics.size, model.metrics.error, model.metrics.accuracy, newFile);
		    	String rtJsonString = new Gson().toJson(rt);
		    	response.setContentType("application/json"); 
				out.print(rtJsonString);
				out.flush();
		    }
		    catch (Exception e) {
				// TODO: handle exception
		    	System.out.println(e);
		    	logger.warning(e.toString());
			}
		    
		    try(FileWriter fileWriter = new FileWriter(modelsListFile);
		            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter))
		            {
		    			for(String line: modelList) {
		    				bufferedWriter.append(line);
		    				bufferedWriter.newLine();        	
		    			}
		            } catch(IOException ex) {
		                System.out.println("Error writing to file '"+ filename + "'");}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			logger.warning(e.toString());	
		}

	}
}

//{
//	  fit time: 408.069 ms,
//	  score time: 31.624 ms,
//	  validation data size: 110,
//	  error: 5,
//	  accuracy: 95.45%
//	}

class ReturnTrained{
	private double fitTime=0.0;
	private double scoreTime=0.0;
	private int validationDataSize=0;
	private int error=0;
	private double accuracy=0.0;
	private String modelName="";
	
	public ReturnTrained(double fitTime, double scoreTime, int validationDataSize, int error, double accuracy,
			String modelName) {
		super();
		this.fitTime = fitTime;
		this.scoreTime = scoreTime;
		this.validationDataSize = validationDataSize;
		this.error = error;
		this.accuracy = accuracy;
		this.modelName = modelName;
	}	
}
