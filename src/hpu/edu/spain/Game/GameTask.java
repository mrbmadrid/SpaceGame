package hpu.edu.spain.Game;

public class GameTask implements Comparable<GameTask>{
	
	double timeStamp;
	double dx, dy;
	Task task;
	
	public static enum Task {
		MOVE;
	}
	
	//Progress level map
	public GameTask(Task task, double speed, double theta, double timeStamp) {
		this.task = task;
		dx += speed*Math.cos(theta);
		dy += speed*Math.sin(theta);
		this.timeStamp = timeStamp;
	}

	@Override
	public int compareTo(GameTask o) {
		return (int)(o.timeStamp - timeStamp);
	}
	
}
