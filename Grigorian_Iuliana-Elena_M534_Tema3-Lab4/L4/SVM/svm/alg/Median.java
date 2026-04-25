package alg;

import svm.SVM;

public class Median extends Algorithm {

    public Median(SVM svm) {
        super(svm);
        if (svm.ind.ALL != null) {
            name = "Median";
            svm.outd.algorithm = name;
            svm.outd.max_stages_count = 1;
            this.w = new float[dim + 1];
            svm.outd.showInputData();
        }
    }

    @Override
    public void run() {
        try {
            if (!running) {
                svm.design.calculates = false;
                svm.design.repaint();
                return;
            }

            t = System.currentTimeMillis();

            // Verificam ca avem date valide
            if (svm.ind.V == null || svm.ind.classes == null) {
                System.out.println("[Median] EROARE: date null");
                svm.design.calculates = false;
                svm.design.repaint();
                return;
            }

            System.out.println("[Median] START - N=" + N + " dim=" + dim);

            int labelA = svm.ind.classes[0].Y;
            int labelB = svm.ind.classes[1].Y;

            float[] centroidA = new float[dim];
            float[] centroidB = new float[dim];
            int countA = 0, countB = 0;

            for (int i = 0; i < N; i++) {
                int label = svm.ind.V[i].cl.Y;
                if (label == labelA) {
                    for (int j = 0; j < dim; j++) centroidA[j] += svm.ind.V[i].X[j];
                    countA++;
                } else if (label == labelB) {
                    for (int j = 0; j < dim; j++) centroidB[j] += svm.ind.V[i].X[j];
                    countB++;
                }
            }

            System.out.println("[Median] countA=" + countA + " countB=" + countB);

            if (!running || countA == 0 || countB == 0) {
                svm.design.calculates = false;
                svm.design.repaint();
                return;
            }

            for (int j = 0; j < dim; j++) {
                centroidA[j] /= countA;
                centroidB[j] /= countB;
            }

            float bias = 0;
            for (int j = 0; j < dim; j++) {
                w[j]  = centroidB[j] - centroidA[j];
                bias += w[j] * (centroidA[j] + centroidB[j]) / 2.0f;
            }
            w[dim] = -bias;

            System.out.println("[Median] w calculat, bias=" + bias);

            // Opreste animatia
            svm.design.calculates = false;

            // Rezultate
            svm.outd.stages_count   = 1;
            svm.outd.computing_time = System.currentTimeMillis() - t;
            svm.outd.w              = w;
            svm.outd.accuracy       = getAccuracy(w);
            svm.outd.showInputData();
            svm.outd.showOutputData();

            System.out.println("[Median] showOutputData DONE");

            // Deseneaza linia
            if (dim == 2) {
                svm.design.setPointsOfLine(w);
            } else {
                svm.design.repaint();
            }

            // Reseteaza butonul
            svm.control.start.enable(true);
            svm.control.start.setLabel("Start Simulation");
            svm.control.init = false;

            System.out.println("[Median] DONE");

        } catch (Exception ex) {
            System.out.println("[Median] EXCEPTIE: " + ex.getClass().getName() + ": " + ex.getMessage());
            ex.printStackTrace();
            svm.design.calculates = false;
            svm.design.repaint();
        }
    }
}