package game.engine.characters;


import java.util.ArrayList;
import java.util.Iterator;

import game.engine.Coordinate;
import game.network.ServerLink;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Towers are used by the player as the primary weapon
 * to defeat monsters.
 *
 * Each tower spawns a worker thread to poll for nearby monsters
 * to make attacks on.
 */
public class Tower implements Target{
	
	//STATIC PANEL FOR DISPLAY TOWER
    private static Pane parentView;
    //Parent
    private ServerLink parent;
    
    
    //Information of owner
    private int ownerID;
    private String ownerName;
    
    //Tower stats
    private int levelTower;
	private int attackDamage;                       // Determines amount of health to reduce from monsters per attack
    private double attackSpeed;                     // Determines the time a tower must wait after an attack
    private double attackCD;
    private double timeout = 0;
    private int attackRange;                        // Sets the minimum range the tower can make attacks in
    private int upgradeTime = 1;                        // Time in milliseconds it takes to complete an upgrade.
    private int buyCost;
    private int sellCost;                           // Determines the resources gained by selling the tower
    private ArrayList<Projectile> projectileList;   // Used by the gui thread to create animations for attacks
    private Coordinate coords;                      // Represents the coordinates of the tower on the map
    private Monster target;
    private Buff buff;
	private int typeTower;
    
	//States
	private boolean isGUI; // false = server
	private boolean hasActivity; // false = connected client
	
	
    //GUI Elements
	private TextFlow view; // main view
	private Text tName;
	private Text tLevel;
	private Circle targetArea;
	private Color color;

    /**
     * GLOBAL METHODS
     */
    public Tower(){
    	  	
    }
    public static Tower hoverTower(){
    	Tower t = new Tower();
    	t.view = new TextFlow();
    	t.tName = new Text();
    	t.tLevel = new Text();
    	t.view.getChildren().addAll(t.tName, t.tLevel);
    	return t;
    }
    
    public Tower(int attack, double AS, double asCD, int range, int buycost, int level, int type, Color colors, Buff b){
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
    	parentView = v;
    }
    
    /**
     * MAIN METODS
     */
    public void add(int x , int y, ServerLink par, boolean visible, boolean activity){
    	projectileList = new ArrayList<Projectile>();  
    	coords = new Coordinate(x , y);
    	parent = par;
    	isGUI = visible;
    	hasActivity = activity;
    	
    	if (isGUI){
    		addGUI();
    	}
    }
    public void addGUI(){
    	view = new TextFlow();
    	tName = new Text();
    	tLevel = new Text();
    	view.getChildren().addAll(tName, tLevel);
    	
    	updateLabels();
    	if(buff != null)
    		buff.setColor(color);
    	
    	//main view
        view.setId("tower_state");	        
        view.setPrefWidth(32);  
        view.setPrefHeight(32); 
        view.setLayoutX(coords.getExactX()-16);
        view.setLayoutY(coords.getExactY()-16);
        
        view.setOnMouseClicked(ActionEvent -> parent.s_setTarget(this));
        
        
        //area of selected tower
    	targetArea = new Circle(getX(), getY(), attackRange);
    	targetArea.setStroke(color);
    	targetArea.setStrokeDashOffset(5);
    	targetArea.setVisible(false);
    	targetArea.setFill(Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.2));
		
    	//adding
    	parentView.getChildren().add(targetArea);
    	parentView.getChildren().add(view);
    }
    public void setOwner(int id){
    	ownerID = id;
    	
    	if (isGUI){
	    	view.getStyleClass().add("towerBG_"+id);
	    	if (buff != null){
	    		buff.setOwnerID(ownerID);
	    	}
    	}
    }
    public void remove(){
    	for(Projectile prj:projectileList){
    		prj.remove();
    	}
    	if (isGUI){
    		parentView.getChildren().remove(this);
    		view.setVisible(false);
    	}
    }
    
    /**
     * CODE ONLY
     */
    
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
		return 1./ServerLink.getFPS();
	}
    public void update(){
    	if (timeout > 0){
    		timeout = timeout - getTick() > 0? timeout - getTick():0;
    	}else{
	    	for (Monster monster:parent.s_getConfigurations().getMonstersAlive()){
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
    			if(buff != null && projectile.getTarget() != null && buff.getLuck()){
    				if (hasActivity){
    					projectile.getTarget().addBuff(buff);
    					parent.s_addBuff(projectile.getTarget().getID(), getTileX(), getTileY());
    				}
        		}
    			projectile.remove();
    	        iterator.remove();
    	    }
    	}
    }
    public void createProjectile(Monster target){
    	Projectile proj = new Projectile(this, target, isGUI);
    	proj.add();
        projectileList.add(proj);
    }
    
    /**
     * Upgrades the towers stats.
     */
    public boolean isUpgradeable(){
    	return Settings.isTowerExist(typeTower, levelTower);
    }
    public int upgradePrice(){
    	if (isUpgradeable()){
    		return Settings.getTower(typeTower, levelTower).getPrice();
    	}
    	return 0;
    }
    
    public void upgradeTower(){
    	if (isUpgradeable()){
    		Tower up = Settings.getTower(typeTower, levelTower);
    		attackDamage = up.attackDamage;
    		attackSpeed = up.attackSpeed;
    		attackRange = up.attackRange;
    		attackCD = up.attackCD;
    		buyCost = up.buyCost;
    		sellCost += buyCost/2;
    		targetArea.setRadius(attackRange);
    		levelTower++;
    		buff = Buff.copy(up.buff);
    		if(buff != null)
        		buff.setColor(color);
    		updateLabels();
    	}
    }


    
    /**
     * GUI METHODS
     */
    
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
     * GETTERS AND SETTERS
     * @return 
     */
    public TextFlow getView(){
    	return view;
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
		return levelTower;
	}
    public int getType() {
		return typeTower;
	}
    
    public Buff getBuff(){
    	return buff;
    }
    
    public void setGUIlable(boolean b){
    	this.isGUI = b;
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
	
	/**
	 * AS TARGET
	 * @see game.engine.characters.Target
	 */
	
	@Override
	public void activeTarget() {
		targetArea.setVisible(true);
	}
	@Override
	public void deactiveTarget() {
		targetArea.setVisible(false);
	}
	@Override
	public String[] getFullInfo() {
		// TODO Auto-generated method stub
		String[] d = new String[] {
				"X["+(levelTower+1)+"]",
				buff != null? buff.getDesc(): "",
				"F(x)",
				String.valueOf(attackDamage),
				"R",
				String.valueOf(attackRange),
				"μ",
				String.format( "%.2f", attackCD),
				
		};
		return d;
	}
	@Override
	public int getUpgradePrice(){
		return upgradePrice();
	}
	@Override
	public int getSellPrice(){
		return sellCost;
	}
	@Override
	public boolean tEquals(Target obj) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public String getTargetID() {
		return "T"+getTileX()+"X"+getTileY();
	}


}
