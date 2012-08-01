package terrain;


import util.Geometry;
import util.GeometryFactory;

public class ClipMap {
	
	private int stage;
	private int size;
	private int m;
	
	public ClipMap(int n, int stage){
		this.size = n;
		this.stage = stage;
		m = (n+1)/4;
	}
	
	public Geometry createMxMgrid(){
		Geometry geo = GeometryFactory.createGrid(m, m);
		return geo;
	}
	
	public Geometry createNxMgrid(){
		Geometry geo = GeometryFactory.createGrid(m-1, (size-1)-((m-1)*4));
		return geo;
	}
	
	public Geometry createMxNgrid(){
		Geometry geo = GeometryFactory.createGrid((size-1)-((m-1)*4),m-1);
		return geo;
	}
	
	public Geometry createTopLeft(){
		return GeometryFactory.createL((size+1)/2, 2);
	}
	public Geometry createTopRight(){
		return GeometryFactory.createL((size+1)/2, 3);
	}
	public Geometry createBottomLeft(){
		return GeometryFactory.createL((size+1)/2, 1);
	}
	public Geometry createBottomRight(){
		return GeometryFactory.createL((size+1)/2, 0);
	}
}
