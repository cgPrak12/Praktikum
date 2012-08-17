package terrain;

import java.io.File;
import java.util.Random;

/**
 * Testklasse fuer BlockUtil
 * 
 */
public class TestCase {

	public static void main(String[] args) {
		
		Random r = new Random();

		for(int g = 0; g < 2; g++) 
		{
			System.out.println("<<<<< Testdurchlauf " + (g + 1) + " >>>>>");
			//BlockUtil.DataInfoExist();
			long t0 = System.currentTimeMillis();
			
			if(BlockUtil.DataInfoExist() != true) 
			{
				System.out.println("DataInfo.dat does not exist");
				for(int t = 0; t < 100; t++)
				{
					Block block1 = new Block(t, t);
					
					for(int i = 0; i < 256; i++)
					{
						for(int j = 0; j < 256; j++) 
						{
							for(int k = 0; k < 5; k++) 
							{
								block1.setInfo(i, j, k, r.nextFloat());
							}
						}
					}			
					File file = BlockUtil.writeBlockData(block1);
					
					Block block2 = BlockUtil.readBlockData(file);
				}
			}
			else
			{
	    		System.out.println("DataInfo.dat does exist");
			}

			long t5 = System.currentTimeMillis();
			System.out.println((t5 - t0) + " ms");
			System.out.println(BlockUtil.readDataInfo() + " blockfiles");
			System.out.println();
		}
		
//		Block block1 = new Block(0, 0);
//				
//		for(int i = 0; i < 256; i++)
//		{
//			for(int j = 0; j < 256; j++) 
//			{
//				for(int k = 0; k < 5; k++) 
//				{
//					block1.setInfo(i, j, k, r.nextFloat() * 100);
//				}
//			}
//		}	
//		
//		long t0 = System.currentTimeMillis();
//		File file = BlockUtil.writeBlockData(block1);
//		long t1 = System.currentTimeMillis();
//		System.out.println((t1 - t0) + " ms");
//		Block block2 = BlockUtil.readBlockData(file);
//		
//		for(int i = 0; i < 256; i++)
//		{
//			for(int j = 0; j < 256; j++) 
//			{
//				for(int k = 0; k < 5; k++) 
//				{
//					//System.out.println(block2.getInfo(i, j, k));
//					
//					if(block1.getInfo(i, j, k) != block2.getInfo(i, j, k))
//					{
//						System.out.println("values not equal");
//					}
//				}
//			}
//		}
		System.out.println("<<< Test beendet >>>");
	}
}
