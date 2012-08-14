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
	private static final int blockHeight = 5; 	/* vertexlayout length */
	private static int blockCount = 0;			/* Anzahl geschriebener Block Objekte */
	private static File DataInfo = null;		/* Datei die blockCount enthaelt */
				
	/**
	 * Liest einen gegebenen Block ein und schreibt diesen in eine blockfile (.bf) Datei
	 * 
	 * @param block		einzulesener Block
	 * @return file		auf Festplatte geschriebene .bf Datei	
	 */
	public static File writeBlockData(Block block)
	{	
		/* construct file path */
		String filePath = "." + File.separator + "Data" + File.separator + block.getX() + "_" + block.getZ() + "_.bf"; 
		File file = new File(filePath);
	
		try(DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))))
		{
			if(!file.exists())
			{
				file.createNewFile();
			}	
			
			/* write floats in outputstream */
			for(int i = 0; i < blockSize; i++)
			{
				for(int j = 0; j < blockSize; j++)
				{
					for(int k = 0; k < blockHeight; k++)
					{							
						output.writeFloat(block.getInfo(i, j, k));
					}		
				}
			}		
			blockCount++;
			return file;
		}
		catch (IOException e)
		{
			System.err.println(e.getClass().getName() + " : " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Liest eine blockfile Datei ein und schreibt deren Inhalt in einen neuen Block
	 * 
	 * @param blockdata		von Festplatte einzulesende .bf Datei
	 * @return newblock		aus der Datei ausgelesener Block
	 */
	public static Block readBlockData(File blockData)
	{
		try(DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(blockData))))
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
					for(int k = 0; k < blockHeight; k++)
					{		
						newBlock.setInfo(i, j, k, input.readFloat());
					}		
				}
			}	
			return newBlock;
		}
		catch (IOException e)
		{
			System.err.println(e.getClass().getName() + " : " + e.getMessage());
			return null;
		}				
	}
	
	/**
	 * Liest ein Blockfile ( x_z_.bf ) aus, dessen ID mit der gegeben uebereinstimmt
	 * 
	 * @param x		X_0_.bf
	 * @param z		0_Z_.bf
	 * @return block
	 */
	public static Block readBlockData(int x, int z)
	{
		return readBlockData(new File("." + File.separator + "Data" + File.separator + x + "_" + z + "_.bf"));
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
	 * @param x		x-Position im Terrain
	 * @param z		z-Position im Terrain
	 * @return block
	 */
	public static Block getBlock(int x, int z)
	{
		return readBlockData(x / 256, z / 256);
	}
	
	/**
	 * Prueft, ob DataInfo.dat existiert und liefert dementsprechend true oder false
	 * 
	 * @return boolean
	 */
	public static boolean DataInfoExist()
	{
		DataInfo = new File("DataInfo.dat");
		try
		{
			if(DataInfo.exists())
			{
				System.out.println("DataInfo.dat does exist");
				return true;
			}
		}
		catch (Exception e)
		{
			System.out.println("DataInfo.dat does not exist");
			return false;
		}
		return false;
	}
	
	/**
	 * Schreibt die Anzahl an blockfiles aus dem Ordner Data in DataInfo.dat
	 * 
	 * @return
	 */
	public static File writeDataInfo()
	{	
		DataInfo = new File("DataInfo.dat");
		
		try(DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(DataInfo))))
		{			
			if(!DataInfo.exists())
			{
				DataInfo.createNewFile();
			}	
			
			output.writeInt(blockCount);
			
			return DataInfo;		
		}
		catch (IOException e)
		{
			System.err.println(e.getClass().getName() + " : " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Liest die Anzahl an blockfiles aus
	 * @return count	Anzahl blockfiles
	 */
	public static int readDataInfo()
	{
		try(DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(DataInfo))))
		{
			int count = input.readInt();
			return count;
		}
		catch (IOException e)
		{
			System.err.println(e.getClass().getName() + " : " + e.getMessage());
			return 0;
		}	
	}
	
}
