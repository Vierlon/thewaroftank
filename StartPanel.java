package thewaroftank.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

import thewaroftank.config.Config;

/**
 * <li>游戏启动初始面板
 * <li>只会显示一次,开始游戏后置空引用,关闭线程
 * @author Yun-Long
 *
 */
public class StartPanel extends JPanel implements Runnable {

	private static final long serialVersionUID = 2L;
	private static StartPanel sp = null;
	private static boolean flag = true; //终止线程标识
	private int count; //计数器,用于闪烁

	private StartPanel() {}
	/**
	 * 懒汉单例,只首次调用创建对象并启动线程
	 * @return
	 */
	public static StartPanel showStartPanel() {
		if(sp == null) {
			sp = new StartPanel();
			sp.setBackground(Config.PANEL_BG_COLOR);
			new Thread(sp).start();
		}
		return sp;
	}
	/**
	 * 开始面板画法,由线程调用repaint方法执行
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		MainGamePanel.drawPanel(g);
		int W=Config.GAME_FRAME_W,H=Config.GAME_FRAME_H;
		if(count%2==0){
			count = 0;
			g.setColor(Color.ORANGE);
			g.setFont(new Font("华文行楷",Font.BOLD,W/10));
			g.drawString("坦克大战",W/2-W/5-W/65,3*H/7);
		}
		g.setColor(Color.GRAY);
		g.fill3DRect(W/2-W/5-10,H/2,2*W/5+20,H/60,true);
		g.setColor(new Color(35,150,0));
		g.setFont(new Font("宋体",Font.BOLD,W/50));
		g.drawString("F1:开始游戏  F2:读取存档  F8:游戏菜单",W/2-W/5+W/180,4*H/7);
	}
	/**
	 * 开始面板绘画线程
	 */
	@Override
	public void run() {
		while(flag){
			try{
				Thread.sleep(600);
				count++;
				repaint();
			}catch(Exception e){}
		}
	}
	
}
