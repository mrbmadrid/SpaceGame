package hpu.edu.spain.Objects2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

public class ParticleSystem {
	
	List<Particle> particles;
	boolean infinite, isDead;
	int time, respawnRate, respawnAmount, lifeSpan;
	double xx, yy, v, omin, omax;
	int xrange, yrange, size;
	Polygon p;
	Color c;
	Point anchor;
	
	public ParticleSystem(double xx, double yy, int type, Color c, int numParticles, int size, int velocity, Point anchor){
		this(xx,yy, type, c, numParticles, size, velocity);
		this.anchor = anchor;
	}
	
	public ParticleSystem(double xx, double yy, int type, Color c, int numParticles, int size, int velocity){
		this.xx = xx;
		this.yy = yy;
		particles = new ArrayList<>();
		p = getParticle(type);
		this.c = c;
		respawnRate = Integer.MAX_VALUE;
		respawnAmount = 1;
		infinite = true;
		this.size = size;
		this.v = velocity;
		omin = 0;
		omax = 2*Math.PI;
		for(int i = 0; i < numParticles; i++){
			double scale = size*Math.random();
			int[] xpoints = new int[p.npoints];
			int[] ypoints = new int[p.npoints];
			for(int j = 0; j < p.npoints; j++){
				xpoints[j] = (int)(scale*p.xpoints[j]);
				ypoints[j] = (int)(scale*p.ypoints[j]);
			}
			Polygon pc = new Polygon(xpoints, ypoints, p.npoints);
			pc.translate((int)xx, (int)yy);
			Particle part = new Particle(xx, yy, pc, c, 60);
			part.setVelocity(Math.random()*v+v);
			part.setTrajectory(Math.random()*(omax-omin)+omin);
			particles.add(part);
		}
	}
	
	public void setLifeSpan(int lifeSpan){
		this.lifeSpan = lifeSpan;
		infinite = false;
	}
	
	public void setRespawn(int rate){
		respawnRate = rate;
	}
	
	public void setRespawnAmount(int amount){
		respawnAmount = amount;
	}
	
	public void translate(double dx, double dy){
		xx+=dx;
		yy+=dy;
	}
	
	public void setSpawnRange(int x, int y){
		xrange = x;
		yrange = y;
	}
	
	public void setParticleVelocity(double v){
		for(Particle part: particles){
			part.setVelocity(v);
		}
		this.v = v;
	}
	
	public void setParticleTrajectoryRange(double min, double max){
		for(Particle part: particles){
			part.setTrajectory(Math.random()*(max-min)+min);
		}
		this.omin = min;
		this.omax = max;
	}
	
	public void setSize(int size){
		this.size = size;
	}
	
	public void tick(double step){
		if(anchor != null){
			xx = anchor.getX();
			yy = anchor.getY();
		}
		if(++time%respawnRate==0){
			for(int i = 0; i < respawnAmount; i++){
				double scale = size*Math.random();
				int[] xpoints = new int[p.npoints];
				int[] ypoints = new int[p.npoints];
				for(int j = 0; j < p.npoints; j++){
					xpoints[j] = (int)(scale*p.xpoints[j]);
					ypoints[j] = (int)(scale*p.ypoints[j]);
				}
				Polygon pc = new Polygon(xpoints, ypoints, p.npoints);
				int ypos = (int)(yy+Math.random()*yrange-yrange/2);
				int xpos = (int)(xx+Math.random()*xrange-xrange/2);
				pc.translate(xpos, ypos);
				Particle part = new Particle(xpos, ypos, pc, c, 60);
				part.setVelocity(Math.random()*v+v);
				part.setTrajectory(Math.random()*(omax-omin)+omin);
				particles.add(part);
			}
		}
		for(int i = 0; i < particles.size(); i++){
			particles.get(i).tick(step);
			if(particles.get(i).isDead){
				particles.remove(i);
			}
		}
		if(!infinite && --lifeSpan == 0) isDead = true;
	}
	
	public void render(Graphics2D g2d){
		for(int i = 0; i < particles.size(); i++){
			particles.get(i).render(g2d);
		}
	}
	
	public Polygon getParticle(int type){
		switch (type){
		case 0:
			return new Polygon(
					new int[]{-1, 1, 1, -1}, 
					new int[]{1, 1, -1, -1}, 4);
		case 1:
			return new Polygon(
					new int[]{1, -1, 1}, 
					new int[]{0, 1, 2}, 3);
		case 2:
			return new Polygon(
					new int[]{ 0, 1, 1, 3, 3, 1, 1, 0, 0, -2, -2, 0 },
					new int[]{ 0, 0, 2, 2, 3, 3, 5, 5, 3, 3, 2, 2}, 12);
		case 3:
			new Polygon(
					new int[]{0,5,0,-5},
					new int[]{0,5,10,5}, 4);
			default:
				return new Polygon(new int[]{-1, 1, 1, -1}, new int[]{1, 1, -1, -1}, 4);
		}
	}
}
