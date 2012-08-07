package util;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class TestingGrid {

	public static void main(String[] argv)
    {
		int dimX,dimZ;
		dimX=dimZ= 512;
		int size = 4;
		Camera cam = new Camera();
		
		Terrain myAS  = new Terrain(0.5f,dimX,dimZ,2,1,null);
		
		float[][][][] testing = GridFactory.minGrid(myAS.getTerra(), cam, size);
		
		try
	    {
	            FileWriter fstream = new FileWriter("testingGrid.txt");
	            BufferedWriter out = new BufferedWriter(fstream);
	            
	            int[][] tmp = new int[dimX][dimZ];
	            
	            for(int x = 0; x < dimX; x++)
                {
	            	 for(int z = 0; z < dimZ; z++)
                     {
	            		 tmp[x][z] = 0;
                     }
                }
	            
	            for(int block = 1; block < testing.length; block++)
                {
	            	for(int x = 0; x < testing[0].length; x++)
	                {
		            	 for(int z = 0; z < testing[0][0].length; z++)
	                     {
		            		 int tx = (int)testing[block-1][x][z][0];
		            		 int tz = (int)testing[block-1][x][z][2];
//		            		 System.out.println("x: "+tx+" z: "+tz);
		            		 if( tx>0 || tz>0 || tx<=dimX || tz<=dimZ){
		            			 tmp[tx][tz] = block;
//		            			 System.out.println(tmp[tx][tz]);
		            		 }
	                     }
	                }
                }
	            
	            for(int i = 0; i < dimX; i++)
                {
                        for(int j = 0; j < dimZ; j++)
                        {
                                if(tmp[i][j] == 0)
                                {
                                        out.write("  ");
                                }
                                else{
                                    out.write(tmp[i][j] + " ");
                                }   
                                
                        }
                        out.write("\r\n");
                }
	            
	    }
		catch(Exception e)
		{
			System.err.println("Error: " + e.getMessage());
	    }
    }
}
