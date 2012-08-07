package util;

/**
 * @brief Speichert eine bis zehn MipMaps fuer ein Terrain mit angegebener Kameraposition
 */
public class View
{
	private MipMap[] myMipMaps;
	private Camera myCam;
	private Terrain myTerrain;
	
	/**
	 * @brief Berechnet und speichert alle MipMaps bis zur angegebenen Groesse
	 * @param t		angegebenes Terrain
	 * @param cam	angegebene Kamera
	 * @param size	Anzahl der Abstufungen (1 bis 10)
	 */
	public View(Terrain t, Camera cam, int size)
	{
		if(t == null) t = new Terrain();
		if(cam == null) cam = new Camera();
		myTerrain = t;
		myCam = cam;
		
		if(size < 1)  size = 1;
		if(size > 10) size = 10;
		
		myMipMaps = new MipMap[size];
		for(int i = 0; i < size; i++)
		{
			myMipMaps[i] = new MipMap(t, cam, i + 1);
		}
	}
	
	/**
	 * @brief s.o., Standardwert size : 5
	 * @param t
	 * @param cam
	 */
	public View(Terrain t, Camera cam)
	{
		this(t, cam, 5);
	}
	
	public void update()
	{
		for(int i = 0; i < myMipMaps.length; i++)
		{
			if(myMipMaps[i].changeCenter(myCam))
			{
				myMipMaps[i] = new MipMap(myTerrain, myCam, i);
			}
		}
	}
	
	/**
	 * @brief liefert die MipMap der angegebenen Stufe
	 * @param level gewuenschte Stufe
	 * @return MipMap der angegebenen Stufe
	 */
	public MipMap getLevel(int level)
	{
		if(level < 1) level = 1;
		if(level > myMipMaps.length) level = myMipMaps.length;
		
		return myMipMaps[level-1];
	}
	
	/**
	 * @brief liefert die urspruenglich gewuenschte Anzahl der Abstufungen
	 * @return Anzahl der Abstufungen
	 */
	public int getSize()
	{
		return myMipMaps.length;
	}
}