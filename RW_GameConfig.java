package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import bin.gui.GameControlListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * 用于加载或保存游戏配置
 * @author WuYaoLong
 *	游戏配置(P1和P2的操作键设置)
 *	玩家历史最高分
 *
 */
public abstract class RW_GameConfig {

	private static File gameConfig = new File(Config.DATA_PATH + "GameConfig.dat");
	
	/**
	 * 保存配置及历史最高分信息
	 * @return 成功返回true
	 */
	public static boolean save() {
		if(gameConfig.exists() && !gameConfig.canWrite()) {
			return false;
		}
		try(DataOutputStream dos = new DataOutputStream(new FileOutputStream(gameConfig))) {
			System.out.println("--->开始保存游戏配置信息<---");
			dos.writeInt(Config.P1_UP);
			dos.writeInt(Config.P1_DOWN);
			dos.writeInt(Config.P1_LEFT);
			dos.writeInt(Config.P1_RIGHT);
			dos.writeInt(Config.P1_FIRE);
			dos.writeInt(Config.P2_UP);
			dos.writeInt(Config.P2_DOWN);
			dos.writeInt(Config.P2_LEFT);
			dos.writeInt(Config.P2_RIGHT);
			dos.writeInt(Config.P2_FIRE);
			int max = GameControlListener.PL1.getScore()+GameControlListener.PL2.getScore();
			dos.writeInt(max>Config.maxScore?max:Config.maxScore);
			dos.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("--->游戏配置保存成功<---");
		return true;
	}
	
	/**
	 * 读取配置及历史最高分信息
	 * @return 成功返回true
	 */
	public static boolean load() {
		if(!gameConfig.exists() || !gameConfig.canRead()) {
			return false;
		}
		try(DataInputStream dis = new DataInputStream(new FileInputStream(gameConfig))){
			System.out.println("--->开始读取游戏配置信息<---");
			Config.P1_UP = dis.readInt();
			Config.P1_DOWN = dis.readInt();
			Config.P1_LEFT = dis.readInt();
			Config.P1_RIGHT = dis.readInt();
			Config.P1_FIRE = dis.readInt();
			Config.P2_UP = dis.readInt();
			Config.P2_DOWN = dis.readInt();
			Config.P2_LEFT = dis.readInt();
			Config.P2_RIGHT = dis.readInt();
			Config.P2_FIRE = dis.readInt();
			Config.maxScore = dis.readInt();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("--->游戏配置读取成功<---");
		return true;
	}

}
