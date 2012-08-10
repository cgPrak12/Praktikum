package util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class BlockUtil {

	private static final int blockSize = 256; 	/* block length / width */
	private static final int vertexInfos = 5; 	/* vertexInfo quantity */
	
	/**
	 * Liest einen gegebenen Block ein und schreibt diesen in eine blockfile (.bf) Datei
	 * 
	 * @param block		einzulesener Block
	 * @return file		geschriebene .bf Datei	
	 */
	public static File writeBlockData(Block block)
	{				
		File file = new File(block.getID()[0] + "_" + block.getID()[1] + "_.bf");
						
		try( FileOutputStream fos = new FileOutputStream(file);
			 ByteArrayOutputStream bos = new ByteArrayOutputStream(blockSize * blockSize * vertexInfos * 4);
			 DataOutputStream output = new DataOutputStream(new BufferedOutputStream(bos)) )
		{
			if(!file.exists())
			{
				file.createNewFile();
			}
			
			// wenn ein Block sich ausserhalb des Terrains befindet
			if(block.getID()[0] < 0 || block.getID()[1] < 0)
			{
				for(int i = 0; i < blockSize; i++)
				{
					for(int j = 0; j < blockSize; j++)
					{
						for(int k = 0; k < vertexInfos; k++)
						{		
							output.writeFloat(0.0f);
						}		
					}
				}
				fos.write(bos.toByteArray());
				
				return file;
			}
					
			// wenn ein Block sich innerhalb des Terrains befindet
			for(int i = 0; i < blockSize; i++)
			{
				for(int j = 0; j < blockSize; j++)
				{
					for(int k = 0; k < vertexInfos; k++)
					{	
						output.writeFloat(block.getInfo(i, j, k));
					}		
				}
			}
			fos.write(bos.toByteArray());						
			return file;
		}
		catch (IOException e1)
		{
			System.err.println(e1.getClass().getName() + " : " + e1.getMessage());
			return null;
		}
	}
	
	/**
	 * Liest eine blockfile Datei ein und schreibt deren Inhalt in einen neuen Block
	 * 
	 * @param blockdata		einzulesende .bf Datei
	 * @return newblock		aus der Datei erzeugter Block
	 */
	public static Block readBlockData(File blockData)
	{
		byte[] bytes = new byte[blockSize * blockSize * vertexInfos * 4];
		
		try( FileInputStream fis = new FileInputStream(blockData); 
			 ByteArrayInputStream bis = new ByteArrayInputStream(bytes);	
			 DataInputStream input = new DataInputStream(new BufferedInputStream(bis)))
		{				
			String fileName = blockData.getName();
			String[] tmp;
			String delimiter = "_";
			tmp = fileName.split(delimiter);
			int x = new Integer(tmp[0]);
			int z = new Integer(tmp[1]);
			
			Block newblock = new Block(x,z);
				
			for(int i = 0; i < blockSize; i++)
			{
				for(int j = 0; j < blockSize; j++)
				{
					for(int k = 0; k < vertexInfos; k++)
					{		
						newblock.setInfo(i, j, k, input.readFloat());
					//	System.out.println(newblock);
					}		
				}
			}
			fis.read(bytes);
			return newblock;
		}
		catch (IOException e2)
		{
			System.err.println(e2.getClass().getName() + " : " + e2.getMessage());
			return null;
		}				
	}
	
	/**
	 * Liest ein Blockfile aus, dessen ID mit der gegeben uebereinstimmt
	 * 
	 * @param x		X_0_.bf
	 * @param z		0_Z_.bf
	 * @return block
	 */
	public static Block readBlockData(int x, int z)
	{
		return readBlockData(new File(x + "_" + z + "_.bf"));
	}
	
	/**
	 * 
	 * @param cam
	 * @return
	 */
	public static Block getBlock(Camera cam)
	{
		int x = Math.round(cam.getCamPos().x);
		int z = Math.round(cam.getCamPos().z);
		
		return getBlock(x, z);
	}
	
	/**
	 * Greift auf einen Block im Terrain zu
	 * 
	 * @param x		X-pos im Terrain
	 * @param z		Z-pos im Terrain
	 * @return block
	 */
	public static Block getBlock(int x, int z)
	{
		return readBlockData(x / 256, z / 256);
	}
}
