package thewaroftank.gui;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import thewaroftank.config.Config;
import thewaroftank.program.ElementFactory;
import thewaroftank.program.Players;
import thewaroftank.program.Tank;
import thewaroftank.program.enums.Direction;
import thewaroftank.program.enums.Player;
/**
 * 游戏窗口事件侦听类,同时对玩家生命进行监控
 * @author Yun-Long
 *
 */
public class GameControlListener implements Runnable, KeyListener, MouseListener,
	MouseMotionListener, ActionListener {

	public static volatile boolean flag; //GameOver标记,用于终止线程
	public static volatile boolean isPause; //游戏暂停标识
	public static volatile boolean p1_up_state = false; //玩家1移动控制
	public static volatile boolean p1_down_state = false;
	public static volatile boolean p1_left_state = false;
	public static volatile boolean p1_right_state = false;
	public static volatile boolean p2_up_state = false; //玩家2移动控制
	public static volatile boolean p2_down_state = false;
	public static volatile boolean p2_left_state = false;
	public static volatile boolean p2_right_state = false;
	public volatile long p1_fireTime = 0; //玩家1射击间隔控制
	public volatile long p2_fireTime = 0; //玩家2射击间隔控制
	public static Players PL1 = new Players(Player.PL1); //玩家1对象
	public static Players PL2 = new Players(Player.PL2); //玩家2对象
	private Tank tank_PL1; //作为玩家1坦克引用
	private Tank tank_PL2; //作为玩家2坦克引用
	private boolean isDoubleMan; //是否双人游戏
//	private boolean f2 = false; //快捷键F2和菜单对应功能的启用开关
	private boolean f3 = false; //快捷键F3,F4和菜单对应功能的启用开关
	private boolean f5 = false; //快捷键F5和菜单对应功能的启用开关
	private boolean dragEnable = false; //鼠标拖动开关
	private Point oldPoint; //拖动前鼠标位置
	private Point newPoint; //拖动结束鼠标位置
	private JFrame frame = GameFrame.getGameFrame(); //游戏窗口引用
	private JPopupMenu gameMenu = GameMenu.showMenu(); //弹出菜单引用
	private MainGamePanel gameArea = MainGamePanel.showGameArea(); //游戏区域面板引用
	private ElementFactory factory = ElementFactory.createEF(); //PC坦克工厂引用
	private Thread gameAreaDraw = null; //游戏区域绘图线程引用
	private Thread productPCTK = null; //PC坦克工厂生产线程引用
	private Thread playersListen = null; //玩家侦听线程引用
	private static GameControlListener gcl = null; //本侦听器唯一对象引用
	
	private GameControlListener() {}
	
	/**
	 * 获取游戏控制侦听器
	 * @return
	 */
	public static GameControlListener getGameCL() {
		if(gcl == null) {
			gcl = new GameControlListener();
		}
		return gcl;
	}
	
	/**
	 * 菜单选择操作事件处理
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		switch (event.getActionCommand()) {
		case "newGame":
			gcl.newGame(); //些调用方法,使用this无效(为什么会产生NullPointerExcption)
			break;
		case "goonGame":
			gcl.goonGame();
			break;
		case "saveGame":
			gcl.saveGame();
			break;
		case "save_Exit":
			gcl.saveGame();
			gcl.exit("当前游戏进度已保存，确定退出？");
			break;
		case "addPlayer2":
			gcl.addPlayer2();
			break;
		default:
			if(tank_PL1 != null) {
				gcl.exit("确定不保存当前游戏进度并退出？");
			}else {
				gcl.exit("确定不进入游戏挑战一下嘛?-_-");
			}
			break;
		}
	}
	
	/**
	 * 快捷键操作事件处理
	 */
	@Override
	public void keyPressed(KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.VK_F1:
			this.newGame();
			break;
		case KeyEvent.VK_F2:
			this.goonGame();
			break;
		case KeyEvent.VK_F3:
			this.saveGame();
			break;
		case KeyEvent.VK_F4:
			this.saveGame();
			this.exit("当前游戏进度已保存，确定退出？");
			break;
		case KeyEvent.VK_F5:
			this.addPlayer2();
			break;
		case KeyEvent.VK_SPACE:
			this.setPause();
			break;
		case KeyEvent.VK_ESCAPE:
			if(tank_PL1 != null) {
				this.exit("确定不保存当前游戏进度并退出？");
			}else {
				this.exit("确定不进入游戏挑战一下嘛?-_-");
			}
			break;
		case KeyEvent.VK_F8:
			gameMenu.show(frame,15,35);
			break;
		}
	}
	
	/**
	 * 鼠标进入界面
	 */
	@Override
	public void mouseEntered(MouseEvent event) {
		frame.setCursor(new Cursor(Cursor.HAND_CURSOR)); //设置鼠标为手形
	}
	
	/**
	 * 鼠标离开界面
	 */
	@Override
	public void mouseExited(MouseEvent event) {
		frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //设置为默认外形
	}
	
	/**
	 * 鼠标按下
	 */
	@Override
	public void mousePressed(MouseEvent event) {
		frame.setCursor(new Cursor(Cursor.MOVE_CURSOR)); //设置为移动十字外形
		oldPoint = event.getPoint(); //获取鼠标当前按下的位置坐标点
		dragEnable = true; //鼠标按下,设置可以拖动
	}
	
	/**
	 * 鼠标弹起
	 */
	@Override
	public void mouseReleased(MouseEvent event) {
		frame.setCursor(new Cursor(Cursor.HAND_CURSOR));
		dragEnable = false;
	}
	
	/**
	 * 鼠标拖动窗口事件处理
	 */
	@Override
	public void mouseDragged(MouseEvent event) {
		if(dragEnable) {
			newPoint = new Point(event.getX()-oldPoint.x+frame.getX(),
					event.getY()-oldPoint.y+frame.getY()); //鼠标新点减去旧点位置,加上原窗口位置等于新窗口位置
			frame.setLocation(newPoint); //设置游戏窗口新位置
		}
	}
	
	@Override
	public void keyReleased(KeyEvent event) {} //未使用事件,不具体实现
	
	@Override
	public void keyTyped(KeyEvent event) {} //未使用事件,不具体实现
	
	@Override
	public void mouseClicked(MouseEvent event) {} //未使用事件,不具体实现
	
	@Override
	public void mouseMoved(MouseEvent e) {} //未使用事件,不具体实现
	
	/**
	 * 新游戏事件处理过程
	 * <li>该方法作为新游戏初始化入口
	 */
	private void newGame() { //F1
		if(tank_PL1 != null) { //判断游戏是否已经开始
			int value=JOptionPane.showConfirmDialog(frame,"游戏已开始,是否重新开始?","警告",JOptionPane.YES_NO_OPTION);
			if(value!=JOptionPane.YES_OPTION){
				return; 
			}
			 //选择取消则设置重新初始化游戏
			System.out.println("--->重新开始游戏<---");
			flag = false;
			for(int i = 0; i < Config.TANK_SET.size(); i ++) {
				try {
					Config.TANK_SET.get(i).setHealth(0);
				} catch (Exception e) {}
			}
			try {
				Thread.sleep(300);
				while(productPCTK.isAlive()) {
					System.out.println("--->等待productPCTK线程结束<---");
					productPCTK.join();
				}
			} catch (Exception e) {}
			tank_PL1 = null; tank_PL2 = null; isDoubleMan = false;
			p1_up_state = false;p1_down_state = false;
			p1_left_state = false;p1_right_state = false;
			p2_up_state = false;p2_down_state = false;
			p2_left_state = false;p2_right_state = false;
		}
		Config.TANK_SET.clear();
		Config.BULLETS_SET.clear();
		flag = true; isPause = false; ElementFactory.level = 1; //重置游戏控制标记
		tank_PL1 = factory.getPlayerTank(PL1); //初始化玩家坦克(包括PL2的)
		frame.addKeyListener(new PlayerTankCtrlListenner(Player.PL1)); //注册坦克控制侦听器
		frame.remove(StartPanel.showStartPanel()); //移除开始面板
		frame.add(gameArea); //添加主游戏面板
		frame.setVisible(true); //显示游戏面板
		gameAreaDraw = new Thread(gameArea);
		gameAreaDraw.setName("gameAreaDraw");
		gameAreaDraw.start(); //创建并启动绘图线程
		productPCTK = new Thread(factory);
		productPCTK.setName("productPCTK");
		productPCTK.start(); //创建并启动PC坦克工厂线程
		playersListen = new Thread(gcl);
		playersListen.setName("playersListen");
		playersListen.start(); //创建并启动玩家生命和关卡数监听线程
		gameMenu.getComponent(2).setEnabled(f3 = true);
		gameMenu.getComponent(3).setEnabled(true);
		gameMenu.getComponent(4).setEnabled(f5 = true);
		System.out.println("--->初始化完成,游戏开始<---");
	}
	/**
	 * 设置游戏暂停方法
	 */
	public void setPause() {
		if(f3) { //只有游戏已开始运行时可以设置暂停状态(f3在游戏开始后才会置true)
			isPause = isPause ? false : true;
			System.out.println(isPause?"--->游戏已暂停<---":"--->取消暂停,继续游戏<---");
		}
	}
	/**
	 * 继续历史存档游戏事件处理过程
	 * <li>调用数据读取类从文件中读取保存的游戏数据
	 */
	private void goonGame() { //F2
		
	}
	/**
	 * 保存游戏事件处理过程
	 * <li>调用数据输出类将当前游戏进度保存到文件中
	 */
	private void saveGame() { //F3 , F4
		if(f3) {
			
		}
	}
	/**
	 * 添加玩家2事件处理过程
	 */
	private void addPlayer2() { //F5
		if(f5) {
			tank_PL2 = factory.getPlayerTank(PL2);
			isDoubleMan = true; //双人游戏状态
			frame.addKeyListener(new PlayerTankCtrlListenner(Player.PL2)); //注册坦克控制侦听器
			gameMenu.getComponent(4).setEnabled(f5=false); //P2玩家添加后对应菜单与快捷键设为不可用
			System.out.println("--->添加玩家PL2成功<---");
		}
	}
	/**
	 * 退出游戏事件处理过程
	 * @param str 退出前弹出的提示信息
	 */
	private void exit(String str){ //ESC 弹出一个确认提示窗口（参数：所属对象，消息内容，标题，窗口类型）
		int value=JOptionPane.showConfirmDialog(frame,str,"提示",JOptionPane.YES_NO_OPTION);
		if(value==JOptionPane.YES_OPTION){//选择"确认"后退出
			System.out.println("--->窗口关闭,游戏结束!谢谢使用,再见<---");
			System.exit(0);
		}else if(tank_PL1 != null && !flag){ //回到初始界面并重置所有容器和标记
			frame.remove(gameArea);
			frame.add(StartPanel.showStartPanel());
			Config.TANK_SET.clear();
			System.out.println("清除TANK_SET中坦克,结果="+Config.TANK_SET.size());
			Config.BULLETS_SET.clear();
			System.out.println("清除BULLETS_SET中子弹,结果="+Config.BULLETS_SET.size());
			tank_PL1 = null; tank_PL2 = null; isDoubleMan = false;
			gameMenu.getComponent(2).setEnabled(f3 = false);
			gameMenu.getComponent(3).setEnabled(false);
			gameMenu.getComponent(4).setEnabled(f5 = false);
			p1_up_state = false;p1_down_state = false;
			p1_left_state = false;p1_right_state = false;
			p2_up_state = false;p2_down_state = false;
			p2_left_state = false;p2_right_state = false;
		}
	}
	
	/**
	 * 游戏结束时调用显示面板方法,true为正常通关,false为所有玩家生命归0
	 * <li>该方法暂未实现
	 * @param type
	 */
	public void gameOver(boolean type) {
		if(type) {
			System.out.println("--->恭喜您通关了,游戏结束<---"); //调用通关结束面板
			this.exit("恭喜您通关了,游戏结束!是否退出?");
		}else {
//			Config.TANK_SET.clear();
			System.out.println("--->玩家被消灭,游戏结束<---"); //调用被消灭结束面板
			this.exit("玩家被消灭,游戏结束!是否退出");
		}
	}
	
	/**
	 * 玩家生命和关卡监控线程
	 */
	@Override
	public void run() {
		System.out.println("--->playersListen线程启动<---");
		while(flag) {
			if(isPause) {
				try { //线程休眠间断,避免暂停时CPU使用率过高
					Thread.sleep(100);
				}catch (InterruptedException e) {}
				continue; //如果已暂停直接进入下一次循环
			}
			int life1 = PL1.getLifes();
			int life2 = PL2.getLifes();
			if ((!isDoubleMan&&life1==0)||(isDoubleMan&&life1 == 0 && life2 == 0)) { //玩家被消灭,强制结束
				GameControlListener.flag = false;
				this.gameOver(false);
				break;
			}
			if(ElementFactory.level>16) { //消灭所有坦克,通关结束(最大16)
				GameControlListener.flag = false;
				this.gameOver(true);
				break;
			}
			if (tank_PL1.getHealth() == 0 && life1-- > 0) { //玩家坦克死亡但有生命则初始化玩家坦克
				PL1.setLifes(life1);
				System.out.println("玩家坦克死亡:"+PL1);
				if(life1 > 0) {
					tank_PL1.setX(Config.P1_INIT_X);
					tank_PL1.setY(Config.P_INTI_Y);
					tank_PL1.setDirection(Direction.UP);
					tank_PL1.setHealth(3);
					tank_PL1.setMoveEnable(true);
					tank_PL1.setFireEnable(false);
					tank_PL1.setVisible(true);
				}
			}
			if (isDoubleMan && tank_PL2.getHealth() == 0 && life2-- > 0) {
				PL2.setLifes(life2);
				System.out.println("玩家坦克死亡:"+PL1);
				if(life2 > 0) {
					tank_PL2.setX(Config.P2_INIT_X);
					tank_PL2.setY(Config.P_INTI_Y);
					tank_PL2.setDirection(Direction.UP);
					tank_PL2.setHealth(3);
					tank_PL2.setMoveEnable(true);
					tank_PL2.setFireEnable(false);
					tank_PL2.setVisible(true);
				}
			}
			if(p1_up_state||p1_down_state||p1_left_state||p1_right_state) {
				tank_PL1.move();
			}
			if(isDoubleMan&&(p2_up_state||p2_down_state||p2_left_state||p2_right_state)) {
				tank_PL2.move();
			}
			long delay = ElementFactory.level<8?600:(ElementFactory.level<12?500:400);
			if(tank_PL1.isFireEnable() && System.currentTimeMillis()-p1_fireTime>=delay) {
				tank_PL1.fire();
				p1_fireTime = System.currentTimeMillis();
			}
			if(isDoubleMan && tank_PL2.isFireEnable() && System.currentTimeMillis()-p2_fireTime>=delay) {
				tank_PL2.fire();
				p2_fireTime = System.currentTimeMillis();
			}
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("--->playersListen线程结束<---");
	}

}
