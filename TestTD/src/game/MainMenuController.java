package game;

import java.io.IOException;

import game.engine.GameManager;
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
            gameManager.initialize();}catch (IOException ex){ex.printStackTrace();}
        	MenuNavigator.newGame();
        	loadGameButton.setDisable(false);
    }
    public void continueGame(){
    	MenuNavigator.setScene(1);
    }
    public void exitGame(){
        System.exit(1);
    }
}