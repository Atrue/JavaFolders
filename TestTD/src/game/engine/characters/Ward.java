package game.engine.characters;

import game.engine.Coordinate;
import javafx.scene.control.Label;

public class Ward {
	private Coordinate coord;
	private double maxHP;
	private double curHP;
	private WardReward reward;
	private double liveTime;
	private double respawnTime;
	private double liveLost;
	private double respawnLost;
	private Label view;
	
	Ward(){
		this.respawnTime =98;
	}
	
	private class WardReward{
		private int money;
		private int helps;
	}
}
