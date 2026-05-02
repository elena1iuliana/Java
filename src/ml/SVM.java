package ml;
import java.io.Serializable;

public class SVM implements Serializable {
    public double[][] supportVectors;
    public int[] labels;
    public double[] alphas;
    public double b;
    public Kernel kernel;

    public SVM(double[][] sv, int[] l, double[] a, double b, Kernel k) {
        this.supportVectors = sv; this.labels = l; this.alphas = a; this.b = b; this.kernel = k;
    }

    public double predictRaw(double[] x) {
        double f = b;
        for (int i = 0; i < alphas.length; i++) {
            if (alphas[i] > 0) f += alphas[i] * labels[i] * kernel.compute(supportVectors[i], x);
        }
        return f;
    }
}