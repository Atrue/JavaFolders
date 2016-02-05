package game.engine;
import java.util.ArrayList;
import java.util.Iterator;

import game.engine.characters.Monster;
import game.engine.characters.Tower;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
public class Scheduler{
	private GameManager parent;
	private Timeline timeline;
	private double gametime = 5;
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
			parent.levelUp();
			gametime = 30;
		}
	}
	public int getGameTime(){
		return (int)gametime;
	}
	public void start(){
		timeline.play();
	}
	public void stop(){
		timeline.stop();
	}
	
	
	private void updateLocations(){
		GameState.getLevels().update();
        if(!GameState.getMonstersAlive().isEmpty()){
        	ArrayList<Monster> m = new ArrayList<>();
            Iterator<Monster> monsters = GameState.getMonstersAlive().iterator();
            Monster monster;
            while(monsters.hasNext()) {
                monster = monsters.next();
                monster.updateLocation(1);
                if(monster.isPathFinished()){
                	m.add(monster);
                	//removeMonster(monster);
                }
            }
            for(Monster mo:m){
            	parent.removeMonster(mo);
            }
            
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
        parent.getController().updateLabels(
            Integer.toString(GameState.getLevel()) ,
            Integer.toString(GameState.getLives()) ,
            Integer.toString(GameState.getResources()) ,
            Integer.toString(GameState.getScore()) ,
            Integer.toString(getGameTime())
    );
        if (GameState.getTarget() != null){
        	parent.getController().updateTarget(
            	Integer.toString(GameState.getTarget().getAttackDamage()),
                Integer.toString(GameState.getTarget().getAttackRange()),
                Double.toString(GameState.getTarget().getAttackSpeed())
            );
        }
}
}
