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

public class MyLabel extends Label {  

	
private double angle = 0;	

private double dX = 0;
private double dY = 0;
private double dAngle = 0;	
private double dSpeed = 0;


private double maxAngle = 3;



private double maxSpeed = 10;
private double lostSpeed = 0.01;
private double brakSpeed = 0.15;
private double lostTurnKoef = 0.01;
private double addSpeed = 0.04;
private double addPostSpeed = 0.015;

private boolean _up = false;
private boolean _left = false;
private boolean _right = false;
private boolean _down = false;




private static Random r = new Random(); // ����������� ������  
private boolean isSelected = false;    // ������ ������ �� ��� ������  
private Point2D diff;                 // ���������� ������� ���� ������������ ������ �������,  
private MyCanvas canvas;
private double X;
private double Y;// ��� �������� ������������  
private boolean isWay = true;
public MyLabel(String text, MyCanvas canvas){      //�����������  
   super(text);  
   this.canvas = canvas; 
   setPrefWidth(100);  
   setPrefHeight(50); 
   setId("my_label");
    setAlignment(Pos.CENTER);  
    X = r.nextDouble() * 500;
    Y = r.nextDouble() * 300 + 200;
    setLayoutY(X);  
    setLayoutX(Y);  
    setRotate(angle);  
}
public MyLabel(String text, MyCanvas canvas, int x, int y){
	   super(text);  
	   this.canvas = canvas; 
	   setPrefWidth(30);  
	   setPrefHeight(70); 
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
	if (_left && !_right){
		dAngle = -maxAngle * Math.min(1, dSpeed);
		dSpeed -= dSpeed * lostTurnKoef - lostSpeed;
	}
	if (_right && !_left){
		dAngle = maxAngle * Math.min(1, dSpeed);
		dSpeed -= dSpeed * lostTurnKoef - lostSpeed;
	}
	if (_up && !_down){
		
		if (dSpeed < maxSpeed)
			dSpeed += addSpeed;
		else
			dSpeed += addPostSpeed;
	}
	if (_down && !_up){
		if (dSpeed > 0)
			dSpeed = dSpeed > brakSpeed ? dSpeed - brakSpeed: 0;
		else
			dSpeed -= addSpeed;
				
	}
	
	
	dX = Math.sin(-angle * Math.PI / 180)*dSpeed;
	dY = Math.cos(angle * Math.PI / 180)*dSpeed;
	
	
	
	
	
	System.out.println();
	
	
	if ( X + dX < 50){
		dSpeed = dSpeed - Math.abs(dX)/dSpeed;
		dX = -dX;
		dAngle = -dY;
	}
	if (X + dX > 550){
		dSpeed = dSpeed - Math.abs(dX)/dSpeed;
		dX = -dX;
		dAngle = dY;
	}
	if (Y + dY < 50 ){
		dSpeed = dSpeed - Math.abs(dY)/dSpeed;
		dY = -dY;
		dAngle = dX;
	}
	if (Y + dY > 550 ){
		dSpeed = dSpeed - Math.abs(dY)/dSpeed;
		dY = -dY;
		dAngle = -dX;
	}
	
	
	X += dX;
	Y += dY;
	angle += dAngle;
	
	
	setLayoutX(X);
	setLayoutY(Y);
	setRotate(angle);
	dAngle = 0;
	dX = 0;
	dY = 0;
	if (dSpeed > 0)
		dSpeed = dSpeed > lostSpeed ? dSpeed - lostSpeed: 0;
		else
			dSpeed = Math.abs(dSpeed) > lostSpeed ? dSpeed + lostSpeed: 0;
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
