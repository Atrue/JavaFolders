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
<<<<<<< HEAD
   private boolean isStarted = false;
   
   public Server(int port, int count) throws IOException    {
     clients = new ClientHandler[count];
	 mainsocket = new ServerSocket(port);
	 gameServer = new NetworkState(clients);
     new Thread(this).start();
      
=======
   public Server(int port, int number)    {
       try {
    	 mainsocket = new ServerSocket(port);
    	 clients = new ClientHandler[number];
    	 gameServer = new NetworkState(clients);
      } catch(Exception x) { x.printStackTrace(); }
>>>>>>> b793fd38b593565fcc041fd37b2ceee953df3299
   }
   public void start(){
	   new Thread(this).start();
   }
   @Override
   public void run() {
	   try {
		   //get clients
		   while (hasPlace() && !isStarted) {
			   Socket socket = mainsocket.accept(); 
		       newClient(socket);
		   }
	   } catch(Exception x) { x.printStackTrace(); }
   }
   public void startGame(){
	   isStarted = true;
	   gameServer.start();
   }
   public void stop() throws IOException{
	   if(mainsocket!=null){
		   mainsocket.close();
	   }
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
