package bin.gui;

import java.applet.Applet;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import bin.ElementFactory;
import bin.Players;
import bin.Tank;
import bin.enums.Direction;
import bin.enums.Player;
import config.Config;
import config.RW_GameConfig;
import config.RW_GameData;
/**
 * 游戏窗口事件侦听类,同时对玩家生命等状态进行监控
 * 
 * @author WuYaoLong
 *
 */
public class GameControlListener implements Runnable, KeyListener, MouseListener,
	MouseMotionListener, ActionListener {

	public static volatile boolean flag; //GameOver标记,用于终止线程
	public static volatile boolean isPause; //游戏暂停标识
	public static volatile boolean isDoubleMan; //是否双人游戏
	public static volatile boolean isReaded = false; //是否读档游戏
	public static volatile boolean p1_up_state = false; //玩家1移动控制
	public static volatile boolean p1_down_state = false;
	public static volatile boolean p1_left_state = false;
	public static volatile boolean p1_right_state = false;
	public static volatile boolean p2_up_state = false; //玩家2移动控制
	public static volatile boolean p2_down_state = false;
	public static volatile boolean p2_left_state = false;
	public static volatile boolean p2_right_state = false;
	public static Players PL1 = new Players(Player.PL1); //玩家1对象
	public static Players PL2 = new Players(Player.PL2); //玩家2对象
	public volatile long p1_fireTime = 0; //玩家1射击间隔控制
	public volatile long p2_fireTime = 0; //玩家2射击间隔控制
	private Tank tank_PL1; //作为玩家1坦克引用
	private Tank tank_PL2; //作为玩家2坦克引用
	private PlayerTankCtrlListenner ptcl1 = null; //玩家1操作侦听器
	private PlayerTankCtrlListenner ptcl2 = null; //玩家2操作侦听器
	private boolean f3 = false; //快捷键F3,F4和菜单对应功能的启用开关
	private boolean f5 = false; //快捷键F5和菜单对应功能的启用开关
	private boolean dragEnable = false; //鼠标拖动开关
	private Point oldPoint; //拖动前鼠标位置
	private Point newPoint; //拖动结束鼠标位置
	private JFrame frame = GameFrame.getGameFrame(); //游戏窗口引用
	private JPopupMenu gameMenu = null; //弹出菜单引用
	private MainGamePanel gameArea = MainGamePanel.showGameArea(); //游戏区域面板引用
	private ElementFactory factory = ElementFactory.createEF(); //PC坦克工厂引用
	private Thread gameAreaDraw = null; //游戏区域绘图线程引用
	private Thread productPCTK = null; //PC坦克工厂生产线程引用
	private Thread playersListen = null; //玩家侦听线程引用
	private volatile File gameData = new File(Config.DATA_PATH+"GameData.dat"); //游戏数据存档文件
	private static GameControlListener gcl = null; //本侦听器唯一对象引用
	
	private GameControlListener() {}
	
	/**
	 * 获取游戏控制侦听器
	 * @return
	 */
	public static GameControlListener getGameCL() {
		if(gcl == null) {
			gcl = new GameControlListener();
			System.out.println("--->构造GameControlListener<---");
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
			gcl.newGame(); //这里调用方法,使用this无效(为什么会产生NullPointerExcption)
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
			if(gameMenu == null) { //若为第一次调用则赋值引用
				gameMenu = GameMenu.showMenu();
			}
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
			 //选择确定则设置重新初始化游戏
			System.out.println("--->重新开始游戏<---");
			gcl.endGame(); //中止当前游戏
		}
		gcl.initGame(); //初始化游戏
		System.out.println("--->初始化完成,游戏开始<---");
	}
	
	/**
	 * 结束(中止)当前游戏方法
	 */
	private void endGame() {
		flag = false; //设置游戏结束标记
		try { //将所有坦克生命置0(防止重新开始游戏后还有隐藏坦克)
			for(int i = 0; i < Config.TANK_SET.size(); i ++) {
				Config.TANK_SET.get(i).setHealth(0);
			}
		} catch (Exception e) {}
		try {
			Thread.sleep(100);
			while(productPCTK.isAlive()) {
				System.out.println("--->等待productPCTK线程结束<---");
				productPCTK.interrupt(); //中断可能的休眠状态
				productPCTK.join(); //等待线程结束
			}
		} catch (Exception e) {}
		Config.TANK_SET.clear(); //清空坦克
		Config.BULLETS_SET.clear(); //清空子弹
		PL2.setLifes(0); PL2.setScore(0); //初始化PL2数据
		frame.removeKeyListener(this.ptcl1); //移除侦听器
		frame.removeKeyListener(this.ptcl2);
		tank_PL1 = null; tank_PL2 = null; isDoubleMan = false;
		p1_up_state = false;p1_down_state = false;
		p1_left_state = false;p1_right_state = false;
		p2_up_state = false;p2_down_state = false;
		p2_left_state = false;p2_right_state = false;
		gameMenu.getComponent(2).setEnabled(f3 = false); //使相应菜单项和功能键不可用
		gameMenu.getComponent(3).setEnabled(false);
		gameMenu.getComponent(4).setEnabled(f5 = false);
	}
	
	/**
	 * 初始化游戏
	 */
	private void initGame() {
		flag = true; //重新初始化游戏控制标记
		if(gameMenu == null) {
			gameMenu = GameMenu.showMenu(); //加载游戏菜单
		}
		if(!isReaded) { //如果为读档游戏则无需生成新的玩家坦克及关卡
			isDoubleMan = false;
			factory.setLevel(1);
			tank_PL1 = factory.getPlayerTank(PL1); //初始化玩家坦克(包括PL2的)
		}else {
			tank_PL1 = Config.TANK_SET.get(0);
			tank_PL1.setFireEnable(false);
			tank_PL2 = Config.TANK_SET.get(1);
			tank_PL2.setFireEnable(false);
		}
		frame.addKeyListener(ptcl1 = new PlayerTankCtrlListenner(Player.PL1)); //注册坦克1控制侦听器
		if(GameControlListener.isDoubleMan) {
			frame.addKeyListener(ptcl2 = new PlayerTankCtrlListenner(Player.PL2)); //注册坦克2控制侦听器
			gameMenu.getComponent(4).setEnabled(f5 = false); //设置菜单项"双人游戏"和快捷键F5可用
		}else {
			gameMenu.getComponent(4).setEnabled(f5 = true); //设置菜单项"双人游戏"和快捷键F5可用
		}
		gameMenu.getComponent(2).setEnabled(f3 = true); //设置菜单项"保存游戏"和快捷键F3可用
		gameMenu.getComponent(3).setEnabled(true); //设置菜单项"保存退出"可用
		frame.remove(StartPanel.showStartPanel()); //移除开始面板
		frame.add(gameArea); //添加游戏主面板
		frame.setVisible(true); //显示游戏面板
		gameAreaDraw = new Thread(gameArea); //创建并启动绘图线程
		gameAreaDraw.setName("gameAreaDraw");
		gameAreaDraw.start();
		productPCTK = new Thread(factory); //创建并启动PC坦克工厂线程
		productPCTK.setName("productPCTK");
		productPCTK.start();
		playersListen = new Thread(gcl); //创建并启动玩家状态和关卡监听线程(控制游戏结束)
		playersListen.setName("playersListen");
		playersListen.start();
		GameControlListener.isPause = false;
		Applet.newAudioClip(Config.KAISHI).play(); //播放游戏开始声音
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
		//1.设置游戏暂停
		GameControlListener.isPause = true;
		//2.检查数据文件是否存在,不存在则返回警告后继续游戏
		if(!gameData.exists()) {
			JOptionPane.showMessageDialog(frame,"游戏数据存档不存在!","警告",JOptionPane.WARNING_MESSAGE);
			GameControlListener.isPause = false;
			return; 
		}
		//3.若游戏已开始则中止当前游戏进程,否则下一步
		if(tank_PL1 != null) {
			this.endGame();
		}
		//4.读取存档中的游戏数据,读取失败则返回弹窗确认
		if(!RW_GameData.load(gameData)) {
			JOptionPane.showMessageDialog(frame, "读取数据存档失败!请重试或重新开始游戏!", "错误", JOptionPane.ERROR_MESSAGE);
			Config.TANK_SET.clear(); //清除可能读取到的游戏数据
			Config.BULLETS_SET.clear();
			PL2.setLifes(0); PL2.setScore(0);
			frame.remove(gameArea); //移除游戏区域
			frame.add(StartPanel.showStartPanel()); //重新添加欢迎界面
			return;
		}
		//5.读取成功初始化相关数据并启动游戏
		GameControlListener.isReaded = true; //设置为读档模式
		GameControlListener.flag = true; //设置游戏为可开始
		 //启动已显示的PC坦克和子弹的移动线程
		for(int i = 2;i < Config.TANK_SET.size();i ++) {
			Tank tk = Config.TANK_SET.get(i);
			if(tk.isVisible()) {
				new Thread(tk).start();
			}
		}
		for(int i = 0;i < Config.BULLETS_SET.size();i ++) {
			new Thread(Config.BULLETS_SET.get(i)).start();
		}
		this.initGame(); //初始化三大主线程
		GameControlListener.isPause = true;
		JOptionPane.showConfirmDialog(frame, "读档成功,确认后开始游戏!", "提示", JOptionPane.PLAIN_MESSAGE);
		GameControlListener.isPause = false;
		System.out.println("--->初始化完成,游戏开始<---");
	}
	/**
	 * 保存游戏事件处理过程
	 * <li>调用数据输出类将当前游戏进度保存到文件中
	 */
	private void saveGame() { //F3 , F4
		if(f3) {
			GameControlListener.isPause = true;
			if(RW_GameData.save(gameData)) {
				JOptionPane.showMessageDialog(frame, "存档成功!", "提示", JOptionPane.PLAIN_MESSAGE);
				GameControlListener.isPause = false;
				return;
			}
			JOptionPane.showMessageDialog(frame, "存档失败!请重试...", "警告", JOptionPane.WARNING_MESSAGE);
			GameControlListener.isPause = false;
		}
	}
	/**
	 * 添加玩家2事件处理过程
	 */
	private void addPlayer2() { //F5
		if(f5) {
			tank_PL2 = factory.getPlayerTank(PL2);
			isDoubleMan = true; //双人游戏状态
			frame.addKeyListener(ptcl2 = new PlayerTankCtrlListenner(Player.PL2)); //注册坦克2控制侦听器
			gameMenu.getComponent(4).setEnabled(f5=false); //P2玩家添加后对应菜单与快捷键设为不可用
			Applet.newAudioClip(Config.TIANJIA).play(); //播放添加玩家声音
			System.out.println("--->添加玩家PL2成功<---");
		}
	}
	/**
	 * 结束游戏事件处理过程
	 * @param str 结束前弹出的提示信息
	 */
	private void exit(String str){ //ESC 弹出一个确认提示窗口（参数：所属对象，消息内容，标题，窗口类型）
		GameControlListener.isPause = true;
		int value=JOptionPane.showConfirmDialog(frame,str,"提示",JOptionPane.YES_NO_OPTION);
		if(value==JOptionPane.YES_OPTION){//选择"确认"后退出
			System.out.println("--->窗口关闭,游戏结束!谢谢使用,再见<---");
			if(!RW_GameConfig.save()) {
				JOptionPane.showMessageDialog(frame, "保存配置信息失败!", "警告", JOptionPane.WARNING_MESSAGE);
			}
			System.exit(0);
		}else if(tank_PL1 != null && !flag){ //选择取消回到初始界面并重置所有容器和标记
			frame.remove(gameArea); //移除游戏区域
			frame.add(StartPanel.showStartPanel()); //添加欢迎界面
			this.endGame(); //结束当前游戏进程
		}
		GameControlListener.isPause = false;
	}
	
	/**
	 * 游戏正常结束时调用弹窗提示
	 * 
	 * @param type 结束类型,true为正常通关,false为所有玩家生命归0
	 */
	public void gameOver(boolean type) {
		if(type) {
			System.out.println("--->恭喜您通关了,游戏结束<---"); //调用通关结束面板
			this.exit("恭喜您通关了,游戏结束!是否退出?");
		}else {
			System.out.println("--->玩家被消灭,游戏结束<---"); //调用被消灭结束面板
			this.exit("玩家被消灭,游戏结束!是否退出?");
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
			if(factory.getLevel()>16) { //消灭所有坦克,通关结束(最大16)
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
			long delay = factory.getLevel()<8?600:(factory.getLevel()<12?500:400);
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
