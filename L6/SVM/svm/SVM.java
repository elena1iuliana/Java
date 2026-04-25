package svm;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import gui.*;
import alg.*;
import tools.*;
import io.*;

public class SVM extends Frame {
    public Toolkit tool;
    public MenuBar mb;
    public Dimension res;      
    public Image ico, bkg, color, calculates;
    
    public Design design;
    public Settings settings;
    public SimulationControl control;
    public About about;
    public Options options;
        
    public OutputData outd;
    public InputData ind;
    
    public Algorithm algorithm;
         
    public static void main (String args[]){new SVM();}

    public SVM(){
        tool=getToolkit(); 
        res=tool.getScreenSize();
        loadImages();
        setIconImage(ico);
        setTitle("SVM Simulator - Dual Perceptron Edition"); 
        adaugaMenuBar();    
        
        design = new Design(this);
        add("Center", design);
        
        settings = new Settings(this);
        settings.resize(376, 600);
        settings.move((res.width-376)/2,(res.height-600)/2);    
        
        about = new About(this);
        about.resize(712, 410);
        about.move((res.width-712)/2,(res.height-410)/2);   
        
        control = new SimulationControl(this, 400, res.height-80);
        control.resize(400, res.height-80);
        control.move(res.width-405,35); 
        
        options = new Options(this);
        outd = new OutputData(this);    
        ind = new InputData(this);
        
        setResizable(false);    
        setBackground(settings.background_color);
        resize(res.width,res.height-40);    
        move(0,0);  
        show();     
    }

    void adaugaMenuBar(){
        mb=new MenuBar();    
        Menu file = new Menu("File");
        file.add("Load Input Data");
        file.add("-");
        file.add("Exit");    
        mb.add(file);

        Menu algorithms = new Menu("Algorithms");
        algorithms.add("Median");
        algorithms.add("Perceptron Dual");
        algorithms.add("Perceptron Dual (Pocket)"); 
        mb.add(algorithms);

        Menu view = new Menu("View");
        view.add("Show Simulation Control");
        view.add("Show Input Data");
        view.add("Show Output Data");
        view.add("-");  
        view.add("Show Cursor Coordinates"); 
        mb.add(view);

        Menu tools = new Menu("Tools");
        tools.add("Input Data Generator"); 
        tools.add("-"); 
        tools.add("Settings");  
        mb.add(tools);

        Menu help = new Menu("Help");
        help.add("Help");   
        help.add("About");
        mb.add(help);
        setMenuBar(mb);    
    }

    public URL getResources(String s) {return this.getClass().getResource(s);}

    public void loadImages(){
        try {                                     
            bkg = tool.getImage(getResources("res/bkg.jpg"));        
            ico = tool.getImage(getResources("res/ico.png"));  
            color = tool.getImage(getResources("res/color.png")); 
            calculates = tool.getImage(getResources("res/calculates.gif"));    
        }
        catch(Throwable e) {System.out.println("Eroare la incarcarea imaginilor!");}
    }

    public boolean handleEvent(Event e){
        if(e.id==Event.WINDOW_DESTROY){
            System.exit(0);
        }else if(e.id==Event.ACTION_EVENT && e.target instanceof MenuItem){
            String arg = (String)e.arg;

            if("Exit".equals(arg)){
                System.exit(0);
            }else if("Load Input Data".equals(arg)){
                ind.show();             
                return true; 
            }
            
            // --- LOGICA SELECTIE ALGORITMI ---
            else if("Median".equals(arg) || "Perceptron Dual".equals(arg) || "Perceptron Dual (Pocket)".equals(arg)){
                if(ind.V != null){
                    // Oprim algoritmul curent daca ruleaza ceva
                    if(algorithm != null){ 
                        algorithm.stop_(); 
                        algorithm = null; 
                    }
                    
                    // Resetam starea vizuala
                    resetUI();

                    if("Median".equals(arg)) {
                        algorithm = new Median(this);
                    } else {
                        // Verificam daca e varianta Pocket
                        boolean isPocket = arg.contains("Pocket");
                        algorithm = new PerceptronDual(this, isPocket);
                    }
                    
                    control.show();
                    mb.getMenu(2).getItem(0).setLabel("Hide Simulation Control");
                } else {
                    System.out.println("Eroare: Incarcati datele inainte de a alege un algoritm!");
                }
                return true;        
            }
            // ---------------------------------

            else if("Show Simulation Control".equals(arg)){
                control.show();
                mb.getMenu(2).getItem(0).setLabel("Hide Simulation Control");
                return true;
            }else if("Hide Simulation Control".equals(arg)){
                control.hide();
                mb.getMenu(2).getItem(0).setLabel("Show Simulation Control");
                return true;    
            }else if("Show Input Data".equals(arg)){
                ind.show();
                mb.getMenu(2).getItem(1).setLabel("Hide Input Data");
                return true;
            }else if("Hide Input Data".equals(arg)){
                ind.hide();
                mb.getMenu(2).getItem(1).setLabel("Show Input Data");
                return true;
            }else if("Show Output Data".equals(arg)){
                outd.show();
                mb.getMenu(2).getItem(2).setLabel("Hide Output Data");
                return true;
            }else if("Hide Output Data".equals(arg)){
                outd.hide();
                mb.getMenu(2).getItem(2).setLabel("Show Output Data");
                return true;                        
            }else if("Show Cursor Coordinates".equals(arg)){
                design.show_coords = true;
                design.repaint();
                mb.getMenu(2).getItem(4).setLabel("Hide Cursor Coordinates");
                return true;
            }else if("Hide Cursor Coordinates".equals(arg)){
                design.show_coords = false;
                design.repaint();
                mb.getMenu(2).getItem(4).setLabel("Show Cursor Coordinates");
                return true;            
            }else if("Input Data Generator".equals(arg)){
                new InputDataGenerator(this).show();
                return true; 
            }else if("Settings".equals(arg)){
                settings.loadSettings();
                settings.show();
                return true; 
            }else if("Help".equals(arg)){
                handleHelp();
                return true; 
            }else if("About".equals(arg)){
                about.show();
                return true; 
            }               
        }
        return super.handleEvent(e);
    }

    private void handleHelp() {
        File help = new File("svm/SVM.pdf");
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(help);
            } else {
                // Fallback pentru sisteme mai vechi
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + help);
            }
        } catch(Exception ex) {
            System.out.println("Nu s-a putut deschide fisierul help!");
        }
    }

    public void init(){
        if(algorithm !=null){
            algorithm.stop_();
            algorithm = null;
        }       
        ind.input_file = null;
        ind.V = null;
        resetUI();
    }

    public void init2(){
        resetUI();  
        // ind.init(); 
    }   

    private void resetUI() {
        design.show_line = false;
        design.calculates = false; // Aceasta variabila opreste animația de loading
        control.init = true;       // Permite butonului de Start sa reia procesul
        control.start.setLabel("Start Simulation");     
        design.repaint();
    }
}