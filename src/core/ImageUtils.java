package core;

public class ImageUtils {
    public static int[][] toGrayscale(byte[] data, int w, int h, int ch) {
        int[][] gray = new int[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int i = (y * w + x) * ch;
                if (ch == 3) {
                    int b = data[i] & 0xFF, g = data[i+1] & 0xFF, r = data[i+2] & 0xFF;
                    gray[y][x] = (int)(0.299 * r + 0.587 * g + 0.114 * b);
                } else gray[y][x] = data[i] & 0xFF;
            }
        }
        return gray;
    }

    public static int[][] resize128(int[][] src, int w, int h) {
        int[][] dst = new int[128][128];
        for (int i = 0; i < 128; i++)
            for (int j = 0; j < 128; j++)
                dst[i][j] = src[Math.min((int)(i*h/128.0), h-1)][Math.min((int)(j*w/128.0), w-1)];
        return dst;
    }
}