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
 * ���ڼ��ػ򱣴���Ϸ����
 * 
 * @author WuYaoLong
 *
 *<li>��Ҫ�����������:
 *	<ol>����̹�˶���
 *	<p>�ӵ�����
 *	<p>��Ҷ���
 *	<p>��ǰ�ؿ���
 *
 */
public abstract class RW_GameData {
	
	private static Vector<Tank> set_T = Config.TANK_SET;
	private static Vector<Bullet> set_B = Config.BULLETS_SET;
	
	/**
	 * ��Ϸ���ݴ浵
	 */
	public static boolean save(File dataFile) {
		if(dataFile.exists() && !dataFile.canWrite()) { //����ļ������Ҳ���д
			return false;
		}
		System.out.println("--->��ʼ������Ϸ����<---");
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFile));) {
			for (Tank tk : set_T) {
				oos.writeObject(tk); //д��̹�˶�������
			}
			for (Bullet blt : set_B) {
				oos.writeObject(blt); //д���ӵ���������
			}
			oos.writeObject(GameControlListener.isDoubleMan); //д���Ƿ�˫����Ϸ
			oos.writeObject(ElementFactory.createEF().getLevel()); //д�뵱ǰ�ؿ���
			oos.flush();
			System.out.println("--->��Ϸ���ݱ���ɹ�<---");
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
	 * ��Ϸ���ݼ���(����)
	 */
	public static boolean load(File dataFile) {
		if(!dataFile.canRead()) { //����ļ����ɶ�
			return false;
		}
		System.out.println("--->��ʼ��ȡ�浵����<---");
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
		} catch (EOFException e) { //�ﵽ�ļ�ĩβ�׳����쳣
			//ʹPL1��PL2��������ָ��Ϊ�浵�е���Ҷ���
			Players p1 = set_T.get(0).getOwner();
			GameControlListener.PL1 = p1;
			MainGamePanel.showGameArea().setPl1(p1);
			Players p2 = set_T.get(1).getOwner();
			if(p2 != null) {
				GameControlListener.PL2 = p2;
				MainGamePanel.showGameArea().setPl2(p2);
			}
			System.out.println("--->��ȡ��Ϸ�浵�ɹ�<---");
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
