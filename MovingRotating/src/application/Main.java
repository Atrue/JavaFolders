package application;
	
import java.util.Random;

import com.sun.javafx.geom.Vec2f;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.event.Event;
import javafx.event.EventHandler;

public class Main extends Application {
	private int windowWidth = 600;  
	private int windowHeight = 600;
	private MyCanvas canvas; 
	    
	private Scene createScene(){
		
		Group root = new Group();  
		Scene myScene = new Scene(root, windowWidth, windowHeight);  
		canvas = new MyCanvas(myScene);
		root.getChildren().add(canvas); 
		MyLabel a = new MyLabel("Some text", canvas, 250, 400);  
	    root.getChildren().add(a); 
		
		for(int i =0; i < 5; i++) {  
			MyLabel b = new MyLabel("Some text", canvas, 100 + 70*i, 50 + 13*i);  
			root.getChildren().add(b);  
		}
		myScene.setOnKeyPressed(new EventHandler<KeyEvent>() {

			 @Override  
	            public void handle(KeyEvent keyEvent) {  
	                if (keyEvent.getCode() == KeyCode.ENTER)// Если зажата кнопка Ентер   
	                   //pool.select(null);               //  Снять выделение  
	                   canvas.repaintContext();         // Перерисовать  
	            }  
	              
		});
		return myScene; 
	    
	} 
	
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			//Scene scene = new Scene(root,400,400);
			//
			Scene scene = createScene();
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm()); 
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
