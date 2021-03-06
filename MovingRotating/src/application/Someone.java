package application;

import java.util.Random;


import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class Someone extends Label {  

	
private double angle = 0;
private double falling = 0;
private double G = 9.8;

private boolean _up = false;
private boolean _left = false;
private boolean _right = false;
private boolean _down = false;

private double x1;
private double x2;
private double y1;
private double y2;


private static Random r = new Random(); // ����������� ������  
private boolean isSelected = false;    // ������ ������ �� ��� ������  
private Point2D diff;                 // ���������� ������� ���� ������������ ������ �������,  
private MyCanvas canvas;
private double X;
private double Y;// ��� �������� ������������  
private boolean isWay = true;
public Someone(String text, MyCanvas canvas){      //�����������  
   super(text);  
   this.canvas = canvas; 
   setPrefWidth(100);  
   setPrefHeight(50); 
   setId("my_label");
    setAlignment(Pos.CENTER);  
    X = 100;
    Y = r.nextDouble() * 300;
    setLayoutY(X);  
    setLayoutX(Y);  
    setRotate(angle);  
}
public Someone(String text, MyCanvas canvas, int x, int y){
	   super(text);  
	   this.canvas = canvas; 
	   setPrefWidth(70);  
	   setPrefHeight(30); 
	   setId("my_label");
	    setAlignment(Pos.CENTER);  
	    X = x;
	    Y = y;
	    setLayoutX(X);  
	    setLayoutY(Y);  
}
  
public double getCenterX(){  
    return getLayoutX() + getPrefWidth()/2;  
}  
public void move(){
	X = isWay ? X + 5 : X - 5;
	if (X > 450){
		isWay = false;
	}
	if (X < 50){
		isWay = true;
	}
	setLayoutX(X);		
	
	
}

public double getCenterY(){  
    return getLayoutY() + getPrefHeight()/2;  
}  
public void select(boolean fl){  
    isSelected = fl;  
    if (fl){  
        setId("my_label_selected");  
        toFront();  
    } else{  
        setId("my_label");  
    } 
}
public void update(){
	
	x1 = getLayoutX();
	x2 = x1 + getPrefHeight();
	y1 = getLayoutY();
	y2 = y1 + getPrefWidth();
	
	if (_left && !_right){
		angle -= 1;
	}
	if (_right && !_left){
		angle += 1;
	}
	if (_up && !_down){
		
		X++;
	}
	if (_down && !_up){
		X--;
	}
	
	
	//
	if (Y < 450){
		falling += G/30;
	}else{
		if(x1 > 250 || x2> 250){
			System.out.println("ok");
			if (x1 > 250){
				falling += G/30;
			}else{
				double a = (250-x1)/getPrefWidth();
				if (a < 0.5){
					falling += G/30 * (1-a);
				}
			}
			
		}
		
		
		falling = 0;
		
		
		
		
		double ss = Math.abs(angle % 90);
		if (ss < 45){
			angle = Math.signum(angle) * (Math.abs(angle) - 2 > 0? Math.abs(angle) - 2: 0);
			
		}else{
			angle = Math.signum(angle) * (Math.abs(angle) - 2 > 0? Math.abs(angle) - 2: 0);
		}
		
	}
	
	//
	
	Y += falling;
	
	
	
	
	
	
	
	setLayoutX(X);
	setLayoutY(Y);
	setRotate(angle);
}
public void keyPress(KeyEvent keyEvent) {
	if (keyEvent.getCode() == KeyCode.W){
		_up = true;
	}
	if (keyEvent.getCode() == KeyCode.A){
		_left = true;
	}
	if (keyEvent.getCode() == KeyCode.D){
		_right = true;
	}
	if (keyEvent.getCode() == KeyCode.S){
		_down = true;
	}
}
public void keyUp(KeyEvent keyEvent){
	if (keyEvent.getCode() == KeyCode.W){
		_up = false;
	}
	if (keyEvent.getCode() == KeyCode.A){
		_left = false;
	}
	if (keyEvent.getCode() == KeyCode.D){
		_right = false;
	}
	if (keyEvent.getCode() == KeyCode.S){
		_down = false;
	}
}
}  
