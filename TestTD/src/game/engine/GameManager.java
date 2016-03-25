package game.engine;

import java.io.IOException;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import game.MenuNavigator;
import game.engine.characters.Levels;
import game.engine.characters.Settings;
import game.engine.characters.Monster;
import game.engine.characters.Projectile;
import game.engine.characters.Target;
import game.engine.characters.Tower;
import game.network.Client;
import game.network.ClientLink;
import game.network.Network;
import game.network.ServerLink;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
/**
 * Responsible for all communications between user interface and underlying
 * frameworks. The initialize method starts the game loop when called through
 * creating or loading a game.
 */
public class GameManager implements ServerLink, ClientLink {
	private GameController gameController; // Handles fxml attributes 
	private Configurations config;
	private Scheduler timer;
	private boolean NET;

	private Scene gameScene;
	private Client client;

	// private Group tilemapGroup;
	private Target target;
	// private Label hoverTower;
	/**
	 * Initializes the game
	 *
	 * @throws java.io.IOException
	 */
	public void initialize(int type) throws java.io.IOException {
		NET = type == 0 ? false : true;
		// Initializes the game state
		config = new Configurations();
		config.init(type, 0);

		// Creates gui hierarchy
		FXMLLoader loader = new FXMLLoader(MenuNavigator.GAMEUI);
		// Opens stream to get controller reference
		Node gameUI = (Node) loader.load(MenuNavigator.GAMEUI.openStream());
		gameController = loader.<GameController> getController();
		gameController.setGameManager(this);
		// Generates the map with the given resolution
		StackPane gamePane = new StackPane();
		//gamePane.getChildren().add(gameController.getGeneralLayout());
		config.setParentView(gameController.getGeneralLayout());

		gamePane.getChildren().add(gameUI);
		gameScene = new Scene(gamePane);
		gameScene.getStylesheets().add(GameManager.class.getResource("res/menu/gamestyle.css").toExternalForm());

		gameController.setListeners();
		gameController.setNetPane(type == 1);
		gameController.setTooltips();
		if (!Settings.isinit())
			Settings.init();

		MenuNavigator.addScene(gameScene, 1);
		MenuNavigator.setScene(1);
		// MenuNavigator.stage.setScene(gameScene);

		Projectile.setParentView(gameController.getGeneralLayout());
		Monster.setParentView(gameController.getGeneralLayout());
		Tower.setParentView(gameController.getGeneralLayout());

		Levels levels = new Levels(this);
		config.setLevels(levels);
		config.initPath();

		startGameLoop(type);

		s_updateLabels();
		
		
	    
	}

	
	
	public void setConfig(JSONObject json) {
		try {
			config.setMap(json.getJSONArray("map"));
			config.setLives(json.getInt("lives"));
			config.setResources(json.getInt("money"));
			gameController.repaintBG(config.getMap());
			s_updateLabels();
		} catch (JSONException e) {
			System.err.println("INVALID DATA IN JSON CONFIG!!!");
		}
	}

	public GameController getController() {
		return gameController;
	}

	public Scene getGameScene() {
		return gameScene;
	}
	

	
	
	

	public boolean transition(int money) {
		if (config.getResources() - money >= 0) {
			config.setResources(config.getResources() - money);
			s_updateLabels();
			return true;
		}
		return false;
	}

	// buy tower;
	public void tryBuyTower(int type, double xCords, double yCords) {
		// Convert the clicked coordinates to a tile coordinate
		int xTile = (int) (xCords / 32);
		int yTile = (int) (yCords / 32);
		
		if (!config.nodeOpen(xTile, yTile))
			return;
		
		if (!NET) {
			// Verify the user can afford the tower
			Tower tower = Settings.getTower(type, 0);
			if (config.getResources() >= tower.getPrice()) {
				if (config.tryMapNode(xTile, yTile, 3)) {
					addTower(xTile, yTile, type, 0);
					transition(tower.getPrice());
				}else{
					gameController.showBlockedPop(xCords, yCords);
				}
			}
		} else {
			try {
				client.buyTower(xTile, yTile, type);
			} catch (JSONException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void addTower(int x, int y, int type, int who) {
		config.tryMapNode(x, y, 3);
		gameController.repaintBG(config.getMap());
		Tower tower = Settings.getTower(type, 0);
		Tower buyTower = Tower.copy(tower);
		config.addTower(buyTower);
		buyTower.add(x, y, this, true, !NET);
		buyTower.setOwner(who);
	}

	// get tower by coords
	public Tower getTower(double xCords, double yCords) {
		Coordinate clickedTiled = new Coordinate(xCords, yCords);
		// Find tower with matching coordinate
		for (Tower tower : config.getPlayerTowers()) {
			if (tower.getCoords().equals(clickedTiled)) {
				return tower;
			}
		}
		return null;
	}
	// get tower by coords
	public Tower getTower(int xCords, int yCords) {
		Coordinate clickedTiled = new Coordinate(xCords, yCords);
		// Find tower with matching coordinate
		for (Tower tower : config.getPlayerTowers()) {
			if (tower.getCoords().equals(clickedTiled)) {
				return tower;
			}
		}
		return null;
	}
	

	// ??
	public void tryUpgradeTower(Tower tower) {
		if (!NET){
			if (transition(tower.upgradePrice()))
				upgradeTower(tower.getTileX(), tower.getTileY());
		}else{
			try {
				client.upgradeTower(tower.getTileX(), tower.getTileY());
			} catch (JSONException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void upgradeTower(int x, int y){
		Tower tower = getTower(x, y);
		if (tower.isUpgradeable()) {			
			tower.upgradeTower();
		} else {
			System.err.println("Upgrade notupgradable");
		}
	}

	public void trySellTower(Tower tower) {
		if (!NET){
			removeTower(tower.getTileX(), tower.getTileY());
			transition(-tower.getSellCost());
		}else{
			try {
				client.sellTower(tower.getTileX(), tower.getTileY());
			} catch (JSONException | IOException e) {
				e.printStackTrace();
			}
		}

	}
	public void removeTower(int x, int y){
		config.tryMapNode(x, y, 0);		
		gameController.repaintBG(config.getMap());
		Tower tower = getTower(x, y);
		tower.remove();
		config.removeTower(tower);
	}
	public void addMonsterWith(double vx, double vy, int hash, int index) {
		Monster monster = config.getLevels().getWave().get_copy();
		monster.addVariancy(vx, vy, hash);
		monster.add(config.getStartCords().get(index), this, true, false);
		config.addMonster(monster);

	}

	
	public void sendMessage(String text) throws JSONException, IOException {
		if (NET) {
			client.sendMessage(text);
		}
	}

	public void tryPause() throws JSONException, IOException {
		if (config.isStopped())
			return;
		if (!NET) {
			int state = config.isPaused() ? config.IS_RUNNING : config.IS_PAUSED;
			doPause(state);
			if (state != config.IS_PAUSED) {
				timer.start();
			} else {
				timer.stop();
			}
		} else {
			JSONObject json = new JSONObject();
			json.put("event", "pause");
			json.put("state", config.getState() != config.IS_PAUSED);
			client.send(json);
		}
	}

	private void doPause(int state) {
		config.setState(state);
		gameController.setPauseView(state == config.IS_PAUSED);

	}

	/**
	 * Removes a monster from the graphical interface and from the reference
	 * list. The player is rewarded if they defeated the monster or punished if
	 * the monster finished the path.
	 *
	 * @param monster
	 *            The monster to remove from the game.
	 */
	public Monster getMonster(int id){
		for (Monster m : config.getMonstersAlive()) {
			if (m.getID() == id) {
				return m;
			}
		}
		return null;
	}
	public void removeMonster(int id, boolean isKilled, int who) {
		Monster m = getMonster(id);
		if (m != null){
			m.remove(isKilled, who);
			config.removeMonster(m);
		}
	}

	/**
	 * GAME LOOP
	 *
	 * Responsible for all graphical updates, including playing animations and
	 * updating monster locations.
	 */
	private void startGameLoop(int type) {

		timer = new Scheduler(this, type == 1);
		if (type > 0) {
			client.setLink(this);
		}
	}

	public void openMenu() {
		if (!NET) {
			timer.stop();
			MenuNavigator.setScene(0);
		} else {
			alertDialog("Открыть меню", "Выход в меню отключит вас от текущей игры",
					"Если вы хотите отключиться от игры\nи выйти в меню, нажмите ОК");

		}

	}

	private void alertDialog(String title, String header, String content) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			Network.closeConnections();
			MenuNavigator.setScene(0);
		} else {
			// ... user chose CANCEL or closed the dialog
		}
	}
	
	private void engGameDialog(Boolean b) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Конец игры!");
		alert.setHeaderText(b? "Неплохо!": "Плохо!");
		alert.setContentText(b? "Вы выиграли и прошли всю игру.": "Вы проиграли. Можете попробовать еще раз.");

		ButtonType buttonTypeOne = new ButtonType("Меню");
		ButtonType buttonTypeTwo = new ButtonType("Выход");
		ButtonType buttonTypeCancel = new ButtonType("Закрыть", ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeOne){
			Network.closeConnections();
			MenuNavigator.setScene(0);
		} else if (result.get() == buttonTypeTwo) {
			System.exit(1);
		} else {
		    // ... user chose CANCEL or closed the dialog
		}
	}

	public void setClient(Client client) {
		this.client = client;
	}

	// AS SERVER

	public void s_endGame(boolean st) {
		config.setState(config.IS_STOPPED);
		gameController.setPauseView(config.isPaused());
		timer.stop();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				engGameDialog(st);
			}
		});
		
	}

	@Override
	public void s_addBuff(int id, int tileX, int tileY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void s_createMonsters(Monster moncopy) {
		for (Coordinate c : config.getStartCords()) {
			Monster monster = Monster.copy(moncopy);
			monster.setId(config.iterIdMonster());
			if (!monster.isBoss())
				monster.addVariancy();
			monster.add(c, this, true, true);
			config.addMonster(monster);
		}
	}

	@Override
	public void s_levelUp() {
		config.setLevel(config.getLevel() + 1);
		config.getLevels().nextWave();
	}

	@Override
	public void s_removeMonster(Monster monster, boolean isKilled, int who) {
		if (config.getMonstersAlive().contains(monster)) {
			// Punish player
			if (!isKilled) {
				config.setLives((config.getLives()) - monster.getLiveCost());
				if (config.getLives() == 0){
					s_endGame(false);
				}
			}
			// Reward player
			else {
				config.setResources((config.getResources()) + monster.getReward());
			}
			config.getMonstersAlive().remove(monster);
		}
	}
	
	@Override
	public void s_updateLabels() {

		gameController.updateLabels(Integer.toString(config.getLevel()),
				Integer.toString(config.getLives()), Integer.toString(config.getResources()),
				Integer.toString(timer.getGameTime()));
		gameController.updateBuyers(config.getResources());
		if (target != null) {
			String[] info = target.getFullInfo();			
			gameController.updateTarget(
					info[0], info[1], info[2], info[3], info[4], info[5], info[6], info[7],
					String.valueOf(target.getUpgradePrice()),
					String.valueOf(target.getSellPrice()),
					target.getUpgradePrice() != 0,
					target.getSellPrice() != 0,
					config.getResources() >= target.getUpgradePrice()
					);
		}
	}
	
	@Override
	public Configurations s_getConfigurations(){
		return config;
	}
	

	@Override
	public void s_debug(String string) {
		System.out.println(string);
	}
	@Override
	public Target s_getTarget(){
		return target;
	}
	@Override
	public void s_setTarget(Target t){
		if(target != null)
			target.deactiveTarget();
		target = t;
		if(target != null){
			target.activeTarget();
		}
		s_updateLabels();
		gameController.setEnableTargetInfo(target != null);
	}
	
	// As client

	@Override
	public void c_special(String key, Object value) {
		Platform.runLater(new Runnable() {
			public void run() {
				switch (key) {
				case "level": {
					config.setLevel((int) value);
					config.getLevels().nextWave();
					timer.notifyTimer();
					break;
				}
				case "lives":{
					config.setLives((int)value);
					break;
				}
				}
			}
		});
	}

	@Override
	public void c_send(String string) {
		gameController.appendMessage(string);
	}

	@Override
	public void c_users(int id, String string, boolean b) {
	}

	@Override
	public void c_start(JSONObject object) {
	}

	@Override
	public void c_tower(int x, int y, int type, int who, boolean what) {
		Platform.runLater(new Runnable() {
			public void run() {
				if (what) {
					addTower(x, y, type, who);
				}else{
					removeTower(x, y);
				}
			}
		});
	}

	@Override
	public void c_pause(boolean state, String name) {
		doPause(state ? config.IS_PAUSED : config.IS_RUNNING);
		gameController.appendMessage(name);
	}

	@Override
	public void c_money(int money) {
		Platform.runLater(new Runnable() {
			public void run() {
				config.setResources(money);
				s_updateLabels();
			}
		});

	}

	@Override
	public void c_monster(JSONArray jsonArray, boolean b) {
		Platform.runLater(new Runnable() {
			public void run() {
				try {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject vari = jsonArray.getJSONObject(i);
						if (b) {
							addMonsterWith(vari.getDouble("vx"), vari.getDouble("vy"), vari.getInt("id"), i);
						} else {
							removeMonster(vari.getInt("id"), vari.getBoolean("state"), vari.getInt("who"));
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void c_endGame(boolean boolean1) {
		Platform.runLater(new Runnable() {
			public void run() {
				s_endGame(boolean1);
			}
		});
	}

	@Override
	public void c_tick() {
		Platform.runLater(new Runnable() {
			public void run() {
				timer.lightTick();
			}
		});
	}

	@Override
	public void c_buff(int id, int towerX, int towerY) {
		Monster mon = getMonster(id);
		if(mon != null){
			Tower tower = getTower(towerX, towerY);
			if (tower != null)
				mon.addBuff(tower.getBuff());
			else
				System.err.println("NO TOWER WITH ["+towerX+","+towerY+"]");
		}else{
			System.err.println("NO MONSTER WITH ["+id+"]");
		}
	}

	@Override
	public void c_upgrade(int int1, int int2) {
		Platform.runLater(new Runnable() {
			public void run() {
				upgradeTower(int1, int2);
			}
		});
	}


}
