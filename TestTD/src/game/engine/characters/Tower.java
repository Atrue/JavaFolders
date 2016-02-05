package game.engine.characters;


import game.engine.Coordinate;
import game.engine.GameState;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Towers are used by the player as the primary weapon
 * to defeat monsters.
 *
 * Each tower spawns a worker thread to poll for nearby monsters
 * to make attacks on.
 */
public class Tower {
    private static final int BUILD_TIME = 10000;    // Time used to build tower.
    private int attackDamage;                       // Determines amount of health to reduce from monsters per attack
    private double attackSpeed;                     // Determines the time a tower must wait after an attack
    private double attackCD = 1.0;
    private double timeout = 0;
    private int attackRange;                        // Sets the minimum range the tower can make attacks in
    private int upgradeTime;                        // Time in milliseconds it takes to complete an upgrade.
    private int upgradeCost;                        // Determines the resource cost to upgrade the tower
    private int sellCost;                           // Determines the resources gained by selling the tower
    private ArrayList<Projectile> projectileList;   // Used by the gui thread to create animations for attacks
    private Coordinate coords;                      // Represents the coordinates of the tower on the map
    private Monster target;
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
    public Tower(int x , int y){
        projectileList = new ArrayList<Projectile>();
        coords = new Coordinate(x , y);
        attackDamage = 5;
        attackSpeed = 1.0;
        attackRange = 200;
        sellCost = 35;
        upgradeCost = 20;
        upgradeTime = 5000;
    }
    public boolean inRange(Monster target){
    	int x2 = target.getX();
    	int y2 = target.getY();
    		
    	if ( Math.pow(x2 - getX(), 2) + Math.pow(y2 - getY(), 2) <= Math.pow(attackRange, 2)){
    		return true;
    	}

    	return false;
    }
    public boolean isReady(){
    	return this.timeout == 0;
    }
    public double getTick(){
		return 1./GameState.getFPS();
	}
    public void update(){
    	if (timeout > 0){
    		timeout = timeout - getTick() > 0? timeout - getTick():0;
    	}else{
	    	for (Monster monster:GameState.getMonstersAlive()){
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
    	/*
    	Iterator<Projectile> iterP = projectileList.iterator();
    	while (iterP.hasNext()){
    		Projectile projectile = iterP.next();
    		if (!projectile.update()){
    			iterP.remove(projectile);
    			projectile.remove();
    		}
    	}*/
    	/*
    	for (Projectile projectile:projectileList){
    		if (!projectile.update()){
    			projectileList.remove(projectile);
    			projectile.remove();
    		}
    	}
    	*/
    }
    
    /**
     * Upgrades the towers stats.
     */
    public void upgradeTower(){
        attackDamage++;
        attackSpeed = attackSpeed - 0.1;
        attackRange = attackRange + 50;
        upgradeTime += 3000;
        upgradeCost += 20;
    }

    /**
     * Creates a projectile when the tower attacks a monster.
     *
     * @param target
     * The target location of the projectile
     */
    public void createProjectile(Monster target){
    	Projectile proj = new Projectile(target , coords.getExactX() , coords.getExactY() , Color.BLACK);
    	proj.add();
        projectileList.add(proj);
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

    public int getUpgradeCost(){
        return upgradeCost;
    }

    public int getSellCost(){
        return sellCost;
    }

    public int getUpgradeTime(){
        return upgradeTime;
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

    public void setAttackDamage(int attackDamage){
        this.attackDamage = attackDamage;
    }

    public void setAttackRange(int attackRange) {
        this.attackRange = attackRange;
    }

    public void setAttackSpeed(double attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

}
