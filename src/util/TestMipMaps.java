package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TestMipMaps
{
	public static void main(String[] argv)
	{	
		Terrain t = new Terrain(2048, 2048, 1);
		int maxX = t.getXDim();
		int maxZ = t.getZDim();
		
		int[][] myArray = new int[maxX][maxZ];
		
		Camera cam = new Camera();		
		View view = new View(t, cam);
		for(int size = view.getSize(); size > 0; size--)
		{
			System.out.println(size);
			for(int i = 0; i < maxX; i++)
			{
				for(int j = 0; j < maxZ; j++)
				{
					float[] help = view.getLevel(size).getAbs(i, j);
					if(!(help[0] == -6.0f && help[1] == 0.0f && help[2] == 0.0f&& help[3] == 0.0f && help[4] == 0.0f))
					{
						myArray[i][j] = size;
					}
					if(myArray[i][j] == 0)
						myArray[i][j] = 0;
				}
			}
		}
		
		try
		{
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
			