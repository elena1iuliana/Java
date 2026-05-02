package ml;
import java.io.Serializable;
import java.util.Map;

public class TrainedData implements Serializable {
   
    public Map<String, SVM> models;
    
    public TrainedData(Map<String, SVM> models) {
        this.models = models;
    }
}