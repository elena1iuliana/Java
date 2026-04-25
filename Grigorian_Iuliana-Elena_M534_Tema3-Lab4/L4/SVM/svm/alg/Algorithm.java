package alg;

import svm.SVM;
import io.*;

public abstract class Algorithm extends Thread {
    public SVM svm;
    public String name;    
    public float eta;
    public long P = 10000000;
    public int N;
    public int dim;
    public long t;
    public float[] w; 
    
    // Mecanism nou pentru controlul thread-ului
    protected volatile boolean running = true;

    public Algorithm(SVM svm) {
        this.svm = svm;
        eta = svm.settings.learning_rate;
        if(svm.ind.ALL != null) {
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

    public void start_simulation() {
        svm.design.calculates = true;
        svm.design.repaint();
        running = true;
        start();
    }

    // Metode de control actualizate
    public void stop_() { 
        running = false;
        this.interrupt(); // Trezește thread-ul dacă este în sleep
    }

    public void suspend_() { 
        // Pentru algoritmi instanți precum Median, suspend nu e critic, 
        // dar poate fi implementat cu un flag 'paused' în algoritmi iterativi.
    }

    public void resume_() { }

    public abstract void run();
    
    /**
     * Creeaza o noua instanta a aceluiasi algoritm cu aceeasi referinta SVM.
     * Necesar deoarece un Thread Java nu poate fi restartat dupa ce a terminat.
     * Fiecare subclasa concreta este instantiata prin reflection.
     */
    public Algorithm newInstance() {
        try {
            return this.getClass()
                       .getConstructor(svm.SVM.class)
                       .newInstance(svm);
        } catch (Exception e) {
            e.printStackTrace();
            return this;
        }
    }
    
    public int getAccuracy(float[] w) {
        if (w == null) return 0;
        int accuracy = 100;
        if(svm.ind.T != null) {
            svm.outd.testing_vectors_count = svm.ind.T.length;
            int hit = 0;
            for(int i = 0; i < svm.ind.T.length; i++) {
                float s = 0;
                for(int j = 0; j < dim; j++)
                    s += w[j] * svm.ind.T[i].X[j];
                s += w[dim]; // Bias
                int y = (s >= 0) ? 1 : 0;
                if(y == svm.ind.T[i].cl.Y) hit++;
            }
            accuracy = (hit * 100) / svm.ind.T.length;
        }
        return accuracy;
    }
}