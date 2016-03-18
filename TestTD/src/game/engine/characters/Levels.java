package game.engine.characters;

import java.util.ArrayList;
import java.util.Iterator;

import game.engine.ServerLink;

public class Levels {
	private ServerLink parent;
	
	private int levels = 100;
	private Iterator<Wave> iterWave;
	private Wave current;
	private double timeWaveOut = 0.5;
	public Levels(ServerLink gm){
		parent = gm;
		ArrayList<Wave> waves = new ArrayList<>();
		for(int i=0;i<levels;i++){
			int typew = i > 0 && i%5==0? 1: 0;
			typew = (i-2)%5==0 && i >5 ?2:typew;
			Wave wave = new Wave(typew, (Math.pow(10,i*10./100)) );
			if ( (i+1) % 10 == 0)
				wave.toBOSS();
			waves.add(wave);
			System.out.print((i+1)+"- wave:");
			System.out.println("MaxHP:"+wave.copy.getMaxHP()+" type:"+wave.copy.getType()+" reward:"+wave.copy.getReward());
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
				parent.s_createMonsters(current.getMonster());
			}
		}
		
	}
	public boolean hasNext(){
		return iterWave.hasNext();
	}
	public void nextWave(){
		if(iterWave.hasNext()){
			current = iterWave.next();
			
		}else
			System.out.println("END GAME");
	}
	public class Wave{
		private int count = 10;
		private Monster copy;
		private double lastAdded = 0;
		
		
		Wave(int type, double koef){
			copy = Monster.copy(ListOfCharacters.getMonster(type));
			copy.powWith(koef);
		}
		private void toBOSS(){
			this.count = 1;
			copy.toBOSS();
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
			return 1./ServerLink.getFPS();
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
