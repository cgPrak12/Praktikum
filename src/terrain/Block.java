package terrain;

import java.io.Serializable;

public class Block implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private float[][][] vertexInfo;
	private int posX;
	private int posZ;
	
	/**
	 * Ctor mit Standardwerten (Blockgroesse 256, vertexSize 5)
	 */
	public Block(int posX, int posZ)
	{
		this.posX = posX;
		this.posZ = posZ;
		vertexInfo = new float[256][256][5];		
	}
	
	public float getInfo(int x, int z, int pos)
	{
		if(x >= 0 && z >= 0 && pos >= 0 && x < 256 && z < 256 && pos < 5)
		{
			return vertexInfo[x][z][pos];
		}
		else
		{
			System.out.println("return ist 0");
			return 0.0f;
		}			
	}
	
	/**
	 * 
	 * @param x
	 * @param z
	 * @param info
	 */
	public void setInfo(int x, int z, int pos, float info)
	{
	
		if(x >= 0 && z >= 0 && pos >= 0 && x < 256 && z < 256 && pos < 5)
		{
				vertexInfo[x][z][pos] = info;
		}		
	}
	
	/**
	 * Getter X
	 * @return posX
	 */
	public int getX()	
	{	
		return posX;	
	}
	
	/**
	 * Getter Z
	 * @return posZ
	 */
	public int getZ()	
	{	
		return posZ;	
	}	
	
	/**
	 * Getter Z
	 * @return result	int Array mit posX und posZ
	 */
	public int[] getID()
	{
		int[] result = {posX, posZ};
		return result;
	}	
}
