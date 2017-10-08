package config;

import java.io.File;
import java.io.EOFException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import bin.Bullet;
import bin.ElementFactory;
import bin.Players;
import bin.Tank;
import bin.gui.GameControlListener;
import bin.gui.MainGamePanel;

/**
 * 用于加载或保存游戏数据
 * 
 * @author WuYaoLong
 *
 *<li>需要保存的数据有:
 *	<ol>所有坦克对象
 *	<p>子弹对象
 *	<p>玩家对象
 *	<p>当前关卡数
 *
 */
public abstract class RW_GameData {
	
	private static Vector<Tank> set_T = Config.TANK_SET;
	private static Vector<Bullet> set_B = Config.BULLETS_SET;
	
	/**
	 * 游戏数据存档
	 */
	public static boolean save(File dataFile) {
		if(dataFile.exists() && !dataFile.canWrite()) { //如果文件存在且不可写
			return false;
		}
		System.out.println("--->开始保存游戏数据<---");
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFile));) {
			for (Tank tk : set_T) {
				oos.writeObject(tk); //写入坦克对象数据
			}
			for (Bullet blt : set_B) {
				oos.writeObject(blt); //写入子弹对象数据
			}
			oos.writeObject(GameControlListener.isDoubleMan); //写入是否双人游戏
			oos.writeObject(ElementFactory.createEF().getLevel()); //写入当前关卡数
			oos.flush();
			System.out.println("--->游戏数据保存成功<---");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 游戏数据加载(读档)
	 */
	public static boolean load(File dataFile) {
		if(!dataFile.canRead()) { //如果文件不可读
			return false;
		}
		System.out.println("--->开始读取存档数据<---");
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile))){
			Object obj = ois.readObject();
			while(obj != null) {
				if(obj instanceof Tank) {
					set_T.add((Tank)obj);
				}else if(obj instanceof Bullet) {
					set_B.add((Bullet)obj);
				}else if(obj instanceof Boolean) {
					GameControlListener.isDoubleMan = (Boolean)obj;
				}else if(obj instanceof Integer) {
					ElementFactory.createEF().setLevel((Integer)obj);
				}
				obj = ois.readObject();
			}
			return true;
		} catch (EOFException e) { //达到文件末尾抛出此异常
			//使PL1和PL2引用重新指向为存档中的玩家对象
			Players p1 = set_T.get(0).getOwner();
			GameControlListener.PL1 = p1;
			MainGamePanel.showGameArea().setPl1(p1);
			Players p2 = set_T.get(1).getOwner();
			if(p2 != null) {
				GameControlListener.PL2 = p2;
				MainGamePanel.showGameArea().setPl2(p2);
			}
			System.out.println("--->读取游戏存档成功<---");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
