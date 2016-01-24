package application;

import java.util.Random;


import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class MyLabel extends Label implements EventHandler<MouseEvent> {  
	  
private static Random r = new Random(); // ����������� ������  
private boolean isSelected = false;    // ������ ������ �� ��� ������  
private Point2D diff;                 // ���������� ������� ���� ������������ ������ �������,  
private MyCanvas canvas;
                            // ��� �������� ������������  
  
public MyLabel(String text, MyCanvas canvas){      //�����������  
   super(text);  
   this.canvas = canvas; 
   setPrefWidth(100);  
   setPrefHeight(50); 
   setId("my_label");
   setEventHandler(MouseEvent.ANY, this);
    setAlignment(Pos.CENTER);  
    setLayoutY(r.nextDouble() * 500);  
    setLayoutX(r.nextDouble() * 500);  
    setRotate(r.nextDouble() * 180);  
}

@Override
public void handle(MouseEvent mouseEvent) {
	// TODO Auto-generated method stub
	if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED){  
        //if (!isSelected) select(true);
		select(!isSelected);
    } else  
    if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED){  
    	select(!isSelected);
        diff = new Point2D(-mouseEvent.getSceneX() + getLayoutX(), -mouseEvent.getSceneY() + getLayoutY());  
        
        	
    } else  
    if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED){  
        diff = null;  
    } else  
    if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED && diff != null){  
        setLayoutX(mouseEvent.getSceneX() + diff.getX());  
        setLayoutY(mouseEvent.getSceneY() + diff.getY());  
        canvas.repaintContext();  
    }  
}  
public double getCenterX(){  
    return getLayoutX() + getPrefWidth()/2;  
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
  
}  
