package com.jordan;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.util.*;

import smile.classification.*;
import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.validation.ClassificationMetrics;
import smile.validation.metric.Accuracy;

// class TestSmile 
public class TestSmile implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// variables to store the attributes of the objects created
	public RandomForest forest; // stores the trained forest
	public ClassificationMetrics metrics; // stores the metrics of the forest
	public double accuracy = 0.0; // holds the accuracy value of the model
	public int[] testResult; // store the test result array to compute accuracy
	
	
	// function to train the created model
	void trainModel(DataFrame irisTrain) {

		// define the formula used for fitting
		Formula formula = Formula.lhs("V5"); // here the column species (5th column) will be take as lhs value that is class value
	    
		
		// now we create a accurate fit based on the data in training set
	    Properties prop = new Properties();
	    prop.setProperty("smile.random.forest.trees", "200");
	    RandomForest forest = RandomForest.fit(formula, irisTrain, prop); // create a fit and store it in a RandomForest object forest
	    ClassificationMetrics metrics = forest.metrics();  // get the metrics of the model
	    this.forest = forest;
	    this.metrics = metrics;
	    
	}
	
	// function to test the created model
	int[] testModel(DataFrame irisTest) {
		
		// we get the testing set as irisTest dataframe
		this.testResult = this.forest.predict(irisTest);
		// we print the resultant array to the console
		for(int j: this.testResult)
	    	System.out.println(j);
		// also the resultant array is returned
		return this.testResult;
	}
	
	// function to test the accuracy the result provided
	double testAccuracy(String fileToVerify) throws FileNotFoundException {
		
		// we receive the correct classifications from the user and cross check them with the predicted classifications
		int[] iAccu = this.csvToArr(fileToVerify);
		// we store the accuracy
	    this.accuracy = Accuracy.of(this.testResult, iAccu)*100.0;
	    return this.accuracy;  
	}
	
	// function to display the metrics of the model 
	void modelMetrics() {
		System.out.format("OOB error rate = %.2f%%%n", (this.metrics.accuracy));
		System.out.println("Accuracy= "+this.accuracy);   
	}
	
	// function to transform a single column csv to an integer array
	int[] csvToArr(String path) throws FileNotFoundException {
		
		// read the contents of the column
		Scanner obj = new Scanner(new BufferedReader(new FileReader(path)));
		int i = 0;
	    int[] iAccu = new int[this.testResult.length];
	    // store them in an integer array
	    while(obj.hasNextLine()) {
	    	iAccu[i] = Integer.parseInt(obj.nextLine());
	    	i++;
	    }
	    // return the array
	    return iAccu;
	}

}
