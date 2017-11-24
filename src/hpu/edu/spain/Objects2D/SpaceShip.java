package hpu.edu.spain.Objects2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpaceShip extends DynamicEntity{
	
	private static final long serialVersionUID = -1625064927221645542L;
	ParticleSystem thrust1, thrust2;
	Point exhaust1, exhaust2; //Anchor points for thrusts
	BufferedImage sprite;
	
	/**
	 * Constructor
	 * @param xx absolute position on world x axis
	 * @param yy absolute position on world y axis
	 */
	
	public SpaceShip(double xx, double yy, World world) {
		super(xx, yy, SpaceShip.getShape(), world);
		exhaust1 = new Point((int)xx-3, (int)yy+7);
		thrust1 = new ParticleSystem(xx-2, yy, 1, new Color(0, 150, 200), 0, 4, 75, exhaust1);
		thrust1.setParticleTrajectoryRange(9*Math.PI/10, 11*Math.PI/10);
		thrust1.setSpawnRange(0, 2);
		thrust1.setRespawn(1);
		exhaust2 = new Point((int)xx-3, (int)yy+9);
		thrust2 = new ParticleSystem(xx-2, yy, 0, new Color(0, 100, 150), 0, 4, 75, exhaust2);
		thrust2.setParticleTrajectoryRange(9*Math.PI/10, 11*Math.PI/10);
		thrust2.setSpawnRange(0, 2);
		thrust2.setRespawn(1);
		c = new Color(100, 100, 255);
		id = 3;
		life = 200;
		try {
		    sprite = ImageIO.read(new File("./res/ys.png"));
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
			if(life > 200) life = 200;
			exhaust1.setLocation((int)xx-3, (int)yy+7);
			exhaust2.setLocation((int)xx-3, (int)yy+9);
			thrust1.tick(step);
			thrust2.tick(step);
		}
		super.tick(step);
	}
	
	@Override
	public void render(Graphics2D g2d){
		if(alive){
			thrust2.render(g2d);
			thrust1.render(g2d);
			g2d.drawImage(sprite, null, x-5, y);
		}
	}

}
