package ml;
import java.io.Serializable;

public class SigmoidKernel implements Kernel, Serializable {
    private double gamma;
    private double coef0;

    public SigmoidKernel(double gamma, double coef0) {
        this.gamma = gamma;
        this.coef0 = coef0;
    }

    @Override
    public double compute(double[] x1, double[] x2) {
        double dotProduct = 0;
        for (int i = 0; i < x1.length; i++) {
            dotProduct += x1[i] * x2[i];
        }
        
        return Math.tanh(gamma * dotProduct + coef0);
    }
}