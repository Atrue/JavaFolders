package game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public final static int RESOLUTION_X = 1024;
    public final static int RESOLUTION_Y = 544;
    @Override
    public void start(Stage stage) throws Exception{
        stage.setTitle("Tower Defense");
        MenuNavigator.addScene(createScene(loadMainPane()), 0);
        //stage.setScene(mainScene);
        stage.setWidth(RESOLUTION_X);
        stage.setHeight(RESOLUTION_Y);
        stage.setResizable(false);
        stage.show();
        MenuNavigator.setStage(stage);
        stage.getIcons().add(new Image("game/engine/res/menu/icon/arr_up.png"));
        MenuNavigator.setScene(0);
    }

    /**
     * Loads the main fxml layout.
     * Sets up the vista switching VistaNavigator.
     * Loads the first vista into the fxml layout.
     */
    private Pane loadMainPane() throws IOException {
        FXMLLoader loader = new FXMLLoader();

        Pane mainPane = (Pane) loader.load(
                getClass().getResourceAsStream(
                        MenuNavigator.MAIN
                )
        );

        MainController mainController = loader.getController();

        MenuNavigator.setMainController(mainController);
        MenuNavigator.loadVista(MenuNavigator.MAINMENU);

        return mainPane;
    }

    //Creates the main application scene.
    private Scene createScene(Pane mainPane) {
        Scene scene = new Scene(
                mainPane
        );

        scene.getStylesheets().setAll(
                getClass().getResource("engine/res/menu/menustyle.css").toExternalForm()
        );

        return scene;
    }


    public static void main(String[] args) {
        launch(args);

    }
}
