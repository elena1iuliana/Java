package ui;

import ml.*;
import core.*;
import util.*;
import org.opencv.core.*;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs; 
import java.util.*;
import java.io.File;
import java.text.SimpleDateFormat;

public class FaceApp {
    private VideoCapture cap;
    private CascadeClassifier faceDet;
    private Map<String, SVM> trainedModels = new HashMap<>();
    private boolean isReady = false;
    private WindowHelper win;

    public void start() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        cap = new VideoCapture(0);
        faceDet = new CascadeClassifier("haarcascade_frontalface_alt.xml");
        
       
        loadModels();
        
        
        this.win = new WindowHelper("Sistem Recunoastere Om vs Obiect", this);
        
        Mat frame = new Mat();
        HOGExtractor hog = new HOGExtractor();

        while (cap.read(frame)) {
            MatOfRect detections = new MatOfRect();
            faceDet.detectMultiScale(frame, detections);
            
            for (Rect r : detections.toArray()) {
                
                Imgproc.rectangle(frame, r, new Scalar(0, 255, 0), 2);
                
                String statusText = "Detectat: Om"; 
                
                if (isReady) {
                    Mat faceROI = new Mat(frame, r);
                    
                    byte[] data = new byte[(int)(faceROI.total() * faceROI.channels())];
                    faceROI.get(0, 0, data);
                    int[][] gray = ImageUtils.toGrayscale(data, faceROI.cols(), faceROI.rows(), faceROI.channels());
                    double[] features = hog.extract(ImageUtils.resize128(gray, faceROI.cols(), faceROI.rows()));

                    String nameFound = "Necunoscut";
                    double maxScore = -Double.MAX_VALUE;

                    
                    for (var entry : trainedModels.entrySet()) {
                        double score = entry.getValue().predictRaw(features);
                        if (score > maxScore && score > 0) {
                            maxScore = score;
                            nameFound = entry.getKey();
                        }
                    }
                    statusText = "Om: " + nameFound;
                }

                Imgproc.putText(frame, statusText, new Point(r.x, r.y - 10), 
                                Imgproc.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(0, 255, 0), 2);
            }

            if (detections.empty() && isReady) {
                 Imgproc.putText(frame, "Scanare... Nicio fata detectata", new Point(20, 50), 
                                1, 1.5, new Scalar(0, 0, 255), 2);
            }

            win.updateImage(frame);
        }
    }
    
   
    public void captureDataset(String nickname) {
        int count = 0;
        Mat frame = new Mat();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");

        while (count < 200) {
            if (cap.read(frame)) {
                MatOfRect faces = new MatOfRect();
                faceDet.detectMultiScale(frame, faces);
                
                for (Rect r : faces.toArray()) {
                    if (count >= 200) break;
                    
                    Mat face = new Mat(frame, r);
                   
                    String timestamp = sdf.format(new Date());
                    String fileName = "learning_set/" + nickname + "_" + timestamp + ".jpg";
                    
                    Imgcodecs.imwrite(fileName, face);
                    count++;

                    Imgproc.rectangle(frame, r, new Scalar(255, 0, 0), 2);
                    Imgproc.putText(frame, "Captura: " + count + "/200", new Point(r.x, r.y - 10), 
                                    1, 1.5, new Scalar(255, 255, 255), 2);
                }
                win.updateImage(frame);
            }
        }
        System.out.println("Captura finalizata pentru " + nickname);
    }

    public void train() {
        try {
            System.out.println("Incepere extragere trasaturi HOG...");
            var data = new DatasetManager().loadFeatures(); // Punctul 5
            
            for (String label : data.keySet()) {
                if (label.equalsIgnoreCase("obiect")) continue;
                
                List<double[]> pos = data.get(label);
                List<double[]> neg = new ArrayList<>();
                
                for (var e : data.entrySet()) {
                    if (!e.getKey().equals(label)) neg.addAll(e.getValue());
                }
                
                double[][] X = new double[pos.size() + neg.size()][];
                int[] y = new int[pos.size() + neg.size()];
                int i = 0;
                for(double[] d : pos) { X[i] = d; y[i++] = 1; }
                for(double[] d : neg) { X[i] = d; y[i++] = -1; }
                
                
                System.out.println("Antrenare clasificator pentru: " + label);
                trainedModels.put(label, new SMO(new SigmoidKernel(0.01, 0)).train(X, y));
            }
            
            isReady = true;
            saveModels(); 
            System.out.println("Antrenare finalizata si salvata!");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void saveModels() {
        try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(
                new java.io.FileOutputStream("trained_models.bin"))) {
            oos.writeObject(new TrainedData(trainedModels));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadModels() {
        File f = new File("trained_models.bin");
        if (f.exists()) {
            try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(
                    new java.io.FileInputStream(f))) {
                TrainedData data = (TrainedData) ois.readObject();
                this.trainedModels = data.models;
                this.isReady = true;
                System.out.println("Modele incarcate cu succes!");
            } catch (Exception e) { System.out.println("Nu s-au gasit modele pre-antrenate."); }
        }
    }
}