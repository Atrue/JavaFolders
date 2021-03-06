package application;
	
import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
	private int windowWidth = 600;  
	private int windowHeight = 600;
	private MyCanvas canvas; 
	private MyLabel a;
	private Group root;    
	private static Random r = new Random();
	private Scene createScene(){
		
		root = new Group();  
		Scene myScene = new Scene(root, windowWidth, windowHeight);  
		canvas = new MyCanvas(myScene);
		root.getChildren().add(canvas); 
		a = new MyLabel("Some text", canvas, 250, 100);  
	    root.getChildren().add(a); 
		myScene.setOnKeyPressed(new EventHandler<KeyEvent>() {

			 @Override  
	            public void handle(KeyEvent keyEvent) {  
	                a.keyPress(keyEvent);
	            }  
	              
		});
		myScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override  
            public void handle(KeyEvent keyEvent) {  
                a.keyUp(keyEvent);
            }
		});
		return myScene; 
	    
	} 
	public void update(){
		a.update();
		canvas.update();
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
			
			
			canvas.repaintContext();
			Timeline timeline = new Timeline(new KeyFrame(
			        Duration.millis(33),
			        ae -> update()));
			timeline.setCycleCount(Animation.INDEFINITE);
			timeline.play();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
