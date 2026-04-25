package alg;

import svm.SVM;
import io.*;

public abstract class Algorithm  extends Thread{
	SVM svm;
	public String name;	
	public float eta;
	public long P = 10000000;
	public int N;
	public int dim;
	public long t;

	// flag-uri pentru controlul thread-ului
	protected volatile boolean stopped = false;
	protected volatile boolean paused  = false;
	
	public Algorithm(SVM svm){
		this.svm = svm;
		eta = svm.settings.learning_rate;
		if(svm.ind.ALL != null){
			N = svm.ind.V.length;
			dim = svm.ind.V[0].getDimension();		
			svm.outd.dataInputFile = svm.ind.input_file;
			svm.outd.vectors_count = N;
			svm.outd.attributes_count = dim;
			svm.outd.max_stages_count = P;	
			svm.control.start.enable(true);	
			svm.control.options.enable(true);
		}
	}
	
	public void suspend_(){
		paused = true;
	}	
	
	public void resume_(){
		paused = false;
		synchronized(this){ notifyAll(); }
	}	

	public void stop_(){
		stopped = true;
		paused  = false;
		svm.design.calculates = false;
		synchronized(this){ notifyAll(); }
	}		

	public void start_simulation(){
		svm.design.calculates = false; // GIF dezactivat - acoperea linia
		svm.design.repaint();
		start();
	}

	public abstract void run();
	
	public int getAccuracy(float[] w){
		int accuracy = 100;
		if(svm.ind.T != null && w != null){
			svm.outd.testing_vectors_count = svm.ind.T.length;
			int hit = 0;
			for(int i = 0; i < svm.ind.T.length; i++){
				float s = 0;
				for(int j = 0; j < dim; j++)
					s += w[j]*svm.ind.T[i].X[j];
				s += w[dim];
				int y = 1;
				if(s < 0) y = 0;
				if(y == svm.ind.T[i].cl.Y) hit++;
			}
			accuracy = (hit*100)/svm.ind.T.length;
		}
		return accuracy;
	}
	
}