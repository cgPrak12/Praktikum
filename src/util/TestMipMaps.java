package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TestMipMaps
{
	public static void main(String[] argv)
	{	
		Terrain t = new Terrain(512, 512, 1);
		int maxX = t.getXDim();
		int maxZ = t.getZDim();
		
		int[][] myArray = new int[maxX][maxZ];
		
		Camera cam = new Camera();		
		View view = new View(t, cam, 2);
//		for(int size = view.getSize(); size > 0; size--)
//		{
//			System.out.println(size);
//			for(int i = 0; i < maxX; i++)
//			{
//				for(int j = 0; j < maxZ; j++)
//				{
//					float[] help = view.getLevel(size).getAbs(i, j);
//					if(!(help[0] == -6.0f && help[1] == 0.0f && help[2] == 0.0f&& help[3] == 0.0f && help[4] == 0.0f))
//					{
//						myArray[i][j] = size;
//					}
//					if(myArray[i][j] == 0)
//						myArray[i][j] = 0;
//				}
//			}
//		}
		
		for(int i = 0; i < maxX; i++)
		{
			for(int j = 0; j < maxZ; j++)
			{
				myArray[i][j] = 0;
			}
		}
		
		
		for(int size = view.getSize(); size > 0; size--)
		{
			MipMap myMipMap = view.getLevel(size);
			for(int i = 0; i < myMipMap.getXDim(); i++)
			{
				for(int j = 0; j < myMipMap.getZDim(); j++)
				{
					int x = myMipMap.getCenterX();
					int z = myMipMap.getCenterZ();
					int density = myMipMap.getDensity();
					
					int dx = Math.round(Util.scale(i, 0, myMipMap.getXDim() - 1, x - size * density, x + size * density));
					int dz = Math.round(Util.scale(j, 0, myMipMap.getZDim() - 1, z - size * density, z + size * density));
										
					if(dx >= 0 && dz >= 0 && dx < maxX && dz < maxZ)					
						myArray[dx][dz] = size;
					
				}
			}
		}
		
		try
		{
			System.out.println("test");
			FileWriter fstream = new FileWriter("test7.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			
			for(int i = 0; i < maxX; i++)
			{
				for(int j = 0; j < maxZ; j++)
				{
					if(myArray[i][j] == 0)
					{
						out.write("  ");
					}
					else
							out.write(myArray[i][j] + " ");
						
					
				}
				out.write("\r\n");
			}
			
			out.close();			
		}
		
		catch (IOException e)
		{
			System.err.println("Error: " + e.getMessage());
		}
	}
}
			