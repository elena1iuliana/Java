package ml;
import java.util.Random;

public class SMO {
    private Kernel kernel;
    private double C = 1.0;
    public SMO(Kernel k) { this.kernel = k; }

    public SVM train(double[][] X, int[] y) {
        int m = X.length;
        double[] alphas = new double[m];
        double b = 0;
        Random rand = new Random();
        for (int p = 0; p < 20; p++) { 
            for (int i = 0; i < m; i++) {
                int j = rand.nextInt(m);
                double Ei = computeF(X, y, alphas, b, X[i]) - y[i];
                double Ej = computeF(X, y, alphas, b, X[j]) - y[j];
                if ((y[i]*Ei < -0.001 && alphas[i] < C) || (y[i]*Ei > 0.001 && alphas[i] > 0)) {
                    double eta = 2 * kernel.compute(X[i], X[j]) - kernel.compute(X[i], X[i]) - kernel.compute(X[j], X[j]);
                    if (eta >= 0) continue;
                    double oldAj = alphas[j];
                    alphas[j] -= (y[j] * (Ei - Ej)) / eta;
                    alphas[j] = Math.max(0, Math.min(C, alphas[j]));
                    alphas[i] += y[i] * y[j] * (oldAj - alphas[j]);
                    b = (b - Ei + b - Ej) / 2.0;
                }
            }
        }
        return new SVM(X, y, alphas, b, kernel);
    }
    private double computeF(double[][] X, int[] y, double[] a, double b, double[] x) {
        double s = b;
        for (int i = 0; i < a.length; i++) if (a[i] > 0) s += a[i] * y[i] * kernel.compute(X[i], x);
        return s;
    }
}