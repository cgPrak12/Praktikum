package util;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.BufferUtils;

public class Grid {
	
	/**
	 * Liefert ein FloatBuffer-Array mit allen Detail-Bloecken
	 * result[0] ist dabei der innerste Block
	 * quantity gibt die Anzahl der Bloecke an (1 nur der innerste, 10 liefert 9 weitere)
	 * 
	 * @param dst Karte
	 * @param cam Kamera
	 * @param quantity Unterteilung (auf einer Skala von 1 bis 10)
	 * @param density Dichte (auf einer Skala von 1 bis 10)
	 * @return
	 */
	public static FloatBuffer[] minimizeGrid(Map dst, Camera cam, int quantity, int density)
	{
		density = density - density % 4;
		return minimizeGrid(dst, (int) cam.getCamPos().x, (int) cam.getCamPos().z, quantity, density);
	}
	
	/**
	 * Hilfsmethode
	 * @param dst Karte
	 * @param p x-Koordinate der Kameraposition
	 * @param q y-Koordinate der Kameraposition
	 * @param quantity Unterteilung (auf einer Skala von 1 bis 10)
	 * @return
	 */
	private static FloatBuffer[] minimizeGrid(Map dst, int p, int q, int quantity, int density)
	{
		if(quantity <= 0 || quantity > 10) { quantity = 1; System.err.println("ERROR: wrong quantity!"); }
		
		if(density  <= 0 || density  > 50) { density  = 1; System.err.println("ERROR: wrong density!"); }
		
		FloatBuffer[] result = new FloatBuffer[quantity];
		
		if(p < 0 || q < 0 || p >= dst.getXDim() || q >= dst.getZDim())
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
				if(p + i >= 0 && p + i < dst.getXDim() && q + j >= 0 && q + j < dst.getZDim())
				{
					vertexList01.add(dst.getInfo(p + i, q + j));
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
		
		for(int i = 1; i < quantity; i++)
		{
			result[i] = getOuterGrids(dst, p, q, quantity - 1, density)[i-1];
		}
		return result;
	}
	
	
	/**
	 * 
	 * @param dst
	 * @param p
	 * @param q
	 * @param quantity
	 * @return
	 */
	private static FloatBuffer[] getOuterGrids(Map dst, int p, int q, int quantity, int density)
	{
		FloatBuffer[] result = new FloatBuffer[quantity];
		
		@SuppressWarnings("unchecked")
		List<VertexInfo>[] help = (LinkedList<VertexInfo>[]) new LinkedList[quantity];
		
		for(int i = 0; i < quantity; i++)
		{
			help[i] = new LinkedList<VertexInfo>();
		}

		int step = 2;
		int count = 0;
		while(count < quantity && ((p - density)/2 >= 0 || (p + density)/2 < dst.getXDim() || (q - density)/2 >= 0 || (q + density)/2 < dst.getZDim()))
		{	
			for(int i = -density; i <= density; i += step)
			{
				for(int j = -density; j <= density; j += step)
				{
					if(p + i >= 0 && p + i < dst.getXDim() && q + j >= 0 && q + j < dst.getZDim() && 
							!(i < density / 2 && i > -density / 2 && j < density / 2 && j > -density / 2))
					{
						help[count].add(dst.getInfo(p + i, q + j));
					}
				}
			}
			density *= 2;
			step *= 2;
			count++;
		}		
		
		for(int i = 0; i < quantity; i++)
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
