package alg;

import svm.SVM;
import io.*;

public class Perceptron extends Algorithm {

    public Perceptron(SVM svm) {
        super(svm);
        if (svm.ind.V != null) {
            name = "Perceptron";
            svm.outd.algorithm = name;
            // P este numărul maxim de epoci/iterații definit în clasa părinte
            svm.outd.showInputData();
        }
    }

    public void run() {
        t = System.currentTimeMillis();
        float[] w = new float[dim + 1]; // w[0...dim-1] sunt ponderile, w[dim] este bias-ul (b)
        
        // Inițializare ponderi cu 0 sau valori mici
        for (int i = 0; i <= dim; i++) w[i] = 0.0f;

        long step = 0;
        boolean convergenta = false;

        while (step < P && !convergenta) {
            convergenta = true;
            for (int i = 0; i < N; i++) {
                // Calculăm ieșirea liniară: f(x) = w*x + b
                float suma = 0;
                for (int j = 0; j < dim; j++) {
                    suma += w[j] * svm.ind.V[i].X[j];
                }
                suma += w[dim]; // Adăugăm bias-ul

                // Determinăm clasa prezisă (-1 pentru clasa 0, 1 pentru clasa 1)
                // În proiectul tău, clasele sunt 0 și 1. Convertim pentru calcul:
                int y_real = (svm.ind.V[i].cl.Y == 0) ? -1 : 1;
                
                // Verificăm condiția de clasificare corectă: y * f(x) > 0
                if (y_real * suma <= 0) {
                    // Clasificare greșită -> Actualizăm ponderile
                    for (int j = 0; j < dim; j++) {
                        w[j] = w[j] + eta * y_real * svm.ind.V[i].X[j];
                    }
                    w[dim] = w[dim] + eta * y_real; // Actualizare bias
                    convergenta = false;
                }
            }
            step++;

            // Opțional: Trimitem ponderile parțiale către interfață pentru animație
            if (step % 100 == 0) {
                svm.design.setPointsOfLine(w);
                try { Thread.sleep(1); } catch (Exception e) {}
            }
        }

        // Finalizare: calculăm timpul și acuratețea
        long t1 = System.currentTimeMillis();
        svm.outd.computing_time = t1 - t;
        svm.outd.stages_count = step;
        svm.outd.w = w;
        svm.outd.accuracy = getAccuracy(w);
        
        // Afișăm linia finală și rezultatele
        svm.design.setPointsOfLine(w);
        svm.outd.showOutputData();
        svm.design.calculates = false;
        svm.design.repaint();
    }
}