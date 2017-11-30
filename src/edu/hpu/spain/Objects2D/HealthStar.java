package edu.hpu.spain.Objects2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

public class HealthStar extends DynamicEntity {

	private static final long serialVersionUID = -8434538224737225649L;
	
	Point partAnchor;
	ParticleSystem part;
	
	public HealthStar(double xx, double yy, World world){
		this(xx, yy, new Polygon(
				new int[]{
						0,
						3,
						3,
						9,
						9,
						3,
						3,
						0,
						0,
						-6,
						-6,
						0,
				},
				new int[]{
						0,
						0,
						6,
						6,
						9,
						9,
						15,
						15,
						9,
						9,
						6,
						6	
				}, 12), world);
	}
	
	public HealthStar(double xx, double yy, Polygon p, World world) {
		super(xx, yy, p, world);
		collisionDamage = -50;
		c = Color.GREEN.brighter();
		partAnchor = new Point((int)xx+3, (int)yy+7);
		part = new ParticleSystem(xx, yy, 2, Color.GREEN, 1, 3, 20, partAnchor);
		part.setRespawn(1);
		part.setRespawnAmount(3);
		part.setSpawnRange(5, 5);
		id = 6;
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
