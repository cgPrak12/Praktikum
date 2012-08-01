package util;

import java.nio.FloatBuffer;

public class Grid {
	
	private Map myMap;
	private Camera myCam;
	private float camPosX, camPosZ;
	private FloatBuffer[] myFB;
	
	/**
	 * Ctor
	 * @param map Erstellt ein minimiertes Grid aus der gegebenen Map
	 * @param cam mit angegebener Kamera-Position
	 * @param size angegebener Anzahl umliegender Bloecke
	 * @param density und angegebener Dichte der Bloecke
	 */
	public Grid(Map map, Camera cam, int size, int density)
	{
		myMap   = map;
		myCam   = cam;
		camPosX = cam.getCamPos().x;
		camPosZ = cam.getCamPos().z;
		myFB    = GridFactory.minimizeGrid(map, cam, size, density);
	}
	
	/**
	 * Ctor
	 * @param map Erstellt ein minimiertes Grid aus der gegebenen Map
	 * @param cam mit angegebener Kamera-Position
	 */
	public Grid(Map map, Camera cam)
	{
		this(map, cam, 8, 12);
	}
	
	/**
	 * Ctor
	 * @param map erstellt ein minimiertes Grid aus der gegebenen Map
	 */
	public Grid(Map map)
	{
		this(map, new Camera());
	}
	
	public FloatBuffer[] update()
	{
		float newCamPosX = myCam.getCamPos().x;
		float newCamPosZ = myCam.getCamPos().z;
		
		int dx = (int) (newCamPosX - camPosX);
		int dz = (int) (newCamPosZ - camPosZ);
		
		camPosX = newCamPosX;
		camPosZ = newCamPosZ;
		
		for(int i = 0; i < myFB.length; i++)
		{
			for(int j = 0; j < myFB[i].capacity(); j += 7)
			{
				myFB[i].put(j, myFB[i].get(j) + dx);
				myFB[i].put(j + 1, myMap.getInfo((int)newCamPosX, (int)newCamPosZ).getHeight());
				
				myFB[i].put(j + 2, myFB[i].get(j + 2) + dz);
			}
		}		
		return myFB;
	}
	
	/* Getter */
	public Map getMap()                   { return myMap; }
	public Camera getCam()                { return myCam; }
	public float getCamPosX()             { return camPosX; }
	public float getCamPosZ()             { return camPosZ; }
	public FloatBuffer[] getFloatBuffer() { return myFB; }
	
	/* Setter */
	public void setMap(Map map)           { myMap = map; }
	public void setCam(Camera cam)        { myCam = cam; }
	public void setCamPosX(float x)       { camPosX = x; }
	public void setCamPosZ(float z)       { camPosZ = z; }
	public void setFloatBuffer(FloatBuffer[] fb)
	{
		int length = fb.length;
		myFB = new FloatBuffer[length];
		for(int i = 0; i < length; i++)
		{
			myFB[i] = fb[i];
		}
	}
	

}
