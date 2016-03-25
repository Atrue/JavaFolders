package game.engine.characters;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Monsters are the enemies of the player. They're job is to traverse
 * the path. They are created during timed intervals and removed when
 * their healthPoints is zero or they reach the end point of the path.
 */

import game.engine.Coordinate;
import game.network.ServerLink;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

public class Monster implements Target{
	//STATIC PANEL FOR DISPLAY TOWER
    private static Pane parentView;
    //Parent
    private ServerLink parent;
    
    //Monster stats
    private boolean isBOSS = false;
    
    private boolean isDied = false;
    
    private int ID;
	
	private ArrayList<Buff> buffList = new ArrayList<>();
    private Label view;                        // Graphical view of monster
    //private Rectangle HPview;
    
    private int type;
    private double maxHP;
    private double curHP;                   // Determines if the monster is still alive
    private double maxSpeed;                  // Determines time to complete path
    private double curSpeed;
    private double reward;                         // Monster death will trigger a resource reward
    
    private double x;
    private double y;
    private double vX = 0;
    private double vY = 0;
	private boolean hasDirection;
	private Coordinate nextPoint;
	private double[] effectColor;
	private Circle targetArea;
    
	//STATS
    private boolean isGUI;
    private boolean hasActivity;
    

    /**
     * GLOBAL METHODS
     */
    public Monster(double hp, double speed, double kGold, int t){
        maxHP = hp;
        curHP = maxHP;
        maxSpeed = speed;
        curSpeed = maxSpeed;
        type = t;
        
        reward = kGold;
        
    }
    public static void setParentView(Pane v){
    	parentView = v;
    }
    public static Monster copy(Monster from){
    	Monster to = new Monster(from.maxHP, from.maxSpeed, from.reward, from.type);
    	to.isBOSS = from.isBOSS;
    	return to;
    }
    public void add(Coordinate start, ServerLink pa, boolean visible, boolean activ){
        isGUI = visible;
    	hasActivity = activ;
    	parent = pa;
    	if (isGUI){
    		addGUI();
    	}
    	setX(start.getExactX() - 16);
        setY(start.getExactY());
    }
    private void addGUI(){
    	view = new Label("z");
        String style = "";
        if (type == 1){
        	style = "-fx-font-style:italic;";
        }else if(type == 2 && !isBOSS){
        	style = "-fx-font-weight:bold;";
        }
        int kSize = 1;
        if (isBOSS){
        	style += "-fx-font-weight:bold;";
        	kSize = 2;
        }
        
        
        view.setStyle(style);
        view.setFont(new Font(30 * kSize));
        view.setTextFill(Color.color(0, 1, 0));
        view.setPrefHeight(32 * kSize);
        view.setPrefWidth(32 * kSize);        
        
        effectColor = new double[]{0,0,0};
        
        view.setOnMouseClicked(ActionEvent -> parent.s_setTarget(this));
        
        targetArea = new Circle(x, y, 10*kSize);
    	targetArea.setStroke(Color.BROWN);
    	targetArea.setStrokeDashOffset(5);
    	targetArea.setVisible(false);
    	targetArea.setFill(Color.color(0.5,0,0,0.2));
        
        
    	
    	parentView.getChildren().add(view);
    	parentView.getChildren().add(targetArea);
    }
    public void addVariancy(){
    	vX = Math.random()*30 - 15;
    	vY = Math.random()*30 - 15;
    }
    public void addVariancy(double vx, double vy, int id){
    	vX = vx;
    	vY = vy;
    	ID = id;
    }
    public void remove(boolean isKilled, int who){
    	if(isDied)
    		return;
    	
    	if(isGUI){
    		parentView.getChildren().remove(view);
    		parentView.getChildren().remove(targetArea);
    		//parentView.getChildren().remove(HPview);
    		view.setVisible(false);
    		//HPview.setVisible(false);
    		targetArea.setVisible(false);
    		if(parent.s_getTarget() != null && parent.s_getTarget().tEquals(this)){
    			parent.s_setTarget(null);
    		}
    		
    	}
    	if(hasActivity){
    		parent.s_removeMonster(this, isKilled, who);
    		isDied = true;
    	}
    }
    //public Rectangle getHPView(){
    //	return HPview;
    //}
    public double getX(){
        return x + vX;
    }
    public double getY(){
        return y + vY;
    }
    public void setX(double x){
    	this.x = x;
    	if(isGUI){
    		view.setLayoutX(x + vX - view.getPrefWidth()/2);
    		targetArea.setCenterX(x + vX-6);
        	//HPview.setX(x + vX - view.getPrefWidth()*(5./16) - 5);
    	}
    }
    public void setY(double y){
    	this.y = y;
    	if(isGUI){
    		view.setLayoutY(y + vY - view.getPrefHeight()/2 - 5);
    		targetArea.setCenterY(y + vY);
    		//HPview.setY(y + vY - HPview.getHeight()/2 - 13);
    	}
    }
    public void setdX(double x){
    	this.x += x;
    	if(isGUI){
    		view.setLayoutX(view.getLayoutX() + x);
    		targetArea.setCenterX(this.x + vX -6);
        	//HPview.setX(HPview.getX() + x);
    	}
    }
    public void setdY(double y){
    	this.y += y;
    	if(isGUI){
    		view.setLayoutY(view.getLayoutY() + y);
    		targetArea.setCenterY(this.y + vY);
    		//HPview.setY(HPview.getY() + y);
    	}
    }
    public int getReward(){
        return (int)reward;
    }
    public Label getView(){
        return view;
    }
    public void powWith(double koef){
    	//reward *= 1+koef/25.;
    	maxHP *= koef;
    	reward = Math.pow(maxHP, 0.6);
    	curHP = maxHP;
    }
    public void powWith(double k1, double k2){
    	//reward *= 1+koef/25.;
    	maxHP *= k1;
    	reward *= k2;
    	curHP = maxHP;
    }
    public void toBOSS(){
    	isBOSS = true;
    	maxHP *= 13;
    	reward *= 15;
    	curHP = maxHP;
    }
    
    
    
    public int getTileX(){
    	//return (int)((x-16)/32);
    	return (int)(x/32);
    }
    public int getTileY(){
    	//return (int)((y+15)/32);
    	return (int)(y/32);
    }
    /**
     * Reduces the monster's health points
     *
     * @param damage
     * The damage comes from the attacking tower which signals how
     * much health points are deduced from the monster.
     */
    public void slow(double k){
    	double ns = maxSpeed *(1-k);
    	if (ns < curSpeed)
    		curSpeed = ns;
    }
    public void hardSet(double hp, Buff b){
    	double kHP = curHP / maxHP;
    	//HPview.setWidth(20 * kHP);
    	view.setTextFill(Color.color(1 - kHP, kHP, 0));
    	//HPview.setFill(Color.color(1 - kHP, kHP, 0));
    	b.setTarget(this);
    	buffList.add(b);
    }
    public void takeDamage(double damage, int who){
    	
        curHP = curHP - damage;
        if (curHP <= 0){
            
        	if(hasActivity)
        		remove(true, who);
            
        }else if(isGUI){
        	double kHP = curHP / maxHP;
        	//HPview.setWidth(20 * kHP);
        	//HPview.setFill(Color.color(1 - kHP, kHP, 0));
        	view.setTextFill(Color.color(1 - kHP, kHP, 0));
        }
    }
    private void complicateColorEffect(boolean add, Color c){
    	float v = add? 1: -1;
    	effectColor[0] += v * c.getRed();
    	effectColor[1] += v * c.getGreen();
    	effectColor[2] += v * c.getBlue();
    }
    public void addBuff(Buff b){
		Buff buff = Buff.copy(b);
		buff.setTarget(this);
		buffList.add(buff);
		complicateColorEffect(true, buff.getColor());
    }
    public boolean hasDirection(){
    	int dir = parent.s_getConfigurations().getDirection(getTileX(), getTileY());
    	return (dir >= -8 && dir < 0) || (dir == 5);
    }
    public boolean hasDirectionIn(int[][] map){
    	int dir = map[nextPoint.getTileX()][nextPoint.getTileY()];
    	return dir < 0 || dir == 5;
    }
    public void getNextCoord(){
    	int dir = parent.s_getConfigurations().getDirection(getTileX(), getTileY());
    	if (dir < 0){
    		nextPoint = parent.s_getConfigurations().getNextCoord(getTileX(), getTileY());
    	}else if(dir == 5){
    		remove(false, 0);
    	}else{
    		System.out.println("NODIRECTION");
    	}
    	
    }
    /**
     * Updates the location of the monster along the path that is created
     * by the TileMap in the GameManager initialize method. Movement is
     * made exclusively on the X or Y axis until the path is complete or the
     * monster's healthPoints reach 0.
     */
    private void moveIt(){
    	double dx = nextPoint.getExactX() - x;
    	double dy = nextPoint.getExactY() - y;
    	
    	double k = Math.pow(curSpeed, 2) / Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    	if (k < 1){
    		setdX(dx*k);
    		setdY(dy*k);
    	}else{
    		setX(nextPoint.getExactX());
    		setY(nextPoint.getExactY());
    		hasDirection = false;
    	}
    	curSpeed = maxSpeed;
    }
    public void update(){
    	
    	if (hasDirection){
    		for (Iterator<Buff> iterator = buffList.iterator(); iterator.hasNext();) {
        		Buff buff = iterator.next();
        		if (!buff.update()){
        			//buff.remove();
        			complicateColorEffect(false, buff.getColor());
        	        iterator.remove();
        	    }
        	}
    		moveIt();
    	}else{
    		hasDirection = true;
    		getNextCoord();
    	}
    	if(isGUI){
    		if(buffList.size() > 0){
    		DropShadow borderGlow = new DropShadow();
    		borderGlow.setColor(Color.color(
    				Math.min(effectColor[0], 1),
    				Math.min(effectColor[1], 1),
    				Math.min(effectColor[2], 1)
    				));
    		borderGlow.setOffsetX(0f);
    		borderGlow.setOffsetY(0f);
    		view.setEffect(borderGlow);
    		}else{
    			view.setEffect(null);
    		}
    	}
       
    }
	public int getType() {
		// TODO Auto-generated method stub
		return this.type;
	}
	public double getMaxHP() {
		// TODO Auto-generated method stub
		return this.maxHP;
	}
	public double getVariancyX(){
		return vX;
	}
	public double getVariancyY(){
		return vY;
	}
	public int getID(){
		return ID;
	}
	public void setId(int dieCount) {
		ID = dieCount;
	}
	public boolean isBoss() {
		// TODO Auto-generated method stub
		return isBOSS;
	}
	public int getLiveCost() {
		// TODO Auto-generated method stub
		return !isBOSS? 1: 7;
	}
	public String getPrettyHP(double hp){
		String pretty = "";
		while(hp > 1000){
			hp /= 1000;
			pretty += "k";
		}
		String f = hp > 10? String.valueOf((int)(hp)): String.format("%.1f", hp);
		return f+pretty;
	}
	public String getBuffDesc(){
		String ss = "";
		for(Buff b:buffList){
			ss += b.getId();
		}
		return ss;
	}
	
	////////////////
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
		return new String[] {
				!isBOSS? "Z": "!Z!",
				getBuffDesc(),
				"+",
				getPrettyHP(curHP)+"/"+getPrettyHP(maxHP),
				"u",
				String.format( "%.1f", curSpeed),
				"$",
				String.valueOf(getReward())
				
		};
	}
	@Override
	public int getUpgradePrice(){
		return 0;
	}
	@Override
	public int getSellPrice(){
		return 0;
	}
	@Override
	public boolean tEquals(Target obj) {
		return obj.getTargetID().equals(this.getTargetID());
	}
	@Override
	public String getTargetID() {
		return "M"+getID();
	}
}
