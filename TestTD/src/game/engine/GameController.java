package game.engine;

/*
    handles the button inputs for the game
    and links to fxml ui
 */


import java.net.URL;
import java.util.ResourceBundle;

import game.engine.characters.Tower;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

public class GameController {

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Label currentResources;
    @FXML
    private Label targetSpeed;
    @FXML
    private Label targetName;
    @FXML
    private Button buyTowerButton;
    @FXML
    private Label currentScore;
    @FXML
    private Label targetUpgradePrice;
    @FXML
    private Label targetAttack;
    @FXML
    private Label resourceLabel;
    @FXML
    private Label livesLabel;
    @FXML
    private Button openMenuButton;
    @FXML
    private Label targetRange;
    @FXML
    private GridPane targetTowerInfo;
    @FXML
    private Label targerSellPrice;
    @FXML
    private Button targetUpgradeButton;
    @FXML
    private Label currentLevel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Button targetSellButton;
    @FXML
    private Label levelLabel;
    @FXML
    private Label currentLives;
    @FXML
    private Label timeLabel;

   
    @FXML
    private Pane ongrouppane;
    
    private GameManager gameManager; //for pause game. Can be deleted
    private GameState gameState;
    
    
    private boolean hoverState = false;
    private Label hoverTower;
    
    @FXML
    void initialize() {
        assert currentResources != null : "fx:id=\"currentResources\" was not injected: check your FXML file 'gameui.fxml'.";
        assert targetSpeed != null : "fx:id=\"targetSpeed\" was not injected: check your FXML file 'gameui.fxml'.";
        assert targetName != null : "fx:id=\"targetName\" was not injected: check your FXML file 'gameui.fxml'.";
        assert buyTowerButton != null : "fx:id=\"buyTowerButton\" was not injected: check your FXML file 'gameui.fxml'.";
        assert currentScore != null : "fx:id=\"currentScore\" was not injected: check your FXML file 'gameui.fxml'.";
        assert targetUpgradePrice != null : "fx:id=\"targetUpgradePrice\" was not injected: check your FXML file 'gameui.fxml'.";
        assert targetAttack != null : "fx:id=\"targetAttack\" was not injected: check your FXML file 'gameui.fxml'.";
        assert resourceLabel != null : "fx:id=\"resourceLabel\" was not injected: check your FXML file 'gameui.fxml'.";
        assert livesLabel != null : "fx:id=\"livesLabel\" was not injected: check your FXML file 'gameui.fxml'.";
        assert openMenuButton != null : "fx:id=\"openMenuButton\" was not injected: check your FXML file 'gameui.fxml'.";
        assert targetRange != null : "fx:id=\"targetRange\" was not injected: check your FXML file 'gameui.fxml'.";
        assert targetTowerInfo != null : "fx:id=\"targetTowerInfo\" was not injected: check your FXML file 'gameui.fxml'.";
        assert targerSellPrice != null : "fx:id=\"targerSellPrice\" was not injected: check your FXML file 'gameui.fxml'.";
        assert targetUpgradeButton != null : "fx:id=\"targetUpgradeButton\" was not injected: check your FXML file 'gameui.fxml'.";
        assert currentLevel != null : "fx:id=\"currentLevel\" was not injected: check your FXML file 'gameui.fxml'.";
        assert scoreLabel != null : "fx:id=\"scoreLabel\" was not injected: check your FXML file 'gameui.fxml'.";
        assert targetSellButton != null : "fx:id=\"targetSellButton\" was not injected: check your FXML file 'gameui.fxml'.";
        assert levelLabel != null : "fx:id=\"levelLabel\" was not injected: check your FXML file 'gameui.fxml'.";
        assert currentLives != null : "fx:id=\"currentLives\" was not injected: check your FXML file 'gameui.fxml'.";
        assert timeLabel != null : "fx:id=\"timeLabel\" was not injected: check your FXML file 'gameui.fxml'.";
        
    }
    public void setGameManager(GameManager gameManager, GameState gameState, ImageView imageview){
        this.gameManager = gameManager;
        this.gameState = gameState;       
        
        hoverTower = new Label();
        hoverTower.setPrefWidth(64);  
        hoverTower.setPrefHeight(64); 
        hoverTower.setLayoutX(40);
        hoverTower.setLayoutY(40);
        hoverTower.setId("my_label");
        hoverTower.setText("OLOL");
        hoverTower.setVisible(false);

        
        Group g = new Group();
        g.getChildren().add(imageview);
        g.getChildren().add(hoverTower);
        ongrouppane.getChildren().add(g);
        		
        		
        //ongrouppane.getChildren().add(imageview);
        //generalLayer.getChildren().add(hoverTower);
        
        /*
        //generalLayer.setTranslateX(-160);
        System.out.println(generalLayer.getLayoutX()+"|"+generalLayer.getTranslateX()+"|"+generalLayer.getScaleX());
        
        generalLayer = new Group();
        generalLayer.getChildren().add(imageview);
        System.out.println(imageview.getFitWidth()+"|"+imageview.getLayoutX()+"|"+imageview.getX()+"|"+imageview.getTranslateX()+"|"+imageview.getScaleX());
        generalLayer.getChildren().add(hoverTower);
        */
    }
    public void setListeners(){
        gameManager.getGameScene().setOnMouseClicked(new buyTower());
        gameManager.getGameScene().setOnMouseMoved(new MouseMove());
    }
    public Pane getGeneralLayout(){
    	return this.ongrouppane;
    }
    
    
    //set mouse clicks to buy and place tower
    public void buyTower(){
        hoverState = !hoverState;
        hoverTower.setVisible(hoverState);
    }
    public void openMenu(){
        gameManager.openMenu();
        //open Game Menu
    }
    public void updateLabels(String currentLevel , String currentLives , String currentResources , String currentScore , String timeLabel){
        this.currentLevel.setText(currentLevel);
        this.currentLives.setText(currentLives);
        this.currentResources.setText(currentResources);
        this.currentScore.setText(currentScore);
        this.timeLabel.setText(timeLabel);
    }
    public void updateTarget(String targetAttack, String targetRange, String targetSpeed){
        this.targetAttack.setText(targetAttack);
        this.targetRange.setText(targetRange);
        this.targetSpeed.setText(targetSpeed);
    }

    
    //buy tower at mouse click tile
    class buyTower implements EventHandler<MouseEvent> {
        public void handle(MouseEvent me) {
        	if (hoverState){
        		
        		gameManager.buyTower(me.getX(),me.getY());
        		hoverTower.setVisible(false);
        		hoverState = false;
            }else{
            	Tower tower = gameManager.getTower(me.getX(), me.getY());
            	if (tower != null){
            		gameState.setTarget(tower);
            		targetTowerInfo.setVisible(true);
            	}else{
            		gameState.setTarget(null);
            		targetTowerInfo.setVisible(false);
            		
            	}
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
        			hoverTower.setId("my_label");
        		}else{
        			hoverTower.setId("my_label_selected");
        		}
        		hoverTower.setLayoutX(xTile*64);
        		hoverTower.setLayoutY(yTile*64);
            }else{
            	return;
            }
        }
    }
}
