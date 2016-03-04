package game.network;

import java.io.IOException;
import java.net.URL;

import javax.swing.SwingUtilities;

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
	public static void createServer(String port){
		server = new Server(Integer.parseInt(port));
		controller.connectClient(null);
	}
	
	public static void connectClient(String addr, String port, String name) {
		client = new Client(addr, Integer.parseInt(port), name, MenuLink());
		client.start();
		
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
		try {
			server.stop();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MenuNavigator.setScene(0);
	}
	public static NetworkLink MenuLink(){
		return new NetworkLink(){
			@Override
			public String get() {
				return null;
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
			public void users(String string, boolean b) {
				Platform.runLater(new Runnable() {
				    public void run() {
				    	controller.addUser(string, Color.BLUEVIOLET);
				    }
				});
			}
			@Override
			public void start(JSONObject object) {
				Platform.runLater(new Runnable() {
				    public void run() {
				    	try {
							GameManager game = new GameManager();
							game.initialize(1);
							game.setClient(client);
							game.setConfig(object);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
				});
			}
			@Override
			public void tower(int x, int y, int type, boolean what) {
				// TODO Auto-generated method stub
				
			}
		};
	}

}
