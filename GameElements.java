package thewaroftank.program;

import java.awt.Color;
import java.awt.Graphics;

import thewaroftank.config.Config;

/**
 * 游戏中所有元素的抽象父类
 * @author Yun-Long
 */
public abstract class GameElements {
	
	private int x; //元素在游戏面板中的x轴坐标
	private int y; //元素在游戏面板中的y轴坐标
	private int width; //元素在游戏面板中由坐标点在x轴的延伸长度
	private int high; //元素在游戏面板中由坐标点在y轴的延伸长度
	private Color color = Config.TANK_COLOR_0; //元素的颜色,默认0
	private int health; //元素生命值
	private boolean isVisible = false; //元素显示状态,默认为false不可见的

	/**
	 * 元素的画法
	 * @param g 传入一个画笔
	 */
	public abstract void draw(Graphics g);
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHigh() {
		return high;
	}

	public void setHigh(int high) {
		this.high = high;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

}
