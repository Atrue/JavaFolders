package game.engine.characters;

import game.engine.GameState;
import javafx.scene.paint.Color;

public class ListOfCharacters {
	
	private static Tower[][] towers = new Tower[7][5];
	private static Monster[] monsters = new Monster[4];
	private static Buff[] buffs = new Buff[5];
	public static void init(){
		initBuffs();
		initTowers();
		initMonsters();
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
		buffs[2] = new Buff(1, 0, 0.2, 1./GameState.getFPS(), "!");
		buffs[3] = new Buff(0.1, 1, 0.2, 1.2, "&");
	}
	private static Buff bWith(int id, double k){
		return buffs[id].setA(buffs[id].getFullDamage()*k, buffs[id].getSlow()*k, buffs[id].getChanse()*k, buffs[id].getDuration());
	}
	private static void initTowers(){
		towers[0][0] = new Tower(2, 3.0, 1.0, 70, 5, 1, 0, Color.BLACK, null);
		towers[0][1] = new Tower(7, 3.0, 1.0, 75, 15, 2,0, Color.BLACK, null);
		towers[0][2] = new Tower(61, 3.0, 1.0, 80, 130, 3, 0, Color.BLACK, null);
		towers[0][3] = new Tower(623, 3.0, 0.8, 100, 1500, 4, 0, Color.BLACK, null);
		towers[0][4] = new Tower(5479, 3.0, 0.6, 150, 10000, 5, 0, Color.BLACK, null);
		
		towers[1][0] = new Tower(15, 4.5, 1.5, 90, 30, 1, 1, Color.color(0.7, 0, 0), null);
		towers[1][1] = new Tower(72, 4.5, 1.5, 95, 110, 2, 1, Color.color(0.7, 0, 0), null);
		towers[1][2] = new Tower(607, 4.5, 1.5, 100, 1225, 3, 1, Color.color(0.7, 0, 0), null);
		towers[1][3] = new Tower(5280, 4.5, 1.4, 120, 8700, 4, 1, Color.color(0.7, 0, 0), null);
		towers[1][4] = new Tower(15823, 4.5, 1.3, 160, 26100, 5, 1, Color.color(0.7, 0, 0), null);
	
		towers[2][0] = new Tower(13, 3.14, 1.2, 90, 30, 1, 2, Color.color(0, 0.7, 0), buffs[0]);
		towers[2][1] = new Tower(47, 3.14, 1.15, 95, 110, 2, 2, Color.color(0, 0.7, 0), buffs[0]);
		towers[2][2] = new Tower(413, 3.14, 1.1, 100, 1225, 3, 2, Color.color(0, 0.7, 0), buffs[0]);
		towers[2][3] = new Tower(3164, 3.14, 1.05, 105, 8700, 4, 2, Color.color(0, 0.7, 0), buffs[0]);
		towers[2][4] = new Tower(9999, 3.14, 1.0, 110, 26100, 5, 2, Color.color(0, 0.7, 0), buffs[0]);
		
		towers[3][0] = new Tower(10, 5.7, 2.0, 90, 30, 1, 3, Color.color(0, 0, 0.7), bWith(1,1));
		towers[3][1] = new Tower(36, 5.7, 1.9, 95, 110, 2, 3, Color.color(0, 0, 0.7), bWith(1,1.2));
		towers[3][2] = new Tower(309, 5.7, 1.8, 100, 1225, 3, 3, Color.color(0, 0, 0.7), bWith(1,1.2));
		towers[3][3] = new Tower(1897, 5.7, 1.7, 120, 8700, 4, 3, Color.color(0, 0, 0.7), bWith(1,1.2));
		towers[3][4] = new Tower(5280, 5.7, 1.6, 160, 26100, 5, 3, Color.color(0, 0, 0.7), bWith(1,1.2));
		
		towers[4][0] = new Tower(111, 4.5, 1.34, 90, 450, 1, 4, Color.color(0.7, 0, 0.7), bWith(2,1));
		towers[4][1] = new Tower(349, 4.5, 1.34, 95, 1755, 2, 4, Color.color(0.7, 0, 0.7), bWith(2,1.2));
		towers[4][2] = new Tower(1096, 4.5, 1.34, 100, 6669, 3, 4, Color.color(0.7, 0, 0.7), bWith(2,1.2));
		towers[4][3] = new Tower(3442, 4.5, 1.34, 120, 24675, 4, 4, Color.color(0.7, 0, 0.7), bWith(2,1.2));
		towers[4][4] = new Tower(10812, 4.5, 1.34, 160, 86363, 5, 4, Color.color(0.7, 0, 0.7), bWith(2,1.2));
		
		towers[5][0] = new Tower(50, 5.5, 2.5, 90, 450, 1, 5, Color.color(0, 0.7, 0.7), bWith(3,1));
		towers[5][1] = new Tower(150, 5.5, 2.4, 95, 1755, 2, 5, Color.color(0, 0.7, 0.7), bWith(3,1.2));
		towers[5][2] = new Tower(450, 5.5, 2.3, 100, 6669, 3, 5, Color.color(0, 0.7, 0.7), bWith(3,1.2));
		towers[5][3] = new Tower(1350, 5.5, 2.2, 120, 24675, 4, 5, Color.color(0, 0.7, 0.7), bWith(3,1.2));
		towers[5][4] = new Tower(4050, 5.5, 2.1, 160, 86363, 5, 5, Color.color(0, 0.7, 0.7), bWith(3,1.2));
		
		towers[6][0] = new Tower(33, 4.5, 0.33, 90, 450, 1, 6, Color.color(0.7,0.7, 0), null);
		towers[6][1] = new Tower(186, 4.5, 0.31, 95, 1755, 2, 6, Color.color(0.7, 0.7, 0), null);
		towers[6][2] = new Tower(672, 4.5, 0.28, 100, 6669, 3, 6, Color.color(0.7, 0.7, 0), null);
		towers[6][3] = new Tower(2304, 4.5, 0.24, 120, 24675, 4, 6, Color.color(0.7, 0.7, 0), null);
		towers[6][4] = new Tower(7680, 4.5, 0.2, 160, 86363, 5, 6, Color.color(0.7, 0.7, 0), null);
		
	}
	private static void initMonsters(){
		monsters[0] = new Monster(3.5, 1.0, 1, 0);
		monsters[1] = new Monster(2.5, 1.5, 1.1, 1);
		monsters[2] = new Monster(6, 0.8, 1.5, 2);
	}

}
