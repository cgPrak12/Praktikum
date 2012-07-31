package terrain;

import java.nio.FloatBuffer;

import util.Geometry;
import util.GeometryFactory;

public class ClipMap {
	
	private float[] vertices;
	private float[] indices;
	private int stage;
	private int size;
	private int m;
	
	public ClipMap(int n, int stage){
		m = (n+1)/4;
	}
	
	private Geometry createMxMgrid(){
		Geometry geo = GeometryFactory.createGrid(m-1, m-1);
		return geo;
	}
	
	private Geometry createNxMgrid(){
		Geometry geo = GeometryFactory.createGrid(m-1, (size-1)-((m-1)*4));
		return geo;
	}
	
	private Geometry createMxNgrid(){
		Geometry geo = GeometryFactory.createGrid((size-1)-((m-1)*4),m-1);
		return geo;
	}
	
	private Geometry createTopLeft(){
		Geometry geo = 
	}
}
