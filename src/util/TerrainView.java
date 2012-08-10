package util;

import java.io.File;

/**
 * this class take care of the blocks which are in use
 * @author group data
 *
 */
public class TerrainView {

	// private values
	private Block[][] myBl;
	private Camera cam;
	private int[] middle;
	private Block dummy;
	
	/**
	 * constructor
	 * @param c Camera
	 */
	public TerrainView(Camera c)
	{		
		myBl = new Block[9][9];
		cam =  c;
		
		// dummy block erstellen
		dummy = BlockUtil.readBlockData(BlockUtil.writeBlockData(new Block(-1,-1)));
		
		init();
	}
	
	/**
	 * set the blocks 
	 */
	private void init()
	{

		myBl[4][4] = BlockUtil.getBlock(cam);
		middle = myBl[4][4].getID();
		
	
		int idI = middle[0];
		int idJ = middle[1];
	
		for(int i=0; i<9; i++)
		{
			for(int j=0; j<9; j++)
			{
				if(i!=4 && j!=4)
				{
					if(idI+i-4>0 && idJ+j-4>0 && idI+i-4<(Terrain.getSize()/256) && idJ+j-4<(Terrain.getSize()/256))
					{
						myBl[i][j] = BlockUtil.readBlockData(new File((idI+i-4)+"_"+(idJ+j-4)+"_.bf"));
					}
					else
					{
						myBl[i][j] = dummy;
					}
				}
			}
		}
	}

	
	/**
	 * update the whole blocks
	 */
	public void updateTerrainView()
	{	
		// hier muss der erste Block mit Fehlerbehandlung gesetzt werden, falls Camera au�erhalb
		// einschr�nkung der Camera oder spezielle Fehlerbehandlung hier
		int diffX = (BlockUtil.getBlock(cam)).getID()[0] - middle[0];
		int diffY = (BlockUtil.getBlock(cam)).getID()[1] - middle[1];
		myBl[4][4] =  BlockUtil.getBlock(cam);

		for(int i=0; i<9; i++)
		{
			for(int j=0; j<9; j++)
			{
				if(i!=4 && j!=4)
				{
					if( i+diffX<0 || i+diffX>2 || j+diffY<0 || j+diffY>2)
					{
						
						if(!((middle[0]-4+i)<0 || (middle[1]-4+j)<0 || (middle[0]-4+i)>(Terrain.getSize()/256)
								|| (middle[1]-4+j)>(Terrain.getSize()/256)))
						{	
							String file = (myBl[i][j].getID()[0] + diffX) 
					         + "_" + (myBl[i][j].getID()[1] + diffY) + "_.bf";
							myBl[i][j] = BlockUtil.readBlockData(new File(file));
						}
						else
						{
							myBl[i][j] = dummy;
						}
					}
					else
					{
						myBl[i][j] = myBl[i+diffX][j+diffY];
					}
				}
			}
		}
	}
	
	public float[][] getHeightMap(){
		
		float[][] heightMap = new float[9*256][9*256];
		
		for(int x=0; x<heightMap.length; x++){
			for(int z=0; z<heightMap.length; z++){
				
				int bx = (int)x/256;
				int bz = (int)z/256;
				
				heightMap[x][z]= myBl[bx][bz].getInfo(x%256, z%256, 0);
			}
		}
		
		return heightMap;
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
}