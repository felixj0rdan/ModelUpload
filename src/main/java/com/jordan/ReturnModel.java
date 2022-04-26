package com.jordan;

public class ReturnModel {
	private int[] results;
	private double fitAccuarcy=0.0;
	private double fitTime=0.0;
	public ReturnModel(int[] results, double fitAccuarcy, double fitTime) {
		super();
		this.results = results;
		this.fitAccuarcy = fitAccuarcy;
		this.fitTime = fitTime;
	}
}
