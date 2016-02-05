package game.engine;


import java.util.ArrayList;
import java.util.Iterator;

import game.MenuNavigator;
import game.engine.characters.Levels;
import game.engine.characters.Monster;
import game.engine.characters.Projectile;
import game.engine.characters.Tower;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

/**
 * Responsible for all communications between user interface and underlying
 * frameworks. The initialize method starts the game loop when called through
 * creating or loading a game.
 */
public class GameManager {
	private  GameController gameController;			// Handles fxml attributes (buttons and labels)
	private Scheduler timer;
	
	
	
	
    private  Scene gameScene;                       // The main viewport
             
	//private Group tilemapGroup;
	
	//private Label hoverTower;
    /**
     * Initializes the game
     *
     * @throws java.io.IOException
     */
    public void initialize() throws java.io.IOException{
        // Initializes the game state
        GameState.init();
        
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
        
        MenuNavigator.stage.setScene(gameScene);
        
        Projectile.setParentView(gameController.getGeneralLayout());
        Monster.setParentView(gameController.getGeneralLayout());
        
        
        Levels levels = new Levels(this);
        GameState.setLevels(levels);
        
        
        
        startGameLoop();
        

    }
    public GameController getController(){
    	return gameController;
    }
    public  Scene getGameScene(){
        return gameScene;
    }
    
    
    // level up
    public void levelUp(){
    	GameState.setLevel(GameState.getLevel() + 1);
    	GameState.getLevels().nextWave();
    }
    // buy tower;
    public void buyTower(double xCords , double yCords){
        // Convert the clicked coordinates to a tile coordinate
        int xTile = (int)(xCords / 64);
        int yTile = (int)(yCords / 64);

        // Verify the node is not occupied
        if(GameState.getMap().nodeOpen(xTile,yTile)){
            // Verify the user can afford the tower
            if(GameState.getResources() > 49) {
            	GameState.addTower(new Tower(xTile, yTile));
            	GameState.setResources(GameState.getResources() - 50);
            	GameState.getMap().setMapNode(((int) (xCords / 64)), ((int) (yCords / 64)), 7);
            }
        }
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
        //tower.getTowerAttacker().cancel();
        tower.upgradeTower();
        //tower.getTowerAttacker().pollTower(tower.getUpgradeTime());
    }


    // add monster
    public void createMonster(Monster monster){
    	monster.add();
    	GameState.addMonster(monster);
        //gameController.getGeneralLayout().getChildren().add(monster.getView());
        //gameController.getGeneralLayout().getChildren().add(monster.getHPView());
    }

    

    
    public void openMenu(){
    	if (GameState.getState() == GameState.IS_PAUSED){
    		GameState.setState(GameState.IS_RUNNING);
    		timer.start();
    	}else{
    		GameState.setState(GameState.IS_PAUSED);
    		timer.stop();
    	}
    }


    /**
     * Removes a monster from the graphical interface and from the reference
     * list. The player is rewarded if they defeated the monster or punished
     * if the monster finished the path.
     *
     * @param monster
     * The monster to remove from the game.
     */
    void removeMonster(Monster monster){
        // Punish player
        if (monster.isPathFinished()){
        	GameState.setLives((GameState.getLives()) - 1);
        }
        // Reward player
        else{
        	GameState.setResources((GameState.getResources()) + monster.getReward());
        	GameState.setScore(GameState.getScore() + (monster.getReward() * GameState.getLevel()));
        }

        // Remove monsters graphic and reference
        monster.getView().setVisible(false);
        GameState.getMonstersAlive().remove(monster);

    }

    /**
     * GAME LOOP
     *
     * Responsible for all graphical updates, including playing
     * animations and updating monster locations.
     */
    private void startGameLoop() {
    	
    	timer = new Scheduler(this);
    	timer.start();
    	/*
        final LongProperty secondUpdate = new SimpleLongProperty(0);
        final LongProperty fpstimer = new SimpleLongProperty(0);
        final AnimationTimer timer = new AnimationTimer() {
            int timer = 10;

            @Override
            public void handle(long timestamp) {

                // Times each second
                if (timestamp/ 1000000000 != secondUpdate.get()) {
                    timer--;
                    if(timer > 19) {
                        createMonster(3);
                    }
                    else if(timer <= 0){
                    	GameState.setLevel(GameState.getLevel() + 1);
                        timer = 30;
                    }
                }
                createProjectiles();
                if(timestamp / 10000000 != fpstimer.get()){
                    updateLocations();
                }
                fpstimer.set(timestamp / 10000000);
                secondUpdate.set(timestamp / 1000000000);
                updateLabels(timer);
            }
        };
        gameLoop = timer;
        timer.start();
        */
    }

}
