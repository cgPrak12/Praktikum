package terrain;


import org.lwjgl.util.vector.Matrix4f;

import util.Geometry;
import util.GeometryFactory;
import util.ShaderProgram;
import util.Util;

public class ClipMap {
	
	private int stage;
	private int gridsize;
	private int middlesize;
	private int lsize;
	private ShaderProgram program;
	private Matrix4f translation;
	
	public ClipMap(int n, int stage, ShaderProgram program){
		this.gridsize = n/4;
		this.middlesize = n%4;
		this.lsize = n/2+2;
		this.stage = stage;
		this.program = program;
	}
	
	public Geometry createMxMgrid(){
		Geometry geo = GeometryFactory.createGrid(gridsize, gridsize);
		return geo;
	}
	
	public Geometry createNxMgrid(){
		Geometry geo = GeometryFactory.createGrid(middlesize, gridsize);
		return geo;
	}
	
	public Geometry createMxNgrid(){
		//Geometry geo = GeometryFactory.createMxNGrid(gridsize+1, middlesize+1);
		Geometry geo = GeometryFactory.createMxNGrid(4, 12);
		
		return geo;
	}
	
	public Geometry createTopLeft(){
		return GeometryFactory.createL(lsize, 2);
	}
	public Geometry createTopRight(){
		return GeometryFactory.createL(lsize, 3);
	}
	public Geometry createBottomLeft(){
		return GeometryFactory.createL(lsize, 1);
	}
	public Geometry createBottomRight(){
		return GeometryFactory.createL(lsize, 0);
	}
	
	public void setProgram(){
		this.program.setUniform("translation", translation);
	}
	
	public void createClip(){
		translation = new Matrix4f();
//		Util.translationX(-3, translation);
//		setProgram();
//		createTopLeft().draw();
//		
//		Util.translationY(4, translation);
//		setProgram();
//		createTopRight().draw();
//		
//		Util.translationZ(6, translation);
//		setProgram();
//		createMxMgrid().draw();
		
		
		setProgram();		
		System.out.println(gridsize);
		
		createMxNgrid().draw();
	}
}
