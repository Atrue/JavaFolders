package game.engine.characters;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Monsters are the enemies of the player. They're job is to traverse
 * the path. They are created during timed intervals and removed when
 * their healthPoints is zero or they reach the end point of the path.
 */

import game.engine.Coordinate;
import game.engine.GameManager;
import game.engine.GameState;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class Monster {
	private static Pane parentView;
	private static GameManager parent;
    
	private ArrayList<Buff> buffList = new ArrayList<>();
    private Label view;                        // Graphical view of monster
    private Rectangle HPview;
    
    private int type;
    private double maxHP;
    private double curHP;                   // Determines if the monster is still alive
    private double maxSpeed;                  // Determines time to complete path
    private double curSpeed;
    private double reward;                         // Monster death will trigger a resource reward
    
    private double x;
    private double y;
	private boolean hasDirection;
	private Coordinate nextPoint;

    /**
     * Monster initialization
     *
     * @param healthPoints
     * The health points increase as the game progresses to increase
     * the difficulty for the player.
     */
    public Monster(double hp, double speed, double kGold, int t){
        maxHP = hp;
        curHP = maxHP;
        maxSpeed = speed;
        curSpeed = maxSpeed;
        type = t;
        
        reward = kGold;
        
    }
    public static void init(GameManager gstate){
    	parent = gstate;
    }
    public static void setParentView(Pane v){
    	parentView = v;
    }
    public static Monster copy(Monster from){
    	Monster to = new Monster(from.maxHP, from.maxSpeed, from.reward, from.type);
    	return to;
    }
    
    public void add(){
        x = GameState.getStartCord().getExactX();
        y = GameState.getStartCord().getExactY();
        view = new Label("z");
        String style = "";
        if (type == 1){
        	style = "-fx-font-style:italic;";
        }else if(type == 2){
        	style = "-fx-font-weight:bold;";
        }
        
        view.setStyle(style);
        view.setFont(new Font(30));
        view.setLayoutX(x-10);
        view.setLayoutY(y-25);
        view.setPrefHeight(32);
        view.setPrefWidth(32);        
        
        HPview = new Rectangle(20, 5);
        HPview.setFill(Color.color(0, 1, 0));
        HPview.setX(x - 16);
        HPview.setY(y - 16);
    	
    	parentView.getChildren().add(view);
    	parentView.getChildren().add(HPview);
    }
    public void remove(boolean isKilled){
    	
    	parentView.getChildren().remove(view);
    	parentView.getChildren().remove(HPview);
    	view.setVisible(false);
    	HPview.setVisible(false);
    	parent.removeMonster(this, isKilled);
    }
    public Rectangle getHPView(){
    	return HPview;
    }
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public void setX(double x){
    	this.x = x;
    	view.setLayoutX(x - 10);
        HPview.setX(x - 10);
    }
    public void setY(double y){
    	this.y = y;
    	view.setLayoutY(y-25);
        HPview.setY(y-16);
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
    public void takeDamage(double damage){
        curHP = curHP - damage;
        if (curHP <= 0){
            
            remove(true);
            
        }else{
        	double kHP = curHP / maxHP;
        	HPview.setWidth(20 * kHP);
        	HPview.setFill(Color.color(1 - kHP, kHP, 0));
        }
    }
    public void addBuff(Buff b){
    	Buff buff = Buff.copy(b);
    	buff.setTarget(this);
    	buffList.add(buff);
    }
    public boolean hasDirection(){
    	int dir = GameState.getDirection(getTileX(), getTileY());
    	return (dir >= -8 && dir < 0) || (dir == 5);
    }
    public boolean hasDirectionIn(int[][] map){
    	int dir = map[nextPoint.getTileX()][nextPoint.getTileY()];
    	return dir < 0 || dir == 5;
    }
    public void getNextCoord(){
    	int dir = GameState.getDirection(getTileX(), getTileY());
    	if (dir < 0){
    		nextPoint = GameState.getNextCoord(getTileX(), getTileY());
    	}else if(dir == 5){
    		remove(false);
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
    		setX(getX() + dx*k);
    		setY(getY() + dy*k);
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
        	        iterator.remove();
        	    }
        	}
    		moveIt();
    	}else{
    		hasDirection = true;
    		getNextCoord();
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


}
