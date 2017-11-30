package edu.hpu.spain.Objects2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import edu.hpu.spain.Game.Main;
import edu.hpu.spain.Objects2D.WorldTask.TaskType;

public class BossShip extends DynamicEntity{
	
	private static final long serialVersionUID = -1625064927221645542L;
	ParticleSystem thrust1, thrust2, thrust3, thrust4;
	Point exhaust1, exhaust2; //Anchor points for thrusts
	BufferedImage body;
	BufferedImage arm;
	List<BufferedImage> arms;
	boolean opening, closing, open, closed, started;
	int timer;
	int armIndex;
	double startx, starty;
	World world;
	
	/**
	 * Constructor
	 * @param xx absolute position on world x axis
	 * @param yy absolute position on world y axis
	 */
	
	public BossShip(double xx, double yy, double startx, double starty, World world) {
		super(xx, yy, SpaceShip.getShape(), world);
		this.world = world;
		this.startx = startx;
		this.starty = starty;
		exhaust1 = new Point((int)xx+30, (int)yy+5);
		thrust1 = new ParticleSystem(xx+32, yy, 0, Color.ORANGE, 0, 4, -75, exhaust1);
		thrust1.setParticleTrajectoryRange(9*Math.PI/10, 11*Math.PI/10);
		thrust1.setSpawnRange(0, 2);
		thrust1.setRespawn(1);
		thrust2 = new ParticleSystem(xx+32, yy, 1, Color.ORANGE, 0, 4, -75, exhaust1);
		thrust2.setParticleTrajectoryRange(9*Math.PI/10, 11*Math.PI/10);
		thrust2.setSpawnRange(0, 2);
		thrust2.setRespawn(1);
		exhaust2 = new Point((int)xx+30, (int)yy+27);
		thrust3 = new ParticleSystem(xx+32, yy, 0, Color.ORANGE, 0, 4, -75, exhaust2);
		thrust3.setParticleTrajectoryRange(9*Math.PI/10, 11*Math.PI/10);
		thrust3.setSpawnRange(0, 2);
		thrust3.setRespawn(1);
		thrust4 = new ParticleSystem(xx+32, yy, 1, Color.ORANGE, 0, 4, -75, exhaust2);
		thrust4.setParticleTrajectoryRange(9*Math.PI/10, 11*Math.PI/10);
		thrust4.setSpawnRange(0, 2);
		thrust4.setRespawn(1);
		c = new Color(255, 0, 0);
		id = 4;
		collisionDamage = 100;
		life = 1500;
		try {
		    body = ImageIO.read(Main.class.getResource("/res/bossbody.png"));
		    arm = ImageIO.read(Main.class.getResource("/res/bossarms.png"));
		    arms = new ArrayList<>();
		    for(int i = 0; i < 10; ++i){
		    	arms.add(arm.getSubimage(i*32, 0, 32, 32));
		    }
		    arm = arms.get(0);
		    closed = true;
		} catch (IOException e) {
		}
	}
	
	/**
	 * Creates the collision shape
	 * @return the polygon shape for the collision body
	 */
	
	public static Polygon getShape(){
		return new Polygon(
				new int[]{
					0,
					15,
					45,
					40,
					15,
					0
				},
				new int[]{
					7,
					0,
					10,
					20,
					25,
					15
				},
				6);		
	}

	@Override
	public void tick(double step){
		if(alive){
			if(started){
				if(yy > 400){
					if(v < 100)
						accelerate(5);
				}
				else if(yy < 50){
					if(v > -100)
						accelerate(-5);
				}else{
					if(Math.abs(v) < 200)
						if(v<0)
						accelerate(-1);
						if(v>0)
						accelerate(1);
				}
				if(timer % 300 == 0){
					if(closed){
						opening = true;
						closed = false;
					}else{
						closing = true;
						open = false;
					}
				}else{
					if(open){
						if(timer%30==0){
							Missile m = new Missile(xx-32, yy+15, world);
							m.setVelocity(-300);
							world.addTask(new WorldTask(TaskType.SPAWN, m));
						}
					}
					if(closed){
						if(timer%10==0){
							Bullet b = new Bullet(xx-32, yy+15, world);
							b.setVelocity(-500);
							world.addTask(new WorldTask(TaskType.SPAWN, b));
						}
					}
				}
				if(opening){
					if(timer % 5 == 0 && armIndex < 9){
						arm = arms.get(++armIndex);
						if(armIndex == 9){
							opening = false;
							open = true;
						}
					}
				}else if(closing){
					if(timer % 5 == 0 && armIndex > 0){
						arm = arms.get(--armIndex);
						if(armIndex == 0){
							closing = false;
							closed = true;
						}
					}
				}
			}else{
				if(Math.abs(xx-startx) > 5)
					xx+= ((xx-startx < 0) ? 1.0 : -1.0)*Math.max(2, (xx-startx)/120.0);
				if(Math.abs(yy-starty) > 5)
					yy+= ((yy-starty < 0) ? 1.0 : -1.0)*Math.max(2, (yy-starty)/120.0);
				if(Math.abs(xx-startx) < 5 && Math.abs(yy-starty) < 5){
					started = true;
					setVelocity(75);
					setTrajectory(Math.PI/2);
				}
			}
			exhaust1.setLocation((int)xx+30, (int)yy+5);
			exhaust2.setLocation((int)xx+30, (int)yy+27);
			thrust1.tick(step);
			thrust2.tick(step);
			thrust3.tick(step);
			thrust4.tick(step);
			++timer;
		}
		super.tick(step);
	}
	
	public boolean hasStarted(){
		System.out.println(started);
		return started;
	}
	public boolean open(){
		return open;
	}
	public boolean closed(){
		return closed;
	}
	
	@Override
	public void doDamage(int damage){
		if(open){
			super.doDamage(damage);
			if(alive){
			ParticleSystem p = new ParticleSystem(xx, yy, 1, Color.GREEN.brighter(), 50, 4, 50);
			p.setLifeSpan(30);
			world.addTask(new WorldTask(TaskType.PARTICLE, p));
			}
		}
	}
	
	@Override
	public void render(Graphics2D g2d){
		if(alive){
			thrust1.render(g2d);
			thrust2.render(g2d);
			thrust3.render(g2d);
			thrust4.render(g2d);
			g2d.drawImage(body, null, x-5, y);
			g2d.drawImage(arm, null, x-15, y-3*armIndex);
		}
	}

}
