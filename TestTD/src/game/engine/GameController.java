package game.engine;

/*
    handles the button inputs for the game
    and links to fxml ui
 */

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;


public class GameController {

    @FXML
    private Label currentScore;
    @FXML
    private Label currentResources;
    @FXML
    private Label currentLevel;
    @FXML
    private Label currentLives;
    @FXML
    private Label timeLabel;

    private GameManager gameManager;

    private boolean hoverState = false;
    
    public void setGameManager(GameManager gameManager){
        this.gameManager = gameManager;
        gameManager.getGameScene().setOnMouseClicked(new buyTower());
        gameManager.getGameScene().setOnMouseMoved(new MouseMove());
    }

    //set mouse clicks to buy and place tower
    public void buyTower(){
        hoverState = !hoverState;
    }
    public void openMenu(){
        gameManager.pauseGame();
        //open Game Menu
    }
    public void updateLabels(String currentLevel , String currentLives , String currentResources , String currentScore , String timeLabel){
        this.currentLevel.setText(currentLevel);
        this.currentLives.setText(currentLives);
        this.currentResources.setText(currentResources);
        this.currentScore.setText(currentScore);
        this.timeLabel.setText(timeLabel);
    }

    
    //buy tower at mouse click tile
    class buyTower implements EventHandler<MouseEvent> {
        public void handle(MouseEvent me) {
        	if (hoverState){
        		
        		gameManager.buyTower(me.getX(),me.getY());
            }else{
            	return;
            }
        }
    }
    //buy tower at mouse click tile
    class MouseMove implements EventHandler<MouseEvent> {
        public void handle(MouseEvent me) {
        	if (hoverState){
        		int xTile = (int)(me.getX() / 64);
        		int yTile = (int)(me.getY() / 64);
        		if(gameManager.getMap().nodeOpen(xTile,yTile)){
        			gameManager.getView().setId("my_label");
        		}else{
        			gameManager.getView().setId("my_label_selected");
        		}
        		gameManager.getView().setLayoutX(xTile*64);
        		gameManager.getView().setLayoutY(yTile*64);
            }else{
            	return;
            }
        }
    }
}
