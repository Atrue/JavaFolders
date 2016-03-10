package game.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Client implements Runnable{
	private NetworkLink link;
	private String name;
	private DataOutputStream out;
	private DataInputStream in;
	private Socket socket;
	private boolean isConnection;
	public Client(String address, int port, String n, NetworkLink l){
		link = l;
		name = n;
		try {
			InetAddress ipAddress = InetAddress.getByName(address);
	        socket = new Socket(ipAddress, port);
	        InputStream sin = socket.getInputStream();
	        OutputStream sout = socket.getOutputStream();
	        in = new DataInputStream(sin);
	        out = new DataOutputStream(sout);
		}catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void start(){
		isConnection = true;
		new Thread(this).start();
	}
	public void stop(){
		isConnection = false;
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
                if (json.has("selfmade")){
                	continue;
                }
                switch(json.getString("event")){
                case "message":{
                	link.send(json.getString("message"));
                	break;
                }
                case "tick":{
                	JSONObject conf = json.getJSONObject("config");
                	link.tick();
                	if(conf.has("newMonsters")){
                		link.monster(json.getJSONArray("newMonsters"), true);
                	}
                	if(conf.has("levelUp")){
                		link.special("level", conf.getInt("levelUp"));
                	}
                	if(conf.has("dieMonsters")){
                		link.monster(json.getJSONArray("dieMonsters"), false);
                	}
                	
                	break;
                }
                case "newUser":{
                	String name = json.getString("name");
                	link.send("Connected new user "+name+"!");
                	link.users(name, true);
                	break;
                }
                case "login":{
                	link.send("You connected to "+socket.getInetAddress()+":"+socket.getPort());
                	for(int i=0;i<json.getJSONArray("names").length();i++){
                		link.users(json.getJSONArray("names").getString(i), true);
                	}
                	if (json.getBoolean("special")){
                		link.special("server", true);
                	}
                	break;
                }
                case "start":{
                	link.start(json.getJSONObject("config"));
                	break;
                }
                case "pause":{
                	link.pause(json.getBoolean("state"), json.getString("message"));
                	break;
                }
                case "tower":{
             	   if(json.getBoolean("state")){
             		  JSONArray conf = json.getJSONArray("options");
             		  link.tower(conf.getInt(0), conf.getInt(1), conf.getInt(2), true);
             	   }
             	   break;
                }
                case "money":{
                	link.money(json.getInt("money"));
                	break;
                }
                case "logout":{
                	link.send(json.getString("message"));
                	link.users(json.getString("name"), false);
                	break;
                }
                case "endGame":{
                	link.endGame(json.getBoolean("state"));
                	break;
                }
                }
                System.out.println("Debug:"+json.toString());
            }
            logOut();
            socket.close();
        } catch (IOException x) {
            x.printStackTrace();
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
    public void setLink(NetworkLink l){
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
}