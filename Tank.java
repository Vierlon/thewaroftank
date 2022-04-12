package bin;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.Random;

import bin.Players;
import bin.enums.BulletMode;
import bin.enums.Direction;
import bin.gui.GameControlListener;
import config.Config;

/**
 * 坦克类
 * 
 * @author WuYaoLong
 */
public class Tank extends GameElements implements Runnable,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2017100721L;
	private int speed; // 移动速度
	private boolean moveEnable; // 移动控制,true为可移动
	private boolean fireEnable; // 射击控制,true为可射击
	private Direction direction; // 坦克炮口方向
	private BulletMode bltMode = BulletMode.PLAIN; // 坦克炮弹类型,默认为普通
	private Players owner = null; // 坦克所有者,PC坦克为null
	private Random rd = Config.RD; //获随机数生成器
	private int count = 1; //自动开火控制计数器
	private int initHP = 1; //坦克初始生命值

	/**
	 * 无参构造方法
	 */
	public Tank() {
		super();
	}

	/**
	 * 有参构造方法
	 * 
	 * @param x
	 *            初始坐标X值
	 * @param y
	 *            初始坐标Y值
	 * @param health
	 *            初始生命值
	 * @param speed
	 *            初始速度
	 * @param moveEnalbe
	 *            可否移动
	 * @param fireEnalbe
	 *            可否射击
	 * @param color
	 *            坦克颜色
	 * @param direction
	 *            初始方向
	 */
	public Tank(int x, int y, int health, int speed, boolean moveEnable, boolean fireEnable, Color color,
			Direction direction) {
		this();
		this.setX(x);
		this.setY(y);
		this.setHealth(health);
		this.setColor(color);
		this.setWidth(Config.TANK_W_UP);
		this.setHigh(Config.TANK_H_UP);
		this.initHP = health;
		this.speed = speed;
		this.moveEnable = moveEnable;
		this.fireEnable = fireEnable;
		this.direction = direction;
	}

	/**
	 * 坦克开火方法
	 */
	public void fire() {
		if (!this.fireEnable) {
			return;
		}
		Direction dir = this.direction;
		Bullet blt = new Bullet(); // 创建子弹
		blt.setOwner(this.owner); //设置子弹所有者
		blt.setMode(this.bltMode); // 设置子弹类型
		blt.setSpeed(this.speed+2); //设置子弹速度
		blt.setDirection(dir); //设置子弹方向
		blt.setMoveEnable(true); //可移动
		int x = this.getX(), y = this.getY();
		int W = Config.TANK_W_UP, H = Config.TANK_H_UP;
		int w_up = Config.BULLET_W_UP, h_up = Config.BULLET_H_UP;
		if (dir == Direction.UP || dir == Direction.DOWN) {
			blt.setX((x + W / 2 - W / 32) - w_up / 2 + W / 32); // 上下方向时,X值相同=炮管X-子弹W/2+炮管W/2
			if (dir == Direction.UP) {
				blt.setY(y - (H / 2 - W / 4) / 2 - h_up); // 向上时,子弹的Y=炮管Y-子弹H
			} else {
				blt.setY(y + H / 2 + W / 4 + (H / 2 - W / 4) * 3 / 2); // 向下时,子弹的Y=炮管Y+炮管H
			}
		} else {
			blt.setY((y + W / 2 - W / 32) - w_up / 2 + W / 32); // 与上下翻转90度,x与y互换
			if (dir == Direction.LEFT) {
				blt.setX(x - (H / 2 - W / 4) / 2 - h_up);
			} else {
				blt.setX(x + H / 2 + W / 4 + (H / 2 - W / 4) * 3 / 2);
			}
		}
		blt.setVisible(true); //可见
		Config.BULLETS_SET.add(blt); // 子弹初始化完成,添加进集合由绘图线程绘图
		new Thread(blt).start(); //启动自动移动
		if(this.owner != null && Config.SHEJI != null) {
			Applet.newAudioClip(Config.SHEJI).play(); //播放射击声音
		}
	}

	/**
	 * 坦克移动方法
	 */
	public void move() {
		if (!moveEnable) {
			return;
		}
		int x = this.getX(), y = this.getY();
		if (this.direction == Direction.UP && !this.isTouch()) {
			if (y > speed + 5) {
				y -= speed;
			} else if (y > 5) { // 控制坦克不超出边界
				y -= 1;
			}
			this.setY(y);
		} else if (this.direction == Direction.DOWN && !this.isTouch()) {
			if (y < (Config.GAME_FRAME_H - Config.TANK_H_UP - 5 - speed)) {
				y += speed;
			} else if (y < Config.GAME_FRAME_H - Config.TANK_H_UP - 5) {
				y += 1;
			}
			this.setY(y);
		} else if (this.direction == Direction.LEFT && !this.isTouch()) {
			if (x > speed + 5) {
				x -= speed;
			} else if (x > 5) {
				x -= 1;
			}
			this.setX(x);
		} else if (!this.isTouch()) {
			if (x < Config.GAME_FRAME_W - Config.TANK_H_UP - 5 + speed) {
				x += speed;
			} else if (x < Config.GAME_FRAME_W - Config.TANK_H_UP - 5) {
				x += 1;
			}
			this.setX(x);
		}
	}

	/**
	 * 判断是否有碰撞
	 */
	private synchronized boolean isTouch() {
		Direction dir = this.direction;
		int x = this.getX(), y = this.getY();
		int w = this.getWidth(), h = this.getHigh();
		for(int i = 0;i < Config.TANK_SET.size();i ++) {
			Tank tk = null;
			try {
				tk = Config.TANK_SET.get(i);
			} catch (Exception e) {} //避免数组下标越界异常造成线程中断
			if (tk != null && tk.isVisible() && (!tk.equals(this))) { // 除自己之外的元素
				if (x > tk.getX() - w && x < tk.getX() + tk.getWidth()) {
					if (dir == Direction.UP && y <= tk.getY() + tk.getHigh() &&
							y > tk.getY() + tk.getHigh() - 5) {
						return true;
					} else if (dir == Direction.DOWN && y >= tk.getY() - h - 1 && y < tk.getY() - h + 5) {
						return true;
					}
				}
				if (y > tk.getY() - h && y < tk.getY() + tk.getHigh()) {
					if (dir == Direction.LEFT && x <= tk.getX() + tk.getWidth() + 2
							&& x > tk.getX() + tk.getWidth() - 5) {
						return true;
					} else if (dir == Direction.RIGHT && x >= tk.getX() - w - 2 & x < tk.getX() - w + 5) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 坦克画法
	 */
	@Override
	public void draw(Graphics g) {
		g.setColor(this.getColor());
		int x = this.getX(), y = this.getY();
		int H = this.getHigh(), W = this.getWidth();
		if (this.direction == Direction.UP || this.direction == Direction.DOWN) {
			g.fill3DRect(x, y, W / 4, H, true);
			g.fill3DRect(x + 3 * W / 4, y, W / 4, H, true);
			g.fill3DRect(x + W / 4, y + H / 2 - W / 4, W / 2, W / 2, true);
			if (this.direction == Direction.UP) {
				g.fill3DRect(x + W / 2 - W / 32, y - (H / 2 - W / 4) / 2, W / 16, (H / 2 - W / 4) * 3 / 2, true);
			} else {
				g.fill3DRect(x + W / 2 - W / 32, y + H / 2 + W / 4, W / 16, (H / 2 - W / 4) * 3 / 2, true);
			}
		} else if (this.direction == Direction.LEFT || this.direction == Direction.RIGHT) {
			g.fill3DRect(x, y, H, W / 4, true);
			g.fill3DRect(x, y + 3 * W / 4, H, W / 4, true);
			g.fill3DRect(x + H / 2 - W / 4, y + W / 4, W / 2, W / 2, true);
			if (this.direction == Direction.LEFT) {
				g.fill3DRect(x - (H / 2 - W / 4) / 2, y + W / 2 - W / 32, (H / 2 - W / 4) * 3 / 2, W / 16, true);
			} else {
				g.fill3DRect(x + H / 2 + W / 4, y + W / 2 - W / 32, (H / 2 - W / 4) * 3 / 2, W / 16, true);
			}
		}
		if(this.owner != null) { //画玩家标识
			g.drawString(this.getOwner().getPL().toString(), x+H/5, y+H+27);
		}
		if(this.getHealth() > 0) { //画血条
			g.setColor(Color.RED);
			g.drawRect(x-H/2+this.getWidth()/2, y+H+10, H, 5);
			g.fillRect(x-H/2+this.getWidth()/2, y+H+10, H*this.getHealth()/this.initHP, 5);
		}
	}
	
	/**
	 * PC坦克自动移动,开火线程
	 */
	@Override
	public void run() {
		while(GameControlListener.flag&&this.getHealth()>0) { //坦克死亡线程结束
			if(GameControlListener.isPause) {
				try {
					Thread.sleep(100);
				}catch (InterruptedException e) {}
				continue;
			}
			try {
				if(this.moveEnable) {
					this.move();
				}
				if(this.fireEnable && ++count%30==0) { //PC坦克开火频率
					this.fire();
				}
				if (this.moveEnable && count%60==0) { //PC坦克换方向频率
					count = 1;
					switch (rd.nextInt(4)) { //随机设置PC坦克方向
					case 0:
						this.direction=Direction.UP;
						break;
					case 1:
						this.direction=Direction.DOWN;
						break;
					case 2:
						this.direction=Direction.LEFT;
						break;
					case 3:
						this.direction=Direction.RIGHT;
						break;
					}
				}
				Thread.sleep(30);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Config.TANK_SET.remove(this); //PC坦克死亡后从集合中移除自己
		System.out.println("删除死亡坦克!剩余数量= " + (Config.TANK_SET.size()-2)); //测试坦克删除
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public boolean isMoveEnable() {
		return moveEnable;
	}

	public void setMoveEnable(boolean moveEnable) {
		this.moveEnable = moveEnable;
	}

	public boolean isFireEnable() {
		return fireEnable;
	}

	public void setFireEnable(boolean fireEnable) {
		this.fireEnable = fireEnable;
	}

	public BulletMode getBltMode() {
		return bltMode;
	}

	public void setBltMode(BulletMode bltMode) {
		this.bltMode = bltMode;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public Players getOwner() {
		return owner;
	}

	public void setOwner(Players owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		return owner + ":[" + moveEnable + "," + fireEnable + "," + direction + "," + getHealth() + "," + getSpeed()
				+ "]";
	}

}
