package thewaroftank.program;

import java.awt.Color;

import thewaroftank.config.Config;
import thewaroftank.program.enums.Player;

public class Players {
	
	private volatile int lifes = 0; // 玩家生命数,归0则GameOver
	private Player PL; //玩家名字
	private volatile int score; // 玩家当前游戏分数
	
	public Players(Player PL) {
		this.PL = PL;
	}
	
	public int getLifes() {
		return lifes;
	}

	public void setLifes(int lifes) {
		this.lifes = lifes;
	}
	
	public Player getPL() {
		return PL;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int i) {
		score = i;
	}
	
	/**
	 * 玩家计分方法
	 * @param tk 被击毁的坦克
	 */
	public void setScore(Tank tk) {
		Color color = tk.getColor();
		int score = 0;
		if(color == Config.TANK_COLOR_0) {
			score = 10;
		}else if(color == Config.TANK_COLOR_1) {
			score = 20;
		}else if(color == Config.TANK_COLOR_2) {
			score = 50;
		}else if(color == Config.TANK_COLOR_3) {
			score = 100;
		}else if(color == Config.TANK_COLOR_4) {
			score = 200;
		}else if(color == Config.TANK_COLOR_5) {
			score = 500;
		}else if(color == Config.TANK_COLOR_6) {
			score = 1000;
		}
		this.score += score;
	}

	@Override
	public String toString() {
		return  PL + "-剩余生命" + lifes + "-当前得分" + score ;
	}

	
}
