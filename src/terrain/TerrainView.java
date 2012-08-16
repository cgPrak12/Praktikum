package terrain;

import util.Camera;

/** this class takes care of the blocks which are in use
 * @author group data */
public class TerrainView
{

	// private values
	private static Block[][] myBl;
	private static Camera cam;
	private static int[] middle;
	private static Block dummy;
	private static Terrain terra;
	private static boolean initialised;

	/** constructor
	 * @param c Camera */

	static
	{
		myBl = new Block[9][9];
		dummy = BlockUtil.readBlockData(BlockUtil.writeBlockData(new Block(-1, -1)));
		initialised = false;
	}

	/** set the blocks */
	public static void init(Terrain terra, Camera cam)
	{
		TerrainView.terra = terra;
		TerrainView.cam = cam;
		initialised = true;
		int dim = terra.getSize() / 256;
		
		// setzen der ID des mittleren Blockes mit Fehlerbehandlung, 
		// falls Kamera ausserhalb des Terrains ist
		// in x-Richtung
		if((int)cam.getCamPos().x < 0){
			middle[0] = (int)cam.getCamPos().x / 256 -1;
		}
		else{
			middle[0] = (int)cam.getCamPos().x / 256;
		}
		// in z-Richtung
		if((int)cam.getCamPos().z < 0){
			middle[1] = (int)cam.getCamPos().z / 256 -1;
		}
		else{
			middle[1] = (int)cam.getCamPos().z / 256;
		}
		
		
		for (int i = 0; i < 9; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				if (       middle[0] + i - 4 >= 0  && middle[1] + j - 4 >= 0 
						&& middle[0] + i - 4 < dim && middle[1] + j - 4 < dim)
				{
					myBl[i][j] = terra.getBlock(middle[0] + i - 4,middle[1] + j - 4);
					if(myBl[i][j] == null){
						System.err.println("Hier existiert kein Block");
						myBl[i][j] = dummy;
					}
				}
				else
				{
					myBl[i][j] = dummy;
				}
			}
		}
	}

	/** update the whole blocks */
	public static void updateTerrainView()
	{

		if (!initialised)
			throw new IllegalStateException("Klasse wurde nicht initialisiert!");
		
		int diffX,diffY;
		
		// setzen der ID des mittleren Blockes mit Fehlerbehandlung, 
		// falls Kamera ausserhalb des Terrains ist
		// in x-Richtung
		if((int)cam.getCamPos().x < 0){
			diffX = (int)cam.getCamPos().x / 256 -1- middle[0];
		}
		else{
			diffX = (int)cam.getCamPos().x / 256- middle[0];
		}
		// in z-Richtung
		if((int)cam.getCamPos().z < 0){
			diffY = (int)cam.getCamPos().z / 256 -1- middle[1];
		}
		else{
			diffY = (int)cam.getCamPos().z / 256- middle[1];
		}
		
		int dim = terra.getSize() / 256;

		if (!(diffX == 0 && diffY == 0))
		{
			middle[0] += diffX;
			middle[1] += diffY;

			for (int i = 0; i < 9; i++)
			{
				for (int j = 0; j < 9; j++)
				{
					if (i + diffX < 0 || i + diffX > 8 || j + diffY < 0 || j + diffY > 8)
					{

						if (   middle[0] - 4 + i >= 0 
							&& middle[1] - 4 + j >= 0
							&& middle[0] - 4 + i < dim
							&& middle[1] - 4 + j < dim)
						{
							System.out.println("Neuer Block wird gelesen");
							myBl[i][j] = terra.getBlock(myBl[i][j].getID()[0] + diffX, myBl[i][j].getID()[1] + diffY);
							if(myBl[i][j] == null){
								System.err.println("Hier existiert kein Block");
								myBl[i][j] = dummy;
							}
						}
						else
						{
							System.out.println("Neuer dummy-Block wird gelesen");
							myBl[i][j] = dummy;
						}
					} else
					{
						System.out.println("Block wird umgesetzt");
						myBl[i][j] = myBl[i + diffX][j + diffY];
					}
				}
			}
		}
	}

	public static float[][] getHeightMap()
	{
		if (!initialised)
			throw new IllegalStateException("Klasse wurde nicht initialisiert!");
		float[][] heightMap = new float[9 * 256][9 * 256];
		for (int x = 0; x < heightMap.length; x++)
		{
			for (int z = 0; z < heightMap[0].length; z++)
			{
				int bx = (int) x / 256;
				int bz = (int) z / 256;

				if (myBl[bx][bz] == null)
				{
					System.err.println("Hier existiert kein Block");
				}
				heightMap[x][z] = myBl[bx][bz].getInfo(x % 256, z % 256, 0);
			}
		}
		return heightMap;
	}

//	/** method for getting the latest blocks as an float[][][] with sizes
//	 * [2304][2304][5]
//	 * @return float[][][] */
//	public static float[][][] getArray()
//	{
//		if (!initialised)
//			throw new IllegalStateException("Klasse wurde nicht initialisiert!");
//		float[][][] area = new float[9 * 256][9 * 256][5];
//
//		for (int x = 0; x < area.length; x++)
//		{
//			for (int z = 0; z < area.length; z++)
//			{
//
//				int bx = (int) x / 256;
//				int bz = (int) z / 256;
//
//				for (int dat = 0; dat < 5; dat++)
//				{
//					area[x][z][dat] = myBl[bx][bz].getInfo(x % 256, z % 256, dat);
//				}
//			}
//		}
//
//		return area;
//	}

	/** method gives the camPosX as related to the float[][][]
	 * @return int */
	public static int arrayCamPosX()
	{
		if (!initialised)
			throw new IllegalStateException("Klasse wurde nicht initialisiert!");
		return (int) ((cam.getCamPos().x % 256) + 256);
	}

	/** method gives the camPosZ as related to the float[][][]
	 * @return */
	public static int arrayCamPosZ()
	{
		if (!initialised)
			throw new IllegalStateException("Klasse wurde nicht initialisiert!");
		return (int) ((cam.getCamPos().z % 256) + 256);
	}
}
