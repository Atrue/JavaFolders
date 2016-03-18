package game.network;

import java.io.IOException;
import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import game.engine.Configurations;
import game.engine.Coordinate;
import game.engine.Scheduler;
import game.engine.ServerLink;
import game.engine.characters.Levels;
import game.engine.characters.ListOfCharacters;
import game.engine.characters.Monster;
import game.engine.characters.Tower;

public class NetworkState implements Serializable, ServerLink {
	private static final long serialVersionUID = 7170212054503203348L;
	
	private ClientHandler[] clients;
	private Scheduler timer;
	// game state flags
	private Configurations config;
	private JSONObject configTick;

	private int startResourse;

	private int dieCount;
	// CONSTRUCTORS
	NetworkState(ClientHandler[] cl) {
		clients = cl;
		config = new Configurations();
		config.init(1, cl.length);
		startResourse = 10;
		dieCount = 0;
		
		ListOfCharacters.init();
		Levels levels = new Levels(this);
		config.setLevels(levels);
		
		configTick = new JSONObject();
		
		timer = new Scheduler(this, false);
				
		config.initPath();
	}
	
	public int getStartResourse(){
		return startResourse;
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

	
	public void removeLive(int lic) throws JSONException{
		int l = config.getLives();
		if (l > 0){
			config.setLives(l-lic);
			configTick.put("lives", config.getLives());
		}else{
			s_endGame(false);
		}
	}
	
	@Override
	public void s_endGame(boolean st) {
		try {
			config.setState(config.IS_STOPPED);
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
	public void s_createMonsters(Monster moncopy) {
		
		try {
			JSONArray mon = new JSONArray();
			
			for(Coordinate c: config.getStartCords()){
				JSONObject var = new JSONObject();
				Monster monster = Monster.copy(moncopy);
				if (!monster.isBoss())
					monster.addVariancy();
				monster.setId(dieCount);
				dieCount++;
				monster.add(c, this, false, true);
				var.put("vx", monster.getVariancyX());
				var.put("vy", monster.getVariancyY());
				var.put("id", monster.getID());
				mon.put(var);
		    	config.addMonster(monster);
			}			
			configTick.put("addMonsters", mon);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void s_removeMonster(Monster monster, boolean isKilled, int who) {
		try {
			JSONObject mon = new JSONObject();
			mon.put("id", monster.getID());
			mon.put("state", isKilled);
			mon.put("who", who);
			if(configTick.has("dieMonsters")){
				configTick.getJSONArray("dieMonsters").put(mon);
			}else{
				JSONArray arr = new JSONArray();
				arr.put(mon);
				configTick.put("dieMonsters", arr);
			}
			config.removeMonster(monster);
			if (isKilled){
				clients[who].doTransition(monster.getReward());
			}else{
				removeLive(monster.getLiveCost());
			}
		} catch (JSONException | IOException e) {
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
		if (config.isPaused() != pause){
			JSONObject json = new JSONObject();
			json.put("event", "pause");
			json.put("state", pause);
			json.put("name", clients[id].getName());
			String message = clients[id].getName() + (pause ? " paused" : " resumed") +" game";
			json.put("message", message);
			sendAll(json.toString());
			
			config.setState(pause? config.IS_PAUSED: config.IS_RUNNING);
			if(pause){
				timer.stop();
			}else{
				timer.start();
			}
		}
	}
	public synchronized void tryBuyTower(int id, int x, int y, int type) throws JSONException, IOException{
		Tower tower = Tower.copy(ListOfCharacters.getTower(type, 0));
		if(clients[id].isTransition( -tower.getPrice()) &&	config.tryMapNode(x, y, 3)){
			clients[id].doTransition( -tower.getPrice());
			tower.setOwner(id);
			tower.add(x, y, this, false, true);
			config.addTower(tower);
			JSONObject json = new JSONObject();
			json.put("event", "tower");
			json.put("state", true);
	    	JSONArray options = new JSONArray();
	    	options.put(x);
	    	options.put(y);
	    	options.put(type);
	    	options.put(id);
	    	json.put("options", options);
	    	
	    	sendAll(json.toString());
		}
	}
	public synchronized void tryUpTower(int id,int x,int y) throws JSONException, IOException{
		Tower t = getTower(x, y);
		if (t != null && t.getOwnerID() == id && clients[id].isTransition(-t.upgradePrice())){
			clients[id].doTransition( -t.upgradePrice());
			t.upgradeTower();
			
			JSONObject json = new JSONObject();
			json.put("event", "upgradeTower");
	    	JSONArray options = new JSONArray();
	    	options.put(x);
	    	options.put(y);
	    	json.put("options", options);;
	    	sendAll(json.toString());
	    	
		}
		
	}
	public synchronized void trySellTower(int id,int x,int y) throws JSONException, IOException{
		Tower t = getTower(x, y);
		if (t != null && t.getOwnerID() == id){
			clients[id].doTransition( t.getPrice());
			config.removeTower(t);
			config.tryMapNode(x, y, 0);
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
		int[][] map = config.getMap();
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
			JSONObject jconfig = new JSONObject();
			jconfig.put("lives", config.getLives());
			jconfig.put("map", mapToArray());
			jconfig.put("money", startResourse);
			json.put("config", jconfig);
			sendAll(json.toString());
			//timer.start();
	   } catch (JSONException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void s_addBuff(int id, int tileX, int tileY) {
		JSONArray jar = new JSONArray();
		jar.put(id);
		jar.put(tileX);
		jar.put(tileY);
		try {
			if(configTick.has("addBuff")){
				configTick.getJSONArray("addBuff").put(jar);
			}else{
				JSONArray arr = new JSONArray();
				arr.put(jar);
				configTick.put("addBuff", arr);
			}
		} catch (JSONException e1) {}
	}

	@Override
	public void s_levelUp() {
		config.setLevel(config.getLevel() + 1);
		config.getLevels().nextWave();
    	try {
			configTick.put("levelUp", config.getLevel());
		} catch (JSONException e1) {}
	}

	@Override
	public void s_updateLabels() {
		try {
			sendTick();
		} catch (JSONException | IOException e) {}
	}

	@Override
	public Configurations s_getConfigurations() {
		return config;
	}

	@Override
	public void s_debug(String string) {
		try {
			JSONObject h = new JSONObject();
			h.put("event", "debug");
			h.put("message", string);
			sendAll(h.toString());
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	



	
}
