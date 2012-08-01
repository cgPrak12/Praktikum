package util;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.BufferUtils;

public class GridFactory {

	/**
	 * Liefert ein FloatBuffer-Array mit allen Detail-Bloecken
	 * result[0] ist dabei der innerste Block
	 * quantity gibt die Anzahl der Bloecke an (1 nur der innerste, 10 liefert 9 weitere)
	 * 
	 * @param src Karte
	 * @param cam Kamera
	 * @param size Unterteilung (auf einer Skala von 1 bis 10)
	 * @param density Dichte (auf einer Skala von 1 bis 10)
	 * @return
	 */
	public static FloatBuffer[] minimizeGrid(Map src, Camera cam, int size, int density)
	{
		if(size <= 0 || size > 10) { size = 5; System.err.println("ERROR: wrong quantity!"); }
		
		if(density  <= 0 || density  > 10) { density  = 8; System.err.println("ERROR: wrong density!"); }
		
		density *= 5;
		density -= density % 4;

		return minimizeGrid(src, (int) cam.getCamPos().x, (int) cam.getCamPos().z, size, density);
	}
	
	/**
	 * minimizeGrid mit Standardwerten
	 * @param src
	 * @param cam
	 * @return
	 */
	public static FloatBuffer[] minimizeGrid(Map src, Camera cam)
	{
		return minimizeGrid(src, cam, 8, 12);
	}
	
	/**
	 * Hilfsmethode
	 * @param src Karte
	 * @param p x-Koordinate der Kameraposition
	 * @param q y-Koordinate der Kameraposition
	 * @param size Unterteilung (auf einer Skala von 1 bis 10)
	 * @return
	 */
	private static FloatBuffer[] minimizeGrid(Map src, int p, int q, int size, int density)
	{
		FloatBuffer[] result = new FloatBuffer[size];
		
		if(p < 0 || q < 0 || p >= src.getXDim() || q >= src.getZDim())
		{
			// Kamera-Position
			result[0] = BufferUtils.createFloatBuffer(0);
			return result;
		}
		
		List<VertexInfo> vertexList01 = new LinkedList<VertexInfo>();
		
		for(int i = -density/2 ; i <= density/2; i ++)
		{
			for(int j = -density/2; j <= density/2; j ++)
			{
				if(p + i >= 0 && p + i < src.getXDim() && q + j >= 0 && q + j < src.getZDim())
				{
					vertexList01.add(src.getInfo(p + i, q + j));
				}
			}
		}
		
		// Fuelle den ersten FloatBuffer mit Vertices aus dem innersten Block
		FloatBuffer fb01 = BufferUtils.createFloatBuffer(7 * vertexList01.size());
		for(VertexInfo vi : vertexList01)
		{
			fillList(fb01, vi);
		}
		result[0] = fb01;
		
		for(int i = 1; i < size; i++)
		{
			result[i] = getOuterGrids(src, p, q, size - 1, density)[i-1];
		}
		return result;
	}
	
	
	/**
	 * 
	 * @param src
	 * @param p
	 * @param q
	 * @param size
	 * @return
	 */
	private static FloatBuffer[] getOuterGrids(Map src, int p, int q, int size, int density)
	{
		FloatBuffer[] result = new FloatBuffer[size];
		
		@SuppressWarnings("unchecked")
		List<VertexInfo>[] help = (LinkedList<VertexInfo>[]) new LinkedList[size];
		
		for(int i = 0; i < size; i++)
		{
			help[i] = new LinkedList<VertexInfo>();
		}

		int step = 2;
		int count = 0;
		while(count < size && ((p - density)/2 >= 0 || (p + density)/2 < src.getXDim() || (q - density)/2 >= 0 || (q + density)/2 < src.getZDim()))
		{	
			for(int i = -density; i <= density; i += step)
			{
				for(int j = -density; j <= density; j += step)
				{
					if(p + i >= 0 && p + i < src.getXDim() && q + j >= 0 && q + j < src.getZDim() && 
							!(i < density / 2 && i > -density / 2 && j < density / 2 && j > -density / 2))
					{
						help[count].add(src.getInfo(p + i, q + j));
					}
				}
			}
			density *= 2;
			step *= 2;
			count++;
		}		
		
		for(int i = 0; i < size; i++)
		{
			FloatBuffer fb = BufferUtils.createFloatBuffer(7 * help[i].size());
			for(VertexInfo vi : help[i])
			{
				fillList(fb, vi);
			}
			result[i] = fb;
		}
		
		return result;
	}	
	
	/**
	 * Hilfsmethode
	 * @param fb
	 * @param vi
	 */
    private static void fillList(FloatBuffer fb, VertexInfo vi)
    {
			fb.put(vi.getX());
			fb.put(vi.getHeight());
			fb.put(vi.getZ());
			fb.put(vi.getNX());
			fb.put(vi.getNY());
			fb.put(vi.getNZ());
			fb.put(vi.getMat());
    }
}
