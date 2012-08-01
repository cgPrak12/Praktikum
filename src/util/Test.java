package util;

import java.io.*;
import java.nio.FloatBuffer;

public class Test {
	
	public static void main(String[] argv)
	{
		int maxX, maxZ;
		maxX = maxZ = 512;
		int size = 10;
		
		Map myAS = new Map(maxX, maxZ);
		
		for(int i = 0; i < maxX; i++)
		{
			for(int j = 0; j < maxZ; j++)
			{
				VertexInfo vi = new VertexInfo((float)i, 1.0f, (float)j, 0.0f, 0.0f, 0.0f, 0.0f);
				myAS.setInfo(i, j, vi);
			}
		}
		
		FloatBuffer[] myFBArray = GridFactory.minimizeGrid(myAS, new Camera(), size, 5);
		
		try
		{
			FileWriter fstream = new FileWriter("test6.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			
			int[][] temp = new int[maxX][maxZ];
			for(int i = 0; i < maxX; i++)
			{
				for(int j = 0; j < maxZ; j++)
				{
					temp[i][j] = 0;
				}
			}
			
			for(int s = 0; s < size; s++)
			{
				for(int pos = 0; (pos * 7) < myFBArray[s].limit(); pos++)
				{
					temp[(int)myFBArray[s].get(7 * pos)][(int)myFBArray[s].get(7 * pos + 2)] = s+1;
				}
			}
			
			for(int i = 0; i < maxX; i++)
			{
				for(int j = 0; j < maxZ; j++)
				{
					if(temp[i][j] == 0)
					{
						out.write("  ");
					}
					else
							out.write(temp[i][j] + " ");
						
					
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
