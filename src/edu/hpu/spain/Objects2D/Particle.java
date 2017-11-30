package edu.hpu.spain.Objects2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

public class Particle {
	
	Polygon particle;
	Color c;
	int lifeSpan;
	double xx, yy, v, a, o;
	int x, y;
	boolean isDead;
	
	public Particle(double xx, double yy, Polygon p, Color c, int lifeSpan){
		this.particle = p;
		this.c = c;
		this.lifeSpan = lifeSpan;
		this.xx = xx;
		x = (int)xx;
		this.yy = yy;
		y = (int)yy;
	}
	
	/**
	 * Set velocity
	 * @param v Body velocity in pixels/step
	 */
	
	public void setVelocity(double v){
		this.v = v;
	}
	
	public void setAcceleration(double a){
		this.a = a;
	}
	
	public void accelerate(double dv){
		v += dv;
	}
	
	/**
	 * Set trajectory
	 * @param o Body trajectory in radians
	 */
	
	public void setTrajectory(double o){
		this.o = o;
	}
	
	public void turn(double t){
		o += t;
		o = (o < 0) ? 2*Math.PI+o : (o > 2*Math.PI) ? o - 2*Math.PI : o;
	}
	
	
	/**
	 * Move body in direction of its trajectory
	 * by its velocity parameter. Scale by step
	 * for slower/faster frame rates.
	 * @param step >1.0 faster, <1.0 slower
	 */
	
	public void move(double step){
		xx+= v*Math.cos(o)*step;
		yy-= v*Math.sin(o)*step;
		int prevx = x;
		int prevy = y;
		x = (int)xx;
		y = (int)yy;
		particle.translate(x-prevx, y-prevy);
		particle.invalidate();
	}
	
	public void translate(double dx, double dy){
		xx+=dx;
		yy+=dy;
		int prevx = x;
		int prevy = y;
		x = (int)xx;
		y = (int)yy;
		particle.translate(x-prevx, y-prevy);
		particle.invalidate();
	}
	
	public void tick(double step){
		move(step);
		c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 19*c.getAlpha()/20);
		if(--lifeSpan == 0){
			isDead = true;
		}
	}
	
	public void render(Graphics2D g2d){
		g2d.setColor(c);
		g2d.fill(particle);
	}
}
