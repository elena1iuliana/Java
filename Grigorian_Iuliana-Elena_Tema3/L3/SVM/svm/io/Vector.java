package io;

import svm.SVM;

public class Vector{
	public float[] X;
	public Clasa cl;
	
	public Vector(float[] X, Clasa cl){
		this.X = X;
		this.cl = cl;
	}	
		
	public int getDimension(){return X.length;}
	
	public double norm(){
		double s = 0;
		for(int i = 0; i < X.length; i++)
			s += X[i] * X[i];
		return Math.sqrt(s);
	}

}