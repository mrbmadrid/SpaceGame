package hpu.edu.spain.ColorCollision;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.PriorityQueue;

import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.swing.JPanel;

import hpu.edu.spain.ColorCollision.GameTask.Task;
import hpu.edu.spain.Objects2D.Asteroid;
import hpu.edu.spain.Objects2D.Body;
import hpu.edu.spain.Objects2D.BossShip;
import hpu.edu.spain.Objects2D.Bullet;
import hpu.edu.spain.Objects2D.HealthStar;
import hpu.edu.spain.Objects2D.Missile;
import hpu.edu.spain.Objects2D.SpaceShip;
import hpu.edu.spain.Objects2D.Terrain;
import hpu.edu.spain.Objects2D.World;
import hpu.edu.spain.Objects2D.WorldTask;
import hpu.edu.spain.Objects2D.WorldTask.TaskType;

public class ColorCollision extends JPanel{
	
	static PriorityQueue<GameTask> taskQueue;
	Color shipColor;
	Game game;
	
	public ColorCollision(){
		taskQueue = new PriorityQueue<>();
		game = new Game();
		setLayout(new BorderLayout());
		
		this.add(game, BorderLayout.CENTER);
		this.addKeyListener(game.input);
		this.setFocusable(true);
		game.start();
	}
	
	static class Game extends JPanel implements Runnable {
		
		boolean running;
		BufferedImage bg;
		World world;
		int x, y;
		double xx, yy;
		int time, bosses;
		SpaceShip sp;
		BossShip boss;
		Sequencer sequencer;
		Sequence sequence1, sequence2;
		boolean bossSpawned;
		
		static boolean[] controls = new boolean[7];
		public Input input;
		public Game(){
			time = 0; bosses = 0;
			world = new World();
			try {
				Background b = new Background(10000, 800);
				bg = b.getImage();
				List<Terrain> bounds = b.getBounds();
				for(Body bd : bounds){
					world.addStaticBody(bd);
				}
				sp = new SpaceShip(50, 250, world);
				world.setPlayer(sp);
				world.translateBodies(0, y-bg.getHeight()/4);
				input = new Input();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				sequencer = MidiSystem.getSequencer();
				sequencer.open();
				sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
				sequence1 = MidiSystem.getSequence(new File("./res/Avoiding Space Junk.mid"));
				sequence2 = MidiSystem.getSequence(new File("./res/Spacing Out.mid"));
				sequencer.setSequence(sequence2);
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			}
		}
		
		public void start(){
			if(running) return;
			running = true;
			new Thread(this, "Game-Thread").start();
			sequencer.start();
		}
		
		@Override
		public void run() {
			double nsPerTick = 1000000000.0/60.0;
			double lastTime = System.nanoTime();
			double t = 0.0;
			while(running){
				double currentTime = System.nanoTime();
				t += (currentTime-lastTime)/nsPerTick;
				if(t >= 1.0){
					tick(currentTime);
					render();
				}
				lastTime = currentTime;
				try{
					Thread.sleep(10l);
				}catch(InterruptedException e){
					
				}
			}
		}
		
		private void tick(double currentTime){
			double dx = 0, dy = 0;
			if(!sp.isDead()){
				if(controls[0] && sp.getY() > 0){
					sp.translate(0.0, -1.5);
					dy += -1;
				}
				if(controls[1] && sp.getY() < this.getHeight()-40){
					sp.translate(0.0, 1.5);
					dy += 1;
				}
				if(controls[2] && sp.getX() > -1){
					sp.translate(-1.5, 0.0);
				};
				if(controls[3] && sp.getX() < this.getWidth()-50){
					sp.translate(1.5, 0.0);
					dx += 1.0;
				}
				if(controls[4]){
					Bullet b = new Bullet(sp.getX()+45, sp.getY()+18, world);
					world.addTask(new WorldTask(TaskType.SPAWN, b));
					controls[4] = false;
				}
				if(controls[5] && controls [6]){
					saveLevel();
					controls[5]=false;
					controls[6]=false;
				}
			}
			if(xx + bg.getWidth() > this.getWidth()){
				world.moveOrigin(-.5-dx, -dy);
				taskQueue.add(new GameTask(Task.MOVE, 0.5, 0, System.nanoTime()));
				xx-=dx;
				if(xx < -200){
					int spawn = (int)(Math.random()*100);
					if(spawn < 1){
						Missile m = new Missile(700, Math.random()*300+100, world);
						world.addTask(new WorldTask(TaskType.SPAWN, m));
					}else if(spawn > 98){
						Asteroid a = new Asteroid(Math.random()*500, 0, (int)(Math.random()*4+1), world);
						a.setVelocity(100*Math.random()+50);
						a.setTrajectory(Math.random()*Math.PI+Math.PI);
						world.addTask(new WorldTask(TaskType.SPAWN, a));
					}
				}
			}else{
				if(bosses == 0){
					time = 0;
					world.addTask(new WorldTask(TaskType.SPAWN, new HealthStar(300, 300, world)));
					sequencer.stop();
					++bosses;
				}else if(bosses == 1 && time % 100 == 0){
					++bosses;
					boss = new BossShip(800, 100, 600, 20*bosses+100, world);
					world.addTask(new WorldTask(TaskType.SPAWN, boss));
					try {
						sequencer.setSequence(sequence1);
						sequencer.start();
					} catch (InvalidMidiDataException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(dy > 0 && bg.getHeight() > this.getHeight()-(yy-bg.getHeight()/4)
						|| dy < 0){
					world.moveOrigin(0, -dy);
				}
				
			}
			if((dy > 0 && bg.getHeight() > this.getHeight()-(yy-bg.getHeight()/4))
					|| dy < 0){
				yy-=dy;
			}
			GameTask gt = taskQueue.poll();
			while(gt != null){
				process(gt);
				gt = taskQueue.poll();
			}
			x = (int)xx;
			y = (int)yy;
			world.tick(1f/60f, this.getWidth(), this.getHeight());
			++time;
		}
		
		private void saveLevel(){
			Level level = new Level(world.getStaticBodies(), "level.png");
			try{
				FileOutputStream fileOut;
            	fileOut = new FileOutputStream("./res/Level.bin");
    			ObjectOutputStream out = new ObjectOutputStream(fileOut);
    	        out.writeObject(level);
    	        out.close();
    	        File outputfile = new File("./res/level.png");
    	        ImageIO.write(bg, "png", outputfile);
			}catch(IOException e){
				e.printStackTrace();
			};
		}
		
		private void process(GameTask gt){
			switch (gt.task){
			case MOVE:
				xx-=gt.dx;
				yy+=gt.dy;
				break;
			default:
				break;
			}
		}
		
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.drawImage(bg, (int)xx, (int)yy-bg.getHeight()/4, this);
			g2d.setColor(Color.green);
			g2d.fillRect(20, 20, Math.min(200, sp.getLife()), 25);
			g2d.setColor(Color.WHITE);
			g2d.drawRect(20, 20, 200, 25);
			if(boss != null){
				if(!boss.isDead()){
					g2d.setColor(Color.red);
					g2d.fillRect(420, 20, boss.getLife()/5, 25);
					g2d.setColor(Color.WHITE);
					g2d.drawRect(420, 20, 100, 25);
				}else{
					g2d.setColor(Color.WHITE);
					g2d.setFont(new Font("TimesRoman", Font.PLAIN, 30));
					g2d.drawString("YOU WIN!", this.getWidth()/2-75, this.getHeight()/2);
				}
			}
			world.render(g2d);
		}
		
		private void render(){
			repaint();
		}
		
		static class Input implements KeyListener{

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()){
				case KeyEvent.VK_UP:
					controls[0] = true;
					break;
				case KeyEvent.VK_DOWN:
					controls[1] = true;
					break;
				case KeyEvent.VK_LEFT:
					controls[2] = true;
					break;
				case KeyEvent.VK_RIGHT:
					controls[3] = true;
					break;
				case KeyEvent.VK_SPACE:
					controls[4] = true;
					break;
				case KeyEvent.VK_S:
					controls[5] = true;
					break;
				case KeyEvent.VK_V:
					controls[6] = true;
					break;
				}			
			}

			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()){
				case KeyEvent.VK_UP:
					controls[0] = false;
					break;
				case KeyEvent.VK_DOWN:
					controls[1] = false;
					break;
				case KeyEvent.VK_LEFT:
					controls[2] = false;
					break;
				case KeyEvent.VK_RIGHT:
					controls[3] = false;
					break;
				case KeyEvent.VK_SPACE:
					controls[4] = false;
					break;
				}				
			}			
		}
	}	
}
