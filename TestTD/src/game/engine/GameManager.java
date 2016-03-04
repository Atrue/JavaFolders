package game.engine;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.SwingUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import game.network.Client;
import game.network.NetworkLink;
import game.Main;
import game.MenuNavigator;
import game.engine.characters.Levels;
import game.engine.characters.ListOfCharacters;
import game.engine.characters.Monster;
import game.engine.characters.Projectile;
import game.engine.characters.Tower;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 * Responsible for all communications between user interface and underlying
 * frameworks. The initialize method starts the game loop when called through
 * creating or loading a game.
 */
public class GameManager {
	private  GameController gameController;			// Handles fxml attributes (buttons and labels)
	private Scheduler timer;
	private boolean NET;
	
	
	
    private  Scene gameScene;                       // The main viewport
	private int[][] map;
	private Group group;
	private Client client;
             
	//private Group tilemapGroup;
	
	//private Label hoverTower;
    /**
     * Initializes the game
     *
     * @throws java.io.IOException
     */
    public void initialize(int type) throws java.io.IOException{
    	NET = type == 0? false: true;
        // Initializes the game state
        GameState.init(this, type);
        
        // Creates gui hierarchy
        FXMLLoader loader = new FXMLLoader(MenuNavigator.GAMEUI);
        // Opens stream to get controller reference
        Node gameUI = (Node)loader.load(MenuNavigator.GAMEUI.openStream()); 
        gameController = loader.<GameController>getController();
        gameController.setGameManager(this);
        // Generates the map with the given resolution
        StackPane gamePane = new StackPane();
        gamePane.getChildren().add(gameController.getGeneralLayout());
    	GameState.setParentView(gameController.getGeneralLayout());
        
       
        gamePane.getChildren().add(gameUI);
        gameScene = new Scene(gamePane);
        gameScene.getStylesheets().add(GameManager.class.getResource("res/menu/gamestyle.css").toExternalForm());
        
        gameController.setListeners();
        gameController.setTooltips();
        ListOfCharacters.init();
        Monster.init(this);
        
        MenuNavigator.addScene(gameScene, 1);
        MenuNavigator.setScene(1);
        //MenuNavigator.stage.setScene(gameScene);
        
        Projectile.setParentView(gameController.getGeneralLayout());
        Monster.setParentView(gameController.getGeneralLayout());
        Tower.setParentView(gameController.getGeneralLayout());
        
        Levels levels = new Levels(this);
        GameState.setLevels(levels);
        GameState.initPath();
        
        startGameLoop(type);
       
        updateLabels();
    }
    public void setConfig(JSONObject json){
    	try {
			GameState.setMap(json.getJSONArray("map"));
			GameState.setLives(json.getInt("lives"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public GameController getController(){
    	return gameController;
    }
    public  Scene getGameScene(){
        return gameScene;
    }
    
    
    
    public boolean transition(int money){
    	if (GameState.getResources() - money >= 0){
    		GameState.setResources(GameState.getResources() - money);
    		return true;
    	}
    	return false;
    }
    // level up
    public void levelUp(){
    	GameState.setLevel(GameState.getLevel() + 1);
    	GameState.getLevels().nextWave();
    }
    // buy tower;
    public boolean buyTower(int type, double xCords , double yCords){
        // Convert the clicked coordinates to a tile coordinate
        int xTile = (int)(xCords / 32);
        int yTile = (int)(yCords / 32);

        if(!NET){
	        // Verify the user can afford the tower
	    	Tower tower = ListOfCharacters.getTower(type, 0);
	        if(GameState.getResources() >= tower.getPrice()) {
	        	if (GameState.tryMapNode(xTile, yTile, 3)){
		        	addTower(xTile, yTile, type);
		        	GameState.setResources(GameState.getResources() - tower.getPrice());
	        		return true;
	        	}
	        }
	        return false;
        }else{
        	try {
				client.buyTower(xTile, yTile, type);
			} catch (JSONException | IOException e) {
				e.printStackTrace();
			}
        	return false;
        }
    }
    private void addTower(int x,int y,int type){
    	Tower tower = ListOfCharacters.getTower(type, 0);
    	Tower buyTower = Tower.copy(tower);
    	GameState.addTower(buyTower);
    	buyTower.add(x, y);
    }
    // get tower by coords
    public Tower getTower(double xCords , double yCords){
        Coordinate clickedTiled = new Coordinate(xCords , yCords);
        // Find tower with matching coordinate
        for(Tower tower : GameState.getPlayerTowers()){
            if(tower.getCoords().equals(clickedTiled)){
                return tower;
            }
        }
        return null;
    }
    //??
    public void upgradeTower(Tower tower){
    	if(tower.isUpgradeable()){
    		if (transition(tower.upgradePrice()))
    			tower.upgradeTower();
    	}else{
    		System.err.println("Upgrade notupgradable");
    	}
    }
    public void sellTower(Tower tower){
    	if (GameState.tryMapNode(tower.getTileX(), tower.getTileY(), 0)){
    		GameState.getPlayerTowers().remove(tower);
    		transition(-tower.getSellCost());
    		tower.remove();
    	}
    	
    }

    // add monster
    public void createMonster(Monster monster, Coordinate c){
    	monster.addVariancy();
    	monster.add(c);
    	GameState.addMonster(monster);
    }

    
    public void updateLabels() {

        gameController.updateLabels(
            Integer.toString(GameState.getLevel()) ,
            Integer.toString(GameState.getLives()) ,
            Integer.toString(GameState.getResources()) ,
            Integer.toString(timer.getGameTime())
        		);
        gameController.updateBuyers();
        if (GameState.getTarget() != null){
        	gameController.updateTarget(
        		GameState.getTarget().getLevel(),
            	GameState.getTarget().getAttackDamage(),
                GameState.getTarget().getAttackRange(),
                GameState.getTarget().getAttackCD(),
                GameState.getTarget().upgradePrice(),
                GameState.getTarget().getSellCost(),
                GameState.getTarget().getBuff() != null ? 
                		"["+GameState.getTarget().getBuff().getId()+"]" : "",
                GameState.getTarget().getBuff() != null ?
                		GameState.getTarget().getBuff().getDesc() : ""
            );
        }
	}
    
    public void pause(){
    	
    	if (GameState.getState() == GameState.IS_PAUSED){
    		timer.start();
    		GameState.setState(GameState.IS_RUNNING);
    		
    	}else{
    		timer.stop();
    		GameState.setState(GameState.IS_PAUSED);
    		
    	}
    	gameController.setPauseView(GameState.isPaused());
    }


    /**
     * Removes a monster from the graphical interface and from the reference
     * list. The player is rewarded if they defeated the monster or punished
     * if the monster finished the path.
     *
     * @param monster
     * The monster to remove from the game.
     */
    public void removeMonster(Monster monster, boolean isKilled){
    	if (GameState.getMonstersAlive().contains(monster)){
	        // Punish player
	        if (!isKilled){
	        	GameState.setLives((GameState.getLives()) - 1);
	        }
	        // Reward player
	        else{
	        	GameState.setResources((GameState.getResources()) + monster.getReward());
	        }
	        GameState.getMonstersAlive().remove(monster);
    	}

    }

    /**
     * GAME LOOP
     *
     * Responsible for all graphical updates, including playing
     * animations and updating monster locations.
     */
    private void startGameLoop(int type) {
    	
    	timer = new Scheduler(this);
    	if (type > 1){
    		NetworkLink link = new NetworkLink(){
    			@Override
    			public String get() {
    				return null;
    			}
    			@Override
    			public void send(String string) {}
    			@Override
    			public void users(String string, boolean b) {}
    			@Override
    			public void start(JSONObject object) {}
				@Override
				public void tower(int x, int y, int type, boolean what) {
					if (what){
						addTower(x,y, type);
					}
				}
    		};
    		client.setLink(link);
    	}
    }


	public void openMenu() {
		timer.stop();
		MenuNavigator.setScene(0);
	}


	public void endGame(boolean st) {
		GameState.setState(GameState.IS_STOPPED);
		gameController.setPauseView(GameState.isPaused());
		timer.stop();
		MenuNavigator.setResultGame(st);
		MenuNavigator.setScene(0);
	}
	public void setClient(Client client) {
		this.client = client;
	}
	

}
