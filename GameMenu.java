package thewaroftank.gui;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * 游戏菜单绘制类
 * 
 * @author Yun-Long
 *
 */
public class GameMenu {

	private static JPopupMenu gameMenu = null;

	/**
	 * 显示主菜单(懒汉式构建)
	 * @return
	 */
	public static JPopupMenu showMenu() {
		if (gameMenu == null) {
			new GameMenu();
		}
		return gameMenu;
	}

	/**
	 * 游戏主菜单内容
	 */
	private GameMenu() {
		gameMenu = new JPopupMenu();
		GameControlListener listener = GameControlListener.getGameCL();
		// 菜单项——新的游戏
		JMenuItem newGame = new JMenuItem("新的游戏(F1)");
		newGame.addActionListener(listener);
		newGame.setActionCommand("newGame");
		// 菜单项——继续游戏
		JMenuItem goonGame = new JMenuItem("读取存档(F2)");
		goonGame.addActionListener(listener);
		goonGame.setActionCommand("goonGame");
		goonGame.setEnabled(false);
		// 菜单项——保存游戏
		JMenuItem saveGame = new JMenuItem("保存游戏(F3)");
		saveGame.addActionListener(listener);
		saveGame.setActionCommand("saveGame");
		saveGame.setEnabled(false);
		// 菜单项——保存退出
		JMenuItem save_Exit = new JMenuItem("保存退出(F4)");
		save_Exit.addActionListener(listener);
		save_Exit.setActionCommand("save_Exit");
		save_Exit.setEnabled(false);
		// 菜单项——双人游戏
		JMenuItem addPlayer2 = new JMenuItem("双人游戏(F5)");
		addPlayer2.addActionListener(listener);
		addPlayer2.setActionCommand("addPlayer2");
		addPlayer2.setEnabled(false);
		// 菜单项——退出游戏
		JMenuItem exit = new JMenuItem("退出游戏(ESC)");
		exit.addActionListener(listener);
		exit.setActionCommand("exit");

		gameMenu.add(newGame, 0);
		gameMenu.add(goonGame, 1);
		gameMenu.add(saveGame, 2);
		gameMenu.add(save_Exit, 3);
		gameMenu.add(addPlayer2, 4);
		gameMenu.addSeparator();
		gameMenu.add(exit);

		// gameMenu.setPopupSize(100, 150);//设置弹出菜单尺寸
		gameMenu.setLightWeightPopupEnabled(true);// 轻量级（纯 Java 的）弹出菜单

	}
}
