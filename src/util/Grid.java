package util;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.BufferUtils;

public class Grid {
	
	/**
	 * Berechnet die (minimierten) Grids in der angegebenen Umgebung der Camera.
	 * Achtung am Rand!
	 * Ist size z.B. 1 werden 9 Grids berechnet,
	 *               2       25
	 *               3       49
	 *               n	  (2n+1)^2 
	 * @param dst Komplettes Grid
	 * @param cam Camera
	 * @param size Größe des Quadrats
	 * @return Liste mit allen Grids, das aktuelle Grid ist an Stelle 1 + (2n+1)²/ 2
	 */
	public static List<FloatBuffer> getGrids(ArrayStruc dst, Camera cam, int size)
	{
		List<FloatBuffer> result = new LinkedList<FloatBuffer>();
		
		if(size > 0 && size < 10)
		{
			int p = (int) cam.getCamPos().x;
			int q = (int) cam.getCamPos().y;
			
			for(int i = -size; i <= size; i++)
			{
				for(int j = -size; j <= size; j++)
				{
					result.add(minimizeGrid(dst, p + i, q + j));
				}
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param dst
	 * @param p
	 * @param q
	 * @return
	 */
	private static FloatBuffer minimizeGrid(ArrayStruc dst, int p, int q)
	{
		List<VertexInfo> help = new LinkedList<VertexInfo>();
		
		if(p < 0 || q < 0 || p >= dst.getXDim() || q >= dst.getZDim())
		{
			return BufferUtils.createFloatBuffer(0);
		}
		
		// erstes Kreuz
		for(int i = 0; i < dst.getZDim(); i++)
		{
			// Spalte fuellen
			help.add(dst.getInfo(p, i));
		}
		
		for(int i = 0; i < dst.getXDim(); i++)
		{
			// Zeile fuellen
			help.add(dst.getInfo(i, q));
		}
		
		int up = 1;
		for(int size = 1; p + size < dst.getXDim(); size += (up++))
		{
			for(int i = 0; i < dst.getZDim(); i++)
			{
				// Spalten rechts
				help.add(dst.getInfo(p + size, i));
			}
		}
		
		up = 1;		
		for(int size = 1; p - size >= 0; size += (up++))
		{
			for(int i = 0; i < dst.getZDim(); i++)
			{
				// Spalten links
				help.add(dst.getInfo(p - size, i));
			}
		}
		
		up=1;
		for(int size = 1; q + size < dst.getZDim(); size += (up++))
		{
			for(int i = 0; i < dst.getXDim(); i++)
			{
				// Zeilen unten
				help.add(dst.getInfo(i, q + size));
			}
		}
		
		up=1;
		for(int size = 1; q - size >= 0; size += (up++))
		{
			for(int i = 0; i < dst.getXDim(); i++)
			{
				// Zeilen oben
				help.add(dst.getInfo(i, q - size));
			}
		}
		
		// Liste in ein FloatBuffer kopieren
		FloatBuffer result = BufferUtils.createFloatBuffer(7 * help.size());
		for(VertexInfo vi : help)
		{
			result.put(vi.getX());
			result.put(vi.getHeight());
			result.put(vi.getZ());
			result.put(vi.getNX());
			result.put(vi.getNY());
			result.put(vi.getNZ());
			result.put(vi.getMat());
		}
		
		
		return result;
	}
	
	/**
	 * 
	 * @param dst
	 * @param cam
	 * @return
	 */
	public static FloatBuffer minimizeGrid(ArrayStruc dst, Camera cam)
	{
		return minimizeGrid(dst, (int) cam.getCamPos().x, (int) cam.getCamPos().z);
	}	
	
}
