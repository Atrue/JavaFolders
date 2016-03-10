package game.engine.characters;


import java.util.ArrayList;
import java.util.Iterator;

import game.engine.Coordinate;
import game.engine.State;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Towers are used by the player as the primary weapon
 * to defeat monsters.
 *
 * Each tower spawns a worker thread to poll for nearby monsters
 * to make attacks on.
 */
public class Tower extends TextFlow{
    private static Pane view;
    private int ownerID;
    private String ownerName;
    private State parent;
    
	private Text tName;
	private Text tLevel;
	private Text tBuff;
	private boolean isGUI = false;
	private int levelTower;
	private int attackDamage;                       // Determines amount of health to reduce from monsters per attack
    private double attackSpeed;                     // Determines the time a tower must wait after an attack
    private double attackCD = 1.0;
    private double timeout = 0;
    private int attackRange;                        // Sets the minimum range the tower can make attacks in
    private int upgradeTime;                        // Time in milliseconds it takes to complete an upgrade.
    private int buyCost;
    private int sellCost;                           // Determines the resources gained by selling the tower
    private ArrayList<Projectile> projectileList;   // Used by the gui thread to create animations for attacks
    private Coordinate coords;                      // Represents the coordinates of the tower on the map
    private Monster target;
    private Buff buff;

	private int typeTower;

	private Color color;
    //private TowerAttackerService towerAttacker;     // A worker thread for polling monster locations used for attacks


    /**
     * All towers are created with basic stats which are scaled with
     * upgrades.
     *
     * @param x
     * The x location on the map where the tower is placed.
     * @param y
     * The y location on the map where the tower is placed.
     */
    public Tower(){
    	super();
    	projectileList = new ArrayList<Projectile>();
    	tName = new Text();
    	tLevel = new Text();
    	 
    	getChildren().addAll(tName, tLevel);
    }
    public Tower(int attack, double AS, double asCD, int range, int buycost, int level, int type, Color colors, Buff b){
    	super();
    	
    	attackDamage = attack;
    	attackSpeed = AS;
    	attackRange = range;
    	attackCD = asCD;
    	buyCost = buycost;
    	sellCost = buycost/2;
    	levelTower = level;
    	typeTower = type;
    	color = colors;
    	buff = Buff.copy(b);
    	if (buff != null)
    		buff.setB(attackDamage);
    }
    public static Tower copy(Tower from){
    	Tower to = new Tower();
    	to.attackDamage = from.attackDamage;
    	to.attackSpeed = from.attackSpeed;
    	to.attackRange = from.attackRange;
    	to.attackCD = from.attackCD;
    	to.buyCost = from.buyCost;
    	to.sellCost = from.sellCost;
    	to.levelTower = from.levelTower;
    	to.color = from.color;
    	to.typeTower = from.typeTower;
    	to.buff = Buff.copy(from.buff);
    	return to;
    }
    public static void setParentView(Pane v){
    	view = v;
    }
    public void add(int x , int y, State par, boolean visible){
    	coords = new Coordinate(x , y);
    	parent = par;
    	isGUI = visible;
    	if (isGUI){
	    	updateLabels();
	        setId("tower_state");
	        setPrefWidth(32);  
	        setPrefHeight(32); 
	        setHeight(32);
	        setWidth(32);
	    	setLayoutX(coords.getExactX()-16);
	    	setLayoutY(coords.getExactY()-16); 
	    	view.getChildren().add(this);
    	}
    }
    public void setOwner(int id, String name){
    	ownerID = id;
    	ownerName = name;
    }
    public void remove(){
    	for(Projectile prj:projectileList){
    		prj.remove();
    	}
    	if (isGUI){
    		view.getChildren().remove(this);
    		setVisible(false);
    	}
    }
    public boolean inRange(Monster target){
    	double x2 = target.getX();
    	double y2 = target.getY();
    		
    	if ( Math.pow(x2 - getX(), 2) + Math.pow(y2 - getY(), 2) <= Math.pow(attackRange, 2)){
    		return true;
    	}

    	return false;
    }
    public boolean isReady(){
    	return this.timeout == 0;
    }
    public double getTick(){
		return 1./State.getFPS();
	}
    public void update(){
    	if (timeout > 0){
    		timeout = timeout - getTick() > 0? timeout - getTick():0;
    	}else{
	    	for (Monster monster:parent.getMonstersAlive()){
	    		if (inRange(monster)){
	    			createProjectile(monster);
	    			timeout = attackCD;
	    			break;
	    		}
	    	}
    	} 	
    	for (Iterator<Projectile> iterator = projectileList.iterator(); iterator.hasNext();) {
    		Projectile projectile = iterator.next();
    		if (!projectile.update()){
    			projectile.remove();
    	        iterator.remove();
    	    }
    	}
    }
    
    public void updateLabels(){
    	if(isGUI){
	    	tName.setText("Y");
	    	String style = "-fx-font-size:25; -fx-text-alignment:center; ";
	    	if (levelTower > 3)
	    		style += " -fx-font-weight:bold;";
	    	tName.setFill(color);
	        tName.setStyle(style);
	        tLevel.setText(levelTower > 1? String.valueOf(levelTower): "");
	        tLevel.setStyle("-fx-font-size:10; -fx-text-alignment:center");
    	}
    }
    /**
     * Upgrades the towers stats.
     */
    public boolean isUpgradeable(){
    	return ListOfCharacters.isTowerExist(typeTower, levelTower);
    }
    public int upgradePrice(){
    	if (isUpgradeable()){
    		return ListOfCharacters.getTower(typeTower, levelTower).getPrice();
    	}
    	return 0;
    }
    
    public void upgradeTower(){
    	if (isUpgradeable()){
    		Tower up = ListOfCharacters.getTower(typeTower, levelTower);
    		attackDamage = up.attackDamage;
    		attackSpeed = up.attackSpeed;
    		attackRange = up.attackRange;
    		attackCD = up.attackCD;
    		buyCost = up.buyCost;
    		sellCost += buyCost/2;
    		levelTower++;
    		buff = Buff.copy(up.buff);
    		updateLabels();
    	}
    }

    /**
     * Creates a projectile when the tower attacks a monster.
     *
     * @param target
     * The target location of the projectile
     */
    public void createProjectile(Monster target){
    	Projectile proj = new Projectile(this, target, isGUI);
    	proj.add();
        projectileList.add(proj);
    }

    public Text[] getText(){
    	return new Text[]{tName, tLevel}.clone();
    }
    public int getTileX(){
    	return coords.getTileX();
    }
    public int getTileY(){
    	return coords.getTileY();
    }
    public int getX(){
        return coords.getExactX();
    }

    public int getY(){
        return coords.getExactY();
    }

    public int getAttackRange(){
        return attackRange;
    }

    public int getAttackDamage(){
        return  attackDamage;
    }

    public double getAttackSpeed(){
        return attackSpeed;
    }
    
    public double getAttackCD(){
        return attackCD;
    }
    
    public int getSellCost(){
        return sellCost;
    }

    public int getUpgradeTime(){
        return upgradeTime;
    }

    public int getOwnerID(){
    	return ownerID;
    }
    
    public String getOwnerName(){
    	return ownerName;
    }
    
    public Monster getTowerAttacker() {
        return target;
    }

    public ArrayList<Projectile> getProjectileList() {
        return projectileList;
    }

    public Coordinate getCoords(){
        return coords;
    }
    public int getLevel() {
		// TODO Auto-generated method stub
		return levelTower;
	}
    public int getType() {
		// TODO Auto-generated method stub
		return typeTower;
	}
    
    
    public Buff getBuff(){
    	return buff;
    }
    public void setAttackDamage(int attackDamage){
        this.attackDamage = attackDamage;
    }

    public void setAttackRange(int attackRange) {
        this.attackRange = attackRange;
    }

    public void setAttackSpeed(double attackSpeed) {
        this.attackSpeed = attackSpeed;
    }
    public void setText(Text[] texts){
    	//getChildren().removeAll(tName, tLevel);
    	tName.setText(texts[0].getText());
    	tName.setStyle(texts[0].getStyle());
    	tLevel.setText(texts[1].getText());
    	tLevel.setStyle(texts[1].getStyle());
    	//getChildren().addAll(tName, tLevel);
    }
    public void setColor(Color c){
    	color = c;
    }
    public Color getColor(){
    	return color;
    }
	public int getPrice() {
		// TODO Auto-generated method stub
		return buyCost;
	}
	public void setLevel(int level) {
		// TODO Auto-generated method stub
		this.levelTower = level;
	}
	public void setType(int type){
		this.typeTower = type;
	}


}
