package edu.hpu.spain.Game;

import java.io.Serializable;
import java.util.List;

import edu.hpu.spain.Objects2D.Body;

public class Level implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5774175168929506039L;
	
	List<Body> staticEntities;
	String bgFileName;
	
	public Level(List<Body> s, String b){
		staticEntities = s;
		bgFileName = b;
	}
	
	public List<Body> getStaticEntities(){
		return staticEntities;
	}
	
}
