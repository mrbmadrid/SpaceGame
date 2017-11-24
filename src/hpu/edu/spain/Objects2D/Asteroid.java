
package hpu.edu.spain.Objects2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import hpu.edu.spain.Objects2D.WorldTask.TaskType;

public class Asteroid extends DynamicEntity{
	
	private static final long serialVersionUID = 8920501654817667417L;
	int statImmunityTime; //timer for immunity
	int initY, initX; //initial position
	BufferedImage sprite;
	
	/**
	 * @param xx absolute position on world x axis
	 * @param yy absolute position on world y axis
	 * @param size size of the asteroid
	 */
	
	public Asteroid(double xx, double yy, int size, World world) {
		super(xx, yy, Asteroid.getShape(size), world);
		initY = (int)yy;
		initX = (int)xx;
		this.size = size;
		staticImmunity = true;
		statImmunityTime = 150;
		c = Color.DARK_GRAY;
		id = 5;
		collisionDamage = 10*size;
		try {
		    sprite = ImageIO.read(new File("./res/Asteroid.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns shape of the asteroid, at origin.
	 * @param size size of the asteroid
	 * @return
	 */
	
	public static Polygon getShape(int size){
		return new Polygon(
				new int[]{
						0,
						0+size*5,
						0+size*10,
						0+size*15,
						0+size*15,
						0+size*10,
						0+size*5,
						0
				},
				new int[]{
						0,
						0-size*5,
						0-size*5,
						0,
						0+size*5,
						0+size*10,
						0+size*10,
						0+size*5,
				}, 8);			
	}
	
	/**
	 * Returns true if the sensor rectangles collide.
	 */

	@Override
	public boolean sensorCollide(Body b) {
		if(staticImmunity && b.type == BodyType.STATIC)
			return false;
		return sensorCollision(b);
	}
	
	/**
	 * Runs logic, by the specified step. A smaller step scales the tick down.
	 * @step scale of logic step
	 */
	
	@Override
	public void tick(double step){
		if(alive){
			if(Math.abs((yy - initY) + (xx-initX)) > 600){
				alive = false;
				world.addTask(new WorldTask(TaskType.KILL, this));
			}
			if(--statImmunityTime < 1){
				staticImmunity = false;
			}
		}
		super.tick(step);
	}
	
	/**
	 * Render the asteroid.
	 */
	
	@Override
	public void render(Graphics2D g2d){
		if(alive){
			g2d.drawImage(sprite, x, y, sensor.width, sensor.height, null);
		}
	}
}
