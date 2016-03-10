package game.network;

import java.io.IOException;
import java.net.URL;

import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import game.MainController;
import game.MenuNavigator;
import game.engine.GameController;
import game.engine.GameManager;
import game.engine.GameState;
import game.engine.characters.ListOfCharacters;
import game.engine.characters.Monster;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class Network {
	
	private static NetworkController controller;
	private static Client client;
	private static Server server;
	
	public static void init() throws IOException{
		
		
		URL res = GameManager.class.getResource("res/menu/network.fxml");
		FXMLLoader loader = new FXMLLoader(res);
		Node gameUI = (Node)loader.load(res.openStream()); 

		
		controller = loader.<NetworkController>getController();
       
		StackPane p = controller.getStackPane();
        Scene gameScene = new Scene(p);
        gameScene.getStylesheets().add(GameManager.class.getResource("res/menu/menustyle.css").toExternalForm());
        
        
        MenuNavigator.addScene(gameScene, 2);
        MenuNavigator.setScene(2);
	}
	public static void createServer(String port, int number){
		try{
			server = new Server(Integer.parseInt(port), number);
			server.start();
			controller.connectClient(null);
		} catch (NumberFormatException e){
			System.err.println("fck, idiot.");
		}
	}
	
	public static void connectClient(String addr, String port, String name) {
		if (client == null){
			client = new Client(addr, Integer.parseInt(port), name, MenuLink());
			client.start();
		}else{
			closeConnections();
		}
		
	}
	
	public static boolean isServer(){
		return server != null;
	}
	public static void closeConnections(){
		try {
			if (client != null){
				client.stop();
				controller.connectionPack(false);
				if (server != null){
					server.stop();
				}
			}
		
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static Client getClient(){
		return client;
	}
	public static void sendMessage(String text) {
		if (client != null){
			try {
				client.sendMessage(text);
			} catch (JSONException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static void startGame(){
		server.startGame();
	}
	public static void toMenu(){
		closeConnections();
		MenuNavigator.setScene(0);
	}
	public static NetworkLink MenuLink(){
		return new NetworkLink(){
			@Override
			public void special(String key, Object value) {
				switch(key){
				case "server":
					controller.enableStartButton((boolean)value);
				}
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
	}

}
