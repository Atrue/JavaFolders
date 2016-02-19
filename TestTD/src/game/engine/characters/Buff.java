package game.engine.characters;

import game.engine.GameState;
import javafx.scene.text.Text;

public class Buff{
	private double damage;
	private double slow;
	private double duration;
	private double lostion;
	private double chanse;
	
	private String sIcon;
	private String sDesc;
	private Monster target;
	
	public Buff(){
	}
	public Buff(double dmg, double slow, double chanse, double duration, String sI){
		this.damage = dmg/duration;
		this.slow = slow;
		this.chanse = chanse;
		this.duration = duration;
		this.lostion = this.duration;
		this.sIcon = sI;
		this.sDesc = labelString();
	}
	public static Buff copy(Buff from){
		if (from == null)
			return null;
		Buff to = new Buff();
		to.damage = from.damage;
		to.duration = from.duration;
		to.chanse = from.chanse;
		to.lostion = from.duration;
		to.slow = from.slow;
		to.sIcon = from.sIcon;
		to.sDesc = from.sDesc;
		return to;
	}
	public Buff setA(double dmg, double slow, double chanse, double dur){
		this.damage = dmg/dur;
		this.chanse = chanse > 1? 1: chanse;
		this.slow = slow >1? 1: slow;
		this.duration = dur;
		this.sDesc = labelString();
		return this;
	}
	public void setB(double dmgK){
		this.damage = damage*dmgK;
		this.sDesc = labelString();
	}
	
	public void setTarget(Monster monster){
		target = monster;
	}
	public void setFullDamage(double dmg){
		damage = dmg/duration;
	}
	public void setSlow(double k){
		slow = k;
	}
	public void setDuration(double d){
		duration = d;
	}
	
	public double getFullDamage(){
		return damage*duration;
	}
	public double getTickDamage(){
		return damage;
	}
	public double getDuration(){
		return duration;
	}
	public double getSlow(){
		return slow;
	}
	public double getChanse(){
		return chanse;
	}
	
    public double getTick(){
		return 1./GameState.getFPS();
	}
    public String getId(){
    	return sIcon;
    }
    public String getDesc(){
    	return sDesc;
    }
    
    public boolean getLuck(){
    	return Math.random() <= chanse;
    }
	public boolean update(){
		if(lostion > 0){
			
			target.takeDamage(damage * getTick());
			target.slow(slow);
			
			lostion -= getTick();
			return true;
		}else{
			return false;
		}
	}
	public String labelString(){
		
		
		String chs = " have chanse "+(int)(chanse*100)+"% to";
		String dmg = " deal "+(int)(damage*duration)+" damage";
		String slw = " slow on "+(int)(slow*100)+"%";
		String sec = " for "+(int)duration+" seconds";
		
		String fully = "The attack";
		fully += chanse < 1? chs: "";
		fully += damage > 0
					?slow > 0? dmg+" and"+slw: dmg
					:slow > 0? slw: "";
		fully += duration != getTick()? sec: "";
		return fully;
		
	}
}
