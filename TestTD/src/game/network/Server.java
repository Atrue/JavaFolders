package game.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.scene.paint.Color;
public class Server implements Runnable{
	
   private ServerSocket mainsocket;
   private ClientHandler[] clients;
   private NetworkState gameServer;
   public Server(int port, int number)    {
       try {
    	 mainsocket = new ServerSocket(port);
    	 clients = new ClientHandler[number];
    	 gameServer = new NetworkState(clients);
      } catch(Exception x) { x.printStackTrace(); }
   }
   public void start(){
	   new Thread(this).start();
   }
   @Override
   public void run() {
	   try {
		   //get clients
		   while (hasPlace()) {
			   Socket socket = mainsocket.accept(); 
		       newClient(socket);
		   }
	   } catch(Exception x) { x.printStackTrace(); }
   }
   public void startGame(){
	   new Thread(gameServer).start();
   }
   public void stop() throws IOException{
	   mainsocket.close();
   }
   public void newClient(Socket socket){
	   for(int i=0;i<clients.length;i++){
		   if (clients[i] == null){
			   clients[i] = new ClientHandler(clients, socket, gameServer, i, i==0);
			   new Thread(clients[i]).start();
			   break;
		   }
	   }
   }
   public boolean hasPlace(){
	   for(int i=0;i<clients.length;i++){
		   if (clients[i] == null){
			   return true;
		   }
	   }
	   return false;
   }
   
}
