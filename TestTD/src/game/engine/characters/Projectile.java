package game.engine.characters;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Projectile is created when the tower attacks a monster. The GameManager
 * will create the animation using the start and end locations.
 */
public class Projectile extends Circle {
	private static Pane view;
	
    private Monster target;     // The target of the attack
    private double x;   // Starting location of the projectile
    private double y;

    private double speed = 3.0;
    private double damage = 1.0;

    Projectile(Monster target , int towerX , int towerY , Color color){
        super(towerX , towerY , 5 , color);
        this.target = target;
        this.x = towerX;
        this.y = towerY;
    }
    public static void setParentView(Pane v){
    	view = v;
    }
    public void add(){
    	view.getChildren().add(this);
    }
    public void remove(){
    	view.getChildren().remove(this);
    	setVisible(false);
    }
    public void updateView(){
    	setCenterX(x);
    	setCenterY(y);
    }
    public Monster getTarget(){
        return target;
    }

    public int getEndX(){
        return target.getX();
    }

    public int getEndY(){
        return target.getY();
    }
    
    public boolean update(){
    	
    	double pX = Math.pow(getEndX() - x ,2);
    	double pY = Math.pow(getEndY() - y ,2);
    	    	
    	if (pX + pY > Math.pow(speed, 2)){
    	
	    	double dX = pX / ( (pX + pY) / Math.pow(speed, 2));
	    	double dY = pY / ( (pX + pY) / Math.pow(speed, 2));
	    	//move
	    	x = x + Math.signum(getEndX() - x) * dX;
	    	y = y + Math.signum(getEndY() - y) * dY;
	    	updateView();
	    	
	    	return true;
    	}else{
    		target.takeDamage(damage);
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

