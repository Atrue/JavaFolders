package game.network;

import game.engine.Configurations;
import game.engine.characters.Monster;
import game.engine.characters.Target;

public interface ServerLink {
	
	// STATIC CONFIG GAME
	public static int getFPS() {
		return 30;
	}
	
	public void s_createMonsters(Monster monster);
	public void s_levelUp();
	public void s_removeMonster(Monster monster, boolean isKilled, int who);
	public void s_addBuff(int id, int tileX, int tileY);
	public void s_endGame(boolean st);
	public void s_updateLabels();
	public Configurations s_getConfigurations();
	public void s_debug(String string);
	public void s_setTarget(Target t);
	public Target s_getTarget();
	
}
