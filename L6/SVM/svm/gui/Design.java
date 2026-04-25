package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import svm.SVM;

public class Design extends Panel implements MouseListener, MouseMotionListener{
	SVM svm;
	Image im;
	Graphics img;
	int ww, hh;
	int Ox, Oy, cx, cy, ccx, ccy;
	boolean init = true;
	String coords = "";
	public boolean show_coords = false;
	public boolean show_line = false;
	public boolean calculates = false;
	public int x1, y1, x2, y2;
	
	public Design(SVM svm){
		this.svm = svm;
		addMouseListener(this);
		addMouseMotionListener(this);		
	}
	
	public void initO(){
		Ox = ww/2; Oy = hh/2;
		cx = 0; cy = 0;
		repaint();
	}
	
	public void paint(Graphics g){update(g);}
	
	public void update(Graphics g){
		if(init){
			ww = size().width;
			hh = size().height;
			im = createImage(ww, hh);
			img = im.getGraphics();	
			initO();
			init = false;			
		}
		
		img.setColor(svm.settings.background_color);
		img.fillRect(0,0,ww,hh);	
		if(svm.ind.V != null && svm.ind.V[0].getDimension() == 2){
			drawAxis(img);

			// GIF-ul de loading se deseneaza pe fundal, INAINTE de linie si puncte
			if(calculates) img.drawImage(svm.calculates, (ww-svm.calculates.getWidth(this))/2, (hh-svm.calculates.getHeight(this))/2, this);
			
			if(show_line){
				img.setColor(svm.settings.line_color);
				img.drawLine(Ox+x1,Oy-y1,Ox+x2,Oy-y2);
			}
			
			for(int i = 0; i < svm.ind.V.length; i++){
				Point p = new Point(Ox + (int)(svm.ind.V[i].X[0]+0.5), Oy - (int)(svm.ind.V[i].X[1]+0.5));
				img.setColor(svm.ind.V[i].cl.color);
				int r = svm.settings.point_radius;
				img.fillOval(p.x-r,p.y-r,2*r,2*r);
				img.setColor(Color.black);
				img.drawOval(p.x-r,p.y-r,2*r,2*r);			
			}
			
			if(show_coords){
				img.setColor(svm.settings.string_color);
				img.drawString(coords, ccx+15, ccy+30);
			}

		}else{
			// Fara date incarcate: GIF-ul apare normal in centru
			if(calculates) img.drawImage(svm.calculates, (ww-svm.calculates.getWidth(this))/2, (hh-svm.calculates.getHeight(this))/2, this);
		}
		
		g.drawImage(im,0,0,this);
	}

	public void drawAxis(Graphics g){
		//deseneaza gridul
		if(svm.settings.grid){
			g.setColor(svm.settings.grid_color);	
			for(int i=Ox+svm.settings.axis_min; i<=Ox+svm.settings.axis_max; i+=svm.settings.grid_size) g.drawLine(i, Oy+svm.settings.axis_min, i, Oy+svm.settings.axis_max);
			for(int j=Oy+svm.settings.axis_min; j<=Oy+svm.settings.axis_max; j+=svm.settings.grid_size) g.drawLine(Ox+svm.settings.axis_min, j, Ox+svm.settings.axis_max, j);
		}
		//deseneaza axele
		if(svm.settings.axis){
			g.setColor(svm.settings.axis_color);		
			g.drawLine(Ox+svm.settings.axis_min, Oy, Ox+svm.settings.axis_max, Oy);
			g.drawLine(Ox, Oy+svm.settings.axis_min, Ox, Oy+svm.settings.axis_max);	
			//deseneaza gradatiile
			if(svm.settings.gradations){
				for(int i=Ox; i<=Ox+svm.settings.axis_max; i+=svm.settings.axis_gradations) g.drawLine(i, Oy-2, i, Oy+2);
				for(int i=Ox; i>=Ox+svm.settings.axis_min; i-=svm.settings.axis_gradations) g.drawLine(i, Oy-2, i, Oy+2);
				for(int j=Oy; j<=Oy+svm.settings.axis_max; j+=svm.settings.axis_gradations) g.drawLine(Ox-2, j, Ox+2, j);
				for(int j=Oy; j>=Oy+svm.settings.axis_min; j-=svm.settings.axis_gradations) g.drawLine(Ox-2, j, Ox+2, j);
			}
		}
	}

	public void setPointsOfLine(float[] w){
		show_line = true;
		// Folosim jumatate din dimensiunea ferestrei ca limite vizibile
		// (axis_min/max pot fi ±100000 si linia ar iesi din ecran)
		int limMin = -ww/2 - ww;
		int limMax =  ww/2 + ww;
		if(Math.abs(w[0]) < Math.abs(w[1])){
			x1 = limMin;
			y1 = (int)((-w[2]-w[0]*x1)/w[1]+0.5);
			x2 = limMax;
			y2 = (int)((-w[2]-w[0]*x2)/w[1]+0.5);
		}else{
			y1 = limMin;
			x1 = (int)((-w[2]-w[1]*y1)/w[0]+0.5);
			y2 = limMax;
			x2 = (int)((-w[2]-w[1]*y2)/w[0]+0.5);			
		}
		repaint();
	}	
	
	public void mouseClicked(MouseEvent me) {initO();}

	public void mouseEntered(MouseEvent me) {}

	public void mouseExited(MouseEvent me) {}

	public void mouseMoved(MouseEvent me) {
		ccx = me.getX(); ccy = me.getY();
		coords = "(" + (ccx-Ox) + "," + (Oy-ccy) + ")";
		if(ccx<=2 || ccx >= ww-5 || ccy<=5 || ccy>=hh-5) coords = "";
		repaint();
	} 
		
	public void mousePressed(MouseEvent me) { 
		cx = me.getX(); cy = me.getY();  
		coords = "";		
	}
			
	public void mouseDragged(MouseEvent me) {
		int x = me.getX(), y = me.getY();
		if(svm.ind.V != null && svm.ind.V[0].getDimension() == 2){
			cx = x - cx; cy = y - cy;  
			Ox += cx; Oy += cy;
			cx = x; cy = y;
			coords = "";
			repaint();	
		}
	}

	public void mouseReleased(MouseEvent me) {
		int x = me.getX(), y = me.getY(); 
	}	
	
}