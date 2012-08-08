package util;

//import java.util.Date;
//import java.util.Random;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

public class BlockUtil {

	/**
	 * Liest einen gegebenen Block ein und schreibt diesen in eine blockfile (.bf) Datei
	 * 
	 * @param block		einzulesener Block
	 * @return file		geschriebene .bf Datei	
	 */
	public static File writeBlockData(Block block)
	{
//		long time = new Date().getTime();
				
		File file = new File(block.getID()[0] + "_" + block.getID()[1] + "_.bf");
						
		try( FileOutputStream fos = new FileOutputStream(file);
			 ByteOutputStream dos = new ByteOutputStream(256 * 256 * 5); 
			 DataOutputStream output = new DataOutputStream(new BufferedOutputStream(dos)) )
		{
			if(!file.exists())
			{
				file.createNewFile();
			}
			
			if(block.getID()[0] < 0 || block.getID()[1] < 0)
			{
				for(int i = 0; i < 256; i++)
				{
					for(int j = 0; j < 256; j++)
					{
						for(int k = 0; k < 5; k++)
						{		
							output.writeFloat(0.0f);
						}		
					}
				}
				
//				System.out.println("Schreibdauer: " + (new Date().getTime() - time) + " Millisekunden");
				
				return file;
			}
											
			for(int i = 0; i < 256; i++)
			{
				for(int j = 0; j < 256; j++)
				{
					for(int k = 0; k < 5; k++)
					{		
						output.writeFloat(block.getInfo(i, j, k));
					}		
				}
			}
					
			fos.write(dos.getBytes());
			
//			System.out.println("Schreibdauer: " + (new Date().getTime() - time) + " Millisekunden");
			
			return file;
		}
		catch (FileNotFoundException e1)
		{
			System.err.println("FileNotFoundException : " + e1.getMessage());
			return null;
		}
		catch (IOException e2)
		{
			System.err.println("IOException : " + e2.getMessage());
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
//		long time = new Date().getTime();
		
		try( FileInputStream fis = new FileInputStream(blockData); 
			 DataInputStream input = new DataInputStream(new BufferedInputStream(fis)))
		{		
			// posX und posZ mittels string splitting aus dem filename lesen
			String fileName = blockData.getName();
			String[] tmp;
			String delimiter = "_";
			tmp = fileName.split(delimiter);
			int x = new Integer(tmp[0]);
			int z = new Integer(tmp[1]);
			
			// neuen Block erzeugen und mit eingelesenen floats fuellen
			Block newblock = new Block(x,z);
				
				for(int i = 0; i < 256; i++)
				{
					for(int j = 0; j < 256; j++)
					{
						for(int k = 0; k < 5; k++)
						{		
							newblock.setInfo(i, j, k, input.readFloat());
						}		
					}
				}
//			System.out.println("Lesedauer: " + (new Date().getTime() - time) + " Millisekunden");
			
			return newblock;
		}
		catch (FileNotFoundException e3)
		{
			System.err.println("FileNotFoundException : " + e3.getMessage());
			return null;
		}
		catch (IOException e4)
		{
			System.err.println("IOException : " + e4.getMessage());
			return null;
		}				
	}
	
	public static Block readBlockData(int x, int z)
	{
		return readBlockData(new File(x + "_" + z + "_.bf"));
	}
	
	
	public static Block getBlock(Camera cam)
	{
		int x = Math.round(cam.getCamPos().x);
		int z = Math.round(cam.getCamPos().z);
		
		return BlockUtil.readBlockData(x / 256, z / 256);
	}
}
