package game.network;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import game.engine.Coordinate;
import game.engine.GameState;
import game.engine.State;
import game.engine.characters.Levels;
import game.engine.characters.ListOfCharacters;
import game.engine.characters.Monster;
import game.engine.characters.Tower;

public class NetworkState implements Serializable, State {
	private static final long serialVersionUID = 7170212054503203348L;
	
	private ClientHandler[] clients;
	private ServerScheduler timer;
	// game state flags
	public final int IS_RUNNING = 1; // game is active
	public final int IS_PAUSED = 2; // game is temporarily not active
	public final int IS_STOPPED = 3; // game is over and halted

	private final int DIR_TOP = -1;
	private final int DIR_LEFT = -2;
	private final int DIR_BOT = -3;
	private final int DIR_RIGHT = -4;
	private final int DIR_RIGHT_TOP = -7;
	private final int DIR_LEFT_TOP = -8;
	private final int DIR_BOT_LEFT = -5;
	private final int DIR_BOT_RIGHT = -6;

	private final int FPS = 30;
	public int[] SPFERES;
	private ArrayList<Coordinate> startCords;
	private Coordinate endCord;

	// private GameManager parent;

	private int[][] map;

	private int state;
	private int level; // represents the current wave of monsters that
	private int lives; // numbers of monster escapes allowed before game
	private ArrayList<Tower> playerTowers; // holds all tower references
	private ArrayList<Monster> monstersAlive; // holds monster references
	private Levels levels;
	private Tower target = null;
	private int[][] pathMap;
	private JSONObject configTick;

	private int startResourse;

	// CONSTRUCTORS
	NetworkState(ClientHandler[] cl) {
		clients = cl;
		state = IS_PAUSED;
		level = 0;
		lives = 20;
		startResourse = 10;
		playerTowers = new ArrayList<Tower>();
		monstersAlive = new ArrayList<Monster>();
		
		ListOfCharacters.init();
		configTick = new JSONObject();
		levels = new Levels(this);
		timer = new ServerScheduler(this);
		
		startCords = new ArrayList<>();
		startCords.add(new Coordinate(0, 5));
		startCords.add(new Coordinate(0, 11));

		endCord = new Coordinate(21, 8);
		// parent = gamestate;
		map = new int[22][17];
		for (int i = 0; i < map.length; i++) {
			boolean blocked = i == 0 || i + 1 == map.length;
			for (int j = 0; j < map[i].length; j++) {
				map[i][j] = blocked || (j == 0 || j + 1 == map[i].length) ? 1 : 0;
			}
		}
		for (Coordinate c : startCords) {
			map[c.getTileX()][c.getTileY()] = 4;
		}
		map[endCord.getTileX()][endCord.getTileY()] = 5;
		initPath();
	}

	public void initPath() {
		pathMap = algorithmA(fullCopy(map));
	}

	private int[][] fullCopy(int[][] from) {
		int[][] to = from.clone();
		for (int i = 0; i < from.length; i++) {
			to[i] = from[i].clone();
		}
		return to;
	}

	public int getDirection(int xTile, int yTile) {
		return pathMap[xTile][yTile];
	}

	public Coordinate getNextCoord(int xTile, int yTile) {
		switch (getDirection(xTile, yTile)) {
		case DIR_TOP: {
			return new Coordinate(xTile, yTile - 1);
		}
		case DIR_LEFT: {
			return new Coordinate(xTile - 1, yTile);
		}
		case DIR_BOT: {
			return new Coordinate(xTile, yTile + 1);
		}
		case DIR_RIGHT: {
			return new Coordinate(xTile + 1, yTile);
		}
		case DIR_RIGHT_TOP: {
			return new Coordinate(xTile + 1, yTile - 1);
		}
		case DIR_LEFT_TOP: {
			return new Coordinate(xTile - 1, yTile - 1);
		}
		case DIR_BOT_LEFT: {
			return new Coordinate(xTile - 1, yTile + 1);
		}
		case DIR_BOT_RIGHT: {
			return new Coordinate(xTile + 1, yTile + 1);
		}
		default: {
			return new Coordinate(xTile, yTile);
		}
		}
	}
	private boolean testAndAdd(int x, int y, int dir, ArrayList<Integer[]> pather, int[][] map) {
		if (x >= 0 && y >= 0 && x < map.length && y < map[x].length && (map[x][y] == 0 || map[x][y] == 4)) {
			return true;
		}
		return false;
	}

	private void alorithmA_add(int x, int y, int dir, ArrayList<Integer[]> pather, int[][] map) {
		map[x][y] = dir;
		// Label l = new Label(s[dir + 8]);
		// l.setLayoutX(x*32);
		// l.setLayoutY(y*32);
		// group.getChildren().add(l);
		pather.add(new Integer[] { x, y, dir });
	}

	private int[][] algorithmA(int[][] cmap) {
		ArrayList<Integer[]> pather = new ArrayList<>();
		pather.add(new Integer[] { endCord.getTileX(), endCord.getTileY(), 2 });
		while (!pather.isEmpty()) {
			Integer[] comb = pather.get(0);
			int nx = comb[0];
			int ny = comb[1];

			boolean[] trues = new boolean[8];

			trues[0] = testAndAdd(nx, ny + 1, -1, pather, cmap);
			trues[1] = testAndAdd(nx + 1, ny, -2, pather, cmap);
			trues[2] = testAndAdd(nx, ny - 1, -3, pather, cmap);
			trues[3] = testAndAdd(nx - 1, ny, -4, pather, cmap);
			trues[4] = testAndAdd(nx + 1, ny - 1, -5, pather, cmap);
			trues[5] = testAndAdd(nx - 1, ny - 1, -6, pather, cmap);
			trues[6] = testAndAdd(nx - 1, ny + 1, -7, pather, cmap);
			trues[7] = testAndAdd(nx + 1, ny + 1, -8, pather, cmap);

			if (trues[0])
				alorithmA_add(nx, ny + 1, -1, pather, cmap);
			if (trues[1])
				alorithmA_add(nx + 1, ny, -2, pather, cmap);
			if (trues[2])
				alorithmA_add(nx, ny - 1, -3, pather, cmap);
			if (trues[3])
				alorithmA_add(nx - 1, ny, -4, pather, cmap);
			if (trues[4] && trues[1] && trues[2])
				alorithmA_add(nx + 1, ny - 1, -5, pather, cmap);
			if (trues[5] && trues[3] && trues[2])
				alorithmA_add(nx - 1, ny - 1, -6, pather, cmap);
			if (trues[6] && trues[3] && trues[0])
				alorithmA_add(nx - 1, ny + 1, -7, pather, cmap);
			if (trues[7] && trues[1] && trues[0])
				alorithmA_add(nx + 1, ny + 1, -8, pather, cmap);
			/*
			 * fr (int i = -1; i > -0; i--) { int nx = comb[0]; int ny =
			 * comb[1]; switch(i){ case -1:{ny++;break;} case -2:{nx++;break;}
			 * case -3:{ny--;break;} case -4:{nx--;break;} case
			 * -5:{nx++;ny--;break;} case -6:{nx--;ny--;break;} case
			 * -7:{nx--;ny++;break;} case -8:{nx++;ny++;break;} }
			 * trues[Math.abs(i)-1] = testAndAdd(nx, ny, i, pather, cmap); if (i
			 * < -4){ alorithmA_add(nx, ny, i, pather, cmap); } }
			 */

			pather.remove(0);
		}
		return cmap;
	}

	private boolean isValidPath(int[][] map) {

		for (Coordinate c : startCords) {
			if (map[c.getTileX()][c.getTileY()] >= 0)
				return false;
		}
		for (Monster m : monstersAlive) {
			if (!m.hasDirectionIn(map)) {
				return false;
			}
		}
		return true;

	}

	public void setLevel(int l) {
		level = l;
	}

	public void setLives(int l) throws JSONException, IOException {
		lives = l;
		if (lives == 0){
			JSONObject json = new JSONObject();
			json.put("event", "end");
			json.put("state", false);
			sendAll(json.toString());
		}
	}

	public void setState(int s) {
		state = s;
	}

	public void setTarget(Tower t) {
		target = t;
	}

	public void setLevels(Levels l) {
		levels = l;
	}
	
	public void levelUp(){
		level ++;
    	levels.nextWave();
    	try {
			configTick.put("levelUp", level);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	@Override
	public void endGame(boolean st) {
		try {
			state = IS_STOPPED;
			timer.stop();
			JSONObject json = new JSONObject();
			json.put("event", "endGame");
			json.put("state", st);
			sendAll(json.toString());
			
			
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void createMonsters(Monster monster) {
		
		try {
			JSONArray mon = new JSONArray();
			
			for(Coordinate c: getStartCords()){
				JSONObject var = new JSONObject();
				monster.addVariancy();
				monster.add(c, this, false, true);
				var.put("vx", monster.getVariancyX());
				var.put("vy", monster.getVariancyY());
				var.put("id", monster.getID());
				mon.put(var);
		    	addMonster(monster);
			}			
			configTick.put("addMonsters", mon);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeMonster(Monster monster, boolean isKilled) {
		try {
			JSONObject mon = new JSONObject();
			mon.put("id", monster.getID());
			mon.put("state", isKilled);
			if(mon.has("dieMonsters")){
				configTick.getJSONArray("dieMonsters").put(mon);
			}else{
				JSONArray arr = new JSONArray();
				arr.put(mon);
				configTick.put("dieMonsters", arr);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void sendTick() throws JSONException, IOException {
		JSONObject json = new JSONObject();
		json.put("event", "tick");
		json.put("config", configTick);
		sendAll(json.toString());
		configTick = new JSONObject();
	}
	/*
	 * synchronizerd methods
	 */
	public synchronized void trySetState(int id, boolean pause) throws JSONException, IOException{
		if (isPaused() != pause){
			JSONObject json = new JSONObject();
			json.put("event", "pause");
			json.put("state", pause);
			json.put("name", clients[id].getName());
			String message = clients[id].getName() + (pause ? " paused" : " resumed") +" game";
			json.put("message", message);
			sendAll(json.toString());
			
			setState(pause? IS_PAUSED: IS_RUNNING);
			if(pause){
				timer.stop();
			}else{
				timer.start();
			}
		}
	}
	public synchronized void tryBuyTower(int id, int x, int y, int type) throws JSONException, IOException{
		Tower tower = Tower.copy(ListOfCharacters.getTower(type, 0));
		if(clients[id].isTransition( -tower.getPrice()) &&	tryMapNode(x, y, 3)){
			clients[id].doTransition( -tower.getPrice());
			tower.setOwner(id, "");
			tower.add(x, y, this, false);
			addTower(tower);
			JSONObject json = new JSONObject();
			json.put("event", "tower");
			json.put("state", true);
	    	JSONArray options = new JSONArray();
	    	options.put(x);
	    	options.put(y);
	    	options.put(type);
	    	json.put("options", options);
	    	
	    	sendAll(json.toString());
	    	
	    	json = new JSONObject();
	    	json.put("event", "money");
	    	json.put("money", clients[id].getResourse());
	    	sendTo(json.toString(), id);
		}
	}
	public synchronized void tryUpTower(int id,int x,int y) throws JSONException, IOException{
		Tower t = getTower(x, y);
		if (t != null && t.getOwnerID() == id && clients[id].isTransition(-t.upgradePrice())){
			clients[id].doTransition( -t.upgradePrice());
			t.upgradeTower();
			
			JSONObject json = new JSONObject();
			json.put("event", "tower");
			json.put("state", true);
	    	JSONArray options = new JSONArray();
	    	options.put(x);
	    	options.put(y);
	    	json.put("options", options);;
	    	sendAll(json.toString());
	    	
	    	json = new JSONObject();
	    	json.put("event", "money");
	    	json.put("money", clients[id].getResourse());
	    	sendTo(json.toString(), id);
		}
		
	}
	public synchronized void trySellTower(int id,int x,int y) throws JSONException, IOException{
		if(map[x][y] == 3){
			Tower t = getTower(x, y);
			if (t != null && t.getOwnerID() == id){
				clients[id].doTransition( t.getPrice());
				playerTowers.remove(t);
				setMapNode(x, y, 0);
				
				JSONObject json = new JSONObject();
				json.put("event", "tower");
				json.put("state", false);
		    	JSONArray options = new JSONArray();
		    	options.put(x);
		    	options.put(y);
		    	json.put("options", options);;
		    	sendAll(json.toString());
			}
		}
	}
	
	// sets the map node for the given coordinates to input value than repaints
	// adjustment
	public void setMapNode(int xCord, int yCord, int updatedValue) {
		map[xCord][yCord] = updatedValue;
	}

	public boolean tryMapNode(int xCord, int yCord, int updatedValue) {
		if (updatedValue == 0) {
			setMapNode(xCord, yCord, updatedValue);
			pathMap = algorithmA(fullCopy(map));
			return true;
		}
		if (!nodeOpen(xCord, yCord))
			return false;
		int[][] testMap = fullCopy(map);
		testMap[xCord][yCord] = updatedValue;
		testMap = algorithmA(testMap);
		if (isValidPath(testMap)) {
			setMapNode(xCord, yCord, updatedValue);
			pathMap = testMap;
			return true;
		}
		return false;

	}
	

	public ArrayList<Coordinate> getStartCords() {
		return startCords;
	}

	public Coordinate getEndCord() {
		return endCord;
	}
	
	public int getLevel() {
		return level;
	}

	public int getLives() {
		return lives;
	}

	public int getState() {
		return state;
	}

	public Tower getTarget() {
		return target;
	}

	public Levels getLevels() {
		return levels;
	}

	public int getFPS() {
		return FPS;
	}

	public int[][] getMap() {
		return map;
	}

	public int getStartResourse(){
		return startResourse;
	}
	// checks to see if the node is open
	public boolean nodeOpen(int xCord, int yCord) {
		if (xCord < map.length && yCord < map[0].length) {
			if (map[xCord][yCord] == 0) {
				return true;
			}
		}
		return false;
	}

	public boolean isPaused() {
		if (state == IS_PAUSED) {
			return true;
		}
		return false;
	}

	public boolean isRunning() {
		if (state == IS_RUNNING) {
			return true;
		}
		return false;
	}

	public boolean isStopped() {
		if (state == IS_PAUSED) {
			return true;
		}
		return false;
	}

	public ArrayList<Tower> getPlayerTowers() {
		return playerTowers;
	}

	public ArrayList<Monster> getMonstersAlive() {
		return monstersAlive;
	}
	public Tower getTower(double xCords , double yCords){
        Coordinate clickedTiled = new Coordinate(xCords , yCords);
        // Find tower with matching coordinate
        for(Tower tower : getPlayerTowers()){
            if(tower.getCoords().equals(clickedTiled)){
                return tower;
            }
        }
        return null;
    }
	public void addMonster(Monster monster) {
		monstersAlive.add(monster);
	}

	public void addTower(Tower tower) {
		playerTowers.add(tower);
	}

	public void removeMonster(Monster monster) {
		monstersAlive.remove(monster);
	}

	public void removeTower(Tower tower) {
		playerTowers.remove(tower);
	}
	private void sendAll(String message) throws IOException{
		for(int i=0;i<clients.length;i++){
			if(clients[i]!=null){
				clients[i].send(message);
			}
		}
	}
	private void sendExcept(String message, int id) throws IOException{
		for(int i=0;i<clients.length;i++){
			if(clients[i]!=null && id != i){
				clients[i].send(message);
			}
		}
	}
	private void sendTo(String message, int id) throws IOException{
		clients[id].send(message);
	}
	private JSONArray mapToArray(){
		JSONArray jmap = new JSONArray();
		for(int x=0;x<map.length;x++){
			JSONArray row = new JSONArray();
			for(int y=0;y<map[x].length;y++){
				row.put(map[x][y]);
			}
			jmap.put(row);
		}
		return jmap;
	}
	public void start() {
		try {
			JSONObject json = new JSONObject();
			json.put("event", "start");
			JSONObject config = new JSONObject();
			config.put("lives", lives);
			config.put("map", mapToArray());
			json.put("config", config);
			sendAll(json.toString());
	   } catch (JSONException | IOException e) {
			e.printStackTrace();
		}
	}

	



	
}
