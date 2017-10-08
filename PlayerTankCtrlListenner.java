package bin.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import bin.Tank;
import bin.enums.Direction;
import bin.enums.Player;
import config.Config;

/**
 * 玩家坦克移动,开火事件的开关控制
 * 
 * @author WuYaoLong
 *
 */
public class PlayerTankCtrlListenner implements KeyListener {

	private Player player;
	private Tank tk;

	public PlayerTankCtrlListenner(Player pl) {
		player = pl;
		if (pl == Player.PL1) {
			tk = Config.TANK_SET.get(0);
		} else {
			tk = Config.TANK_SET.get(1);
		}
		System.out.println("--->构造PlayerTankCtrlListenner-"+pl+"<---");
	}

	/**
	 * 玩家各方向移动,开火控制开启
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		switch (player) {
		case PL1:
			if(e.getKeyCode() == Config.P1_FIRE) {
				tk.setFireEnable(true);
			}
			if (e.getKeyCode() == Config.P1_UP) {
				tk.setDirection(Direction.UP);
				GameControlListener.p1_up_state=true;
			} else if (e.getKeyCode() == Config.P1_DOWN) {
				tk.setDirection(Direction.DOWN);
				GameControlListener.p1_down_state=true;
			} else if (e.getKeyCode() == Config.P1_LEFT) {
				tk.setDirection(Direction.LEFT);
				GameControlListener.p1_left_state=true;
			} else if (e.getKeyCode() == Config.P1_RIGHT) {
				tk.setDirection(Direction.RIGHT);
				GameControlListener.p1_right_state=true;
			}
			break;
		case PL2:
			if(e.getKeyCode() == Config.P2_FIRE) {
				tk.setFireEnable(true);
			}
			if (e.getKeyCode() == Config.P2_UP) {
				tk.setDirection(Direction.UP);
				GameControlListener.p2_up_state=true;
			} else if (e.getKeyCode() == Config.P2_DOWN) {
				tk.setDirection(Direction.DOWN);
				GameControlListener.p2_down_state=true;
			} else if (e.getKeyCode() == Config.P2_LEFT) {
				tk.setDirection(Direction.LEFT);
				GameControlListener.p2_left_state=true;
			} else if (e.getKeyCode() == Config.P2_RIGHT) {
				tk.setDirection(Direction.RIGHT);
				GameControlListener.p2_right_state=true;
			}
			break;
		}
	}
	/**
	 * 玩家各方向移动,开火控制关闭
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		switch (player) {
		case PL1:
			if(e.getKeyCode() == Config.P1_FIRE) {
				tk.setFireEnable(false);
			}
			if (e.getKeyCode() == Config.P1_UP) {
				GameControlListener.p1_up_state=false;
			} else if (e.getKeyCode() == Config.P1_DOWN) {
				GameControlListener.p1_down_state=false;
			} else if (e.getKeyCode() == Config.P1_LEFT) {
				GameControlListener.p1_left_state=false;
			} else if (e.getKeyCode() == Config.P1_RIGHT) {
				GameControlListener.p1_right_state=false;
			}
			break;
		case PL2:
			if(e.getKeyCode() == Config.P2_FIRE) {
				tk.setFireEnable(false);
			}
			if (e.getKeyCode() == Config.P2_UP) {
				GameControlListener.p2_up_state=false;
			} else if (e.getKeyCode() == Config.P2_DOWN) {
				GameControlListener.p2_down_state=false;
			} else if (e.getKeyCode() == Config.P2_LEFT) {
				GameControlListener.p2_left_state=false;
			} else if (e.getKeyCode() == Config.P2_RIGHT) {
				GameControlListener.p2_right_state=false;
			}
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}
