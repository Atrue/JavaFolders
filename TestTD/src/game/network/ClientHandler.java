package game.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class ClientHandler implements Runnable{
	private ClientHandler[] clients;
	private NetworkState server;
	private boolean isAdmin;
   private Socket socket;
   private boolean isConnection;
   private int id;
   private String name = "";
   private DataInputStream in;
   private DataOutputStream out;
   
   private int resourse;
   ClientHandler(ClientHandler[] other, Socket client, NetworkState server, int id, boolean state){
	   this.clients = other;
	   this.socket = client;
	   this.server = server;
	   this.id = id;
	   this.resourse = server.getStartResourse();
	   this.isConnection = true;
	   this.isAdmin = state;
   }
   @Override
   public void run() {
	try {
         in = new DataInputStream(socket.getInputStream());
         out = new DataOutputStream(socket.getOutputStream());

         String line = null;
         JSONObject json = new JSONObject();
         
         while(isConnection) {
           line = in.readUTF(); // ожидаем пока клиент пришлет строку текста.
           json = new JSONObject(line);
           switch(json.getString("event")){
           case "login":{
        	   getLoginInfo(json);
        	   alertNewUser();
        	   break;
           }
           case "message":{
        	   sendAll(json.getString("message"));
        	   break;
           }
           case "tower":{
        	   JSONArray conf = json.getJSONArray("options");
        	   if(json.getBoolean("state")){
        		   server.tryBuyTower(id, conf.getInt(0), conf.getInt(1), conf.getInt(2));
        	   }else{
        		   server.trySellTower(id, conf.getInt(0), conf.getInt(1));
        	   }
        	   break;
           }
           case "upgradeTower":{
        	   JSONArray conf = json.getJSONArray("options");
        	   server.tryUpTower(id, conf.getInt(0), conf.getInt(1));
        	   break;
           }
           case "pause":{
        	   server.trySetState(id, json.getBoolean("state"));
        	   break;
           }
           case "logout":{
        	   logOut();
        	   break;
           }
           }
         }
         socket.close();
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
   // PUBLIC METHODS FOR SERVER
   public boolean isTransition(int value){
	   return resourse + value >= 0;
   }
   public synchronized void doTransition(int value) throws JSONException, IOException{
	   resourse += value;
	   JSONObject json = new JSONObject();
	   json.put("event", "money");
	   json.put("value", resourse);
	   send(json.toString());
   }
   public synchronized int getResourse(){
	   return resourse;
   }
   public String getName(){
	   return name;
   }
   
   
   // INNER METHODS
   private void logOut() throws JSONException, IOException{
	   JSONObject json = new JSONObject();
	   json.put("event", "logout");
	   json.put("name", name);
	   json.put("id", id);
	   for(int i=0;i<clients.length;i++){
		   if (clients[i] != null && i != id){
			   clients[i].send(json.toString());
		   }
	   }
	   json.put("event", "bye");
	   send(json.toString());
	   isConnection = false;
	   clients[id] = null;
   }
   private void getLoginInfo(JSONObject json) throws JSONException{
	   name = json.getString("user");
   }
   private void alertNewUser() throws IOException, JSONException{
	   JSONObject json = new JSONObject();
	   json.put("event", "newUser");
	   json.put("name", name);
	   json.put("id", id);
	   for(int i=0;i<clients.length;i++){
		   if (clients[i] != null && i != id){
			   clients[i].send(json.toString());
		   }
	   }
	   json.put("event", "login");
	   json.put("message", "You connected to "+socket.getInetAddress()+":"+socket.getPort());
	   json.put("names", getOnlineUsers());
	   json.put("special", isAdmin);
	   send(json.toString());
   }
   public synchronized void send(String message) throws IOException{
	   out.writeUTF(message);
	   out.flush();
   }
   private void sendAll(String message) throws IOException, JSONException{
	   JSONObject json = new JSONObject();
	   json.put("event", "message");
	   json.put("message", name+":"+message);
	   for(int i=0;i<clients.length;i++){
		   if (clients[i] != null){
			   clients[i].send(json.toString());
		   }
	   }
   }
   private JSONArray getOnlineUsers(){
	   JSONArray js = new JSONArray();
	   for(int i=0;i<clients.length;i++){
		   if (clients[i] != null){
			   JSONArray pair = new JSONArray();
			   pair.put(clients[i].id);
			   pair.put(clients[i].name);
			   js.put(pair);
		   }
	   }
	   return js;
   }
}