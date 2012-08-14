package terrain;

public class Terrain
{
	private static final int MEM_BLOCKS = 16;
	
	private String[][] blocks;
	private int size;
	private float initialHeight;
	
	 int[][] currentIDs;
	 Block[] currentBlocks;
	
	/**
	 * Konstruktor mit konkreten Angaben zu Groesse und Initialhoehe
	 * @param size Groesse des Terrains
	 * @param initHeight Initialhoehe der Vertices
	 */
	public Terrain(int size, float initHeight)
	{
		System.out.println("0%      +++      Bloecke werden geschrieben       +++       100%");
		this.size = getLastPow2(size);
		this.initialHeight = initHeight;
		currentBlocks = new Block[MEM_BLOCKS];
		currentIDs = new int[MEM_BLOCKS][2];
		
		init();
	}
	
	/* Konstruktoren mit Standardwerten */
	public Terrain(int size) 
	{ 
		System.out.println("0%      +++      Bloecke werden geschrieben       +++       100%");
		this.size = getLastPow2(size);
		currentBlocks = new Block[MEM_BLOCKS];
		currentIDs = new int[MEM_BLOCKS][2];
		
		initRandom();	
	}
	public Terrain(float initHeight) 	{ this(1024, initHeight); }
	public Terrain() 					{ this(1024, 0.0f); }
	
	/**
	 * Setze alle Vertices initial auf die gleiche Hoehe
	 * Initialisiere dabei Block[] fuer schnelleren Zugriff bei get- und set-Aufrufen
	 */
	public void init()
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
		
//		for(int i = 0; i < MEM_BLOCKS; i++)
//		{
//			System.out.printf("MEM[%d] : Block (%d,  %d)\n", i, currentBlocks[i].getID()[0], currentBlocks[i].getID()[1]);
//		}
	}
	
	public void initRandom()
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
						
//						if(xPos * dim + zPos < MEM_BLOCKS)
//						{
//							currentBlocks[countSet]   = block;
//							currentIDs[countSet][0]   = xPos;
//							currentIDs[countSet++][1] = zPos;	
//						}
						
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
								block.setInfo(m, n, 0, (float) (0.1f * xPos*zPos));
							}
						}
						
						// auf Festplatte schreiben
						blocks[xPos][zPos] = (BlockUtil.writeBlockData(block)).getName();						
					}
				}
			}
		}
		System.out.println();
		
//		for(int i = 0; i < MEM_BLOCKS; i++)
//		{
//			System.out.printf("MEM[%d] : Block (%d,  %d)", i, currentBlocks[i].getID()[0], currentBlocks[i].getID()[1]);
//		}
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
			
//			System.out.printf("SET: writing %f at (%d, %d, %d) in block (%d, %d) at (%d, %d, %d)\n", value, x, z, pos, idX, idZ, blockX, blockZ, pos);
			
			// liegt der Block bereits vor?
			// wenn nicht, neue Bloecke reinladen
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
//			System.out.println("SET: (" + test + "), value was " + currentBlocks[n].getInfo(blockX, blockZ, pos));
						
			currentBlocks[n].setInfo(blockX, blockZ, pos, value);
//			System.out.println("SET: (" + test + "), value now " + currentBlocks[n].getInfo(blockX, blockZ, pos) + "\n");
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
		
//		for(int i = 0; i < MEM_BLOCKS; i++)
//		{
//			System.out.printf("GET: MEM[%d] : Block (%d,  %d)\n", i, currentBlocks[i].getID()[0], currentBlocks[i].getID()[1]);
//		}
				
		// liegt der Block bereits vor?
		// wenn nicht, neue Bloecke reinladen
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

//		System.out.printf("GET: looking in MEM[%d] : Block (%d, %d)\n", n, currentBlocks[n].getID()[0], currentBlocks[n].getID()[1]); 
//		System.out.printf("GET: getting %f at (%d, %d, %d) in block (%d, %d) at (%d, %d, %d)\n", currentBlocks[n].getInfo(blockX, blockZ, pos), x, z, pos, idX, idZ, blockX, blockZ, pos);
//		System.out.println("GET: (" + test + ")\n");
		
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
	 * Hilfsmethode, updated das Block[] fuer get-Aufrufe
	 * @param x x-Koordinate im Terrain
	 * @param z z-Koordinate im Terrain
	 */
	private void updateBlocks(int x, int z)
	{
		for(int i = 0; i < MEM_BLOCKS; i++)
		{
			BlockUtil.writeBlockData(currentBlocks[i]);
		}
		
		int dim = size / 256;
		int idX = dim + x / 256;
		int idZ = dim + z / 256;
//		System.out.printf("updating blocks for (%d,%d), should be in block(%d,%d)\n", x, z, idX % dim, idZ % dim);
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
		
//		for(int i = 0; i < MEM_BLOCKS; i++)
//		{
//			System.out.printf("MEM[%d] : Block (%d,  %d)\n", i, currentBlocks[i].getID()[0], currentBlocks[i].getID()[1]);
//		}
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
