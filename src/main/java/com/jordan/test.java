package com.jordan;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String localDir = System.getProperty("user.dir");
		System.out.println(localDir);
		String filename = "D:\\eclipse-jee\\eclipse\\ModelUpload\\MLmodel\\ListOfModels.txt"; //D:\eclipse-jee\eclipse\ModelUpload\MLmodel D:\eclipse-jee\eclipse\ModelUpload\ModelUpload\MLmodel\ListOfModels.txt

		Random random = new Random();
		String newFile = "Model_"+String.format("%04d", random.nextInt(10000));	
		
		ArrayList<String> modelList = new ArrayList<>(Files.readAllLines(Paths.get(filename)));
		System.out.println(modelList);
		
		while(modelList.contains(newFile)) {
			newFile = "Model_"+String.format("%04d", random.nextInt(10000));
		}
		
		System.out.println(newFile);
		modelList.add(newFile);
		
//		System.out.println(lines);
//		lines.add("accvf");
//		System.out.println(lines);
//		lines.contains("acc");
//		System.out.println(lines.contains("accr"));
		
		try(FileWriter fileWriter = new FileWriter(filename);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter))
        {
			for(String line: modelList) {
				bufferedWriter.write(line);
				bufferedWriter.newLine();        	
			}
        } catch(IOException ex) {
            System.out.println("Error writing to file '"+ filename + "'");}
		
		
//		FileInputStream fis = new FileInputStream(file);
//		ObjectInputStream ois =  new  ObjectInputStream(fis)
//		
//		FileOutputStream fos = new FileOutputStream(file); 
//		ObjectOutputStream oos = new ObjectOutputStream(fos);
//		FileWriter fw = new FileWriter(file);
//		
//		
//		fw.close();
				
//		oos.writeBytes("Model_1"); 
//		oos.close();
	}

}
