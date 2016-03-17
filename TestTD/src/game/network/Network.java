package game.network;

import java.io.IOException;
import java.net.URL;

import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import game.MenuNavigator;
import game.engine.GameManager;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class Network implements ClientLink{
	
	private NetworkController controller;
	private static Client client;
	private static Server server;
	
	public void init() throws IOException{
		
		
		URL res = GameManager.class.getResource("res/menu/network.fxml");
		FXMLLoader loader = new FXMLLoader(res);
		Node gameUI = (Node)loader.load(res.openStream()); 

		
		controller = loader.<NetworkController>getController();
		controller.setParent(this);
		
		StackPane p = controller.getStackPane();
        Scene gameScene = new Scene(p);
        gameScene.getStylesheets().add(GameManager.class.getResource("res/menu/menustyle.css").toExternalForm());
        
        
        MenuNavigator.addScene(gameScene, 2);
        MenuNavigator.setScene(2);
	}
<<<<<<< HEAD
	public void createServer(String port, int count){
		try {
			server = new Server(Integer.parseInt(port), count);
			controller.connectClient(null);
			controller.connectionPack(true);
		} catch (NumberFormatException e) {
			controller.putMessage("Error! Порт должен содержать цифры в диапазоне 100-25000");
			controller.connectionPack(false);
		} catch (IOException e) {
			controller.putMessage("Error! Невозможно создать сервер на этом порту");
			controller.connectionPack(false);
		}
		
=======
	public static void createServer(String port, int number){
		try{
			server = new Server(Integer.parseInt(port), number);
			server.start();
			controller.connectClient(null);
		} catch (NumberFormatException e){
			System.err.println("fck, idiot.");
		}
>>>>>>> b793fd38b593565fcc041fd37b2ceee953df3299
	}
	
	public void connectClient(String addr, String port, String name) {
		if (client == null){
			client = new Client(addr, Integer.parseInt(port), name);
			client.setLink(this);
			client.start();
			controller.connectionPack(true);
		}else{
			closeConnections();
			controller.connectionPack(false);
		}
		
	}
	
	public static boolean isServer(){
		return server != null;
	}
	public static void closeConnections(){
<<<<<<< HEAD
		if (client != null){
			client.stop();
			if (server != null){
				try {
					server.stop();
				} catch (IOException e) {}
=======
		try {
			if (client != null){
				client.stop();
				controller.connectionPack(false);
				if (server != null){
					server.stop();
				}
>>>>>>> b793fd38b593565fcc041fd37b2ceee953df3299
			}
		
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean isServer(){
		return server != null;
	}
	public static Client getClient(){
		return client;
	}
	public void sendMessage(String text) {
		if (client != null){
			try {
				client.sendMessage(text);
			} catch (JSONException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void startGame(){
		server.startGame();
	}
<<<<<<< HEAD
	public void toMenu(){
		try {
			if (server != null)
				server.stop();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
=======
	public static void toMenu(){
		closeConnections();
>>>>>>> b793fd38b593565fcc041fd37b2ceee953df3299
		MenuNavigator.setScene(0);
	}
	
	
	// As client
	
	@Override
	public void c_special(String key, Object value) {
		switch(key){
		case "server":
			controller.enableStartButton((boolean)value);
		}
	}
	@Override
	public void c_send(String string) {
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	controller.putMessage(string);
		    }
		});
	}
	@Override
	public void c_users(int id, String string, boolean b) {
		Platform.runLater(new Runnable() {
		    public void run() {
		    	if (b){
		    		controller.addUser(id, string);
		    	}else{
		    		controller.removeUser(id);
		    	}
		    }
		});
	}
	@Override
	public void c_start(JSONObject object) {
		Platform.runLater(new Runnable() {
		    public void run() {
		    	try {
					GameManager game = new GameManager();
					game.setClient(client);
					game.initialize(1);							
					game.setConfig(object);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
<<<<<<< HEAD
		    }
		});
=======
			}
			@Override
			public void send(String string) {
				SwingUtilities.invokeLater(new Runnable() {
				    public void run() {
				    	controller.putMessage(string);
				    }
				});
			}
			@Override
			public void users(int id, String string, boolean b) {
				Platform.runLater(new Runnable() {
				    public void run() {
				    	if (b){
				    		controller.addUser(id, string);
				    	}else{
				    		controller.removeUser(id);
				    	}
				    }
				});
			}
			@Override
			public void start(JSONObject object) {
				Platform.runLater(new Runnable() {
				    public void run() {
				    	try {
							GameManager game = new GameManager();
							game.setClient(client);
							game.initialize(1);							
							game.setConfig(object);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
				});
			}
			@Override
			public void tower(int x, int y, int type, boolean what) {}
			@Override
			public void pause(boolean state, String name) {}
			@Override
			public void money(int money) {}
			@Override
			public void monster(JSONArray jsonArray, boolean b) {}
			@Override
			public void endGame(boolean boolean1) {}
			@Override
			public void tick() {
				// TODO Auto-generated method stub
				
			}
		};
>>>>>>> b793fd38b593565fcc041fd37b2ceee953df3299
	}
	@Override
	public void c_tower(int x, int y, int type, int who, boolean what) {}
	@Override
	public void c_pause(boolean state, String name) {}
	@Override
	public void c_money(int money) {}
	@Override
	public void c_monster(JSONArray jsonArray, boolean b) {}
	@Override
	public void c_endGame(boolean boolean1) {}
	@Override
	public void c_tick() {}
	@Override
	public void c_buff(int id, int towerX, int towerY) {}
	@Override
	public void c_upgrade(int int1, int int2) {
		// TODO Auto-generated method stub
		
	}
};


