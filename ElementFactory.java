package thewaroftank.program;

import java.util.Random;
import java.util.Vector;

import thewaroftank.config.Config;
import thewaroftank.gui.GameControlListener;
import thewaroftank.program.enums.Direction;
import thewaroftank.program.enums.Player;

/**
 * 元素工厂线程类
 * (目前只生产坦克)
 * @author Yun-Long
 */
public class ElementFactory implements Runnable {
	
	public static volatile int level = 1; //当前关卡数
	private Vector<Tank> set = Config.TANK_SET;
	private Random rd = Config.RD;
	private Tank tank_PL1 = null; //玩家1坦克引用
	private Tank tank_PL2 = null; //玩家2坦克引用
	private static ElementFactory ef;
	
	private ElementFactory() {}
	
	public static ElementFactory createEF() { //懒汉单例,只许存在一个工厂
		if(ef == null) {
			ef = new ElementFactory();
		}
		return ef;
	}

	/**
	 * PC坦克生产线程
	 */
	@Override
	public void run() {
		System.out.println("--->productPCTK线程启动<---");
		while (GameControlListener.flag&&level<17) { //关卡完成则结束线程
			if(GameControlListener.isPause) {
				try {
					Thread.sleep(100);
				}catch (InterruptedException e) {}
				continue;
			}
			System.out.println("--->进入第 "+level+" 关<---"); //测试关卡开始
			this.productPCTank(); //生产当前关卡坦克
			System.out.println("--->第 "+level+" 关坦克大军准备完成,总数: "+(set.size()-2)); //测试PC坦克生产
			while(GameControlListener.flag) {
				if(GameControlListener.isPause) {
					try {
						Thread.sleep(100);
					}catch (InterruptedException e) {}
					continue;
				}
				for(int i = 2;i < set.size();i++) { //每5秒出一辆PC坦克(未实现随机)
					if(!GameControlListener.flag) {
						break;
					}
					Tank tk = null;
					try {
						tk =set.get(i);
					} catch (Exception e) { //避免数组下标越界异常导致线程中断
						e.printStackTrace();
					}
					if(tk != null && !tk.isVisible()) { //放出剩余(不可见)PC坦克
						try {
							Thread.sleep(level<8?5000:7000); //8关前间隔5秒,之后间隔7秒
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						tk.setVisible(true);
						tk.setMoveEnable(true);
						tk.setFireEnable(true);
						new Thread(tk).start(); //使PC坦克自动移动,开火
					}
				}
				if(set.size()<=2) {
					System.out.println("--->第 "+level+" 关结束<---"); //测试关卡控制
					level++; //进入下一关
					for(int i = 0;i < Config.BULLETS_SET.size();i++) {
						try {
							Config.BULLETS_SET.get(i).setHealth(0);;
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
					Config.BULLETS_SET.clear(); //清空剩余子弹
					tank_PL1.setX(Config.P1_INIT_X); //初始化玩家位置
					tank_PL1.setY(Config.P_INTI_Y);
					tank_PL1.setDirection(Direction.UP);
					int sp = level<8?2:(level<12?3:4);
					tank_PL1.setSpeed(sp);
					tank_PL2.setX(Config.P2_INIT_X);
					tank_PL2.setY(Config.P_INTI_Y);
					tank_PL2.setDirection(Direction.UP);
					tank_PL2.setSpeed(sp);
					break;
				}
			}
//			break;//测试第一关
		}
		System.out.println("--->productPCTK线程结束<---");
	}

	/**
	 * 初始化玩家坦克,并将两个玩家坦克放入集合
	 * 
	 * @return 返回可用玩家坦克
	 * @param pl 玩家对象
	 */
	public Tank getPlayerTank(Players pl) {
		pl.setLifes(3); //初始化玩家生命数
		pl.setScore(0); //初始化玩家得分
		if(pl.getPL() == Player.PL1) {
			int sp = level<8?2:(level<12?3:4);
			tank_PL1 = new Tank(Config.P1_INIT_X, Config.P_INTI_Y, 3, sp, true, false,
					Config.TANK_COLOR_2, Direction.UP); //玩家1坦克
			tank_PL2 = new Tank(Config.P2_INIT_X, Config.P_INTI_Y, 3, sp, false, false,
					Config.TANK_COLOR_2, Direction.UP); //玩家2坦克
			set.add(0, tank_PL1);// 固定玩家坦克存放位置
			set.add(1, tank_PL2);
			tank_PL1.setOwner(pl);
			tank_PL1.setVisible(true);
			return tank_PL1;
		}else {
			tank_PL2.setOwner(pl);
			tank_PL2.setMoveEnable(true);
			tank_PL2.setVisible(true);
			return tank_PL2;
		}
	}

	/**
	 * <li>PC坦克生产方法
	 * <li>根据关卡数,产出不同类型不同数量的PC坦克,出口位置随机
	 */
	private void productPCTank() {
		int x1 = Config.PC_INIT_X1;
		int x2 = Config.PC_INIT_X2;
		int x3 = Config.PC_INIT_X3;
		int y = Config.PC_INIT_Y;
		Direction dir = Direction.DOWN;
		int lv = level<8?level*4:0;
		for(int i=0;i<lv;i++) { //添加1级坦克,灰色
			if(i<3) {
				int x = (i==0?x1:(i==1?x2:x3)); //前三辆位置固定
				Tank tk = new Tank(x,y,1,1,false,false,Config.TANK_COLOR_0,dir);
				set.add(tk);
				tk.setVisible(true);
				tk.setMoveEnable(true);
				tk.setFireEnable(true);
				new Thread(tk).start(); //每关前三辆直接出战
			}else {
				int r = rd.nextInt(3);
				int x = (r==0?x1:(r==1?x2:x3));
				set.add(new Tank(x,y,1,1,false,false,Config.TANK_COLOR_0,dir));
			}
		}
		lv = level<8?level*2:(level==8?32:0);
		for(int i=0;i<lv;i++) { //添加2级坦克,绿色
			if(level==8&&i<3) {
				int x = (i==0?x1:(i==1?x2:x3)); //前三辆位置固定
				Tank tk = new Tank(x,y,2,1,false,false,Config.TANK_COLOR_1,dir);
				set.add(tk);
				tk.setVisible(true);
				tk.setMoveEnable(true);
				tk.setFireEnable(true);
				new Thread(tk).start(); //每关前三辆直接出战
			}else {
				int r = rd.nextInt(3);
				int x = r==0?x1:(r==1?x2:x3);
				set.add(new Tank(x,y,2,1,false,false,Config.TANK_COLOR_1,dir));
			}
		}
		lv = level<8?level-1:(level==8?16:(level==9?36:0));
		for(int i=0;i<lv;i++) { //添加3级坦克,黄色
			if(level==9&&i<3) {
				int x = (i==0?x1:(i==1?x2:x3)); //前三辆位置固定
				Tank tk = new Tank(x,y,3,2,false,false,Config.TANK_COLOR_2,dir);
				set.add(tk);
				tk.setVisible(true);
				tk.setMoveEnable(true);
				tk.setFireEnable(true);
				new Thread(tk).start(); //每关前三辆直接出战
			}else {
				int r = rd.nextInt(3);
				int x = r==0?x1:(r==1?x2:x3);
				set.add(new Tank(x,y,3,2,false,false,Config.TANK_COLOR_2,dir));
			}
		}
		lv = level<8?level-2:(level==8?10:(level==9?18:(level==10?40:0)));
		for(int i=0;i<lv;i++) { //添加4级坦克,蓝色
			if(level==10&&i<3) {
				int x = (i==0?x1:(i==1?x2:x3)); //前三辆位置固定
				Tank tk = new Tank(x,y,4,2,false,false,Config.TANK_COLOR_3,dir);
				set.add(tk);
				tk.setVisible(true);
				tk.setMoveEnable(true);
				tk.setFireEnable(true);
				new Thread(tk).start(); //每关前三辆直接出战
			}else {
				int r = rd.nextInt(3);
				int x = r==0?x1:(r==1?x2:x3);
				set.add(new Tank(x,y,4,2,false,false,Config.TANK_COLOR_3,dir));
			}
		}
		lv = level<8?level-3:(level==8?8:(level==9?11:(level==10?20:(level==11?30:0))));
		for(int i=0;i<lv;i++) { //添加5级坦克,橙色
			if(level==11&&i<3) {
				int x = (i==0?x1:(i==1?x2:x3)); //前三辆位置固定
				Tank tk = new Tank(x,y,5,3,false,false,Config.TANK_COLOR_4,dir);
				set.add(tk);
				tk.setVisible(true);
				tk.setMoveEnable(true);
				tk.setFireEnable(true);
				new Thread(tk).start(); //每关前三辆直接出战
			}else {
				int r = rd.nextInt(3);
				int x = r==0?x1:(r==1?x2:x3);
				set.add(new Tank(x,y,5,3,false,false,Config.TANK_COLOR_4,dir));
			}
		}
		lv = level<8?level-4:(level==8?6:(level==9?9:(level==10?12:(level==11?20:(level==12?30:0)))));
		for(int i=0;i<lv;i++) { //添加6级坦克,紫色
			if(level==12&&i<3) {
				int x = (i==0?x1:(i==1?x2:x3)); //前三辆位置固定
				Tank tk = new Tank(x,y,6,4,false,false,Config.TANK_COLOR_5,dir);
				set.add(tk);
				tk.setVisible(true);
				tk.setMoveEnable(true);
				tk.setFireEnable(true);
				new Thread(tk).start(); //每关前三辆直接出战
			}else {
				int r = rd.nextInt(3);
				int x = r==0?x1:(r==1?x2:x3);
				set.add(new Tank(x,y,6,4,false,false,Config.TANK_COLOR_5,dir));
			}
		}
		lv = level<8?level-5:(level==8?4:(level==9?7:(level==10?10:(level-10)*15)));
		for(int i=0;i<lv;i++) { //添加7级坦克,红色
			if(level>12&&i<3) {
				int x = (i==0?x1:(i==1?x2:x3)); //前三辆位置固定
				Tank tk = new Tank(x,y,7,5,false,false,Config.TANK_COLOR_6,dir);
				set.add(tk);
				tk.setVisible(true);
				tk.setMoveEnable(true);
				tk.setFireEnable(true);
				new Thread(tk).start(); //每关前三辆直接出战
			}else {
				int r = rd.nextInt(3);
				int x = r==0?x1:(r==1?x2:x3);
				set.add(new Tank(x,y,7,5,false,false,Config.TANK_COLOR_6,dir));
			}
		}
	}
}
