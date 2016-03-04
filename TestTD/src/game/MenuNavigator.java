package game;

import java.io.IOException;
import java.net.URL;

import game.engine.GameManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Utility class for controlling navigation between vistas.
 *
 * All methods on the navigator are static to facilitate
 * simple access from anywhere in the application.
 */
public class MenuNavigator {

    /**
     * Convenience constants for fxml layouts managed by the navigator.
     */
    public static final String MAIN    = "engine/res/menu/holder.fxml";
    public static final String MAINMENU = "engine/res/menu/mainmenu.fxml";
    public static final URL GAMEUI = GameManager.class.getResource("res/menu/gameui.fxml");
    private static Scene[] activeScenes = new Scene[3];
    //stage for game
    public static Stage stage;

    public static void setStage(Stage stage1){
        stage = stage1;
    }
    public static void addScene(Scene st, int pos){
    	activeScenes[pos] = st;
    }
    public static void setScene(int pos){
    	if ( activeScenes[pos] != null ){
    		stage.setScene(activeScenes[pos]);
    	}else{
    		System.err.println("HAS NO SCENE "+pos+". OPEN MENU");
    	}
    	
    }
    public static void setResultGame(boolean b){
    	mainController.setMessage(b? "NOTBAD. YOU WON!" : "BAD. YOU LOSE!");
    }
    /** The main application layout controller. */
    private static MainController mainController;

    /**
     * Stores the main controller for later use in navigation tasks.
     *
     * @param mainController the main application layout controller.
     */
    public static void setMainController(MainController mainController) {
        MenuNavigator.mainController = mainController;
    }

    /**
     * Loads the vista specified by the fxml file into the
     * vistaHolder pane of the main application layout.
     *
     * Previously loaded vista for the same fxml file are not cached.
     * The fxml is loaded anew and a new vista node hierarchy generated
     * every time this method is invoked.
     *
     * A more sophisticated load function could potentially add some
     * enhancements or optimizations, for example:
     *   cache FXMLLoaders
     *   cache loaded vista nodes, so they can be recalled or reused
     *   allow a user to specify vista node reuse or new creation
     *   allow back and forward history like a browser
     *
     * @param fxml the fxml file to be loaded.
     */
    public static void loadVista(String fxml) {
        try {
            mainController.setVista((Node)(FXMLLoader.load((MenuNavigator.class.getResource(fxml)))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	public static void newGame() {
		mainController.setMessage("");
		
	}

}