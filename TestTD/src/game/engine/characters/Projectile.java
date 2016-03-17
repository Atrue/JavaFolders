package game.engine.characters;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

/**
 * Projectile is created when the tower attacks a monster. The GameManager
 * will create the animation using the start and end locations.
 */
public class Projectile extends Label {
	private static Pane view;
	
	private Tower parent;
    private Monster target;     // The target of the attack
    private double x;   // Starting location of the projectile
    private double y;
    boolean isGUI;
    private double speed = 3.0;
    private double damage = 1.0;

    Projectile(Tower parent, Monster target, boolean isGUI){
        super("x");
        this.isGUI = isGUI;
        this.parent = parent;
    	this.speed = parent.getAttackSpeed();
    	this.damage = parent.getAttackDamage();
    	this.x = parent.getX();
        this.y = parent.getY();
        this.target = target;
        
    }
    public static void setParentView(Pane v){
    	view = v;
    }
    public void add(){
    	if(isGUI){
	    	setFont(new Font(15));
	        setTextFill(parent.getColor());
	        setHeight(20);
	        setWidth(20);
	    	view.getChildren().add(this);
    	}
    }
    
    public void remove(){
    	if(isGUI){
    		view.getChildren().remove(this);
    		setVisible(false);
    	}
    }
    public void updateView(){
    	setLayoutX(x-10);
    	setLayoutY(y-10);
    }
    public Monster getTarget(){
        return target;
    }

    public double getEndX(){
        return target.getX();
    }

    public double getEndY(){
        return target.getY();
    }
    
    public boolean update(){
    	if (target == null){
    		System.out.println("strange");
    		return false;
    	}
    	
    	double pX = Math.pow(getEndX() - x ,2);
    	double pY = Math.pow(getEndY() - y ,2);
    	    	
    	if (pX + pY > Math.pow(speed, 2)){
    	
	    	double koef = speed / Math.sqrt(pX + pY);
    		
	    	x = x + (getEndX() - x)*koef;
	    	y = y + (getEndY() - y)*koef;
	    	
	    	updateView();
	    	
	    	return true;
    	}else{
    		target.takeDamage(damage, parent.getOwnerID());
    		return false;
    	}
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }



}

