package util;

import java.io.*;
import java.util.Date;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

public class BlockParser {

	/**
	 * Liest einen gegebenen Block ein und schreibt diesen in eine blockfile (.bf) Datei
	 * 
	 * @param map	Terrain
	 * @return file	geschriebene Datei	
	 */
	public static File writeBlockData(Block block)
	{
		long time = new Date().getTime();
				
		File file = new File(block.getPosX() + "_" + block.getPosZ()+ "_.bf");
						
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
				
				System.out.println("Schreibdauer: " + (new Date().getTime() - time) + " Millisekunden");
				
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
			
			System.out.println("Schreibdauer: " + (new Date().getTime() - time) + " Millisekunden");
			
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
	 * @param data	einzulesenede Datei
	 * @param x		float.length
	 * @param z		float[0].length
	 * @return map	float[][][]
	 */
	public static Block readBlockData(File blockData)
	{
		long time = new Date().getTime();
		
		try( FileInputStream fis = new FileInputStream(blockData); 
			 DataInputStream input = new DataInputStream(new BufferedInputStream(fis)))
		{		
			// posX und posZ mittels string slitting aus dem filename lesen
			String fileName = blockData.getName();
			String[] tmp;
			String delimiter = "_";
			tmp = fileName.split(delimiter);
			int x = new Integer(tmp[0]);
			int z = new Integer(tmp[1]);
			
			// block erzeugen und mit floats füllen
			Block b = new Block(x,z);
				
				for(int i = 0; i < 256; i++)
				{
					for(int j = 0; j < 256; j++)
					{
						for(int k = 0; k < 5; k++)
						{		
							b.setInfo(i, j, k, input.readFloat());
						}		
					}
				}
			
			System.out.println("Lesedauer: " + (new Date().getTime() - time) + " Millisekunden");
			
			return b;
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
	
	// Test main method
	public static void main(String[] args) {
		
        int maxX, maxZ;
        maxX = maxZ = 256;
        
        Block b = new Block(1,2);
        
        System.out.println("block size: Array[" + maxX + "][" + maxZ + "]" + "[" + 5 + "]");
        System.out.println("vertices: " + (maxX * maxZ));
        System.out.println("floats: " + (maxX * maxZ * 5));
        System.out.println("bytes: " + (maxX * maxZ * 5) * 4);
        System.out.println();
        
        File data = writeBlockData(b);
//        Block block = readBlockData(data);
        data.delete();
	}
}
