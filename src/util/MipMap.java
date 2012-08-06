package util;

/**
 * @brief 	Repraesentiert ein Objekt, das zu einem Terrain mit angegebener Kamera
 *			und gewuenschter Abstufung entsprechend viele Vertices in der Umgebung
 *			speichert.
 */
public class MipMap // extends Terrain?
{
	private float[][][] map;
	private int camX;		// aktuelle x-Koordinate der Kamera bzw. des Mittelpunkts
	private int camZ;		// aktuelle z-Koordinate der Kamera bzw. des Mittelpunkts
	private float camY;		// aktuelle Kamerahoehe
	private float maxY;		// maximale Kamerahoehe
	private float terY;		// Hoehe des Mittelpunkts
	private int startX, stopX, startZ, stopZ;
	private int step;
	
	/**
	 * @brief 	Ctor, berechnet die MipMap von einem angegebenen Terrain t bei
	 *			angegebener Kameraposition
	 * @param 	t 		angegebenes Terrain
	 * @param 	cam 	angegebene Kamera
	 * @param 	level 	gewuenschte Stufe (1 bis 10)
	 */
	public MipMap(Terrain t, Camera cam, int level)
	{
		// schneide level zurecht
		if(level < 1)    level = 1;
		if(level > 10)   level = 10;
	
		if(t == null) t = new Terrain();
		if(cam == null) cam = new Camera();
		
		// berechne die Aufloesung abhaengig von der Kamerahoehe
		// je hoeher die Kamera, desto niedriger die Aufloesung
		camY = cam.getCamPos().y;		
		maxY = cam.getMaxCamHeight();
		terY = t.getInfo((int)cam.getCamPos().x, (int)cam.getCamPos().z)[0];
		int density = (int) Util.scale(camY - terY, maxY - terY, 0, 1.0f, 50.0f);
		
		// berechne die Grenzen der MipMap
		step = (int) Math.pow(2, level - 1);
		camX = (int) cam.getCamPos().x;
		camZ = (int) cam.getCamPos().z;
		
		startX = camX - step * density;
		stopX  = camX + step * density;
		startZ = camZ - step * density;
		stopZ  = camZ + step * density;
		
//		for(int i = 1; i <= density; i++)
//		{
//			if(camX - step * i > 0) startX = camX - step * i;
//			if(camZ - step * i > 0) startZ = camZ - step * i;
//			if(camX + step * i < t.getXDim()) stopX = camX + step * i;
//			if(camZ + step * i < t.getZDim()) stopZ = camZ + step * i;
//		}
		
		// berechne daraus die Dimension der MipMap
//		int dimX = 1 + (stopX - startX) / step;
//		int dimZ = 1 + (stopZ - startZ) / step;
		
		map = new float[2 * density + 1][2 * density + 1][5];
		
			
		for(int i = 0; i < 2 * density + 1; i ++)
		{
			for(int j = 0; j < 2 * density + 1; j ++)
			{
				int dx = camX + (2 * i - (2 * density + 1));
				int dz = camZ + (2 * j - (2 * density + 1));
				if(dx >= 0 && dx < t.getXDim() && dz >= 0 && dz < t.getZDim())
				{
					map[i][j] = t.getInfo(camX + (2 * i - (2 * density + 1)), camZ + (2 * j - (2 * density + 1)));
				}
				else
				{
					float[] dummy = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
					map[i][j] = dummy;
				}
				
//				float[] help = t.getInfo(startX + i * step, startZ + j * step);
//				map[i][j][0] = (float) startX + i * step;
//				map[i][j][0] = help[0];
//				map[i][j][2] = (float) startZ + j * step;
//				map[i][j][1] = help[1];
//				map[i][j][2] = help[2];
//				map[i][j][3] = help[3];
//				map[i][j][4] = help[4];
			}
		}
	}
	
	/**
	 * @brief	s.o., Standardparameter für level: 0, liefert also die innerste MipMap
	 * @param	t angegebenes Terrain
	 * @param	cam angegebene Kamera
	 */
	public MipMap(Terrain t, Camera cam)
	{
		this(t, cam, 0);
	}
	
	/**
	 * @brief ermittelt, ob eine neue MipMap berechnet werden muss
	 * @param cam neue Kameraposition
	 * @return true, wenn neu berechnet werden muss, false sonst
	 */
	public boolean changeCenter(Camera cam)
	{
		if(cam == null) cam = new Camera();
		
		int newX = (int) cam.getCamPos().x;
		int newZ = (int) cam.getCamPos().z;
		
		if(Math.abs(newX - camX) > step|| Math.abs(newZ - camZ) > step)
			return true;			
		else
		{
			float newY = cam.getCamPos().y;
			int newDens = (int) Util.scale(newY - terY, maxY - terY, 0, 1.0f, 50.0f);
			int oldDens = (int) Util.scale(camY - terY, maxY - terY, 0, 1.0f, 50.0f);
			
			if(Math.abs(newDens - oldDens) > 1)			
				return true;
			else
				return false;
		}
	}		
	
	/**
	 * @brief liefert die Informationen eines Vertex' in MipMap-Position
	 * @param x x-Koordinate
	 * @param z z-Koordinate
	 * @return Vertexinformation
	 */
	public float[] getRel(int x, int z)
	{
		if(x >= 0 && z >= 0 && x < map.length && z < map[0].length)
		{
			return map[x][z];
		}
		else
		{
			float[] dummy = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
			return dummy;
		}
	}
	
	/**
	 * @brief liefert die Informationen eines Vertex' in Terrain-Position
	 * @param x x-Koordinate
	 * @param z z-Koordinate
	 * @return Vertexinformation
	 */
	public float[] getAbs(int x, int z)
	{
		if(x >= 0 && z >= 0 && x < map.length && z < map[0].length)
		{
			return map[x][z];
		}
		else
		{
			float[] dummy = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
			return dummy;
		}
	}
	
	
	/**
	 * @brief liefert die x-Dimension der MipMap (bzw. maxX)
	 * @return x-Dimension der MipMap
	 */
	public int getXDim() { return map.length; }
	
	/**
	 * @brief liefert die z-Dimension der MipMap (bzw. maxZ)
	 * @return z-Dimension der MipMap
	 */
	public int getZDim() { return map[0].length; }
	
	/**
	 * @brief setter-Methode, deep copy
	 * @param map neue MipMap
	 */
	public void set(float[][][] map)
	{
		if(map[0][0].length == 5)
		{
			for(int i = 0; i < map.length; i++)
			{
				for(int j = 0; j < map[0].length; j++)
				{
					for(int k = 0; k < 5; k++)
					{
						this.map[i][j][k] = map[i][j][k];
					}
				}
			}
		}
	}
	
	/**
	 * @brief liefert die x-Koordinate des Mittelpunkts
	 * @return x-Koordinate des Mittelpunkts
	 */
	public int getCenterX() { return camX; }
	
	/**
	 * @brief liefert die x-Koordinate des Mittelpunkts
	 * @return x-Koordinate des Mittelpunkts
	 */
	public int getCenterZ() { return camZ; }
}
		
		

