package game.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Client implements Runnable{
	private ClientLink link;
	private String name;
	private DataOutputStream out;
	private DataInputStream in;
	private Socket socket;
	private boolean isConnection;
	public Client(){
		
	}
	public void connect(String address, int port, String n){
		name = n;
		try {
			InetAddress ipAddress = InetAddress.getByName(address);
	        socket = new Socket(ipAddress, port);
	        in = new DataInputStream(socket.getInputStream());
	        out = new DataOutputStream(socket.getOutputStream());
		}catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e.toString());
		}
	}
	public void start(){
		isConnection = true;
		new Thread(this).start();
	}
	public void stop(){
		isConnection = false;
		try {
			logOut();
		} catch (JSONException | IOException e) {
			
		}
	}
	public boolean isRunning(){
		return isConnection;
	}
	@Override
    public void run() {
		String line = null;
        JSONObject json;
        try {
        	setLoginInfo();
            while (isConnection) {
                //System.out.println(in.readLine());
                line = in.readUTF(); // ждем пока сервер отошлет строку текста.
                json = new JSONObject(line);
                switch(json.getString("event")){
                case "message":{
                	link.c_send(json.getString("message"));
                	break;
                }
                case "tick":{
                	JSONObject conf = json.getJSONObject("config");
                	if(conf.has("addMonsters")){
                		link.c_monster(conf.getJSONArray("addMonsters"), true);
                	}
                	if(conf.has("levelUp")){
                		link.c_special("level", conf.getInt("levelUp"));
                	}
                	if(conf.has("dieMonsters")){
                		link.c_monster(conf.getJSONArray("dieMonsters"), false);
                	}
                	if(conf.has("lives")){
                		link.c_special("lives", conf.getInt("lives"));
                	}
                	if(conf.has("addBuff")){
                		JSONArray jbuffs = conf.getJSONArray("addBuff");
                		for(int i=0;i<jbuffs.length();i++){
                			JSONArray jar = jbuffs.getJSONArray(i);
                			link.c_buff(jar.getInt(0), jar.getInt(1), jar.getInt(2));
                		}
                		
                	}
                	link.c_tick();
                	break;
                }
                case "newUser":{
                	String name = json.getString("name");
                	link.c_send("Connected new user "+name+"!");
                	link.c_users(json.getInt("id"), name, true);
                	break;
                }
                case "login":{
                	link.c_send(json.getString("message"));
                	for(int i=0;i<json.getJSONArray("names").length();i++){
                		JSONArray pair = json.getJSONArray("names").getJSONArray(i);
                		link.c_users(pair.getInt(0), pair.getString(1), true);
                	}
                	if (json.getBoolean("special")){
                		link.c_special("server", true);
                	}
                	break;
                }
                case "start":{
                	link.c_start(json.getJSONObject("config"));
                	break;
                }
                case "pause":{
                	link.c_pause(json.getBoolean("state"), json.getString("message"));
                	break;
                }
                case "tower":{
                	JSONArray conf = json.getJSONArray("options");
             	   if(json.getBoolean("state")){
             		  
             		  link.c_tower(conf.getInt(0), conf.getInt(1), conf.getInt(2), conf.getInt(3), true);
             	   }else{
             		  link.c_tower(conf.getInt(0), conf.getInt(1), 0, 0, false);
             	   }
             	   break;
                }
                case "money":{
                	link.c_money(json.getInt("value"));
                	break;
                }
                case "upgradeTower":{
                	JSONArray conf = json.getJSONArray("options");
                	link.c_upgrade(conf.getInt(0), conf.getInt(1));
                	break;
                }
                case "logout":{
                	link.c_send("User "+json.getString("name")+" have disconnected!");
                	link.c_users(json.getInt("id"), json.getString("name"), false);
                	break;
                }
                case "endGame":{
                	link.c_endGame(json.getBoolean("state"));
                	break;
                }
                case "bye":{
                	socket.close();
                	break;
                }
                }
                //System.out.println("Debug:"+json.toString());
            }
        } catch (IOException x) {
            System.err.println("Server is shutting down");
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println(line);
		}
    }
	private void logOut() throws JSONException, IOException{
		JSONObject json = new JSONObject();
    	json.put("event", "logout");
        send(json);
	}
	private void setLoginInfo() throws JSONException, IOException{
		JSONObject json = new JSONObject();
    	json.put("event", "login");
        send(json);
	}
	public void send(JSONObject json) throws JSONException, IOException{
    	json.put("user", name);
    	json.put("selfmade", "true");
    	out.writeUTF(json.toString()); // отсылаем введенную строку текста серверу.
        out.flush(); // заставляем поток закончить передачу данных.
    }
	public void sendMessage(String message) throws JSONException, IOException{
    	JSONObject json = new JSONObject();
    	json.put("user", name);
    	json.put("event", "message");
    	json.put("message", message);
    	send(json);
    }
    public void setLink(ClientLink l){
    	link = l;
    }
	public void buyTower(int xTile, int yTile, int type) throws JSONException, IOException {
		JSONObject json = new JSONObject();
    	json.put("event", "tower");
    	json.put("state", true);
    	JSONArray options = new JSONArray();
    	options.put(xTile);
    	options.put(yTile);
    	options.put(type);
    	json.put("options", options);
    	send(json);
	}
	public void getUsers() throws JSONException, IOException {
		JSONObject json = new JSONObject();
    	json.put("event", "getUsers");
    	send(json);
	}
	public void sellTower(int tileX, int tileY) throws JSONException, IOException {
		JSONObject json = new JSONObject();
    	json.put("event", "tower");
    	json.put("state", false);
    	JSONArray options = new JSONArray();
    	options.put(tileX);
    	options.put(tileY);
    	json.put("options", options);
    	send(json);
	}
	public void upgradeTower(int tileX, int tileY) throws JSONException, IOException {
		JSONObject json = new JSONObject();
    	json.put("event", "upgradeTower");
    	JSONArray options = new JSONArray();
    	options.put(tileX);
    	options.put(tileY);
    	json.put("options", options);
    	send(json);
	}
}