package terrain;

public class Terrain
{
	private static final int MEM_BLOCKS = 1;
	
	private String[][] blocks;
	private int size;
	private float initialHeight;
	
	private int[][] currentIDs;
	private Block[] currentBlocks;
	
	/**
	 * Konstruktor mit konkreten Angaben zu Groesse und Initialhoehe
	 * @param size Groesse des Terrains
	 * @param initHeight Initialhoehe der Vertices
	 * @param overwrite true: neue Daten schreiben, false: alte Daten lesen
	 */
	public Terrain(int size, float initHeight, boolean overwrite)
	{
		boolean test = BlockUtil.DataInfoExist();
		this.size = getLastPow2(size);
		int dim = this.size / 256;
		
		System.out.println("test: " + test + ", size: " + this.size + ", dim: " + dim + ", overwrite: " + overwrite);
		if(test && !overwrite && dim * dim == (BlockUtil.readDataInfo()))
		{
			// Bloecke liegen bereits vor
			this.initialHeight = initHeight;
			blocks = new String[dim][dim];
			for(int i = 0; i < dim; i++)
			{
				for(int j = 0; j < dim; j++)
				{
					blocks[i][j] = i + "_" + j + "_.bf";
				}
			}
			
			currentBlocks = new Block[MEM_BLOCKS];
			currentIDs = new int[MEM_BLOCKS][2];
			int count = 0;
			
			for(int i = 0; i * i < MEM_BLOCKS; i++)
			{
				for(int j = 0; j * j < MEM_BLOCKS; j++)
				{
					currentBlocks[count] = BlockUtil.readBlockData(i, j);
					currentIDs[count][0] = i;
					currentIDs[count][1] = j;
					count++;
				}
			}
		}
		else
		{
			// Bloecke muessen neu erstellt werden
			System.out.println("0%      +++       Bloecke werden initiiert        +++       100%");
			this.size = getLastPow2(size);
			this.initialHeight = initHeight;
			currentBlocks = new Block[MEM_BLOCKS];
			currentIDs = new int[MEM_BLOCKS][2];
			
			init();
			BlockUtil.writeDataInfo();
		}
	}
	
	/* Konstruktoren mit Standardwerten */
	public Terrain(int size, float initHeight)	{ this(size, initHeight, false); }	// overwrite = false
	public Terrain(int size, boolean overwrite)	{ this(size, 0.0f, overwrite); } 	// initHeight = 0.0f
	public Terrain(boolean overwrite) 			{ this(1024, overwrite); }       	// size = 1024
	public Terrain() 							{ this(false); }                  	// overwrite = false
	
	/**
	 * Setze alle Vertices initial auf die gleiche Hoehe
	 * Initialisiere dabei Block[] fuer schnelleren Zugriff bei get- und set-Aufrufen
	 */
	private void init()
	{
		int dim = size / 256;
		
		blocks = new String[dim][dim];
		int count = 0;
		int countDot = 0;
		int factor = (size / 1024) * (size / 1024);
		
		// Unterteilung in 1024x1024er Bloecke
		for(int i = 0; i < size / 1024; i++)
		{
			for(int j = 0; j < size / 1024; j++)
			{				
				// Unterteilung in 256x256er Bloecke
				for(int k = 0; k < 4; k++)
				{
					for(int l = 0; l < 4; l++)
					{
						int xPos = i * 4 + k;
						int zPos = j * 4 + l;
						Block block = new Block(xPos, zPos);
						
						if(xPos * xPos < MEM_BLOCKS && zPos * zPos < MEM_BLOCKS)
						{
							currentBlocks[count]   = block;
							currentIDs[count][0]   = xPos;
							currentIDs[count++][1] = zPos;
						}
						
						// Fuellen der Bloecke mit Initialhoehe
						for(int m = 0; m < 256; m++)
						{
							for(int n = 0; n < 256; n++)
							{
								if(countDot++ % (factor * 16384) == 0)
									System.out.print(".");
								block.setInfo(m, n, 0, initialHeight);
							}
						}
						
						// auf Festplatte schreiben
						blocks[xPos][zPos] = (BlockUtil.writeBlockData(block)).getName();						
					}
				}
			}
		}
		System.out.println();
	}
	
	/**
	 * Gekapselter Arrayzugriff
	 * @param x x-Koordinate im Terrain
	 * @param z z-Koordinate im Terrain
	 * @param pos Position im VertexInfo-Teil
	 * @param value neuer Wert
	 * @return true bei Erfolg, false sonst (z.B. bei out of bounds)
	 */
	public boolean set(int x, int z, int pos, float value)
	{
		if(x >= 0 && z >= 0 && pos >= 0 &&x < size && z < size && pos < 5)
		{
			int idX    = x / 256; // x-Koordinate im Terrain
			int blockX = x % 256; // x-Koordinate im Block
			int idZ    = z / 256; // z-Koordinate im Terrain
			int blockZ = z % 256; // z-Koordinate im Block
			
			// liegt der Block bereits vor?
			boolean test = false;
			int n = 0;
			while(n < MEM_BLOCKS)
			{
				if(currentIDs[n][0] == idX && currentIDs[n][1] == idZ)
				{
					test = true;
					break;
				}
				n++;
			}
			
			// wenn nicht, neue Bloecke reinladen
			if(!test)
			{
				updateBlocks(x, z);
				n = 0;
				while(n < MEM_BLOCKS)
				{
					if(currentIDs[n][0] == idX && currentIDs[n][1] == idZ)
					{
						break;
					}
					n++;
				}
			}
			
			// Blockindex ist in n gespeichert
			currentBlocks[n].setInfo(blockX, blockZ, pos, value);
			return true;
		}
		return false;
	}

	/**
	 * Gekapselter Arrayzugriff
	 * @param x x-Koordinate im Terrain
	 * @param z z-Koordinate im Terrain
	 * @param pos Position im VertexInfo-Teil
	 * @return gewuenschter Wert
	 */
	public float get(int x, int z, int pos)
	{
		int idX    = x / 256; // x-Koordinate im Terrain
		int blockX = x % 256; // x-Koordinate im Block
		int idZ    = z / 256; // z-Koordinate im Terrain
		int blockZ = z % 256; // z-Koordinate im Block
				
		// liegt der Block bereits vor?
		boolean test = false;
		int n = 0;
		while(n < MEM_BLOCKS)
		{
			if(currentIDs[n][0] == idX && currentIDs[n][1] == idZ)
			{
				test = true;
				break;
			}
			n++;
		}
		
		// wenn nicht, neue Bloecke reinladen
		if(!test)
		{
			updateBlocks(x, z);
			n = 0;
			while(n < MEM_BLOCKS)
			{
				if(currentIDs[n][0] == idX && currentIDs[n][1] == idZ)
				{
					break;
				}
				n++;
			}
		}
		
		// Blockindex ist in n gespeichert
		return currentBlocks[n].getInfo(blockX, blockZ, pos);
	}
	
	/**
	 * Addiert zu einem Wert im VertexInfo-Teil etwas hinzu
	 * @param x x-Koordinate im Terrain
	 * @param z z-Koordinate im Terrain
	 * @param pos Position im VertexInfo-Teil
	 * @param dValue Summand
	 */
	public void add(int x, int z, int pos, float dValue)
	{
		set(x, z, pos, get(x, z, pos) + dValue);
	}
	
	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * Hilfsmethode, erneuert das Block[] fuer schnellere Zugriffe
	 * @param x x-Koordinate im Terrain
	 * @param z z-Koordinate im Terrain
	 */
	private void updateBlocks(int x, int z)
	{
		// Heap auf Festplatte schreiben
		for(int i = 0; i < MEM_BLOCKS; i++)
		{
			BlockUtil.writeBlockData(currentBlocks[i]);
		}
		
		// neue Bloecke laden
		int dim = size / 256;
		int idX = dim + x / 256;
		int idZ = dim + z / 256;
		int count = 0;
		for(int i = 0; i * i < MEM_BLOCKS; i++)
		{
			for(int j = 0; j * j < MEM_BLOCKS; j++)
			{
				currentBlocks[count] = BlockUtil.readBlockData((idX + i) % dim, (idZ + j) % dim);
				currentIDs[count][0] = (idX + i) % dim;
				currentIDs[count][1] = (idZ + j) % dim;
				count++;
			}
		}
	}
	
	/**
	 * Liefert einen Block mit allen Werten bei ID (idX, idZ)
	 * @param idX x-Koordinate des Blocks
	 * @param idZ z-Koordinate des Blocks
	 * @return Block mit allen Werten bei ID (idX, idZ)
	 */
	public Block getBlock(int idX, int idZ)
	{
		Block result = new Block(idX, idZ);
		for(int i = 0; i < 256; i++)
		{
			for(int j = 0; j < 256; j++)
			{
				int blockX = idX * 256 + i;
				int blockZ = idZ * 256 + j;
				result.setInfo(i, j, 0, this.get(blockX, blockZ, 0));
				result.setInfo(i, j, 1, this.get(blockX, blockZ, 1));
				result.setInfo(i, j, 2, this.get(blockX, blockZ, 2));
				result.setInfo(i, j, 3, this.get(blockX, blockZ, 3));
				result.setInfo(i, j, 4, this.get(blockX, blockZ, 4));
			}
		}				
		return result;
	}
	
	/**
	 * Berechnet die naechst kleinere 2er-Potenz einer Zahl n
	 * @param n angegebene Zahl
	 * @return naechst kleinere 2er-Potenz
	 */
	private static int getLastPow2(int n)
	{
		if(n < 1024) return 1024;
		int i = 1024;
		while(i <= n) i *= 2;
		i /= 2;
		return i;
	}
}
