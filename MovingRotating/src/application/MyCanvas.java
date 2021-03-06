package application;

import javafx.scene.paint.Color;

import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.Random;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class MyCanvas extends Canvas {  
	private static Random r = new Random();
    private GraphicsContext gc;
    private double dx;
    
    private BGLine lines[] = new BGLine[4];
	private int doX=0;
	private int doY=0;
	private int doZ=0;
    
    public MyCanvas(Scene scene){  
        super(scene.getWidth(), scene.getHeight());  
        widthProperty().bind(scene.widthProperty());
        gc = getGraphicsContext2D();  
        for(int i=0;i<lines.length;i++){
        	lines[i] = new BGLine((int)getHeight(), (int)getWidth(),i*500/lines.length);
        }
        repaintContext();
    }  
     
   public void repaintContext(){ 
	   
	   
	   gc.setFill(Color.color(0.3, 0.4, 1));
       gc.fillRect(0, 0, 600, 10);
       gc.fillRect(0, 590, 600, 10);
       gc.fillRect(0, 0, 10, 600);
       gc.fillRect(590, 0, 10, 600);
       gc.setFill(Color.CHOCOLATE);
       gc.fillRect(10, 10, 580, 580);
	   
       update();
	   
	   double width = getWidth();  
	   double height = getHeight();  
	   //gc.clearRect(0, 0, width, height); // ������� ������� 
	   
	   for(int i = 0; i < lines.length; i++){   
		   //lines[i].update();
		   //gc.setFill(lines[i].getColor());
		   //gc.fillPolygon(lines[i].getDotX(), lines[i].getDotY(), lines[i].getCount()); 
	   } 
   }  
   public void update(){
	   gc.clearRect(0, 0, 600, 600);
	   
	   double kY = Math.sin(30*Math.PI/180);
	   double kX = Math.cos(30*Math.PI/180);
	   double kZ = 1;
	   
	   for(int z=0;z<5;z++){
		   for(int j=0;j<5;j++){
			   for (int i =0;i<5;i++){
				   int lenght = 20;
				   double stX = -i*(lenght + doX)*kX+200 + j*(lenght+doY)*kX;
				   double stY = 300+i*(lenght + doX)*kY + j*(lenght + doY)*kY - z*(lenght +doZ)*kZ;
				   
				   gc.setFill(Color.ALICEBLUE);
				   double[] xPoints = {stX, stX + lenght*kX, stX + lenght*kX, stX};
				   double[] yPoints = {stY, stY - lenght*kY, stY - lenght*kY + lenght, stY+lenght};
				   gc.fillPolygon(xPoints, yPoints, 4);
				   
				   gc.setFill(Color.LIGHTCORAL);
				   double[] xPoints2 = {stX, stX - lenght*kX, stX - lenght*kX, stX};
				   double[] yPoints2 = {stY, stY - lenght*kY, stY - lenght*kY + lenght, stY+lenght};
				   gc.fillPolygon(xPoints2, yPoints2, 4);
				   
				   
				   gc.setFill(Color.LEMONCHIFFON);
				   double[] xPoints3 = {stX, stX - lenght*kX, stX, stX + lenght*kX};
				   double[] yPoints3 = {stY, stY - lenght*kY, stY - 2*lenght*kY, stY - lenght*kY};
				   gc.fillPolygon(xPoints3, yPoints3, 4);
			   }
		   }
	   }
	   
	   if (doX<20 && doY==0 && doZ==0){
		   doX++;
	   }else{
		   if(doY<20 && doZ ==0){
			   doY ++;
		   }else{
			   if(doZ<20 && doY==20){
				   doZ++;
			   }else{
				   if (doY >0){
					   doY--;
				   }else{
					   if(doZ>0){
						   doZ--;
					   }else{
						   if(doX>0){
							   doX--;
						   }
					   }
				   }
			   }
		   }
	   }
	   
   }
   public class BGLine{
	   	private int acc = 200;
	   	private int filling = 10;
	   	private int _partX;
		private double dotX[] = new double[acc];
		private double dotY[] = new double[acc];
		
		private Color bgcolor;
		
		private int height;
		private int width;
		
		private double _dx = 0; 
		public BGLine(int height, int width,int i){
			this.height = height;
			this.width = width;
			this._partX = i;
			bgcolor = Color.color(r.nextDouble(), r.nextDouble(), r.nextDouble());
		}
		public void update(){
			double _koef =  width/(acc/2);
			for(int x=0; x<acc/2; x++){
				   double rad = (x +_dx)*18 * Math.PI / 180;
				   double dY = _koef*x;
				   double dX = Math.sin(rad) *filling + _partX;
				   
				   dotX[x] = dX ;
				   dotX[acc-1 - x] = dX + 250;
				   dotY[x] = dY;
				   dotY[acc-1 - x] = dY;
			   }
			_dx += r.nextDouble();
		}
		public int getCount(){
			return acc;
		}
		public double[] getDotX(){
			return dotX;
		}
		public double[] getDotY(){
			return dotY;
		}
		public Color getColor(){
			return bgcolor;
		}
   }
}  

