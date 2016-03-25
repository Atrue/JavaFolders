package game.engine.characters;

import game.network.ServerLink;
import javafx.scene.paint.Color;

public class Buff{
	private int ownerID;
	private int damage;
	private double crit;
	private double slow;
	private double duration;
	private double lostion;
	private double chanse;
	
	private String sIcon;
	private String sDesc;
	private Monster target;
	
	private Color color;
	
	public Buff(){
	}
	public Buff(double dmg, double slow, double chanse, double duration, String sI){
		this.crit = dmg;
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
		to.crit = from.crit;
		to.damage = from.damage;
		to.duration = from.duration;
		to.chanse = from.chanse;
		to.lostion = from.duration;
		to.slow = from.slow;
		to.sIcon = from.sIcon;
		to.sDesc = from.sDesc;
		to.color = from.color;
		return to;
	}
	public Buff setA(double dmg, double slow, double chanse, double dur){
		this.crit = dmg;
		this.chanse = chanse > 1? 1: chanse;
		this.slow = slow >1? 1: slow;
		this.duration = dur;
		this.sDesc = labelString();
		return this;
	}
	public void setB(int dmg){
		this.damage = dmg;
		this.sDesc = labelString();
	}
	public void setOwnerID(int id){
		this.ownerID = id;
	}
	public int getOwnerId(){
		return ownerID;
	}
	public void setTarget(Monster monster){
		target = monster;
	}
	public void setSlow(double k){
		slow = k;
	}
	public void setDuration(double d){
		duration = d;
	}
	public void setColor(Color c){
		color = c;
	}
	
	public double getCrit(){
		return crit;
	}
	public double getTickDamage(){
		return damage * getTick()/duration;
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
	public Color getColor(){
		return color;
	}
    public double getTick(){
		return 1./ServerLink.getFPS();
	}
    public String getId(){
    	return "["+sIcon+"]";
    }
    public String getDesc(){
    	return sDesc;
    }
    
    public boolean getLuck(){
    	return Math.random() <= chanse;
    }
	public boolean update(){
		if(lostion > 0){
			
			target.takeDamage(getTickDamage(), ownerID);
			target.slow(slow);
			
			lostion -= getTick();
			return true;
		}else{
			return false;
		}
	}
	public String labelString(){
		
		
		String chs = " С шансом "+(int)(chanse*100)+"%";
		String dmg = " наносит доп.:"+(int)((crit)*100)+"%"+"("+(int)(damage*crit)+" урона)";
		String slw = " замедляет на :"+(int)(slow*100)+"%";
		String sec = " в теч. "+(int)duration+"с.";
		
		String fully = getId();
		fully += chanse < 1? chs: "";
		fully += crit > 0? dmg: "";
		fully += slow > 0? slw: "";
		fully += duration != getTick()? sec: "";
		return fully;
		
	}
}
