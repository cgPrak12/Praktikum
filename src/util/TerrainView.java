package util;

import java.io.File;

public class TerrainView {

	private Block[][] myBl;
	private Camera cam;
	private int[] middle;
	
	/**
	 * constructor
	 * @param c Camera
	 */
	public TerrainView(Camera c)
	{		
		myBl = new Block[9][9];
		cam =  c;
		init();
	}
	
	/**
	 * set the blocks 
	 */
	private void init()
	{
		myBl[4][4] = BlockUtil.getBlock(cam);
		middle = myBl[4][4].getID();
		
		int idI = middle[0]-1;
		int idJ = middle[1]-1;
		
		// hier fehlt noch die Fehlerbehandlung am Rand
		// dummy-Block wenn man am/über dem Rand ist 
		for(int i=0; i<9; i++)
		{
			for(int j=0; j<9; j++)
			{
				myBl[i][j] = BlockUtil.readBlockData(new File((idI+i) + "_" + (idJ+j) + "_.bf"));
			}
		}
	}

	/**
	 * update the whole blocks
	 */
	public void updateTerrainView()
	{		
		int diffX = (BlockUtil.getBlock(cam)).getID()[0] - middle[0];
		int diffY = (BlockUtil.getBlock(cam)).getID()[1] - middle[1];
		
		if(Math.abs(diffX) > 2 || Math.abs(diffX) > 2)
		{
			init();
		}
		else
		{
			for(int i=0; i<9; i++)
			{
				for(int j=0; j<9; j++)
				{

					if( i+diffX<0 || i+diffX>2 || j+diffY<0 || j+diffY>2)
					{
						File file = new File((myBl[i][j].getID()[0] + diffX) 
								     + "_" + (myBl[i][j].getID()[0] + diffX) + "_.bf");
						myBl[i][j] = BlockUtil.readBlockData(file);
					}
					else
					{
						myBl[i][j] = myBl[i+diffX][j+diffY];
					}
				}
			}
		}
	}
	
	/**
	 * methode for getting the latest blocks as an float[][][] with sizes [2304][2304][5]
	 * @return float[][][]
	 */
	public float[][][] getArray(){
		
		float[][][] area = new float[9*256][9*256][5];
		
		for(int x=0; x<area.length; x++){
			for(int z=0; z<area.length; z++){
				
				int bx = (int)x/256;
				int bz = (int)z/256;
				
				for(int dat=0; dat < 5; dat++){
					area[x][z][dat]=myBl[bx][bz].getInfo(x%256, z%256, dat);
				}
			}
		}
		
		return area;
	}
	
	/**
	 * methode gives the camPosX as related to the float[][][]
	 * @return int 
	 */
	public int arrayCamPosX(){
		
		return (int)((cam.getCamPos().x%256)+256);
	}
	
	/**
	 * methode gives the camPosZ as related to the float[][][]
	 * @return
	 */
	public int arrayCamPosZ(){
		
		return (int)((cam.getCamPos().z%256)+256);
	}

	public Block[][] getBlocks() {

		return myBl;
	}
	

}
