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
		for(int i = 0; i < maxX; i++)
		{
			for(int j = 0; j < maxZ; j++)
			{
				myArray[i][j] = 0;
			}
		}
		
		Camera cam = new Camera();		
		View view = new View(t, cam, 10);
		
		for(int i = 1; i <= view.getSize(); i++)
		{
			MipMap myMipMap = view.getLevel(i);
			for(int x = 0; x < myMipMap.getXDim(); x++)
			{
				for(int z = 0; z < myMipMap.getZDim(); z++)
				{
					float[] help = myMipMap.get(x, z);
//					System.out.println(help[0] + " " + help[2]);
					myArray[(int)help[0]][(int)help[2]] = i;					
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
			