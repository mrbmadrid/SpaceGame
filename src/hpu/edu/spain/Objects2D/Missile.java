package hpu.edu.spain.Objects2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import hpu.edu.spain.Objects2D.WorldTask.TaskType;

public class Missile extends DynamicEntity{
	
	private static final long serialVersionUID = -1790676289124334902L;
	ParticleSystem thrust1, thrust2;
	Point exhaust1, exhaust2; //Anchor points for thrusts
	BufferedImage sprite;
	
	public Missile(double xx, double yy, World world) {
		super(xx, yy, Missile.getBody(), world);
		setVelocity(-150);
		exhaust1 = new Point((int)xx+35, (int)yy);
		thrust1 = new ParticleSystem(xx+35, yy, 1, Color.RED, 0, 4, 75, exhaust1);
		thrust1.setParticleTrajectoryRange(-Math.PI/10, Math.PI/10);
		thrust1.setSpawnRange(0, 2);
		thrust1.setRespawn(1);
		exhaust2 = new Point((int)xx+35, (int)yy);
		thrust2 = new ParticleSystem(xx+35, yy, 0, Color.RED, 0, 4, 75, exhaust2);
		thrust2.setParticleTrajectoryRange(-Math.PI/10, Math.PI/10);
		thrust2.setSpawnRange(0, 2);
		thrust2.setRespawn(1);
		c = Color.RED;
		id = 2;
		collisionDamage = 25;
		try {
		    sprite = ImageIO.read(new File("./res/missile.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Polygon getBody(){
		return new Polygon(new int[]{
			0,
			0+32,
			0+32,
			0
			},
			new int[]{
			0, 
			0,
			0+8,
			0+8,
			},
			4);
	}
	
	@Override
	public void tick(double step){
		if(alive){
			if(xx < -50) alive = false;
			exhaust1.setLocation((int)xx+35, (int)yy+2);
			exhaust2.setLocation((int)xx+35, (int)yy+2);
			thrust1.tick(step);
			thrust2.tick(step);
			setTrajectory(Math.sin(xx/50));
		}
		super.tick(step);
	}
	
	@Override
	public void render(Graphics2D g2d){
		if(alive){
			thrust2.render(g2d);
			thrust1.render(g2d);
			g2d.drawImage(sprite, null, x, y);
		}
	}
}
