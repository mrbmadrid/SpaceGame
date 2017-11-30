package edu.hpu.spain.Objects2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

public class UpgradeStar extends DynamicEntity {

	private static final long serialVersionUID = -8434538224737225649L;
	
	Point partAnchor;
	ParticleSystem part;
	
	public UpgradeStar(double xx, double yy, World world){
		this(xx, yy, new Polygon(
				new int[]{
						0,
						5,
						0,
						-5
				},
				new int[]{
						0,
						5,
						10,
						5
				}, 4), world);
	}
	
	public UpgradeStar(double xx, double yy, Polygon p, World world) {
		super(xx, yy, p, world);
		collisionDamage = 0;
		c = Color.CYAN.brighter();
		partAnchor = new Point((int)xx+3, (int)yy+7);
		part = new ParticleSystem(xx, yy, 3, Color.CYAN, 1, 3, 10, partAnchor);
		part.setRespawn(1);
		part.setRespawnAmount(3);
		part.setSpawnRange(5, 5);
		id = 7;
	}
	
	@Override
	public void collide(Body b){
		if(b.id == 3){
			super.collide(b);
		}
	}
	
	@Override
	public void tick(double step){
		if(alive){
			part.tick(step);
			partAnchor.setLocation(xx+4, yy+6);
		}
		super.tick(step);
	}

	@Override
	public void render(Graphics2D g2d) {
		if(alive){
			part.render(g2d);
			g2d.setColor(c);
			g2d.fill(body);
		}
	}

}
