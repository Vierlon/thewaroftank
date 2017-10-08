package bin;

import java.applet.Applet;
import java.awt.Graphics;
import java.io.Serializable;

import bin.Players;
import bin.enums.BulletMode;
import bin.enums.Direction;
import bin.gui.GameControlListener;
import config.Config;

/**
 * 子弹类
 * 
 * @author WuYaoLong
 *
 */
public class Bullet extends GameElements implements Runnable,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2017100518L;
	private int speed; // 子弹速度初始为2
	private boolean moveEnable;
	private Direction direction;
	private BulletMode mode = BulletMode.PLAIN;
	private Players owner = null; // 子弹主人

	/**
	 * 无参构造方法,默认生命值为1
	 */
	public Bullet() {
		super();
		this.setHealth(1); // 设置子弹默认生命值为1
	}

	/**
	 * 有参构造方法,默认生命值为1
	 * 
	 * @param x
	 *            坐标X值
	 * @param y
	 *            坐标Y值
	 * @param speed
	 *            子弹速度
	 * @param dir
	 *            射击方向
	 */
//	public Bullet(int x, int y, Direction dir) {
//		this.setX(x);
//		this.setY(y);
//		this.setHealth(1); // 设置子弹默认生命值为1
//		this.direction = dir;
//	}

	/**
	 * 子弹移动方法
	 */
	private void move() {
		if (!moveEnable) {
			return;
		}
		int x = this.getX(), y = this.getY();
		if (this.direction == Direction.UP && !this.isTouch()) {
			if (y > speed + 5) {
				y -= speed;
			} else if (y > 5) { // 控制子弹不超出边界
				y -= 1;
			}
			if (y <= 5) {
				this.setHealth(0); // 击中边界后消失
				return;
			}
			this.setY(y);
		} else if (this.direction == Direction.DOWN && !this.isTouch()) {
			if (y < (Config.GAME_FRAME_H - Config.BULLET_H_UP - 5 - speed)) {
				y += speed;
			} else if (y < Config.GAME_FRAME_H - Config.BULLET_H_UP - 5) {
				y += 1;
			}
			if (y >= Config.GAME_FRAME_H - Config.BULLET_H_UP - 5) {
				this.setHealth(0);
				return;
			}
			this.setY(y);
		} else if (this.direction == Direction.LEFT && !this.isTouch()) {
			if (x > speed + 5) {
				x -= speed;
			} else if (x > 5) {
				x -= 1;
			}
			if (x <= 5) {
				this.setHealth(0);
				return;
			}
			this.setX(x);
		} else if (this.direction == Direction.RIGHT && !this.isTouch()) {
			if (x < Config.GAME_FRAME_W - Config.BULLET_H_UP - 5 + speed) {
				x += speed;
			} else if (x < Config.GAME_FRAME_W - Config.BULLET_H_UP - 5) {
				x += 1;
			}
			if (x >= Config.GAME_FRAME_W - Config.BULLET_H_UP - 5) {
				this.setHealth(0);
				return;
			}
			this.setX(x);
		}
	}

	/**
	 * 子弹击中处理,击中扣减自身HP和敌方HP
	 */
	private synchronized boolean isTouch() {
		Direction dir = this.direction;
		int x = this.getX(), y = this.getY();
		int w = this.getWidth(), h = this.getHigh();
		for(int i = 0;i < Config.TANK_SET.size();i++) {
			Tank tk = null; 
			try {
				tk = Config.TANK_SET.get(i);
			} catch (Exception e) {} //避免数组下标越界造成线程中断
			if(tk == null || !tk.isVisible() || tk.getHealth() == 0) { //异常坦克不碰撞
				continue;
			}
			if (dir == Direction.UP || dir == Direction.DOWN) { //没有碰撞
				if (x <= tk.getX() - w || x >= tk.getX() + tk.getWidth()){
					continue;
				}else if(y>tk.getY() + tk.getHigh() || y < tk.getY() - h) {
					continue;
				}
			}
			if (dir == Direction.LEFT || dir == Direction.RIGHT) { //没有碰撞
				if (y <= tk.getY() - h || y >= tk.getY() + tk.getHigh()) {
					continue;
				}else if(x > tk.getX() + tk.getWidth() || x<tk.getX() - w) {
					continue;
				}
			}
			//以下为子弹击中有效目标的处理
			if(this.owner != null && Config.JIZHONG != null) {
				Applet.newAudioClip(Config.JIZHONG).play(); //播放击中声音
			}
			if (owner == null ^ tk.getOwner() == null) { // 子弹与坦克阵营不同则坦克扣减减HP
				tk.setHealth(tk.getHealth() - 1);
				if (tk.getHealth() == 0) {
					if (Config.BAOZHA != null) {
						Applet.newAudioClip(Config.BAOZHA).play(); //播放爆炸声音
					}
					if (owner != null) {
						owner.setScore(tk); // 玩家击毁计分
						System.out.println("玩家击毁得分:"+owner); //测试玩家分数,生命计算
					}
					tk.setVisible(false);
					tk.setMoveEnable(false);
					tk.setFireEnable(false);
				}
			}
			this.setVisible(false);
			this.setHealth(this.getHealth() - 1); // 子弹扣减HP
			return true;
		}
		return false;
	}

	/**
	 * 子弹画法
	 */
	@Override
	public void draw(Graphics g) {
		g.setColor(this.getColor());
		int x = this.getX();
		int y = this.getY();
		int w_up = Config.BULLET_W_UP;
		int h_up = Config.BULLET_H_UP;
		Direction dir = this.getDirection();
		if (dir == Direction.UP || dir == Direction.DOWN) {
			g.fillOval(x, y, w_up, h_up);
		} else {
			g.fillOval(x, y, h_up, w_up);
		}
	}
	
	/**
	 * 子弹自动移动线程
	 */
	@Override
	public void run() {
		while(GameControlListener.flag&&this.getHealth()>0) {
			if(GameControlListener.isPause) {
				try {
					Thread.sleep(100);
				}catch (InterruptedException e) {}
				continue;
			}
			try {
				this.move();
				Thread.sleep(30);
			} catch (Exception e) {
				e.printStackTrace();
				this.setVisible(false); //出现任何异常,都将子弹设置为不可见
			}
		}
		Config.BULLETS_SET.remove(this); //子弹生命为0后从集合中移除自己
//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException e) {}
//		if(jizhong != null) {
//			jizhong.stop();
//		}
//		if(baozha != null) {
//			baozha.stop();
//		}
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public BulletMode getMode() {
		return mode;
	}

	public void setMode(BulletMode mode) {
		switch (mode) {
		case PLAIN:
			this.setColor(Config.TANK_COLOR_2);
			break;
		case PIERCED:
			this.setColor(Config.TANK_COLOR_6);
			break;
		case THREE_FIRE:
			this.setColor(Config.TANK_COLOR_4);
			break;
		case THREE_SPLIT:
			this.setColor(Config.TANK_COLOR_1);
			break;
		}
		this.mode = mode;
	}

	public Players getOwner() {
		return owner;
	}

	public void setOwner(Players owner) {
		this.owner = owner;
	}

	public boolean isMoveEnable() {
		return moveEnable;
	}

	public void setMoveEnable(boolean moveEnable) {
		this.moveEnable = moveEnable;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

}
