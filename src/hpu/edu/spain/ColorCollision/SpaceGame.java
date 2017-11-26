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

public class SpaceGame extends JPanel{
	
	static PriorityQueue<GameTask> taskQueue;
	Color shipColor;
	Game game;
	
	public SpaceGame(){
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
		boolean bossSpawned, scrolling;
		int lives;
		BufferedImage spl;
		
		static boolean[] controls = new boolean[7];
		public Input input;
		public Game(){
			time = 0; bosses = 0; lives = 3;
			world = new World();
			scrolling = true;
			try {
				Background b = new Background(2000, 800);
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
				spl = ImageIO.read(new File("./res/ys.png"));
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
					sp.translate(0.0, (scrolling) ? -1.5 : -2.5);
					--dy;
				}
				if(controls[1] && sp.getY() < this.getHeight()-32){
					sp.translate(0.0, (scrolling) ? 1.5 : 2.5);
					++dy;
				}
				if(controls[2] && sp.getX() > -1){
					sp.translate((scrolling) ? -1.5 : -2.5, 0.0);
				};
				if(controls[3] && sp.getX() < this.getWidth()-50){
					sp.translate((scrolling) ? 1.5 : 2.5, 0.0);
					++dx;
				}
				if(controls[4]){
					Bullet b = new Bullet(sp.getX()+45, sp.getY()+18, world);
					b.setCollisionDamage(50);
					world.addTask(new WorldTask(TaskType.SPAWN, b));
					controls[4] = false;
				}
				if(controls[5] && controls [6]){
					saveLevel();
					controls[5]=false;
					controls[6]=false;
				}
			}else if(lives > 0){
				--lives;
				sp = new SpaceShip(50, 250, world);
				world.setPlayer(sp);
			}
			if(xx + bg.getWidth() > this.getWidth()){
				world.moveOrigin(-1-dx, -dy);
				xx-=1+dx;
				yy-=dy;
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
					if(time%300 == 0){
						if(Math.random() < 0.3){
							world.addTask(new WorldTask(TaskType.SPAWN, 
									new HealthStar(Math.random()*this.getWidth()+200, Math.random()*this.getHeight(), world)));
						}
					}
				}
			}else{
				if(bosses == 0){
					time = 0;
					sequencer.stop();
					++bosses;
					scrolling = !scrolling;
				}else if(bosses == 1 && time % 100 == 0){
					++bosses;
					boss = new BossShip(800, 0, 600, 100, world);
					world.addTask(new WorldTask(TaskType.SPAWN, boss));
					try {
						sequencer.setSequence(sequence1);
						sequencer.start();
					} catch (InvalidMidiDataException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if((dy > 0 && yy + bg.getHeight() < this.getHeight()) || (dy < 0 && yy > 0)){
					world.moveOrigin(0, -dy);
					yy-=dy;
				}
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
		
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.drawImage(bg, (int)xx, (int)yy-bg.getHeight()/4, this);
			world.render(g2d);
			for(int i = 0; i < lives; ++i){
				g2d.drawImage(spl, (this.getWidth()-lives*30-20)+i*30, 20, 30, 20, this);
			}
			Color c = new Color(225, 0, 0, 225);
			for(int i = 0; i < Math.min(200, sp.getLife())/20*30; i+=30){
				g2d.setColor(c);
				g2d.fillRect(i+20, 20, 20, 25);
				if(c.getGreen() == 225){
					c = new Color(Math.max(0,c.getRed()-75), c.getGreen(), 0, 225);
				}else{
					c = new Color(c.getRed(), Math.min(250, c.getGreen()+75), 0, 225);
				}
				g2d.setColor(Color.white);
				g2d.drawRect(i+20, 20, 20, 25);
			}
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
