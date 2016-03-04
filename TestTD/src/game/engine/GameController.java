package game.engine;

/*
    handles the button inputs for the game
    and links to fxml ui
 */


import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ResourceBundle;

import game.Main;
import game.engine.characters.ListOfCharacters;
import game.engine.characters.Tower;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

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
    private Button buyTower1;
    @FXML
    private Button buyTower2;
    @FXML
    private Button buyTower3;
    @FXML
    private Button buyTower4;
    @FXML
    private Button buyTower5;
    @FXML
    private Button buyTower6;
    @FXML
    private Button buyTower7;
    @FXML
    private Label targetUpgradePrice;
    @FXML
    private Label targetAttack;
    @FXML
    private Label resourceLabel;
    @FXML
    private Label livesLabel;
    @FXML
    private Label openMenuButton;
    @FXML
    private Label targetRange;
    @FXML
    private GridPane targetTowerInfo;
    @FXML
    private Text targetBuffId;
    @FXML
    private Text targetBuffDesc;
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
    
    private GameManager gameManager; 
    
    private final int TILE_GRASS = 0;
    private final int TILE_HORISONTAL = 1;
    private final int TILE_VERTICAL = 2;
    private final int TILE_BOT_RIGHT = 3;
    private final int TILE_BOT_LEFT = 4;
    private final int TILE_RIGHT_TOP = 5;
    private final int TILE_LEFT_TOP = 6;
	private int RESOLUTION_WIDTH = 640+32*2;
	private int RESOLUTION_HEIGHT = 480+32*2;
	
	private ImageView backgroundMap;
    private boolean hoverState = false;
    private Tower hoverTower;
    

    
    @FXML
    void initialize() {
        assert currentResources != null : "fx:id=\"currentResources\" was not injected: check your FXML file 'gameui.fxml'.";
        assert targetSpeed != null : "fx:id=\"targetSpeed\" was not injected: check your FXML file 'gameui.fxml'.";
        assert targetName != null : "fx:id=\"targetName\" was not injected: check your FXML file 'gameui.fxml'.";
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
    public void setGameManager(GameManager gameManager){
        this.gameManager = gameManager;  
        
        backgroundMap = new ImageView();
        repaintBG(GameState.getMap());
        hoverTower = generateHoverLabel();
        
        
        Group g = new Group();
        g.getChildren().add(backgroundMap);
        g.getChildren().add(hoverTower);
        ongrouppane.getChildren().add(g);
    }
    private Tower generateHoverLabel(){
    	Tower hoverTower = new Tower();
        hoverTower.setId("tower_hover");
        hoverTower.setVisible(false);
        hoverTower.setPrefWidth(32);  
        hoverTower.setPrefHeight(32); 
        return hoverTower;
    }
    private void replaceHoverLabel(int type){
    	Tower from = ListOfCharacters.getTower(type, 0);
    	hoverTower.setColor(from.getColor());
    	hoverTower.setType(from.getType());
    	hoverTower.setLevel(from.getLevel());
    	hoverTower.updateLabels();
    }
    //Method paints the map using the given map array and tileset
    public void repaintBG(int[][] map){
    	if(map == null){
    		return;
    	}
        //loads tileset
        Image tileset = new Image("game/engine/res/menu/icon/mapping.png");

        //Pixel reader
        PixelReader tilereader = tileset.getPixelReader();

        //buffer for aRGB 64x64 tiles
        byte[] buffer = new byte[32 * 32 * 4];
        WritablePixelFormat<ByteBuffer> picFormat = WritablePixelFormat.getByteBgraInstance();

        //Pixel writer
        WritableImage paintedMap = new WritableImage(RESOLUTION_WIDTH , RESOLUTION_HEIGHT);
        PixelWriter tileWriter = paintedMap.getPixelWriter();
        
        //Create form
        int TILE_LENGTH_X = map.length;
        int TILE_LENGTH_Y = map[0].length;
        //reads map node than paints the tile
        for(int x = 0; x < TILE_LENGTH_X; x++){
            for(int y = 0; y < TILE_LENGTH_Y; y++ ){
                
            	//populate each rectangle with tile from PixelReader
                switch(map[x][y]){
                    case TILE_GRASS: //paint grass(OPEN NODE)
                        tilereader.getPixels(0 , 0 , 32 , 32 , picFormat , buffer , 0 , 128);
                        break;
                    case 1:
                    	tilereader.getPixels(96 , 0 , 32 , 32 , picFormat , buffer , 0 , 128);
                    	break;
                    case 3: //paint grass and tower
                        tilereader.getPixels(64 , 0 , 32 , 32 , picFormat , buffer , 0 , 128);
                        break;
                    case 4:
                    	tilereader.getPixels(32 , 32 , 32 , 32 , picFormat , buffer , 0 , 128);
                    	break;
                    case 5:
                    	tilereader.getPixels(64 , 32 , 32 , 32 , picFormat , buffer , 0 , 128);
                    	break;
                }
                
                tileWriter.setPixels(x * 32 , y * 32, 32 , 32 , picFormat , buffer , 0 , 128);
                
            }
        };
        backgroundMap.setImage(paintedMap);
    }
    public void setListeners(){
        gameManager.getGameScene().setOnMouseClicked(new buyTower());
        gameManager.getGameScene().setOnMouseMoved(new MouseMove());
		buyTower1.setOnMouseClicked(MousesEvent -> buyTower(0));
		buyTower2.setOnMouseClicked(MousesEvent -> buyTower(1));
		buyTower3.setOnMouseClicked(MousesEvent -> buyTower(2));
		buyTower4.setOnMouseClicked(MousesEvent -> buyTower(3));
		buyTower5.setOnMouseClicked(MousesEvent -> buyTower(4));
		buyTower6.setOnMouseClicked(MousesEvent -> buyTower(5));
		buyTower7.setOnMouseClicked(MousesEvent -> buyTower(6));
		
		EventHandler<? super MouseEvent> upgrade = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub
				gameManager.upgradeTower(GameState.getTarget());
				gameManager.updateLabels();
			}
		};
		targetUpgradeButton.setOnMouseClicked(upgrade);
		EventHandler<? super MouseEvent> sell = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub
				gameManager.sellTower(GameState.getTarget());
				
			}
		};
		targetSellButton.setOnMouseClicked(sell);
    }
    public void setTooltips(){
    	Tooltip  tip = new Tooltip("100\nPaint simple tower");
    	buyTower1.setTooltip(tip);
    	
    }
    public Pane getGeneralLayout(){
    	return this.ongrouppane;
    }
    public void setPauseView(boolean paused){
    	if (!paused){
    		openMenuButton.getStyleClass().remove("label_icon_play");
    		openMenuButton.getStyleClass().add("label_icon_pause");
    	}else{
    		openMenuButton.getStyleClass().remove("label_icon_pause");
    		openMenuButton.getStyleClass().add("label_icon_play");
    	}
    		
    }
    
    //set mouse clicks to buy and place tower
    public void buyTower(int type){
    	if (!hoverState){
    		hoverTower.setLayoutX(-32);
    		hoverTower.setLayoutY(-32);
            replaceHoverLabel(type);
            hoverState = true;
            hoverTower.setVisible(hoverState);
    	}else{
    		hoverState = false;
            hoverTower.setVisible(hoverState);
    	}
        
    }
    public void pauseGame(){
        gameManager.pause();
        //open Game Menu
    }
    public void openMenu(){
    	gameManager.openMenu();
    }
    public void updateLabels(String currentLevel , String currentLives , String currentResources ,  String timeLabel){
        this.currentLevel.setText(currentLevel);
        this.currentLives.setText(currentLives);
        this.currentResources.setText(currentResources);
        this.timeLabel.setText(timeLabel);
    }
    public void updateTarget(int level, int targetAttack, int targetRange, double targetSpeed, int upcost, int sellcost, String bId, String bDesc){
        this.targetName.setText("Y ["+String.valueOf(level)+"]");
        this.targetBuffId.setText(bId);
        this.targetBuffDesc.setText(bDesc);
    	this.targetAttack.setText(String.valueOf(targetAttack));
        this.targetRange.setText(String.valueOf(targetRange));
        this.targetSpeed.setText(String.valueOf(targetSpeed));
        this.targerSellPrice.setText(String.valueOf(sellcost));
        if (upcost > 0){
        	this.targetUpgradeButton.setVisible(true);
        	this.targetUpgradePrice.setText(String.valueOf(upcost));
        	this.targetUpgradeButton.setDisable(GameState.getResources() < upcost);
        }else{
        	this.targetUpgradeButton.setVisible(false);
        }
    }

    
    //buy tower at mouse click tile
    class buyTower implements EventHandler<MouseEvent> {
        public void handle(MouseEvent me) {
        	if (hoverState){
        		if (me.getButton() == MouseButton.PRIMARY){
        			gameManager.buyTower(hoverTower.getType(), me.getX(),me.getY());
        		}else{
        			hoverTower.setVisible(false);
            		hoverState = false;
        		}
        		//hoverTower.setVisible(false);
        		//hoverState = false;
            }else{
            	Tower tower = gameManager.getTower(me.getX(), me.getY());
            	if (tower != null){
            		GameState.setTarget(tower);
            		targetTowerInfo.setVisible(true);
            		gameManager.updateLabels();
            	}else{
            		GameState.setTarget(null);
            		targetTowerInfo.setVisible(false);
            	}
            }
        }
    }
    //buy tower at mouse click tile
    class MouseMove implements EventHandler<MouseEvent> {
        public void handle(MouseEvent me) {
        	if (hoverState){
        		int xTile = (int)(me.getX() / 32);
        		int yTile = (int)(me.getY() / 32);
        		if(GameState.nodeOpen(xTile,yTile)){
        			hoverTower.setId("tower_hover");
        		}else{
        			hoverTower.setId("tower_hover_lock");
        		}
        		hoverTower.setLayoutX(xTile*32);
        		hoverTower.setLayoutY(yTile*32);
            }else{
            	return;
            }
        }
    }
	public void updateBuyers() {
		// TODO Auto-generated method stub
		int m = GameState.getResources();
		buyTower1.setDisable(ListOfCharacters.getTower(0, 0).getPrice() > m);
		buyTower2.setDisable(ListOfCharacters.getTower(1, 0).getPrice() > m);
		buyTower3.setDisable(ListOfCharacters.getTower(2, 0).getPrice() > m);
		buyTower4.setDisable(ListOfCharacters.getTower(3, 0).getPrice() > m);
		buyTower5.setDisable(ListOfCharacters.getTower(4, 0).getPrice() > m);
		buyTower6.setDisable(ListOfCharacters.getTower(5, 0).getPrice() > m);
		buyTower7.setDisable(ListOfCharacters.getTower(6, 0).getPrice() > m);
		
	}
}
