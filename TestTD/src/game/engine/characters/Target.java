package game.engine.characters;

public interface Target {
	public void activeTarget();
	public void deactiveTarget();
	public String[] getFullInfo(); 
	public int getUpgradePrice();
	public int getSellPrice();
	public boolean tEquals(Target obj);
	public String getTargetID();
	
}
