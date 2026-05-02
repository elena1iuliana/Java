package ml;
import java.io.Serializable;

public interface Kernel extends Serializable {
    double compute(double[] x1, double[] x2);
}