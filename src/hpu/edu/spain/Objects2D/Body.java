package hpu.edu.spain.Objects2D;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.Serializable;

import hpu.edu.spain.Objects2D.WorldTask.TaskType;

/**
 * Basic Collision Body for Objects2D physics package.
 * @author Brian Spain
 *
 */

public abstract class Body implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7093490747656594750L;
	Polygon body; //actual specific defined body
	Rectangle sensor; //containing rectangle for basic collision test
	double xx, yy; //precision location points
	int x, y; //integer location points
	double v, o; //velocity and trajectory
	boolean isBullet, alive;
	BodyType type; //denotes static or dynamic body
	Color c; //Color primarily used for destruction particles
	int id; //denotes specific class
	int size; //Size scale modifier for body and rendering size
	int life;
	int collisionDamage;
	
	/**
	 * Standard Body constructor, defaults to dynamic body
	 * @param xx absolute position on world x axis
	 * @param yy absolute position on world y axis
	 * @param p actual shape of body, used for collision
	 */
	
	public Body(double xx, double yy, Polygon p){
		this.xx = xx;
		this.yy = yy;
		x = (int)xx;
		y = (int)yy;
		p.translate(x-p.getBounds().x, y-p.getBounds().y);
		body = p;
		sensor = body.getBounds();
		type = BodyType.DYNAMIC;
		id = 0;
		life = Integer.MAX_VALUE;
		collisionDamage=Integer.MAX_VALUE;
		alive = true;
	}
	
	/**
	 * Get precise y position.
	 * @return double precision y position of body.
	 */
	
	public double getY(){
		return yy;
	}
	
	/**
	 * Get precise x position.
	 * @return double precision x position of body.
	 */
	
	public double getX(){
		return xx;
	}
	
	
	/**
	 * Set a new body polygon shape
	 * @param p new body shape
	 */
	
	public void setBody(Polygon p){
		body = p;
	}
	
	/**
	 * Set velocity
	 * @param v Body velocity in pixels/step
	 */
	
	public void setVelocity(double v){
		this.v = v;
	}
	/**
	 * Change velocity.
	 * @param dv amount to change velocity by.
	 */
	
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
	
	/**
	 * Change trajectory by specific amount
	 * @param t change trajectory by t
	 */
	
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
		body.translate(x-prevx, y-prevy);
		sensor.translate(x-prevx, y-prevy);
	}
	
	/**
	 * Directly move the body, ignoring velocity and trajectory
	 * @param dx amount to translate on x axis
	 * @param dy amount to translate on y axis
	 */
	
	public void translate(double dx, double dy){
		xx+=dx;
		yy+=dy;
		int prevx = x;
		int prevy = y;
		x = (int)xx;
		y = (int)yy;
		body.translate(x-prevx, y-prevy);
		sensor.translate(x-prevx, y-prevy);
	}
	
	/**
	 * Checks if the calling body's sensor intersects the
	 * parameter body.
	 * @param b
	 * @return true if calling body's sensor intersects the
	 * parameter body.
	 */
	
	protected boolean sensorCollision(Body b){
		return b.body.intersects(sensor);
	}
	
	/**
	 * Checks if the calling body intersects the
	 * parameter body.
	 * @param b
	 * @return true if calling body intersects the
	 * parameter body.
	 */
	
	protected boolean bodyCollision(Body b){
		for(int i = 0; i < body.npoints; i++){
			if(b.body.contains(body.xpoints[1], body.ypoints[i]))
				return true;
		}
		for(int i = 0; i < b.body.npoints; i++){
			if(body.contains(b.body.xpoints[i], b.body.ypoints[i]))
				return true;
		}
		return false;
	}
	
	/**
	 * Check if a point lies within the body.
	 * @param p
	 * @return true if p lies within the body.
	 */
	
	public enum BodyType{
		DYNAMIC, STATIC;
	}
	
	public void setType(BodyType t){
		type = t;
	}
	
	public int getCollisionDamage(){
		return collisionDamage;
	}
	
	public void doDamage(int damage){
		if(type != BodyType.STATIC){
			life-=damage;
			if(life < 1){
				alive = false;
			}
		}
	}
	
	public void tick(double step){
		move(step);
	}
	
	
}
