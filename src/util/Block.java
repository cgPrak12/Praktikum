package util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;

import com.sun.xml.internal.messaging.saaj.util.*;

public class Block implements Serializable {
	
	/**
	 * 
	 */
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
	
	public int[] getID()
	{
		int[] result = {posX, posZ};
		return result;
	}
	
	public float getInfo(int x, int z, int pos)
	{
		if(x >= 0 && z >= 0 && pos >= 0 && x < 256 && z < 256 && pos < 5)
		{
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
		}
		
//		if(x == 255 && z == 255)
//		{
//			String filename = "block" + posX + "_" + posZ + ".bf";
//			this.save(filename);
//		}
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
	
	/**
	 * Schreibe Vertex-Informationen aus dem Array in eine Datei
	 * @param filename angegebene Datei
	 * @return true bei Erfolg, false sonst
	 */
	public boolean save(String filename)
	{
		boolean result = false;
		int dimX = vertexInfo.length;
		int dimY = vertexInfo[0].length;
		int dimZ = vertexInfo[0][0].length;
		try(FileOutputStream fos = new FileOutputStream(filename);
			ByteOutputStream bos = new ByteOutputStream(dimX * dimY * dimZ); 	
			DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(bos)))
		{
			for(int i = 0; i < vertexInfo.length; i++)
			{
				for(int j = 0; j < vertexInfo[0].length; j++)
				{
					for(int k = 0; k < vertexInfo[0][0].length; k++)
					{
						dos.writeFloat(vertexInfo[i][j][k]);
					}
				}
			}
			fos.write(bos.getBytes());
			result = true;
		}
		catch (Exception e)
		{
			System.err.println("ERROR: " + e.getMessage());
		}
		return result;
	}

	
	/**
	 * Lade Vertex-Informationen aus einer Datei ins Array
	 * @param filename angegebene Datei
	 * @return true bei Erfolg, false sonst
	 */
	public boolean load(String filename)
	{
		boolean result = false;
		try(FileInputStream fis = new FileInputStream(filename); 	
			DataInputStream dis = new DataInputStream(new BufferedInputStream(fis)))
		{			
			for(int i = 0; i < vertexInfo.length; i++)
			{
				for(int j = 0; j < vertexInfo[0].length; j++)
				{
					for(int k = 0; k < vertexInfo[0][0].length; k++)
					{
						vertexInfo[i][j][k] = dis.readFloat();
					}
				}
			}
			result = true;
		}
		catch (Exception e)
		{
			System.err.println("ERROR: " + e.getMessage());
		}
		return result;
	}

	public int getPosX() {
		return getID()[0];
	}
	
	public int getPosZ() {
		return getID()[1];
	}
	
	

}
