package bin.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JPanel;

import bin.Bullet;
import bin.ElementFactory;
import bin.Players;
import bin.Tank;
import bin.enums.Direction;
import config.Config;

/**
 * 游戏操作主面板绘图类
 * 
 * @author WuYaoLong
 *
 */
public class MainGamePanel extends JPanel implements Runnable {

	private static final long serialVersionUID = 3L;
	private Vector<Tank> set_T = Config.TANK_SET;
	private Vector<Bullet> set_B = Config.BULLETS_SET;
	private Players pl1 = GameControlListener.PL1;
	private Players pl2 = GameControlListener.PL2;
	private Tank tkMode = new Tank(); //坦克模型(画画用)
	private Color labelColor = new Color(160, 160, 160);
	private Color dataColor = new Color(150, 150, 0);
	
	private static MainGamePanel mgp = null;
	
	private MainGamePanel() {}
	
	/**
	 * 显示游戏区域
	 * @return
	 */
	public static MainGamePanel showGameArea() {
		if(mgp == null) {
			mgp = new MainGamePanel();
			mgp.setBackground(Config.PANEL_BG_COLOR);
			System.out.println("--->构造MainGamePanel<---");
		}
		return mgp;
	}
	
	/**
	 * 游戏区域各元素绘图方法
	 */
	@Override
	public void paint(Graphics g){
		super.paint(g);
		//画出底板(可护展加载游戏背景)
		MainGamePanel.drawPanel(g);
		//画出可见有生命值的坦克
		for (int i = 0;i < set_T.size();i++) {
			Tank tk = null;
			try {
				tk = set_T.get(i);
			} catch (Exception e) { //跳过异常
				e.printStackTrace();
			}
			if(tk != null && tk.isVisible() && tk.getHealth()>0) {
				tk.draw(g);
			}
		}
		//画出可见的子弹
		for (int i = 0;i < set_B.size();i++) {
			Bullet bt = null;
			try {
				bt  = set_B.get(i);
			} catch (Exception e) { //跳过异常
				e.printStackTrace();
			}
			if(bt != null && bt.isVisible()) {
				bt.draw(g);
			}
		}
		//画出游戏实时信息(玩家剩余命数,当前得分,总分;当前关卡,剩余PC坦克数)
		this.drawGameInfo(g);
	}
	
	/**
	 * 游戏区域底板和边界画法
	 * @param g
	 */
	public static void drawPanel(Graphics g){
		int W=Config.GAME_FRAME_W;
		int H=Config.GAME_FRAME_H;
		g.setColor(Color.BLACK);
		g.fillRect(5,5,W-10,H-10);
		g.drawLine(0,0,5,5);
		g.drawLine(0,H,5,H-5);
		g.drawLine(W,H,W-5,H-5);
		g.drawLine(W,0,W-5,5);
	}

	/**
	 * 游戏实时信息画法
	 * 玩家剩余命数,当前得分,总分和历史最高分;当前关卡,剩余PC坦克数
	 */
	private void drawGameInfo(Graphics gps) {
		//数据准备
		int life1 = pl1.getLifes();
		int score1 = pl1.getScore();
		int life2 = pl2.getLifes();
		int score2 = pl2.getScore();
		int max = Config.maxScore;
		max = max>score1+score2?max:score1+score2;
		int lv = ElementFactory.createEF().getLevel();
		int tkNums = set_B.size()==0?0:set_T.size()-2;
		tkMode.setColor(dataColor);
		tkMode.setDirection(Direction.RIGHT);
		tkMode.setWidth(17); tkMode.setHigh(20);
		gps.setFont(new Font("楷体", Font.BOLD, 20));
		//开始画
		gps.setColor(labelColor);
		gps.drawString("玩家I:", 10, 25);
		gps.drawString(" 得分:", 120, 25);
		gps.drawString("玩家II:", 250, 25); //约1/3处
		gps.drawString(" 得分:", 370, 25);
		gps.drawString(" 总分:", 490, 25);
		gps.drawString(" 最高分:", 620, 25);
		gps.drawString("当前:", 20, 590);
		gps.drawString("敌人:", 655, 590);
		gps.setColor(dataColor);
		tkMode.setY(10);
		tkMode.setX(75); tkMode.draw(gps);
		gps.drawString(" X"+life1, 95, 25);
		gps.drawString(score1+"", 185, 25);
		tkMode.setX(325); tkMode.draw(gps);
		gps.drawString(" X"+life2, 345, 25);
		gps.drawString(score2+"", 435, 25);
		gps.drawString(score1+score2+"", 555, 25);
		gps.drawString(max+"", 705, 25);
		gps.drawString("第 "+lv+" 关", 85, 590);
		tkMode.setY(575);
		tkMode.setX(715); tkMode.draw(gps);
		gps.drawString(" X"+tkNums, 735, 590);
	}
	
	/**
	 * 游戏区域各元素绘图线程
	 */
	@Override
	public void run() {
		System.out.println("--->gameAreaDraw线程启动<---");
		while(GameControlListener.flag){
			if(GameControlListener.isPause) {
				try {
					Thread.sleep(100);
				}catch (InterruptedException e) {}
				continue;
			}
			try{
				repaint();
				Thread.sleep(20);
			}catch(Exception e){}
		}
		System.out.println("--->gameAreaDraw线程结束<---");
	}

	public void setPl1(Players pl1) {
		this.pl1 = pl1;
	}

	public void setPl2(Players pl2) {
		this.pl2 = pl2;
	}
	
}
