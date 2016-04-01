package game.engine.characters;

public class Resourse {
	private int money;
	private int paint;
	
	Resourse(){
		this.money = 0;
		this.paint = 0;
	}
	
	Resourse(int m){
		this.money = m;
		this.paint = 0;
	}
	
	Resourse(int m, int p){
		this.money = m;
		this.paint = p;
	}
	
	public void plus(Resourse r){
		this.money += r.money;
		this.paint += r.paint;
	}
	
	public void minus(Resourse r){
		this.money -= r.money;
		this.paint -= r.paint;
	}
	
	public boolean transaction(Resourse r){
		return this.money >= r.money && this.paint >= r.paint;
	}

}
