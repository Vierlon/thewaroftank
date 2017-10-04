package thewaroftank.config;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Dimension;
//import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.Vector;

//import thewaroftank.WarOfTank;
import thewaroftank.program.Bullet;
import thewaroftank.program.Tank;

/**
 * 游戏配置接口
 * @author Yun-Long
 */
public interface Config {

	public static final String FOLDER_PATH = WarOfTank.class.getResource("").getPath();// 程序主文件所在文件夹路径
	public static final String IMAGE_FILE_PATH = FOLDER_PATH + "image/";// 图片文件路径
	public static final String SOUND_FILE_PATH = FOLDER_PATH + "Sounds/";// 声音文件路径
	public static final Dimension SYSTEM_SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();// 系统屏幕像素尺寸
	public static final int GAME_FRAME_W = 800;// 游戏默认界面宽度
	public static final int GAME_FRAME_H = 600;// 游戏默认界面高度
	public static final int TANK_H_UP = GAME_FRAME_W / 20;// 向上坦克默认尺寸
	public static final int TANK_W_UP = TANK_H_UP * 7 / 8;
	public static final int BULLET_W_UP = TANK_W_UP*3/20; // 向上子弹的默认尺寸
	public static final int BULLET_H_UP = BULLET_W_UP*2;
	public static final Vector<Tank> TANK_SET = new Vector<>(100);// 非子弹元素集合,固定容量100
	public static final Vector<Bullet> BULLETS_SET = new Vector<>(); // 生命值大于0的子弹集合
	public static final int P1_INIT_X = GAME_FRAME_W / 3 - TANK_W_UP / 2;// 玩家新游戏初始位置
	public static final int P2_INIT_X = P1_INIT_X + GAME_FRAME_W / 3;
	public static final int P_INTI_Y = GAME_FRAME_H - TANK_H_UP - 5;
	public static final int PC_INIT_X1 = GAME_FRAME_W/4-TANK_W_UP/2; //PC坦克出口
	public static final int PC_INIT_X2 = GAME_FRAME_W/2-TANK_W_UP/2;
	public static final int PC_INIT_X3 = GAME_FRAME_W*3/4-TANK_W_UP/2;
	public static final int PC_INIT_Y = 5;
	public static final Color PANEL_BG_COLOR = new Color(250, 125, 15);// 面板背景颜色
	public static final Color TANK_COLOR_0 = Color.GRAY; // 1级PC坦克颜色
	public static final Color TANK_COLOR_1 = new Color(20, 130, 20); // 2级PC坦克颜色
	public static final Color TANK_COLOR_2 = new Color(185, 185, 0); // 玩家坦克颜色 // 3级PC坦克颜色
	public static final Color TANK_COLOR_3 = new Color(80, 80, 255); // 4级PC坦克颜色
	public static final Color TANK_COLOR_4 = new Color(210, 105, 0); // 5级PC坦克颜色
	public static final Color TANK_COLOR_5 = new Color(115, 20, 170); // 6级PC坦克颜色
	public static final Color TANK_COLOR_6 = new Color(200, 0, 25); // 7级PC坦克颜色
	public static final int P1_UP = KeyEvent.VK_W;// 玩家1默认控制键
	public static final int P1_DOWN = KeyEvent.VK_S;
	public static final int P1_LEFT = KeyEvent.VK_A;
	public static final int P1_RIGHT = KeyEvent.VK_D;
	public static final int P1_FIRE = KeyEvent.VK_J;
	public static final int P2_UP = KeyEvent.VK_UP;// 玩家2默认控制键
	public static final int P2_DOWN = KeyEvent.VK_DOWN;
	public static final int P2_LEFT = KeyEvent.VK_LEFT;
	public static final int P2_RIGHT = KeyEvent.VK_RIGHT;
	public static final int P2_FIRE = KeyEvent.VK_NUMPAD1;
	public static final Random RD = new Random(); //随机数生成器

}
