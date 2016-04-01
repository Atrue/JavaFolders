package game.engine.characters;

import game.engine.Coordinate;
import game.network.ServerLink;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

public class Ward implements Target{
	//STATIC PANEL FOR DISPLAY TOWER
    private static Pane parentView;
    
	private ServerLink parent;
	private Coordinate coord;
	private double maxHP;
	private double curHP;
	private WardReward reward;
	private double liveTime;
	private double respawnTime;
	private double liveLost;
	private double respawnLost;
	private Label view;
	
	Ward(ServerLink parent){
		this.parent = parent;
		// respawn beetween 4 and 8 min
		this.respawnTime = 4*60 + Math.random()*4*60;
		this.respawnLost = this.respawnTime;
	}
	
	public static void setParentView(Pane v){
    	parentView = v;
    }
	
	public void add(){
		int lX = parent.s_getConfigurations().getMap().length;
		int lY = parent.s_getConfigurations().getMap()[lX].length;
		int rX = 0;
		int rY = 0;
		
		
		do{
			rX = (int)(Math.random()*lX);
			rY = (int)(Math.random()*lY);
		}while(parent.s_getConfigurations().nodeOpen(rX, rY));
		
		this.coord = new Coordinate(rX, rY);
		parent.s_getConfigurations().setMapNode(rX, rY, 5);
		addView();
	}
	
	
	public void lightAdd(int x, int y){
		this.respawnLost = 0;
		this.coord = new Coordinate(x, y);
		addView();
	}
	
	private void addView(){
		if(!parent.s_isGUI())
			return;
		
		view = new Label("#");       
        view.setStyle("-fx-font-weight:bold;");
        view.setFont(new Font(30));
        view.setTextFill(Color.color(0, 1, 0));
        view.setPrefHeight(32);
        view.setPrefWidth(32);        
        view.setOnMouseClicked(ActionEvent -> parent.s_setTarget(this));    	
    	parentView.getChildren().add(view);
		
	}
	
	public void update(){
		if (this.respawnLost > 0){
			this.respawnLost -= getTick();
		}else{
			parent.s_addWard(this);
		}
	}
	
	private double getTick(){
		return 1./Settings.getFPS();
	}
	
	private class WardReward{
		private int money;
		private int helps;
	}

	@Override
	public void activeTarget() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deactiveTarget() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getFullInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getUpgradePrice() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSellPrice() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean tEquals(Target obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getTargetID() {
		// TODO Auto-generated method stub
		return null;
	}
}
