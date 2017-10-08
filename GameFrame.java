package thewaroftank.gui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import thewaroftank.config.Config;

/**
 * 游戏窗口生成类
 * @author Yun-Long
 *
 */
public class GameFrame {

	private static JFrame mainFrame = null;
	
	public static JFrame getGameFrame() {
		if(mainFrame == null) {
			new GameFrame();
		}
		return mainFrame;
	}
	
	private GameFrame() {
		mainFrame = new JFrame();
		mainFrame.setTitle("坦克大战");
		mainFrame.setAlwaysOnTop(true);//设置窗体总在最上层
		mainFrame.setIconImage(new ImageIcon(Config.IMAGE_FILE_PATH+"Tank.png").getImage());//设置图标
		mainFrame.setBounds(Config.SYSTEM_SCREEN_SIZE.width/2-Config.GAME_FRAME_W/2,
				Config.SYSTEM_SCREEN_SIZE.height/2-Config.GAME_FRAME_H/2,
				Config.GAME_FRAME_W,Config.GAME_FRAME_H);//设置窗体初始位置为屏幕正中间
		mainFrame.setUndecorated(true);//去除窗体装饰（边框）
//		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);//点击红叉无操作
//		mainFrame.getRootPane().setWindowDecorationStyle(JRootPane.WARNING_DIALOG);//设置指定窗体风格
		GameControlListener gcl=GameControlListener.getGameCL();
		mainFrame.addKeyListener(gcl);//注册键盘侦听器
		mainFrame.addMouseListener(gcl);//注册鼠标点击侦听器
		mainFrame.addMouseMotionListener(gcl);//窗体拖动侦听
		mainFrame.add(StartPanel.showStartPanel());
	}
	
}
