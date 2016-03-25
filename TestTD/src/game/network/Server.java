package game.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
public class Server implements Runnable{
	
   private ServerSocket mainsocket;
   private ClientHandler[] clients;
   private NetworkState gameServer;
   private boolean isStarted = false;
   
   
   public Server(){
	   
   }
   
   public void create(int port, int count) throws IOException    {
     clients = new ClientHandler[count];
	 mainsocket = new ServerSocket(port);
	 gameServer = new NetworkState(clients);
	 
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
	   } catch(Exception x) { 
		   System.err.println("Server is down"); 
	   }
   }
   public boolean isRunning(){
	   return mainsocket != null;
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
