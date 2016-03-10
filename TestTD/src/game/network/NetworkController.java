package game.network;


import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class NetworkController {

    @FXML
    private TextField inputSend;
    @FXML
    private TextField user1;
    @FXML
    private TextField user2;
    @FXML
    private TextField user3;
    @FXML
    private TextField user4;
    @FXML
    private Button kick1;
    @FXML
    private Button kick2;
    @FXML
    private Button kick3;
    @FXML
    private Button kick4;
    @FXML
    private TextArea areaMessages;

    @FXML
    private TextField inputPort;
    @FXML
    private ChoiceBox<Integer> selectCount;
    @FXML
    private TextField inputHost;
    private List<Button> kickList ;
    @FXML
    private TextField inputName;
    private List<TextField> userList ;
    @FXML
    private StackPane stackPane;
    @FXML
    private Button buttonStart;
    @FXML
    private Button buttonCreate;
    @FXML
    private Button buttonConnect;
    @FXML
    private ColorPicker userColor;
    
    @FXML
    void initialize() {
        assert user1 != null : "fx:id=\"user1\" was not injected: check your FXML file 'network.fxml'.";
        assert user2 != null : "fx:id=\"user2\" was not injected: check your FXML file 'network.fxml'.";
        assert buttonConnect != null : "fx:id=\"buttonConnect\" was not injected: check your FXML file 'network.fxml'.";
        assert buttonCreate != null : "fx:id=\"buttonCreate\" was not injected: check your FXML file 'network.fxml'.";
        assert areaMessages != null : "fx:id=\"areaMessages\" was not injected: check your FXML file 'network.fxml'.";
        assert buttonStart != null : "fx:id=\"buttonStart\" was not injected: check your FXML file 'network.fxml'.";
        assert inputHost != null : "fx:id=\"inputHost\" was not injected: check your FXML file 'network.fxml'.";
        assert stackPane != null : "fx:id=\"stackPane\" was not injected: check your FXML file 'network.fxml'.";
        assert inputName != null : "fx:id=\"inputName\" was not injected: check your FXML file 'network.fxml'.";
        assert user3 != null : "fx:id=\"user3\" was not injected: check your FXML file 'network.fxml'.";
        assert inputSend != null : "fx:id=\"inputSend\" was not injected: check your FXML file 'network.fxml'.";
        assert user4 != null : "fx:id=\"user4\" was not injected: check your FXML file 'network.fxml'.";
        assert inputPort != null : "fx:id=\"inputPort\" was not injected: check your FXML file 'network.fxml'.";
        assert userColor != null : "fx:id=\"userColor\" was not injected: check your FXML file 'network.fxml'.";
        assert kick3 != null : "fx:id=\"kick3\" was not injected: check your FXML file 'network.fxml'.";
        assert selectCount != null : "fx:id=\"selectCount\" was not injected: check your FXML file 'network.fxml'.";
        assert kick2 != null : "fx:id=\"kick2\" was not injected: check your FXML file 'network.fxml'.";
        assert kick4 != null : "fx:id=\"kick4\" was not injected: check your FXML file 'network.fxml'.";
        assert kick1 != null : "fx:id=\"kick1\" was not injected: check your FXML file 'network.fxml'.";
        
        selectCount.getItems().addAll(2,3);
        userList = new ArrayList<>();
    	userList.add(user1);
    	userList.add(user2);
    	userList.add(user3);
    	userList.add(user4);
    	kickList = new ArrayList<>();
    	kickList.add(kick1);
    	kickList.add(kick2);
    	kickList.add(kick3);
    	kickList.add(kick4);
    }
    
    @FXML
    void connectClient(ActionEvent event) {
    	connectionPack(true);
    	Network.connectClient(inputHost.getText(), inputPort.getText(), inputName.getText());
    	
    }

    @FXML
    void createServer(ActionEvent event) {
    	connectionPack(true);
    	Network.createServer(inputPort.getText(), selectCount.getValue());
    	
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
    
    public void connectionPack(Boolean b){
    	buttonCreate.setDisable(b);
    	buttonConnect.setText(b? "�����������": "������������");
    }
    
    public StackPane getStackPane(){
    	return stackPane;
    }
    
    public void enableStartButton(Boolean b){
    	buttonStart.setDisable(!b);
    }
    
    public void addUser(int id, String name){
    	TextField tf = userList.get(id);
    	tf.setText(name);
    	tf.setVisible(true);
    	if (Network.isServer()){
    		kickList.get(id).setDisable(false);
    		kickList.get(id).setVisible(true);
    	}
    }
    public void removeUser(int id){
    	TextField tf = userList.get(id);
    	tf.setText("");
    	tf.setVisible(false);
    	if (Network.isServer()){
    		kickList.get(id).setDisable(true);
    		kickList.get(id).setVisible(false);
    	}
    }

	public void putMessage(String string) {
		//areaMessages.getChildrenUnmodifiable().add(new Text(string));
		areaMessages.appendText(string+'\n');
	}

}
