package core;

public class HOGExtractor {
    public double[] extract(int[][] gray) {
        int h = gray.length, w = gray[0].length;
        double[][][] cells = new double[h/8][w/8][9];
        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                double gx = gray[y][x+1] - gray[y][x-1];
                double gy = gray[y+1][x] - gray[y-1][x];
                double mag = Math.sqrt(gx*gx + gy*gy);
                double ang = (Math.toDegrees(Math.atan2(gy, gx)) + 180) % 180;
                cells[y/8][x/8][(int)(ang/20) % 9] += mag;
            }
        }
        double[] feat = new double[(h/8-1)*(w/8-1)*36];
        int k = 0;
        for (int i = 0; i < h/8-1; i++) {
            for (int j = 0; j < w/8-1; j++) {
                double sum = 1e-6;
                for (int ii=0; ii<2; ii++)
                    for (int jj=0; jj<2; jj++)
                        for (int b=0; b<9; b++) {
                            feat[k] = cells[i+ii][j+jj][b];
                            sum += feat[k] * feat[k];
                            k++;
                        }
                double norm = Math.sqrt(sum);
                for (int idx = k - 36; idx < k; idx++) feat[idx] /= norm;
            }
        }
        return feat;
    }
}