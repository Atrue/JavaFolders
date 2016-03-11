package game.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;

import game.engine.GameState;
import game.engine.State;
import game.engine.characters.Monster;
import game.engine.characters.Tower;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

class ServerScheduler{
	private final int WAVE_TIME = 25;
	private NetworkState parent;
	private Timeline timeline;
	private double gametime = 0;
	public ServerScheduler(NetworkState parent){
		this.parent = parent;
		timeline = new Timeline(new KeyFrame(
    			Duration.millis(1000 / State.getFPS()),
    			tick -> tick()
    			));
		timeline.setCycleCount(Animation.INDEFINITE);
	}
	
	public double getTick(){
		return 1./State.getFPS();
	}
	private void tick(){
		 // Times each second
		gametime -= getTick();

		updateLocations();
		updateTowers();
		
		if (gametime <= 0){
			if (parent.getLevels().hasNext()){
				parent.levelUp();
				
				parent.setLevel(parent.getLevel() + 1);
				parent.getLevels().nextWave();
				gametime = WAVE_TIME;
			}else{
				if (parent.getMonstersAlive().size() == 0)
					parent.endGame(true);
			}
		}
		try {
			parent.sendTick();
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int getGameTime(){
		return (int)gametime;
	}
	public void start(){
		if (!parent.isStopped())
			timeline.play();
		else 
			System.err.println("Game is ended(SERVER) ");
	}
	public void stop(){
		timeline.stop();
	}
	@SuppressWarnings("unchecked")
	private void updateLocations(){
		parent.getLevels().update();
		
		ArrayList<Monster> monsters = (ArrayList<Monster>) parent.getMonstersAlive().clone();
		for (Iterator<Monster> iterator = monsters.iterator(); iterator.hasNext();) {
    		Monster monster = iterator.next();
    		monster.update();
    	}
    }
	private void updateTowers(){
		if(!parent.getPlayerTowers().isEmpty()){
			for (Tower tower:parent.getPlayerTowers()){
				tower.update();
			}
		}
	}
}