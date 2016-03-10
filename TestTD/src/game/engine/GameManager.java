package game.engine;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import game.MenuNavigator;
import game.engine.characters.Levels;
import game.engine.characters.ListOfCharacters;
import game.engine.characters.Monster;
import game.engine.characters.Projectile;
import game.engine.characters.Tower;
import game.network.Client;
import game.network.Network;
import game.network.NetworkLink;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;

/**
 * Responsible for all communications between user interface and underlying
 * frameworks. The initialize method starts the game loop when called through
 * creating or loading a game.
 */
public class GameManager implements State{
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
        gameController.setNetPane(type == 1);
        gameController.setTooltips();
        if (!ListOfCharacters.isinit())
        	ListOfCharacters.init();
        
        
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
    	GameState.setMapNode(x, y, 3);
    	Tower tower = ListOfCharacters.getTower(type, 0);
    	Tower buyTower = Tower.copy(tower);
    	GameState.addTower(buyTower);
    	buyTower.add(x, y, this, true);
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
    public void createMonsters(Monster monster){
    	for(Coordinate c: GameState.getStartCords()){
			monster.addVariancy();
			monster.add(c, this, true, true);
	    	GameState.addMonster(monster);
		}
    }
    public void addMonsterWith(double vx, double vy, int hash, int index){
    	Monster monster = GameState.getLevels().getWave().getMonster();
    	monster.addVariancy(vx, vy, hash);
    	monster.add(GameState.getStartCords().get(index), this, true, false);
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
    public void sendMessage(String text) throws JSONException, IOException{
    	if(NET){
    		client.sendMessage(text);
    	}
    }
    public void tryPause() throws JSONException, IOException{
    	if (!NET){
    		int state = GameState.isPaused()? GameState.IS_RUNNING: GameState.IS_PAUSED;
    		doPause(state);
    		if (state != GameState.IS_PAUSED){
        		timer.start();
        	}else{
        		timer.stop();    		
        	}
    	}else{
    		JSONObject json = new JSONObject();
    		json.put("event", "pause");
    		json.put("state", GameState.getState() != GameState.IS_PAUSED);
    		client.send(json);
    	}
    }
    private void doPause(int state){
    	GameState.setState(state);
    	gameController.setPauseView(state == GameState.IS_PAUSED);
    	
    }


    /**
     * Removes a monster from the graphical interface and from the reference
     * list. The player is rewarded if they defeated the monster or punished
     * if the monster finished the path.
     *
     * @param monster
     * The monster to remove from the game.
     */
    public void removeMonster(int id, boolean isKilled){
    	for(Monster m:GameState.getMonstersAlive()){
    		if(m.getID() == id){
    			removeMonster(m, isKilled);
    			break;
    		}
    	}
    }
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
    	
    	timer = new Scheduler(this, type == 1);
    	if (type > 0){
    		NetworkLink link = new NetworkLink(){
    			@Override
    			public void special(String key, Object value) {
    				Platform.runLater(new Runnable() {
					    public void run() {
		    				switch(key){
		    					case "level":{
		    						GameState.setLevel((int)value);
		    						GameState.getLevels().nextWave();
		    						timer.notifyTimer();
		    						break;
		    					}
		    				}
					    }
					});
    			}
    			@Override
    			public void send(String string) {
    				gameController.appendMessage(string);
    			}
    			@Override
    			public void users(String string, boolean b) {}
    			@Override
    			public void start(JSONObject object) {}
				@Override
				public void tower(int x, int y, int type, boolean what) {
					Platform.runLater(new Runnable() {
					    public void run() {
					    	if (what){
								addTower(x,y, type);
							}
					    }
					});
				}
				@Override
				public void pause(boolean state, String name) {
					doPause(state? GameState.IS_PAUSED: GameState.IS_RUNNING);
					gameController.appendMessage(name);
				}
				@Override
				public void money(int money) {
					Platform.runLater(new Runnable() {
					    public void run() {
					    	GameState.setResources(money);
					    }
					});
					
				}
				@Override
				public void monster(JSONArray jsonArray, boolean b) {
					Platform.runLater(new Runnable() {
					    public void run() {
					    	try {
					    		for(int i=0;i<jsonArray.length();i++){
					    			JSONObject vari = jsonArray.getJSONObject(i);
					    			if (b){
					    				addMonsterWith(vari.getDouble("vx"), vari.getDouble("vy"), vari.getInt("id"), i);
					    			}else{
					    				removeMonster(vari.getInt("id"), vari.getBoolean("state"));
					    			}
					    		}
							} catch (JSONException e) {
								e.printStackTrace();
							}
					    }
					});
				}
				@Override
				public void endGame(boolean boolean1) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void tick() {
					Platform.runLater(new Runnable() {
					    public void run() {
					    	timer.lightTick();
					    }
					});
				}
    		};
    		client.setLink(link);
    	}
    }


	public void openMenu() {
		if (!NET) {
			timer.stop();
			MenuNavigator.setScene(0);
		}else{
			alertDialog("Открыть меню", "Выход в меню отключит вас от текущей игры", "Если вы хотите отключиться от игры\nи выйти в меню, нажмите ОК");
			
		}
		
	}
	
	private void alertDialog(String title, String header, String content){
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
		    Network.closeConnections();
		    MenuNavigator.setScene(0);
		} else {
		    // ... user chose CANCEL or closed the dialog
		}
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
	
	
	@Override
	public ArrayList<Coordinate> getStartCords() {
		return GameState.getStartCords();
	}
	@Override
	public int getDirection(int tileX, int tileY) {
		// TODO Auto-generated method stub
		return GameState.getDirection(tileX, tileY);
	}
	@Override
	public Coordinate getNextCoord(int tileX, int tileY) {
		// TODO Auto-generated method stub
		return GameState.getNextCoord(tileX, tileY);
	}
	@Override
	public ArrayList<Monster> getMonstersAlive() {
		// TODO Auto-generated method stub
		return GameState.getMonstersAlive();
	}
	

}
