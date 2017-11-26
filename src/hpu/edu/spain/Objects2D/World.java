package hpu.edu.spain.Objects2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import hpu.edu.spain.Objects2D.WorldTask.TaskType;

public class World {
	
	ArrayList<DynamicEntity> dynamicEntities;
	ArrayList<Body> allBodies;
	ArrayList<Body> staticBodies;
	ArrayList<ParticleSystem> particleSystems;
	PriorityQueue<WorldTask> worldTasks;
	SpaceShip player;
	double xx, yy;
	int x, y, px, py, time, width, height;
	boolean test;
	
	public World(){
		allBodies = new ArrayList<>();
		dynamicEntities = new ArrayList<>();
		staticBodies = new ArrayList<>();
		particleSystems = new ArrayList<>();
		worldTasks = new PriorityQueue<>();
	}
	
	public void setPlayer(SpaceShip p){
		player = p;
		dynamicEntities.add(p);
		allBodies.add(p);
	}
	
	public void addStaticBody(Body b){
		staticBodies.add(b);
		allBodies.add(b);
	}
	
	public void addDynamicEntity(DynamicEntity b){
		dynamicEntities.add(b);
		allBodies.add(b);
	}
	
	public void addParticleSystem(ParticleSystem s){
		particleSystems.add(s);
	}
	
	public void translateBodies(int x, int y){
		if(x != 0 || y != 0){
			for(Body b : staticBodies){
				b.body.translate(x, y);
			}
			for(Body b : dynamicEntities){
				if(b != player){
					b.translate(x, y);
				}
			}
		}
	}
	
	public void moveOrigin(double dx, double dy){
		xx += dx;
		yy += dy;
		x = (int)xx;
		y = (int)yy;
	}
	
	public void tick(double step, int width, int height){
		this.width = width;
		this.height = height;
		/**********************************
		 *Process any tasks added to queue* 
		 **********************************/
		processTasks();
		
		/*********************************
		 * Shift all objects in the world*
		 * if the "camera" shifted	     *
		 *********************************/
		translateBodies(x-px, y-py);
		px=x; py=y;
		
		/**********************************
		 *Tick collision bodies          * 
		 **********************************/
		
		for(DynamicEntity e : dynamicEntities){
			e.tick(step);
		}
		
		/**********************************
		 *Check for collisions            * 
		 **********************************/
		
		for(DynamicEntity e : dynamicEntities){
			for(Body b : allBodies){
				if(e != b){
					if(e.sensorCollide(b)){
						e.collide(b);
					}
				}
			}
		}
		
		/**********************************
		 *Tick particle system logic      * 
		 **********************************/
		for(int i = 0; i < particleSystems.size(); i++){
			if(particleSystems.get(i).isDead) particleSystems.remove(i);
			else particleSystems.get(i).tick(step);
		}
	}
	
	public void render(Graphics2D g2d){
		for(int i = 0; i < particleSystems.size(); ++i){
			particleSystems.get(i).render(g2d);
		}
		for(int i = 0; i < dynamicEntities.size(); ++i){
			dynamicEntities.get(i).render(g2d);
		}
	}
	
	public List<Body> getStaticBodies(){
		return staticBodies;
	}
	
	/**
	 * Generates explosion particle system
	 * @param xx absolute position on world x axis
	 * @param yy absolute position on world y axis
	 * @param c color of explosion
	 */
	
	public void explosion(double xx, double yy, Color c, int size){
		ParticleSystem p = new ParticleSystem(xx, yy, 0, c, size*15, 3, 200);
		p.setLifeSpan(100);
		p.setParticleTrajectoryRange(0, 2*Math.PI);
		p.setSpawnRange(size, size);
		particleSystems.add(p);
		if(size > 2){
			ParticleSystem p1 = new ParticleSystem(xx, yy, 1, c, size*10, 5, 150);
			p1.setLifeSpan(120);
			p1.setParticleTrajectoryRange(0, 2*Math.PI);
			p1.setSpawnRange(size, size);
			particleSystems.add(p1);
		}
		if(size > 5){
			ParticleSystem p2 = new ParticleSystem(xx, yy, 0, c, size*5, 7, 100);
			p2.setLifeSpan(140);
			p2.setParticleTrajectoryRange(0, 2*Math.PI);
			p2.setSpawnRange(size, size);
			particleSystems.add(p2);
		}	
	}
	
	/**
	 * Spawns three of the next size down of asteroids
	 * @param temp exploded asteroid
	 */
	
	public void asteroidMult(double xx, double yy, int size){
		explosion(xx, yy, Color.LIGHT_GRAY, size);
		for(int i = 0; i < size; i++){
			double ran = Math.random();
			double angleOffset = 2*Math.PI * (double)i/(double)size + Math.random()*Math.PI;
			if(ran > 0.25){
				Asteroid a1 = new Asteroid(xx+(10*size)*Math.cos(angleOffset),
						yy+(10*size)*Math.sin(angleOffset), size-1, this);
				a1.setTrajectory(angleOffset);
				a1.setVelocity(50+Math.random()*50);
				addTask(new WorldTask(TaskType.SPAWN, a1));
			}else if(size > 1 && ran < 0.25){
				asteroidMult(xx+(10*size)*Math.cos(angleOffset), 
						yy+(10*size)*Math.sin(angleOffset), size-1);
			}
		}
	}
	
	public void addTask(WorldTask t){
		worldTasks.add(t);
	}
	
	public void processTasks(){
		while(!worldTasks.isEmpty()){
			WorldTask t = worldTasks.poll();
			switch(t.t){
			case KILL:
				switch(t.b.id){
				case 1: case 6:
					dynamicEntities.remove(t.b);
					allBodies.remove(t.b);
					break;
				case 5:
					dynamicEntities.remove(t.b);
					allBodies.remove(t.b);
					if(t.b.size > 1) asteroidMult(t.b.xx, t.b.yy, t.b.size);
					else explosion(t.b.xx, t.b.yy, t.b.c, 3);
					break;
				default:
					dynamicEntities.remove(t.b);
					allBodies.remove(t.b);
					explosion(t.b.xx, t.b.yy, t.b.c, 15);
					break;
				}
				break;
			case SPAWN:
				dynamicEntities.add((DynamicEntity)t.b);
				allBodies.add(t.b);
				break;
			case PARTICLE:
				particleSystems.add(t.p);
				break;
			
			}
		}
	}
}
