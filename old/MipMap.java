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
	private int density;
	private int step;
	private int lvl;
	
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
		
		lvl = level;
	
		if(t == null) t = new Terrain();
		if(cam == null) cam = new Camera();
		
		// berechne die Aufloesung abhaengig von der Kamerahoehe
		// je hoeher die Kamera, desto niedriger die Aufloesung
		camY = cam.getCamPos().y;		
		maxY = cam.getMaxCamHeight();
		terY = t.getInfo(Math.round(cam.getCamPos().x), Math.round(cam.getCamPos().z))[0];
		density = (int) Util.scale(camY - terY, maxY - terY, 0, 1.0f, 50.0f);
		
		// berechne die Grenzen der MipMap
		step = (int) Math.pow(2, level - 1);
		camX = Math.round(cam.getCamPos().x);
		camZ = Math.round(cam.getCamPos().z);
		
		int dimX = 2 * density + 1;
		int dimZ = 2 * density + 1;
		
		map = new float[dimX][dimZ][5];
		
//		System.out.println("level: " + level + ", density: " + density + ", step: " + step);
//		System.out.println("   x starts at: " + (camX - (level) * density) + "\n   x ends at:  " + (camX - level * density + step * (dimX-1)));
//		System.out.println("   z starts at: " + (camZ - (level) * density) + "\n   z ends at:  " + (camZ - level * density + step * (dimZ-1)));
//		System.out.println("   so x size should be:  " + (1 + ((camX - (level)*density + step*(dimX-1)) - (camX - level) * density)/step) + "(" + (dimX) + ")");
//		System.out.println("   and z size should be: " + (1 + ((camZ - (level)*density + step*(dimZ-1)) - (camZ - level) * density)/step) + "(" + (dimZ) + ")");
			
		
		System.out.println("level: " + level + ", step: " + step + ", startX: " + (camX - step * density) + ", stopX: " + (camX + step * density) + ", startZ: " + (camZ - step * density) + ", stopZ: " + (camZ + step * density));
		for(int i = 0; i < dimX; i++)
		{
			for(int j = 0; j < dimZ; j++)
			{
				
				int dx = Math.round(Util.scale(i, 0, dimX-1, camX - step * density, camX + step * density));
				int dz = Math.round(Util.scale(j, 0, dimZ-1, camZ - step * density, camZ + step * density));
				
//				System.out.println("level: " + level + " density: " + density + " step: " + step);
				System.out.println("0 | " + i + " | " + (dimX-1) + "; " + (camX - step * density) + "| " + dx + " | " + (camX + step * density));
				System.out.println("0 | " + j + " | " + (dimZ-1) + "; " + (camZ - step * density) + "| " + dz + " | " + (camZ + step * density));
				
				
				if(dx >= 0 && dx < t.getXDim() && dz >= 0 && dz < t.getZDim())
				{
					map[i][j] = t.getInfo(dx, dz);
				}
				else
				{
					float[] dummy = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
					map[i][j] = dummy;
				}
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
		
		int newX = Math.round(cam.getCamPos().x);
		int newZ = Math.round(cam.getCamPos().z);
		
		if(Math.abs(newX - camX) > step|| Math.abs(newZ - camZ) > step)
			return true;			
		else
		{
			float newY = cam.getCamPos().y;
			int newDens = Math.round(Util.scale(newY - terY, maxY - terY, 0, 1.0f, 50.0f));
			int oldDens = Math.round(Util.scale(camY - terY, maxY - terY, 0, 1.0f, 50.0f));
			
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
	 * @param newX x-Koordinate
	 * @param newZ z-Koordinate
	 * @return Vertexinformation
	 */
	public float[] getAbs(int x, int z)
	{
		int newX = Math.round(Util.scale(x, camX - lvl * density, camX + lvl * density, 0, map.length-1));
		int newZ = Math.round(Util.scale(z, camZ - lvl * density, camZ + lvl * density, 0, map[0].length-1));
		
		if(newX >= 0 && newZ >= 0 && newX < map.length && newZ < map[0].length)
		{
			return map[newX][newZ];
		}
		else
		{
			float[] dummy = {-6.0f, 0.0f, 0.0f, 0.0f, 0.0f};
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
	
	public int getDensity() { return density; }
	
	public int getStep() { return step; }
}
		
		

