package util;

import java.io.*;
import java.nio.FloatBuffer;

public class Test {
	
	public static void main(String[] argv)
	{
		int maxX = 128;
		int maxZ = 256;
		
		ArrayStruc myAS = new ArrayStruc(maxX, maxZ);
		
		for(int i = 0; i < maxX; i++)
		{
			for(int j = 0; j < maxZ; j++)
			{
				VertexInfo vi = new VertexInfo((float)i, 1.0f, (float)j, 0.0f, 0.0f, 0.0f, 0.0f);
				myAS.setInfo(i, j, vi);
			}
		}
		
		FloatBuffer myFB = Grid.minimizeGrid(myAS, new Camera());
		myFB.position(0);
		
		try
		{
			FileWriter fstream = new FileWriter("test.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			
			int[][] temp = new int[maxX][maxZ];
			for(int i = 0; i < maxX; i++)
			{
				for(int j = 0; j < maxZ; j++)
				{
					temp[i][j] = 0;
				}
			}
			
			for(int pos = 0; (pos * 7) < myFB.limit(); pos++)
			{
				temp[(int)myFB.get(7 * pos)][(int)myFB.get(7 * pos + 2)] = 1;
			}
						
			for(int i = 0; i < maxX; i++)
			{
				for(int j = 0; j < maxZ; j++)
				{
					if(temp[i][j] == 0)
					{
						out.write(" ");
					}
					else
						out.write("+");
				}
				out.write("\r\n");
			}
			
			out.close();			
		}
		catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
		}
		
		
	}

}
