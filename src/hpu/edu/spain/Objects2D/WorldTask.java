package hpu.edu.spain.Objects2D;

public class WorldTask implements Comparable<WorldTask>{
	
	Body b;
	ParticleSystem p;
	TaskType t;
	long TimeStamp;
	
	
	public WorldTask(TaskType t, Body b){
		this.t=t;
		this.b=b;
		TimeStamp = System.nanoTime();
	}
	
	public WorldTask(TaskType t, ParticleSystem p){
		this.t=t;
		this.p=p;
		TimeStamp = System.nanoTime();
	}
	
	public enum TaskType{
		KILL, SPAWN, PARTICLE;
	}

	@Override
	public int compareTo(WorldTask o) {
		return (int)(TimeStamp-o.TimeStamp);
	}

}
