package alg;

import svm.SVM;
import io.*;

public class PerceptronDual extends Algorithm {

    public boolean pocket;
    public float[] alpha;
    public float[] alpha_best;
    public float b = 0, b_best = 0;
    public int accuracy_best = -1;

    public PerceptronDual(SVM svm, boolean pocket) {
        super(svm);
        this.pocket = pocket;
        if (svm.ind.V != null) {
            name = pocket ? "Perceptron Dual (Pocket)" : "Perceptron Dual";
            svm.outd.algorithm = name;
            alpha      = new float[N];
            alpha_best = new float[N];
            svm.outd.showInputData();
        }
    }

    @Override
    public void run() {
        t = System.currentTimeMillis();

        // Matricea Gram: G[i][j] = <x_i, x_j> — calculata o singura data
        float[][] gram = new float[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j <= i; j++) {
                gram[i][j] = dotProduct(svm.ind.V[i].X, svm.ind.V[j].X);
                gram[j][i] = gram[i][j];
            }

        long epochsDone = 0;
        long totalEpochs = P / N;

        outer:
        for (long epoch = 0; epoch < totalEpochs; epoch++) {

            if (stopped) break;

            boolean anyError = false;

            for (int i = 0; i < N; i++) {
                int y_i = label(svm.ind.V[i].cl.Y);

                float f_i = b;
                for (int j = 0; j < N; j++)
                    f_i += alpha[j] * label(svm.ind.V[j].cl.Y) * gram[j][i];

                if (y_i * f_i <= 0) {
                    alpha[i] += eta;
                    b        += eta * y_i;
                    anyError  = true;

                    if (pocket) {
                        float[] w_crt = getWeightsFromDual(alpha, b);
                        int acc_crt   = getAccuracyTrain(w_crt);
                        if (acc_crt > accuracy_best) {
                            accuracy_best = acc_crt;
                            System.arraycopy(alpha, 0, alpha_best, 0, N);
                            b_best = b;
                        }
                    }
                }
            }

            epochsDone = epoch + 1;

            // Actualizare UI dupa fiecare epoch
            float[] w_draw = pocket
                    ? getWeightsFromDual(alpha_best, b_best)
                    : getWeightsFromDual(alpha, b);

            if (dim == 2) svm.design.setPointsOfLine(w_draw);
            svm.outd.stages_count = epochsDone * N;

            // Cedam controlul thread-ului catre UI
            try { Thread.sleep(5); } catch (InterruptedException e) { break outer; }

            // Convergenta pentru date liniar separabile
            if (!pocket && !anyError) break;
        }

        // Finalizare
        long computing_time = System.currentTimeMillis() - t;
        float[] w_final = pocket
                ? getWeightsFromDual(alpha_best, b_best)
                : getWeightsFromDual(alpha, b);

        svm.outd.w              = w_final;
        svm.outd.stages_count   = epochsDone * N;
        svm.outd.computing_time = computing_time;
        svm.outd.accuracy       = getAccuracy(w_final);

        svm.outd.showOutputData();

        if (dim == 2) svm.design.setPointsOfLine(w_final);
        svm.design.repaint();
        svm.control.start.enable(false);
    }

    public float[] getWeightsFromDual(float[] a, float bias) {
        float[] w = new float[dim + 1];
        for (int j = 0; j < dim; j++) {
            float sum = 0;
            for (int i = 0; i < N; i++)
                sum += a[i] * label(svm.ind.V[i].cl.Y) * svm.ind.V[i].X[j];
            w[j] = sum;
        }
        w[dim] = bias;
        return w;
    }

    public float[] getWeightsFromDual() {
        return getWeightsFromDual(alpha, b);
    }

    private int getAccuracyTrain(float[] w) {
        if (w == null) return 0;
        int hit = 0;
        for (int i = 0; i < N; i++) {
            float s = w[dim];
            for (int j = 0; j < dim; j++) s += w[j] * svm.ind.V[i].X[j];
            if (((s >= 0) ? 1 : 0) == svm.ind.V[i].cl.Y) hit++;
        }
        return (hit * 100) / N;
    }

    private float dotProduct(float[] x1, float[] x2) {
        float dot = 0;
        for (int k = 0; k < x1.length; k++) dot += x1[k] * x2[k];
        return dot;
    }

    private int label(int y) { return (y == 0) ? -1 : 1; }
}