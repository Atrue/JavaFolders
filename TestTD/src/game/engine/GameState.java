package game.engine;

import game.engine.characters.Levels;

/*
    keeps track of the games state, only one state
    can be active at a time
 */

import game.engine.characters.Monster;
import game.engine.characters.Tower;
import javafx.scene.layout.Pane;

import java.io.Serializable;
import java.util.ArrayList;


public class GameState implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1517971545341029527L;
	//game state flags
    public static final int IS_RUNNING = 1;    //game is active
    public static final int IS_PAUSED = 2;     //game is temporarily not active
    public static final int IS_STOPPED = 3;    //game is over and halted
    private static final int FPS = 30;
    

    
    private static int state;
    private static int resources;                  //used for buying and upgrading tower
    private static int level;                      //represents the current wave of monsters that are being spawned
    private static int score;                      //score is calculated by awarding kill points and perfect level points
    private static int lives;;                     //numbers of monster escapes allowed before game ends
    private static ArrayList<Tower> playerTowers;  //holds all tower references on the map
    private static ArrayList<Monster> monstersAlive; //holds monster references
    private static Levels levels;
    private static Tower target = null;
	private static TileMap tileMap;
	private static Pane parentView;

    //CONSTRUCTORS
    public static void init(){
        state = IS_RUNNING;
        resources = 10000;
        level = 0;
        score = 0;
        lives = 20;
        playerTowers = new ArrayList<Tower>();
        monstersAlive = new ArrayList<Monster>();
        
        tileMap = new TileMap(960 ,800);
        Monster.setPath(tileMap.getPath());
        
    }


    //SETTERS
    public static void setResources(int r){
        resources = r;
    }
    public static void setLevel(int l){
        level = l;
    }
    public static void setScore(int s){
        score = s;
    }
    public static void setLives(int l){
        lives = l;
    }
    public static void setState(int s) {
        state = s;
    }
    public static void setTarget(Tower t){
    	target = t;
    }
    public static void setLevels(Levels l){
    	levels = l;
    }
    public static void setParentView(Pane p){
    	parentView = p;
    }
    //GETTERS
    public static TileMap getMap(){
    	return tileMap;
    }
    public static Pane getParentPane(){
    	return parentView;
    }
    public static int getResources(){
        return resources;
    }
    public static int getLevel(){
        return level;
    }
    public static int getScore(){
        return score;
    }
    public static int getLives() {
        return lives;
    }
    public static int getState() {
        return state;
    }
    public static Tower getTarget(){
    	return target;
    }
    public static Levels getLevels(){
    	return levels;
    }
    public static int getFPS(){
    	return FPS;
    }
    public static boolean isPaused(){
        if(state == IS_PAUSED){
            return true;
        }
        return false;
    }

    public static boolean isRunning(){
        if(state == IS_RUNNING){
            return true;
        }
        return false;
    }

    public static boolean isStopped(){
        if(state == IS_PAUSED){
            return true;
        }
        return false;
    }

    public static ArrayList<Tower> getPlayerTowers(){
        return playerTowers;
    }

    public static ArrayList<Monster> getMonstersAlive() {
        return monstersAlive;
    }

    public static void addMonster(Monster monster){monstersAlive.add(monster);}
    public static void addTower(Tower tower){playerTowers.add(tower);}
    public static void removeMonster(Monster monster){monstersAlive.remove(monster);}
    public static void removeTower(Tower tower){playerTowers.remove(tower);}
}
