package game.engine;
import java.util.ArrayList;
import java.util.Iterator;

import game.engine.characters.Monster;
import game.engine.characters.Settings;
import game.engine.characters.Tower;
import game.network.ServerLink;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
public class Scheduler{
	private final int WAVE_TIME = 25;
	private ServerLink parent;
	private Timeline timeline;
	private double gametime = 0;
	public Scheduler(ServerLink manager, boolean light){
		this.parent = manager;
		KeyFrame frame;
		if (!light){
			frame = new KeyFrame(
	    			Duration.millis(1000 / Settings.getFPS()),
	    			tick -> tick()
	    			);
		}else{
			frame = new KeyFrame(
	    			Duration.millis(1000 / Settings.getFPS()),
	    			tick -> lightTick()
	    			);
		}
		timeline = new Timeline(frame);
		timeline.setCycleCount(Animation.INDEFINITE);
	}
	
	public double getTick(){
		return 1./Settings.getFPS();
	}
	public void notifyTimer(){
		gametime = WAVE_TIME;
	}
	public void lightTick(){
		gametime -= getTick();

		updateLocations();
		updateTowers();
		updateLabels();
	}
	private void tick(){
		 // Times each second
		gametime -= getTick();

		updateLevels();
		updateLocations();
		updateTowers();
		if (gametime <= 0){
			if (parent.s_getConfigurations().getLevels().hasNext()){
				parent.s_levelUp();
				gametime = WAVE_TIME;
			}else{
				if (parent.s_getConfigurations().getMonstersAlive().size() == 0)
					parent.s_endGame(true);
			}
		}
		updateLabels();
	}
	public int getGameTime(){
		return (int)gametime;
	}
	public void start(){
		if (!parent.s_getConfigurations().isStopped())
			timeline.play();
		else 
			System.err.println("Game is stopped");
	}
	public void stop(){
		timeline.stop();
	}
	
	private void updateLevels(){
		parent.s_getConfigurations().getLevels().update();
	}
	@SuppressWarnings("unchecked")
	private void updateLocations(){
		
		ArrayList<Monster> monsters = (ArrayList<Monster>) parent.s_getConfigurations().getMonstersAlive().clone();
		for (Iterator<Monster> iterator = monsters.iterator(); iterator.hasNext();) {
    		Monster monster = iterator.next();
    		monster.update();
    	}
    }
	private void updateTowers(){
		if(!parent.s_getConfigurations().getPlayerTowers().isEmpty()){
			for (Tower tower:parent.s_getConfigurations().getPlayerTowers()){
				tower.update();
			}
		}
	}
	private void updateLabels(){
		parent.s_updateLabels();
	
	}
}
