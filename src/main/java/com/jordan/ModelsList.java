package com.jordan;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.google.gson.Gson;

/**
 * Servlet implementation class ModelsList
 */
public class ModelsList extends HttpServlet {
	private static Logger logger = Logger.getLogger("");
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		// to get the file directory
		String localDir = System.getProperty("user.dir");
		System.out.println(localDir);		
		String modelsListFile = localDir+"\\ModelUpload\\MLmodel\\ListOfModels.txt";
		
		ArrayList<String> modelList = new ArrayList<>(Files.readAllLines(Paths.get(modelsListFile)));
		System.out.println(modelList);
		
try(PrintWriter out = response.getWriter();){
	    	
	    	ReturnObject ro = new ReturnObject(modelList);
	    	String modelJsonString = new Gson().toJson(ro);
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

class ReturnObject {
	private ArrayList<String> listOfModels;
	public ReturnObject(ArrayList<String> list) {
		super();
		this.listOfModels = list;
	}
}
