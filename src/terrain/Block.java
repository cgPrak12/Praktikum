package terrain;

import java.io.Serializable;

/**
 * Block Objekt, das einen Teil eines Terrains darstellt
 * 
 * @author daniel, lukas, mareike
 */

public class Block implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private float[][][] vertexInfo;
	private int posX;
	private int posZ;

	/**
	 * Konstruktor
	 * 
	 * @param posX			x-Position im Block
	 * @param posZ			z-Position im Block
	 * @param size			Block length / width
	 * @param vertexLayout	Anzahl Vertex Informationen
	 */
	public Block(int posX, int posZ, int size, int vertexLayout)
	{
		this.posX = posX;
		this.posZ = posZ;
		
		if(size % 256 == 0)
		{
			vertexInfo = new float[size][size][vertexLayout];
		}
		else
		{
			System.err.println("Error: Terrain Size ist kein vielfaches von 256");
		}
	}
	
	/**
	 * Konstruktor mit Standardwerten (Blockgroesse 512, vertexSize 5)
	 */
	public Block(int posX, int posZ)
	{
		this(posX, posZ, 512, 5);
	}
	
	/**
	 * Getter Vertex Information
	 * 
	 * @param x			x-Position im Block
	 * @param z			z-Position im Block
	 * @param pos		Position im Vertex Layout
	 * @return float	Vertex Information
	 */
	public float getInfo(int x, int z, int pos)
	{
		if(x >= 0 && z >= 0 && pos >= 0 && x < 512 && z < 512 && pos < 5)
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
	 * Setter Vertex Information
	 * 
	 * @param x			x-Position im Block
	 * @param z			z-Position im Block
	 * @param pos		Position im Vertex Layout
	 * @param info		zu setzender Wert
	 */
	public void setInfo(int x, int z, int pos, float info)
	{
		if(x >= 0 && z >= 0 && pos >= 0 && x < 512 && z < 512 && pos < 5)
		{
			vertexInfo[x][z][pos] = info;
		}		
	}
	
	/**
	 * Getter x-Position
	 * 
	 * @return posX
	 */
	public int getX()	
	{	
		return posX;	
	}
	
	/**
	 * Getter z-Position
	 * 
	 * @return posZ
	 */
	public int getZ()	
	{	
		return posZ;	
	}	
	
	/**
	 * Getter x-Position und z-Position
	 * 
	 * @return result	int Array mit posX und posZ
	 */
	public int[] getID()
	{
		int[] result = {posX, posZ};
		return result;
	}	
}
