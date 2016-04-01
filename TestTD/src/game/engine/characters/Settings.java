package game.engine.characters;

import game.network.ServerLink;
import javafx.scene.paint.Color;

public class Settings {
	
	private static Tower[][] towers = new Tower[7][5];
	private static Monster[] monsters = new Monster[4];
	private static Buff[] buffs = new Buff[5];
	
	private static boolean isInit = false;
	
	public static void init(){
		initBuffs();
		initTowers();
		initMonsters();
		isInit = true;
	}
	public static int getFPS(){
		return 30;
	}
	public static double powMonOfLvl(int i) {
		return Math.pow(10,i*9.1/100);
	}
	public static double powHPOfLvl(int i) {
		return Math.pow(2, (i+1)/5.);
	}
	public static boolean isinit(){
		return isInit;
	}
	public static boolean isTowerExist(int type, int level){
		if (type < towers.length && level < towers[type].length){
			if (towers[type][level] != null){
				return true;
			}
		}
		return false;
	}
	public static Monster getMonster(int type){
		return monsters[type];
	}
	public static Tower getTower(int type, int level){
		return towers[type][level];
	}
	private static void initBuffs(){
		buffs[0] = new Buff(0.314, 0, 1, 2.0, "i");
		buffs[1] = new Buff(0, 0.25, 1, 1.0, "*");
		buffs[2] = new Buff(1, 0, 0.2, 1./getFPS(), "!");
		buffs[3] = new Buff(0.1, 1, 0.2, 1.2, "&");
	}
	private static Buff bWith(int id, double k){
		return buffs[id].setA(buffs[id].getCrit()*k, buffs[id].getSlow()*k, buffs[id].getChanse()*k, buffs[id].getDuration());
	}
	private static void initTowers(){
		towers[0][0] = new Tower(2, 3.0, 1.0, 70, 5, 1, 0, Color.BLACK, null);
		towers[0][1] = new Tower(7, 3.0, 1.0, 75, 15, 2,0, Color.BLACK, null);
		towers[0][2] = new Tower(81, 3.0, 1.0, 80, 130, 3, 0, Color.BLACK, null);
		towers[0][3] = new Tower(1024, 3.0, 0.8, 100, 1500, 4, 0, Color.BLACK, null);
		towers[0][4] = new Tower(13946, 3.0, 0.6, 150, 10000, 5, 0, Color.BLACK, null);
		
		towers[1][0] = new Tower(15, 4.5, 1.3, 90, 30, 1, 1, Color.color(0.7, 0, 0), null);
		towers[1][1] = new Tower(98, 4.5, 1.3, 95, 110, 2, 1, Color.color(0.7, 0, 0), null);
		towers[1][2] = new Tower(946, 4.5, 1.3, 100, 1225, 3, 1, Color.color(0.7, 0, 0), null);
		towers[1][3] = new Tower(8270, 4.5, 1.3, 120, 8700, 4, 1, Color.color(0.7, 0, 0), null);
		towers[1][4] = new Tower(31823, 4.5, 1.3, 160, 26100, 5, 1, Color.color(0.7, 0, 0), null);
	
		towers[2][0] = new Tower(13, 3.14, 1.2, 90, 30, 1, 2, Color.color(0, 0.7, 0), buffs[0]);
		towers[2][1] = new Tower(67, 3.14, 1.15, 95, 110, 2, 2, Color.color(0, 0.7, 0), buffs[0]);
		towers[2][2] = new Tower(693, 3.14, 1.1, 100, 1225, 3, 2, Color.color(0, 0.7, 0), buffs[0]);
		towers[2][3] = new Tower(4700, 3.14, 1.05, 105, 8700, 4, 2, Color.color(0, 0.7, 0), buffs[0]);
		towers[2][4] = new Tower(14977, 3.14, 1.0, 110, 26100, 5, 2, Color.color(0, 0.7, 0), buffs[0]);
		
		towers[3][0] = new Tower(10, 5.7, 1.5, 90, 30, 1, 3, Color.color(0, 0, 0.7), bWith(1,1));
		towers[3][1] = new Tower(40, 5.7, 1.45, 95, 110, 2, 3, Color.color(0, 0, 0.7), bWith(1,1.2));
		towers[3][2] = new Tower(400, 5.7, 1.4, 100, 1225, 3, 3, Color.color(0, 0, 0.7), bWith(1,1.2));
		towers[3][3] = new Tower(2000, 5.7, 1.35, 120, 8700, 4, 3, Color.color(0, 0, 0.7), bWith(1,1.2));
		towers[3][4] = new Tower(10000, 5.7, 1.3, 160, 26100, 5, 3, Color.color(0, 0, 0.7), bWith(1,1.2));
		
		towers[4][0] = new Tower(111, 4.5, 1.34, 90, 450, 1, 4, Color.color(0.7, 0, 0.7), bWith(2,1));
		towers[4][1] = new Tower(543, 4.5, 1.34, 95, 1755, 2, 4, Color.color(0.7, 0, 0.7), bWith(2,1.2));
		towers[4][2] = new Tower(2048, 4.5, 1.34, 100, 6669, 3, 4, Color.color(0.7, 0, 0.7), bWith(2,1.25));
		towers[4][3] = new Tower(10240, 4.5, 1.34, 120, 24675, 4, 4, Color.color(0.7, 0, 0.7), bWith(2,1.2));
		towers[4][4] = new Tower(51200, 4.5, 1.34, 160, 86363, 5, 4, Color.color(0.7, 0, 0.7), bWith(2,1.2));
		
		towers[5][0] = new Tower(50, 5.5, 2.0, 90, 450, 1, 5, Color.color(0, 0.7, 0.7), bWith(3,1));
		towers[5][1] = new Tower(260, 5.5, 1.93, 95, 1755, 2, 5, Color.color(0, 0.7, 0.7), bWith(3,1.2));
		towers[5][2] = new Tower(1200, 5.5, 1.86, 100, 6669, 3, 5, Color.color(0, 0.7, 0.7), bWith(3,1.2));
		towers[5][3] = new Tower(5300, 5.5, 1.8, 120, 24675, 4, 5, Color.color(0, 0.7, 0.7), bWith(3,1.2));
		towers[5][4] = new Tower(20500, 5.5, 1.7, 160, 86363, 5, 5, Color.color(0, 0.7, 0.7), bWith(3,1.2));
		
		towers[6][0] = new Tower(44, 4.5, 0.3, 90, 450, 1, 6, Color.color(0.9,0.9, 0), null);
		towers[6][1] = new Tower(196, 4.5, 0.28, 95, 1755, 2, 6, Color.color(0.9,0.9, 0), null);
		towers[6][2] = new Tower(972, 4.5, 0.26, 100, 6669, 3, 6, Color.color(0.9,0.9, 0), null);
		towers[6][3] = new Tower(4444, 4.5, 0.24, 105, 24675, 4, 6, Color.color(0.9,0.9, 0), null);
		towers[6][4] = new Tower(13579, 4.5, 0.2, 110, 86363, 5, 6, Color.color(0.9,0.9, 0), null);
		
	}
	private static void initMonsters(){
		monsters[0] = new Monster(200, 1.0, 1, 0);
		monsters[1] = new Monster(1.1, 1.35, 0.8, 1);
		monsters[2] = new Monster(4, 0.8, 1.5, 2);
	}
	

}
