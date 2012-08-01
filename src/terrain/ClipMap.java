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
	private float size;
	
	public ClipMap(int n, int stage, ShaderProgram program){
		this.gridsize = n/4;
		this.middlesize = n%4;
		this.lsize = n/2+2;
		this.stage = stage;
		this.program = program;
		this.size = n;
	}
	
	public Geometry createMxMgrid(){
		Geometry geo = GeometryFactory.createGrid(gridsize+1, gridsize+1);
		return geo;
	}
	
	public Geometry createNxMgrid(){
		Geometry geo = GeometryFactory.createGrid(middlesize+1, gridsize+1);
		return geo;
	}
	
	public Geometry createMxNgrid(){

		Geometry geo = GeometryFactory.createMxNGrid(gridsize+1,middlesize+1);
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
//		// 1
//		Util.mul(translation, Util.translationX(-size/2, null), Util.translationZ(size/2, null));
//		setProgram();
//		createMxMgrid().draw();
//
//		// 2
//		Util.mul(translation, Util.translationX(-size/2+gridsize, null), Util.translationZ(size/2, null));
//		setProgram();
//		createMxMgrid().draw();
//
//		// 3
//		Util.mul(translation, Util.translationX(size/2-gridsize-gridsize, null), Util.translationZ(size/2, null));
//		setProgram();
//		createMxMgrid().draw();
//		
//		// 4
//		Util.mul(translation, Util.translationX(size/2-gridsize, null), Util.translationZ(size/2, null));
//		setProgram();
//		createMxMgrid().draw();
//
//		// 5
//		Util.mul(translation, Util.translationX(-size/2, null), Util.translationZ(size/2-gridsize, null));
//		setProgram();
//		createMxMgrid().draw();
//		
//		// 6
//		Util.mul(translation, Util.translationX(size/2-gridsize, null), Util.translationZ(size/2-gridsize, null));
//		setProgram();
//		createMxMgrid().draw();
		 
		// L
		Util.mul(translation, Util.translationX(-size/2+gridsize, null),Util.translationZ(size/2-2*gridsize-middlesize, null));
		setProgram();
		createTopRight().draw();
		
//		// 7
//		Util.mul(translation, Util.translationX(-size/2, null), Util.translationZ(-size/2+2*gridsize, null));
//		setProgram();
//		createMxMgrid().draw();
//		
//		// 8
//		Util.mul(translation, Util.translationX(size/2-gridsize, null), Util.translationZ(-size/2+2*gridsize, null));
//		setProgram();
//		createMxMgrid().draw();
//
//		// 9
//		Util.mul(translation, Util.translationX(-size/2, null), Util.translationZ(-size/2+gridsize, null));
//		setProgram();
//		createMxMgrid().draw();
//		
//		// 10
//		Util.mul(translation, Util.translationX(-size/2+gridsize, null), Util.translationZ(-size/2+gridsize, null));
//		setProgram();
//		createMxMgrid().draw();
//
//		// 11
//		Util.mul(translation, Util.translationX(size/2-2*gridsize, null), Util.translationZ(-size/2+gridsize, null));
//		setProgram();
//		createMxMgrid().draw();
//
//		// 12
//		Util.mul(translation, Util.translationX(size/2-gridsize, null), Util.translationZ(-size/2+gridsize, null));
//		setProgram();
//		createMxMgrid().draw();
		

		// Oben
	//	Util.translationZ(size/2, null);
		//setProgram();

		//createMxNgrid().draw();
	}
}
