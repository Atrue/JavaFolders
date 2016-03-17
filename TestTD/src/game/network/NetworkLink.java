package game.network;

import org.json.JSONArray;
import org.json.JSONObject;

public interface NetworkLink{
	public void special(String key, Object value);
	public void send(String string);
	public void users(int id, String string, boolean b);
	public void start(JSONObject object);
	public void tower(int x,int y, int type, boolean what);
	public void pause(boolean state, String name);
	public void money(int money);
	public void monster(JSONArray jsonArray, boolean b);
	public void endGame(boolean boolean1);
	public void tick();
}
