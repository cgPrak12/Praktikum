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
 * Methoden zur I/O Verwaltung von Block Objekten.
 * Zudem wird hier InfoData.dat erzeugt, die die Size des zuletzt erzeugten Terrains speichert.
 * Durch diese Datei laesst sich unoetiges neuerzeugen von bereits auf die Festplatte geschriebene 
 * Terrains vermeiden.
 * 
 * HINWEIS: Bei FileNotFoundExceptions sollte zuerst die Existenz des Ordner src/Data ueberprueft 
 * 			werden, da dieser bei Nichtexistenz nicht von java erzeugt wird.
 * 
 * @author daniel, lukas, mareike
 */

public class BlockUtil {

	private static final int blockSize = 512; 	/* block length / width */
	private static final int blockHeight = 5; 	/* vertexlayout length */
				
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
		File blockFile = new File(filePath);
	
		/* java I/O stream chaining ftw */
		try(DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(blockFile))))
		{
			if(!blockFile.exists())
			{
				blockFile.createNewFile();
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
			return blockFile;
		}
		catch (IOException e)
		{
			System.err.println(e.getClass().getName() + " : " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Liest eine blockfile Datei ein und schreibt deren Inhalt in ein neues Block Objekt
	 * 
	 * @param blockdata		von Festplatte einzulesende .bf Datei
	 * @return newblock		aus der Datei ausgelesener Block
	 */
	public static Block readBlockData(File blockFile)
	{
		/* java I/O stream chaining ftw */
		try(DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(blockFile))))
		{
			/* create new block object from given blockfile name */
			String fileName = blockFile.getName();
			String[] tmp;
			String delimiter = "_";
			tmp = fileName.split(delimiter);
			int x = new Integer(tmp[0]);
			int z = new Integer(tmp[1]);
			
			Block newBlock = new Block(x,z);
			
			/* fill block object with floats */
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
	 * @param cam		gegebene Kamera
	 * @return block	erhaltener Block
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
		return readBlockData(x / 512, z / 512);
	}
	
	/**
	 * Prueft, ob DataInfo.dat existiert und liefert dementsprechend true oder false
	 * 
	 * @return boolean
	 */
	public static boolean DataInfoExist()
	{
		try
		{
			if(new File("DataInfo.dat").exists())
			{
				return true;
			}
		}
		catch (Exception e)
		{
			return false;
		}
		return false;
	}
	
	/**
	 * Schreibt die Terrain Groesse in DataInfo.dat
	 * 
	 * @return
	 */
	public static File writeDataInfo(int size)
	{			
		File DataInfo = new File("DataInfo.dat");
		
		/* java I/O stream chaining ftw */
		try(DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(DataInfo))))
		{			
			if(!DataInfo.exists())
			{
				DataInfo.createNewFile();
			}				
			output.writeInt(size);
			
			return DataInfo;		
		}
		catch (IOException e)
		{
			System.err.println(e.getClass().getName() + " : " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Liest die Terrain Groesse aus der InfoData.dat
	 * 
	 * @return size		Terrain Groesse
	 */
	public static int readDataInfo()
	{
		/* java I/O stream chaining ftw */
		try(DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(new File("DataInfo.dat")))))
		{
			int size = input.readInt();
			return size;
		}
		catch (IOException e)
		{
			System.err.println(e.getClass().getName() + " : " + e.getMessage());
			return 0;
		}	
	}	
}
