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
   private Socket socket;
   private int id;
   private String name = "";
   private DataInputStream in;
   private DataOutputStream out;
   
   private int resourse;
   ClientHandler(ClientHandler[] other, Socket client, NetworkState server, int id){
	   this.clients = other;
	   this.socket = client;
	   this.server = server;
	   this.id = id;
	   this.resourse = server.getStartResourse();
   }
   @Override
   public void run() {
	try {
         in = new DataInputStream(socket.getInputStream());
         out = new DataOutputStream(socket.getOutputStream());

         String line = null;
         JSONObject json = new JSONObject();
         
         while(true) {
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
           }
           send(json.toString());
         }
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
   public boolean isTransition(int value){
	   return resourse + value >= 0;
   }
   public synchronized void doTransition(int value){
	   resourse += value;
   }
   private void getLoginInfo(JSONObject json) throws JSONException{
	   name = json.getString("user");
   }
   private void alertNewUser() throws IOException, JSONException{
	   JSONObject json = new JSONObject();
	   json.put("event", "newUser");
	   json.put("name", name);
	   json.put("message", "Connected new user "+name+"!");
	   for(int i=0;i<clients.length;i++){
		   if (clients[i] != null && i != id){
			   clients[i].send(json.toString());
		   }
	   }
	   json.put("event", "login");
	   json.put("names", getOnlineUsers());
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
			   js.put(clients[i].name);
		   }
	   }
	   return js;
   }
}