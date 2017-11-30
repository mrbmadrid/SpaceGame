package edu.hpu.spain.Objects2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

public class Bullet extends DynamicEntity{

	private static final long serialVersionUID = 8001054271595557561L;
	int lifeSpan; //Number of ticks the Body lives for
	
	/**
	 * Basic constructor, creates a generic rectangle shape.
	 * @param xx absolute position on world x axis
	 * @param yy absolute position on world y axis
	 */
	
	public Bullet(double xx, double yy, World world){
		this(xx, yy, 
				new Polygon(
						new int[]{
								0-2,
								0-2, 
								0+6, 
								0+6}, 
						new int[]{
								0-2, 
								0+2, 
								0+2, 
								0-2}, 
						4), world);
	}
	
	/**
	 * Constructor that accepts a specific shape for the bullet
	 * @param xx absolute position on world x axis
	 * @param yy absolute position on world y axis
	 * @param p actual shape of body, used for collision
	 */
	
	public Bullet(double xx, double yy, Polygon p, World world) {
		super(xx, yy, p, world);
		setVelocity(500);
		isBullet = true;
		alive = true;
		type = BodyType.DYNAMIC;
		lifeSpan = 120;
		id = 1;
		collisionDamage = 10;
	}
	
	/**
	 * Runs logic, by the specified step. A smaller step scales the tick down.
	 * @param scale of logic step
	 */
	
	public void tick(double step){
		if(--lifeSpan == 0) alive = false;
		super.tick(step);
	}
	
	/**
	 * Renders the bullet
	 * @param g2d instance of Graphics2D to render
	 */
	
	public void render(Graphics2D g2d){
		g2d.setColor(Color.WHITE);
		g2d.fill(body);
	}
}