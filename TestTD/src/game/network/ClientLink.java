package game.network;

import org.json.JSONArray;
import org.json.JSONObject;

public interface ClientLink{
	public void c_special(String key, Object value);
	public void c_send(String string);
	public void c_users(int id, String string, boolean b);
	public void c_start(JSONObject object);
	public void c_tower(int x,int y, int type,int who, boolean what);
	public void c_pause(boolean state, String name);
	public void c_money(int money);
	public void c_monster(JSONArray jsonArray, boolean b);
	public void c_endGame(boolean boolean1);
	public void c_tick();
	public void c_buff(int id, int towerX, int towerY);
	public void c_upgrade(int int1, int int2);
}
