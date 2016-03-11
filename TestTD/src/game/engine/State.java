package game.engine;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import game.engine.characters.Monster;

public interface State {
	
	// STATIC CONFIG GAME
	public static int getFPS() {
		return 5;
	}
	
	// ONLY SERVER
	//public void createMonsters(Monster monster);
	public ArrayList<Coordinate> getStartCords();
	//public void levelUp();
	//public void removeMonster(Monster monster, boolean isKilled);
	public int getDirection(int tileX, int tileY);
	public Coordinate getNextCoord(int tileX, int tileY);
	public ArrayList<Monster> getMonstersAlive();
	//public void addBuff(int id, int tileX, int tileY);
	
	// ONLY CLIENT
	public void special(String key, Object value);
	public void money(int money);
	//public void monster(JSONArray jsonArray, boolean b);
	public void tick();

	// ALl METHODS
	public void startGame(JSONObject object);	
	public void endGame(boolean boolean1);
	public void monster(int id, boolean state);
	public void levelUp();
	public void addBuff(int id, int tileX, int tileY);
	public void send(String string);
	public void tower(int x,int y, int type, short state);
	public void pause(boolean state, String desc);
	public void users(String string, boolean b);
}
