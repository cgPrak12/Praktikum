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
				}
			}	
		}
		
		System.out.print("writing ... ");
		File file = BlockUtil.writeBlockData(TestBlock1);
		System.out.println("done");
		System.out.print("reading ... ");
		Block TestBlock2 = BlockUtil.readBlockData(file);
		System.out.println("done");
			
		for(int i = 0; i < 256; i++)
		{
			for(int j = 0; j < 256; j++)
			{
				for(int k = 0; k < 5; k++)
				{
					if(TestBlock1.getInfo(i, j, k) == TestBlock2.getInfo(i, j, k))
					{	
						//System.out.println("[" + i + "]" + "[" + j + "]" + "[" + k + "] , " + TestBlock1.getInfo(i, j, k) + " == " + TestBlock2.getInfo(i, j, k) + " , TRUE");
					}
					else
					{
						System.out.println("[" + i + "]" + "[" + j + "]" + "[" + k + "] , " + TestBlock1.getInfo(i, j, k) + " != " + TestBlock2.getInfo(i, j, k) + " , FALSE");
					}
				}
			}
		}
		System.out.println();
	}

}
