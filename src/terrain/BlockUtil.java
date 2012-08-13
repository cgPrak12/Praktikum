package terrain;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import util.Camera;

/**
 * Methoden um Bloecke auf die Festplatte zu schreiben oder von ihr zu lesen
 * 
 * @author daniel, lukas, mareike
 */

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
			 DataOutputStream output = new DataOutputStream(new BufferedOutputStream(fos)) )
		{
			if(!file.exists())
			{
				file.createNewFile();
			}
							
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
		
		try( FileInputStream fis = new FileInputStream(blockData); 			 	
			 DataInputStream input = new DataInputStream(new BufferedInputStream(fis)) )
		{			
			
			String fileName = blockData.getName();
			String[] tmp;
			String delimiter = "_";
			tmp = fileName.split(delimiter);
			int x = new Integer(tmp[0]);
			int z = new Integer(tmp[1]);
			
			Block newBlock = new Block(x,z);
			
			for(int i = 0; i < blockSize; i++)
			{
				for(int j = 0; j < blockSize; j++)
				{
					for(int k = 0; k < vertexInfos; k++)
					{		
						newBlock.setInfo(i, j, k, input.readFloat());
					}		
				}
			}		
			return newBlock;
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
	 * Greift auf den Block zu, in der sich die Kamera aktuell befindet
	 * 
	 * @param cam		Kamera
	 * @return block
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
