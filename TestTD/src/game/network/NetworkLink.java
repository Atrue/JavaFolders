package game.network;

import org.json.JSONObject;

public interface NetworkLink{
	public String get();
	public void send(String string);
	public void users(String string, boolean b);
	public void start(JSONObject object);
	public void tower(int x,int y, int type, boolean what);
}
