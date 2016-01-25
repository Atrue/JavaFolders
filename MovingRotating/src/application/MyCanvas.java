package application;

import java.util.ArrayList;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class MyCanvas extends Canvas {  
	  
    private ArrayList<MyLabel> f = new ArrayList<>(); // листы, в которых хранятся пары  
    private ArrayList<MyLabel> s = new ArrayList<>(); // связанных между собой елементов  
    private GraphicsContext gc; // инструмент для рисования  
  
    public MyCanvas(Scene scene){  
        super(scene.getWidth(), scene.getHeight());  
        widthProperty().bind(scene.widthProperty()); //Делаем привязку высоты полотна к высоте сцены  
        //Делаем привязку ширины полотна к ширине сцены  
        gc = getGraphicsContext2D();  
    }  
     
   public void addPair(MyLabel n1, MyLabel n2){  
        f.add(n1);  
        s.add(n2);  
        repaintContext();
    }  
     
   public void repaintContext(){  
	   double width = getWidth();  
	   double height = getHeight();  
	   gc.clearRect(0, 0, width, height); // Очищаем полотно 
	   for(int i = 0; i < f.size(); i++){   
	      MyLabel n1 = f.get(i);  
	      MyLabel n2 = s.get(i);  
	      //Рисуем линию  
	      strokeLine(n1.getCenterX(), n1.getCenterY(), n2.getCenterX(), n2.getCenterY());  
	   } 
   }
   private void strokeLine(double x1, double y1, double x2, double y2){  
	   double xR = Math.max(2 *Math.PI * Math.abs(x2 - x1) / 200, Math.PI * 2);  
	   double n = 200; // количество мелких итервалов  
	     
	     
	   if (x1 > x2){   // меняем местами, чтобы не использовать многократно abs(double, double)  
	      double b = x1;   x1 = x2; x2 = b;  
	            b = y1;  y1 = y2; y2 = b;  
	   }  
	   double l = (x2 - x1); //длинна проэкции на ось Х  
	   double dWidth = (x2 - x1) / n; // приращение х  
	     
	   double bx = x1; // буферные переменные  
	   double by = y1; // хранятся предыдущие значения   
	     
	   for(double x = x1; x < x2; x+=dWidth){  
	      double xSin = (x - x1) / l * xR; // находим кодированное значение х є [0..xR]  
	      double ySin = Math.sin(xSin)*50  + (x - x1)/  l * Math.abs(y1 - y2) + Math.min(y1, y2); // wtf?  
	      gc.strokeLine(bx, by, x, ySin); // рисуем линию на маленьком участке  
	        
	      bx = x;   // сохранаем текущие значения, чтобы не считать 2 раза.  
	      by = ySin;  
	   }  
	}  
}  