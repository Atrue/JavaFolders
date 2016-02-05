package game.engine.characters;

import java.util.ArrayList;
import java.util.Iterator;

import game.engine.GameManager;
import game.engine.GameState;

public class Levels {
	private GameManager parent;
	
	private int levels = 10;
	private Iterator<Wave> iterWave;
	private Wave current;
	private double timeWaveOut = 1;
	public Levels(GameManager gm){
		parent = gm;
		ArrayList<Wave> waves = new ArrayList<>();
		for(int i=0;i<10;i++){
			waves.add(new Wave());
		}
		iterWave = waves.iterator();
	}
	public Wave getWave(){
		return current;
	}
	public void update(){
		if (current == null)
			return;
		if (current.isExist()){
			current.update(timeWaveOut);
			if (current.canBeGetting()){
				Monster monster = current.getMonster();
				parent.createMonster(monster);
			}
		}
		
	}
	public void nextWave(){
		if(iterWave.hasNext())
			current = iterWave.next();
		else
			System.out.println("END GAME");
	}
	class Wave{
		private int count = 10;
		private Monster copy;
		private double lastAdded = 0;
		
		
		Wave(){
			copy = new Monster(3);
		}
		public Monster get_copy(){
			return Monster.copy(copy);
		}
		public void update(double tostart){
			if (lastAdded > 0){
				lastAdded-= getTick();
				if (lastAdded < 0)
					lastAdded = 0;
			}else{
				lastAdded = tostart;
			}
		}
		public boolean isExist(){
			return count > 0;
		}
		public boolean canBeGetting(){
			return lastAdded == 0 && count > 0;
		}
		public int getLost(){
			return count;
		}
		public double getTick(){
			return 1./GameState.getFPS();
		}
		public Monster getMonster(){
			if (count > 0){
				count--;
				return get_copy();
			}else{
				System.out.println("empty wave");
				return null;
			}
		}
	}
}
