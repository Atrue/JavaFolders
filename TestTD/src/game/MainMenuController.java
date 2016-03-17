package game;

import java.io.IOException;

import game.engine.GameManager;
import game.network.Network;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Created by jelly on 4/16/14.
 */
public class MainMenuController {
	@FXML
    private Button loadGameButton;
	
    public void startNewGame(){
        try{
            GameManager gameManager = new GameManager();
            gameManager.initialize(0);}catch (IOException ex){ex.printStackTrace();}
        	MenuNavigator.newGame();
        	loadGameButton.setDisable(false);
    }
    public void continueGame(){
    	MenuNavigator.setScene(1);
    }
    public void startNetwork() throws IOException{
    	//GameManager gameManager = new GameManager();
        //gameManager.initialize(1);
    	//loadGameButton.setDisable(true);
    	Network net = new Network();
    	net.init();
    }
    public void exitGame(){
        System.exit(1);
    }
}