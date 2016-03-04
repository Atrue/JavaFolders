package game.network;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class NetworkController {

    @FXML
    private TextField inputSend;

    @FXML
    private TextArea areaMessages;

    @FXML
    private TextField inputPort;

    @FXML
    private TextField inputHost;

    @FXML
    private TextField inputName;

    @FXML
    private StackPane stackPane;
    
    @FXML
    private VBox connectedUsers;
    @FXML
    private ColorPicker userColor;
    
    
    @FXML
    void connectClient(ActionEvent event) {
    	Network.connectClient(inputHost.getText(), inputPort.getText(), inputName.getText());
    }

    @FXML
    void createServer(ActionEvent event) {
    	Network.createServer(inputPort.getText());
    }

    @FXML
    void sendMessage(ActionEvent event) {
    	Network.sendMessage(inputSend.getText());
    	inputSend.setText("");
    }
    @FXML
    void startGame(ActionEvent event){
    	Network.startGame();
    }
    @FXML
    void toMenu(ActionEvent event){
    	Network.toMenu();
    }
    public StackPane getStackPane(){
    	return stackPane;
    }
    public void addUser(String name, Color color){
    	Label user = new Label(name);
    	user.setTextFill(color);
    	connectedUsers.getChildren().add(user);
    }

	public void putMessage(String string) {
		//areaMessages.getChildrenUnmodifiable().add(new Text(string));
		areaMessages.appendText(string+'\n');
	}

}
