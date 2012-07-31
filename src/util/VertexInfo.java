package util;

/**
 * represents a set of vertex information.
 * vertex information is typically a float, can also be a set of floats. 
 * @author LZELLER
 *
 */

public class VertexInfo {
	
	private float x;
	private float height;
	private float z;
	private float normal_x;
	private float normal_y;
	private float normal_z;
	private float mat;
	
	public VertexInfo(float x, float h, float z, float nx, float ny, float nz, float m)
	{
		this.x = x;
		this.height = h;
		this.z = z;
		normal_x = nx;
		normal_y = ny;
		normal_z = nz;
		mat = m;
	}
	
	public void setX(float x)		{ this.x = x; }
	public void setHeight(float h)  { height = h; }
	public void setZ(float z)		{ this.z = z; }
	public void setNX(float nx) 	{ normal_x = nx; }
	public void setNY(float ny) 	{ normal_y = ny; }
	public void setNZ(float nz) 	{ normal_z = nz; }
	public void setMat(float m) 	{ mat = m; }
	
	public float getX()			{ return x; }
	public float getHeight()	{ return height; }
	public float getZ()			{ return z; }
	public float getNX()  		{ return normal_x; }
	public float getNY() 		{ return normal_y; }
	public float getNZ()  		{ return normal_z; }
	public float getMat() 		{ return mat; }
	
	
}
