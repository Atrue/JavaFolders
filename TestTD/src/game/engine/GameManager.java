package game.engine;


import game.MenuNavigator;
import game.engine.characters.Monster;
import game.engine.characters.Projectile;
import game.engine.characters.Tower;
import javafx.animation.AnimationTimer;
import javafx.animation.PathTransition;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Responsible for all communications between user interface and underlying
 * frameworks. The initialize method starts the game loop when called through
 * creating or loading a game.
 */
public class GameManager {
	
	private  GameState gameState;                        // Provides basic game states.    
	
	private  TileMap tileMap;  						// The painted map used as the backgrounds layer
	
    private  Group generalLayer;                    // Used for the monster graphics

    private  Scene gameScene;                       // The main viewport
    private  GameController gameController;         // Handles fxml attributes (buttons and labels)
    private  AnimationTimer gameLoop;               // Used for the gui thread
	//private Group tilemapGroup;
	
	//private Label hoverTower;
    /**
     * Initializes the game
     *
     * @throws java.io.IOException
     */
    public void initialize() throws java.io.IOException{
        // Initializes the game state
        gameState = GameState.getNewGame();
/*
        // Generates the map with the given resolution
        gameMap = new TileMap(1280 ,800);

        // Creates gui hierarchy
        FXMLLoader loader = new FXMLLoader(MenuNavigator.GAMEUI);
        
        StackPane gamePane = new StackPane();
        tilemapGroup = new Group();
        monsterLayer = new Group();
        monsterLayer.getChildren().add(tilemapGroup);
        tilemapGroup.getChildren().add(gameMap);
        gamePane.getChildren().add(monsterLayer);

        // Opens stream to get controller reference
        Node gameUI = (Node)loader.load(MenuNavigator.GAMEUI.openStream());
        gamePane.getChildren().add(gameUI);
        gameScene = new Scene(gamePane);
        gameScene.getStylesheets().add(GameManager.class.getResource("res/menu/gamestyle.css").toExternalForm());
        gameController = loader.<GameController>getController();
        gameController.setGameManager(this, game);

*/
        // Drawing image 
        tileMap = new TileMap(1280 ,800);
        // Creates gui hierarchy
        FXMLLoader loader = new FXMLLoader(MenuNavigator.GAMEUI);
        // Opens stream to get controller reference
        Node gameUI = (Node)loader.load(MenuNavigator.GAMEUI.openStream()); 
        gameController = loader.<GameController>getController();
        gameController.setGameManager(this, gameState, tileMap);
        // Generates the map with the given resolution
        StackPane gamePane = new StackPane();
        gamePane.getChildren().add(gameController.getGeneralLayout());
    	
        
       
        gamePane.getChildren().add(gameUI);
        gameScene = new Scene(gamePane);
        gameScene.getStylesheets().add(GameManager.class.getResource("res/menu/gamestyle.css").toExternalForm());
        
        gameController.setListeners();
        
        MenuNavigator.stage.setScene(gameScene);
        Monster.setPath(tileMap.getPath());
        startGameLoop();
        

    }

    public  Scene getGameScene(){
        return gameScene;
    }
    
    public TileMap getMap(){
    	return tileMap;
    }


    /**
     * Attempts to create a tower at the tile clicked on
     * by the user.
     *
     * @param xCords
     * The clicked x coordinate
     * @param yCords
     * The clicked y coordinate
     */
    public void buyTower(double xCords , double yCords){
        // Convert the clicked coordinates to a tile coordinate
        int xTile = (int)(xCords / 64);
        int yTile = (int)(yCords / 64);

        // Verify the node is not occupied
        if(tileMap.nodeOpen(xTile,yTile)){
            // Verify the user can afford the tower
            if(gameState.getResources() > 49) {
            	gameState.addTower(new Tower(xTile, yTile));
            	gameState.setResources(gameState.getResources() - 50);
                tileMap.setMapNode(((int) (xCords / 64)), ((int) (yCords / 64)), 7);
            }
        }
    }

    /**
     * Gets the tower at a specific coordinate
     *
     * @param xCords
     * The clicked x coordinate passed from the controller
     * @param yCords
     * The clicked y coordinate passed from the controller
     * @return
     * The tower clicked or null if none exist
     */
    public Tower getTower(double xCords , double yCords){
        Coordinate clickedTiled = new Coordinate(xCords , yCords);
        // Find tower with matching coordinate
        for(Tower tower : gameState.getPlayerTowers()){
            if(tower.getCoords().equals(clickedTiled)){
                return tower;
            }
        }
        return null;
    }
    /**
     * Upgrades a user tower. Pauses the tower attacker service
     * which is resumed after a set time.
     *
     * @param tower
     * Tower selected for the upgrade.
     */
    public void upgradeTower(Tower tower){
        tower.getTowerAttacker().cancel();
        tower.upgradeTower();
        tower.getTowerAttacker().pollTower(tower.getUpgradeTime());
    }


    /**
     * Creates a monster.
     *
     * @param health
     * The health points for the monster. Increases as
     * the game progresses.
     */
    private void createMonster(int health){
    	gameState.getMonstersAlive().add(new Monster(health));
        gameController.getGeneralLayout().getChildren().add(gameState.getMonstersAlive().get(gameState.getMonstersAlive().size() - 1).getView());
    }

    /**
     * Updates monsters location on the path and removes any
     * monsters which reach the end of the path.
     */
    private void updateLocations(){
        if(!gameState.getMonstersAlive().isEmpty()){
            Iterator<Monster> monsters = gameState.getMonstersAlive().iterator();
            Monster monster;
            while(monsters.hasNext()) {
                monster = monsters.next();
                monster.updateLocation(1);
                if(monster.isPathFinished()){
                    removeMonster(monster);
                }
            }
        }
    }

    /**
     * Checks all towers for created projectiles which are then created
     * and animated by the main game loop.
     */
    private void createProjectiles(){
        Path projectilePath;
        PathTransition animation;
        for(Tower tower : gameState.getPlayerTowers()){
            for(Projectile projectile : tower.getProjectileList()){
                // Create animation path
                projectilePath = new Path(new MoveTo(projectile.getStartX() , projectile.getStartY()));
                projectilePath.getElements().add(new LineTo(projectile.getEndX() , projectile.getEndY()));
                animation = new PathTransition(Duration.millis(300) , projectilePath , projectile);

                // When the animation finishes, hide it and remove it
                animation.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        PathTransition finishedAnimation = (PathTransition) actionEvent.getSource();
                        Projectile finishedProjectile = (Projectile) finishedAnimation.getNode();

                        // Hide and remove from gui
                        finishedProjectile.setVisible(false);
                        gameController.getGeneralLayout().getChildren().remove(finishedProjectile);

                        // Remove monster if they are dead
                        if(finishedProjectile.getTarget().isDead()){
                            removeMonster(finishedProjectile.getTarget());
                        }
                    }
                });
                gameController.getGeneralLayout().getChildren().add(projectile);
                animation.play();
            }
            tower.getProjectileList().clear();
        }

    }


    /**
     * Updates the labels associated with the game state
     *
     * @param timer
     * Time before the next wave of monsters will be spawned.
     */
    private void updateLabels(int timer){
            gameController.updateLabels(
                Integer.toString(gameState.getLevel()) ,
                Integer.toString(gameState.getLives()) ,
                Integer.toString(gameState.getResources()) ,
                Integer.toString(gameState.getScore()) ,
                Integer.toString(timer)
        );
            if (gameState.getTarget() != null){
            	gameController.updateTarget(
	            	Integer.toString(gameState.getTarget().getAttackDamage()),
	                Integer.toString(gameState.getTarget().getAttackRange()),
	                Double.toString(gameState.getTarget().getAttackSpeed())
	            );
            }
    }


    /**
     * Stops the game. Used when the player chooses to quit
     * or the losing conditions are met.
     */
    public void stopGame(){
        pauseGame();
        gameState.setState(GameState.IS_STOPPED);
        gameLoop.stop();
    }

    /**
     * Used to freeze the game and is called before the
     * game is stopped.
     */
    public void pauseGame(){
    	gameState.setState(GameState.IS_PAUSED);
    }
    /**
     * Called when the game is started or the
     * game returns from a paused state.
     */
    public void resumeGame(){
    	gameState.setState(GameState.IS_RUNNING);
    }


    /**
     * Removes a monster from the graphical interface and from the reference
     * list. The player is rewarded if they defeated the monster or punished
     * if the monster finished the path.
     *
     * @param monster
     * The monster to remove from the game.
     */
    private synchronized void removeMonster(Monster monster){
        // Punish player
        if (monster.isPathFinished()){
        	gameState.setLives((gameState.getLives()) - 1);
        }
        // Reward player
        else{
        	gameState.setResources((gameState.getResources()) + monster.getReward());
        	gameState.setScore(gameState.getScore() + (monster.getReward() * gameState.getLevel()));
        }

        // Remove monsters graphic and reference
        monster.getView().setVisible(false);
        gameState.getMonstersAlive().remove(monster);

    }

    /**
     * GAME LOOP
     *
     * Responsible for all graphical updates, including playing
     * animations and updating monster locations.
     */
    private void startGameLoop() {
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
                    	gameState.setLevel(gameState.getLevel() + 1);
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
    }

}
