package game.engine;
import java.util.ArrayList;
import java.util.Iterator;

import game.engine.characters.Monster;
import game.engine.characters.Projectile;
import game.engine.characters.Tower;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
public class Scheduler{
	private final int WAVE_TIME = 25;
	private GameManager parent;
	private Timeline timeline;
	private double gametime = 0;
	public Scheduler(GameManager manager){
		this.parent = manager;
		timeline = new Timeline(new KeyFrame(
    			Duration.millis(1000 / GameState.getFPS()),
    			tick -> tick()
    			));
		timeline.setCycleCount(Animation.INDEFINITE);
	}
	public double getTick(){
		return 1./GameState.getFPS();
	}
	private void tick(){
		 // Times each second
		gametime -= getTick();

		updateLocations();
		updateTowers();
		updateLabels();
		if (gametime <= 0){
			if (GameState.getLevels().hasNext()){
				parent.levelUp();
				gametime = WAVE_TIME;
			}else{
				if (GameState.getMonstersAlive().size() == 0)
					parent.endGame(true);
			}
		}
	}
	public int getGameTime(){
		return (int)gametime;
	}
	public void start(){
		if (GameState.isPaused())
			timeline.play();
		else 
			System.err.println("Game is in state "+GameState.getState());
	}
	public void stop(){
		timeline.stop();
	}
	
	
	@SuppressWarnings("unchecked")
	private void updateLocations(){
		GameState.getLevels().update();
		
		ArrayList<Monster> monsters = (ArrayList<Monster>) GameState.getMonstersAlive().clone();
		for (Iterator<Monster> iterator = monsters.iterator(); iterator.hasNext();) {
    		Monster monster = iterator.next();
    		monster.update();
    	}
    }
	private void updateTowers(){
		if(!GameState.getPlayerTowers().isEmpty()){
			for (Tower tower:GameState.getPlayerTowers()){
				tower.update();
			}
		}
	}
	private void updateLabels(){
		parent.updateLabels();
	
	}
}
