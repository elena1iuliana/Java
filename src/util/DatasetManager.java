package util;

import core.HOGExtractor;
import core.ImageUtils;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class DatasetManager {
    public Map<String, List<double[]>> loadFeatures() throws Exception {
        Map<String, List<double[]>> dataset = new HashMap<>();
        File folder = new File("learning_set/");
        
        if (!folder.exists()) {
            folder.mkdir();
            System.out.println("[INFO] Folderul learning_set a fost creat.");
            return dataset;
        }

        
        File[] files = folder.listFiles((d, n) -> {
            String name = n.toLowerCase();
            return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png");
        });

        if (files == null || files.length == 0) {
            System.out.println("[AVERTIZARE] Folderul learning_set este gol!");
            return dataset;
        }

        HOGExtractor hog = new HOGExtractor();

        for (File f : files) {
            try {
               
                String fileName = f.getName();
                String label;
                
                if (fileName.contains("_")) {
                    // "Andrei_2026_04.jpg" -> "Andrei"
                    label = fileName.split("_")[0];
                } else {
                    // "om.jpg" -> "om" (scoatem extensia)
                    label = fileName.substring(0, fileName.lastIndexOf('.'));
                }
                
                BufferedImage img = ImageIO.read(f);
                if (img == null) continue;

                int width = img.getWidth();
                int height = img.getHeight();

                
                int[][] gray = new int[height][width];
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int c = img.getRGB(x, y);
                        int r = (c >> 16) & 0xFF;
                        int g = (c >> 8) & 0xFF;
                        int b = c & 0xFF;
                        gray[y][x] = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                    }
                }

                
                int[][] resizedGray = ImageUtils.resize128(gray, width, height);

               
                double[] features = hog.extract(resizedGray);
                
               
                dataset.computeIfAbsent(label, k -> new ArrayList<>()).add(features);
                
                System.out.println("Incarcat: " + fileName + " -> Eticheta: [" + label + "]");

            } catch (Exception e) {
                System.err.println("Eroare la procesarea fisierului: " + f.getName() + " - " + e.getMessage());
            }
        }
        
        System.out.println("--- Rezumat Incarcare ---");
        dataset.forEach((key, value) -> System.out.println("Persoana/Obiect: " + key + " | Imagini: " + value.size()));
        return dataset;
    }
}