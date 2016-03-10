package game.engine;

import java.util.ArrayList;

import game.engine.characters.Monster;

public interface State {
	public static int getFPS() {
		return 1;
	}
	public void createMonsters(Monster monster);
	public ArrayList<Coordinate> getStartCords();
	public void levelUp();
	public void removeMonster(Monster monster, boolean isKilled);
	public int getDirection(int tileX, int tileY);
	public Coordinate getNextCoord(int tileX, int tileY);
	public ArrayList<Monster> getMonstersAlive();
	public void endGame(boolean b);

}
