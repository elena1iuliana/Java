package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import svm.SVM;
import alg.*;

public class Options extends Dialog implements AdjustmentListener{
	SVM svm;
	public Scrollbar sb;
	Label sb_label1, sb_label2;
	String s = "";
	String s1 = "The changes will only be valid on new Load Input Data.";

	public Options(SVM svm){
		super(svm, "Learning Options", true);
		this.svm = svm;
		setBackground(svm.settings.background_color_default);
		setForeground(svm.settings.string_color_default);
		setResizable(false);		
		resize(640,480);
		move((svm.res.width-640)/2,(svm.res.height-480)/2);				
		setLayout(null);
		
		int y = 50;
		sb_label1 = new Label("Percentage of Input Data:");
		sb_label1.setBounds(25,y,150,20);
		sb_label1.setForeground(Color.white);
		add(sb_label1);		
		
		sb = new Scrollbar(Scrollbar.HORIZONTAL, 80, 1, 20, 101);
		sb.setBounds(200,y,150,20);
		sb.setBackground(svm.settings.button_color_default);
		sb.setForeground(svm.settings.button_label_default);
		sb.addAdjustmentListener(this);
		add(sb);
		setValue();
		
		sb_label2 = new Label(sb.getValue()+"%");
		sb_label2.setBounds(360,y,150,20);
		sb_label2.setForeground(Color.white);
		add(sb_label2);	

		
	}
	
	public void setValue(){
		if(10<=svm.settings.percentage_inputData && svm.settings.percentage_inputData<=100)
			sb.setValue(svm.settings.percentage_inputData);
		else{
			svm.settings.percentage_inputData = 80;
			svm.settings.saveSettings();
		}
		s = sb.getValue() + "% of the input data will be used in learning, and " + (100-sb.getValue()) + "% will be used for classifier testing.";			
	}
	
	public boolean handleEvent(Event e){
		if(e.id==Event.WINDOW_DESTROY){
			svm.settings.percentage_inputData = sb.getValue();
			svm.settings.saveSettings();			
			dispose();						
		}	
		return super.handleEvent(e);
	}	

	public void adjustmentValueChanged(AdjustmentEvent e){
		sb_label2.setText(sb.getValue()+"%");
		s = sb.getValue() + "% of the input data will be used in learning, and " + (100-sb.getValue()) + "% will be used for classifier testing.";
		repaint();
	}
	
	
	
	public void paint(Graphics g){
		g.drawString(s,25,90);
		g.drawString(s1,25,115);
	}
}