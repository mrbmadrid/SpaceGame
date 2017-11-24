package hpu.edu.spain.Objects2D;

import java.awt.Graphics2D;
import java.awt.Polygon;

import hpu.edu.spain.Objects2D.WorldTask.TaskType;

public abstract class DynamicEntity extends Body{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6408131470107565133L;
	World world;
	boolean staticImmunity; //Immunity to collisions with static bodies

	
	/**
	 * Constructor. alive is defaulted to true.
	 * @param xx absolute position on world x axis
	 * @param yy absolute position on world y axis
	 * @param p actual shape of body, used for collision
	 * @param world the physics world this entity belongs to
	 */
	
	public DynamicEntity(double xx, double yy, Polygon p, World world) {
		super(xx, yy, p);
		this.world = world;
		alive = true;
		life = 1;
		collisionDamage = 1;
	}
	
	/**
	 * Checks if the body is dead.
	 * @return true if not alive.
	 */
	
	public boolean isDead(){
		return !alive;
	}
	
	public int getLife(){
		return life;
	}
	
	public boolean sensorCollide(Body b) {
		return sensorCollision(b);
	}
	
	public void collide(Body b) {
		if(bodyCollision(b)){
			if(b.alive){
				doDamage(b.getCollisionDamage());
				b.doDamage(collisionDamage);
			}
		}
	}
	
	public abstract void render(Graphics2D g2d);
	
	public void setStaticImmunity(boolean staticImmunity){
		this.staticImmunity = staticImmunity;
	}
	
	@Override
	public void tick(double step){
		if(!alive || life < 1){
			world.addTask(new WorldTask(TaskType.KILL, this));
		}else{
			super.tick(step);
		}
	}
	
	/**
	 * Get size of the dynamic entity.
	 * @return integer size.
	 */
	
	public int getSize(){
		return size;
	}
	
	
}
