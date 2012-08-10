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
//			System.out.printf("BLOCK: gives you %f at [%d][%d][%d]\n", vertexInfo[x][z][pos], x, z, pos);
			return vertexInfo[x][z][pos];
		}
		else
		{
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
//				System.out.printf("BLOCK: wrote %f at [%d][%d][%d]\n", info, x, z, pos);
		}
	}
	
	/**
	 * Liefert die HeightMap eines Blocks
	 * @return HeightMap
	 */
	public float[][] getHeightMap()
	{
		int x = vertexInfo.length;
		int z = vertexInfo[0].length;
		float[][] result = new float[x][z];
		for(int i = 0; i < x; i++)
		{
			for(int j = 0; j < z; j++)
			{
				result[i][j] = vertexInfo[i][j][0];
			}
		}
		return result;
	}
	
	public int getX()	{	return posX;	}
	
	public int getZ()	{	return posZ;	}	
	
	public int[] getID()
	{
		int[] result = {posX, posZ};
		return result;
	}	
}
