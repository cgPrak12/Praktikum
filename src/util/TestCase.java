package util;

import java.io.File;
import java.util.Random;

public class TestCase {

	public static void main(String[] args) {
		
		Random r = new Random();
		
		Block TestBlock1 = new Block(1, 2);
		
		for(int i = 0; i < 256; i++)
		{
			for(int j = 0; j < 256; j++)
			{
				for(int k = 0; k < 5; k++)
				{
					TestBlock1.setInfo(i, j, k, r.nextFloat()*100);
					System.out.println(TestBlock1.getInfo(i, j, k));
				}
			}	
		}
		System.out.println();
		
//		File file = BlockUtil.writeBlockData(TestBlock1);
//		Block TestBlock2 = BlockUtil.readBlockData(file);
//			
//		for(int i = 0; i < 256; i++)
//		{
//			for(int j = 0; j < 256; j++)
//			{
//				for(int k = 0; k < 5; k++)
//				{
//					if(TestBlock1.getInfo(i, j, k) == TestBlock2.getInfo(i, j, k))
//					{	
//						System.out.println("[" + i + "]" + "[" + j + "]" + "[" + k + "]"+" TRUE");
//						//System.out.println(TestBlock1.getInfo(i, j, k) + " - " + TestBlock2.getInfo(i, j, k));
//					}
//					else
//					{
//						System.out.println("[" + i + "]" + "[" + j + "]" + "[" + k + "]"+" FALSE");
//						//System.out.println(TestBlock1.getInfo(i, j, k) + " - " + TestBlock2.getInfo(i, j, k));
//						break;
//					}
//				}
//			}
//		}
//		System.out.println();
	}

}
