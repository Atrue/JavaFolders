package game.engine;

import java.awt.List;
import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import game.engine.characters.Levels;

/*
    keeps track of the games state, only one state
    can be active at a time
 */

import game.engine.characters.Monster;
import game.engine.characters.Tower;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;


public class GameState implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1517971545341029527L;
	//game state flags
    public static final int IS_RUNNING = 1;    //game is active
    public static final int IS_PAUSED = 2;     //game is temporarily not active
    public static final int IS_STOPPED = 3;    //game is over and halted
    
    private static final int DIR_TOP = -1;
    private static final int DIR_LEFT = -2;
    private static final int DIR_BOT = -3;
    private static final int DIR_RIGHT = -4;
    private static final int DIR_RIGHT_TOP = -7;
    private static final int DIR_LEFT_TOP = -8;
    private static final int DIR_BOT_LEFT = -5;
    private static final int DIR_BOT_RIGHT = -6;
    
    private static final int FPS = 30;
	public static int[] SPFERES;
    private static ArrayList<Coordinate> startCords;
    private static Coordinate endCord;
    

    //private static GameManager parent;
    
    private static int[][] map;
    
    private static GameManager manager;
    private static int state;
    private static int resources;                  //used for buying and upgrading tower
    private static int level;                      //represents the current wave of monsters that are being spawned
    private static int lives;                     //numbers of monster escapes allowed before game ends
    private static ArrayList<Tower> playerTowers;  //holds all tower references on the map
    private static ArrayList<Monster> monstersAlive; //holds monster references
    private static Levels levels;
    private static Tower target = null;
	private static Pane parentView;
	private static int[][] pathMap;

    //CONSTRUCTORS
    public static void init(GameManager gm, int type) {
    	manager = gm;
        state = IS_PAUSED;
        resources = 10;
        level = 0;
        lives = 20;
        playerTowers = new ArrayList<Tower>();
        monstersAlive = new ArrayList<Monster>();
        SPFERES = new int[] {0,0,0};
        
        startCords = new ArrayList<>();
        if (type == 0){
        	startCords.add(new Coordinate(0, 8));
        	endCord = new Coordinate(21, 8);
            //parent = gamestate;        
            map = new int[22][17];
        	for(int i=0;i<map.length;i++){
        		boolean blocked = i == 0 || i + 1 == map.length;
        		for(int j=0;j<map[i].length;j++){
        			map[i][j] = blocked || (j == 0 || j + 1 == map[i].length)? 1:0;   
        		}
        	}
        	for(Coordinate c: startCords){
        		map[c.getTileX()][c.getTileY()] = 4;
        	}
        	map[endCord.getTileX()][endCord.getTileY()] = 5;
        }
        
        
        
    	
        
    }
    public static void setMap(JSONArray jmap) throws JSONException{
    	map = new int[jmap.length()][jmap.getJSONArray(0).length()];
    	
    	for(int x=0;x<jmap.length();x++){
    		JSONArray jrow = jmap.getJSONArray(x);
    		for(int y=0;y<jrow.length();y++){
    			int value = jrow.getInt(y);
        		map[x][y] = value;
        		if(value == 4){
        			startCords.add(new Coordinate(x, y));
        		}else if (value == 5){
        			endCord = new Coordinate(x, y);
        		}
        	}
    	}
    	initPath();
    	manager.getController().repaintBG(map);
    }
    public static void initPath(){
    	if (map != null)
    		pathMap = algorithmA(fullCopy(map));
    }
    private static int[][] fullCopy(int[][] from){
    	int[][] to = from.clone();
    	for (int i = 0; i < from.length; i++) {
			to[i] = from[i].clone();
		}
    	return to;
    }
    public static int getDirection(int xTile, int yTile){
    	return pathMap[xTile][yTile];
    }
    public static Coordinate getNextCoord(int xTile, int yTile){
    	switch(getDirection(xTile, yTile)){
	    	case DIR_TOP:{return new Coordinate(xTile, yTile-1);}
	    	case DIR_LEFT:{return new Coordinate(xTile-1, yTile);}
	    	case DIR_BOT:{return new Coordinate(xTile, yTile+1);}
	    	case DIR_RIGHT:{return new Coordinate(xTile+1, yTile);}
	    	case DIR_RIGHT_TOP:{return new Coordinate(xTile+1, yTile-1);}
	    	case DIR_LEFT_TOP:{return new Coordinate(xTile-1, yTile-1);}
	    	case DIR_BOT_LEFT:{return new Coordinate(xTile-1, yTile+1);}
	    	case DIR_BOT_RIGHT:{return new Coordinate(xTile+1, yTile+1);}
	    	default:{return new Coordinate(xTile, yTile);}
    	}
    }
    private static String[] s = new String[]{"^\\","/^","\\v","v/",">","V","<","^"};
    private static Group group;
    private static boolean testAndAdd(int x,int y, int dir, ArrayList<Integer[]> pather, int[][] map){
    	if (x>=0&&y>=0&&x < map.length && y < map[x].length &&(map[x][y] == 0 || map[x][y] == 4)){
			return true;		
		}
    	return false;
	}
    private static void alorithmA_add(int x,int y, int dir, ArrayList<Integer[]> pather, int[][] map){
    	map[x][y] = dir;
    	//Label l = new Label(s[dir + 8]);
		//l.setLayoutX(x*32);
		//l.setLayoutY(y*32);
		//group.getChildren().add(l);
		pather.add(new Integer[]{x,y,dir});
    }
    private static int[][] algorithmA(int[][] cmap){
    	ArrayList<Integer[]> pather = new ArrayList<>();
    	pather.add(new Integer[]{endCord.getTileX(),endCord.getTileY(),2});
    	group.getChildren().clear();
    	parentView.getChildren().remove(group);
    	group = new Group();
    	parentView.getChildren().add(group);
    	//int ld = -3;
    	while(!pather.isEmpty()){
    		Integer[] comb = pather.get(0);
    		int nx = comb[0];
    		int ny = comb[1];
    		
    		boolean[] trues = new boolean[8];
    		
    		trues[0] = testAndAdd(nx, ny+1, -1, pather, cmap);
    		trues[1] = testAndAdd(nx+1, ny, -2, pather, cmap);
    		trues[2] = testAndAdd(nx, ny-1, -3, pather, cmap);
    		trues[3] = testAndAdd(nx-1, ny, -4, pather, cmap);
    		trues[4] = testAndAdd(nx+1, ny-1, -5, pather, cmap);
    		trues[5] = testAndAdd(nx-1, ny-1, -6, pather, cmap);
    		trues[6] = testAndAdd(nx-1, ny+1, -7, pather, cmap);
    		trues[7] = testAndAdd(nx+1, ny+1, -8, pather, cmap);
    		
    		if(trues[0])
    			alorithmA_add(nx, ny+1, -1, pather, cmap);
    		if(trues[1])
    			alorithmA_add(nx+1, ny, -2, pather, cmap);
    		if(trues[2])
    			alorithmA_add(nx, ny-1, -3, pather, cmap);
    		if(trues[3])
    			alorithmA_add(nx-1, ny, -4, pather, cmap);
    		if(trues[4] && trues[1] && trues[2])
    			alorithmA_add(nx+1, ny-1, -5, pather, cmap);
    		if(trues[5] && trues[3] && trues[2])
    			alorithmA_add(nx-1, ny-1, -6, pather, cmap);
    		if(trues[6] && trues[3] && trues[0])
    			alorithmA_add(nx-1, ny+1, -7, pather, cmap);
    		if(trues[7] && trues[1] && trues[0])
    			alorithmA_add(nx+1, ny+1, -8, pather, cmap);
    		/*
    		fr (int i = -1; i > -0; i--) {
    			int nx = comb[0];
        		int ny = comb[1];
    			switch(i){
	    			case -1:{ny++;break;}
	    			case -2:{nx++;break;}
	    			case -3:{ny--;break;}
	    			case -4:{nx--;break;}
	    			case -5:{nx++;ny--;break;}
	    			case -6:{nx--;ny--;break;}
	    			case -7:{nx--;ny++;break;}
	    			case -8:{nx++;ny++;break;}
    			}
    			trues[Math.abs(i)-1] = testAndAdd(nx, ny, i, pather, cmap);
    			if (i < -4){
    				alorithmA_add(nx, ny, i, pather, cmap);
    			}
			}
    		*/
    		
    		pather.remove(0);
    	}
    	return cmap;
    }
    private static boolean isValidPath(int[][] map){
    	
    	for(Coordinate c: startCords){
    		if(map[c.getTileX()][c.getTileY()] >= 0)
    			return false;
    	}
    	for(Monster m:monstersAlive){
			if(!m.hasDirectionIn(map)){
				return false;
			}
		}
		return true;
	
    }
    //SETTERS
    public static void setResources(int r){
        resources = r;
        manager.updateLabels();
    }
    public static void setLevel(int l){
        level = l;
    }
    public static void setLives(int l){
        lives = l;
        if (lives == 0)
        	manager.endGame(false);
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
    	group = new Group();
    	parentView.getChildren().add(group);
    }
    //sets the map node for the given coordinates to input value than repaints adjustment
    public static void setMapNode(int xCord , int yCord , int updatedValue){
        map[xCord][yCord] = updatedValue;
        manager.getController().repaintBG(map);
    }
    public static boolean tryMapNode(int xCord, int yCord, int updatedValue){
    	if (updatedValue == 0){
    		setMapNode(xCord, yCord, updatedValue);
    		pathMap = algorithmA(fullCopy(map));
    		return true;
    	}
    	if (!nodeOpen(xCord, yCord))
    		return false;
    	int[][] testMap = fullCopy(map);
    	testMap[xCord][yCord] = updatedValue;
    	testMap = algorithmA(testMap);
    	if (isValidPath(testMap)){
    		setMapNode(xCord, yCord, updatedValue);
    		pathMap = testMap;
    		return true;
    	}
    	return false;
    		
    }
    public static ArrayList<Coordinate> getStartCords(){
    	return startCords;
    }
    public static Coordinate getEndCord(){
    	return endCord;
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
    public static int[][] getMap(){
    	return map;
    }
    //checks to see if the node is open
    public static boolean nodeOpen(int xCord , int yCord){
    	if(xCord < map.length && yCord < map[0].length){
    		if(map[xCord][yCord] == 0){
    			return true;
        	}
    	}
        return false;
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
