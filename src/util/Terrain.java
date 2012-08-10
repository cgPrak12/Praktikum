package util;

public class Terrain
{
	private static final int MEM_BLOCKS_SET = 16;
	private static final int MEM_BLOCKS_GET = 4;
	
	private String[][] blocks;
	private static int size;
	private float initialHeight;
	
	private int[][] currentIDsSet;
	private int[][] currentIDsGet;
	private Block[] currentBlocksSet;
	private Block[] currentBlocksGet;
	
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
		currentBlocksSet = new Block[MEM_BLOCKS_SET];
		currentBlocksGet = new Block[MEM_BLOCKS_GET];
		currentIDsSet = new int[MEM_BLOCKS_SET][2];
		currentIDsGet = new int[MEM_BLOCKS_GET][2];
		
		init();
	}
	
	/* Konstruktoren mit Standardwerten */
	public Terrain(int size) 
	{ 
		System.out.println("0%      +++      Bloecke werden geschrieben       +++       100%");
		this.size = getLastPow2(size);
		currentBlocksSet = new Block[MEM_BLOCKS_SET];
		currentBlocksGet = new Block[MEM_BLOCKS_GET];
		currentIDsSet = new int[MEM_BLOCKS_SET][2];
		currentIDsGet = new int[MEM_BLOCKS_GET][2];
		
		init();	
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
		int countSet = 0;
		int countGet = 0;
		int count = 0;
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
//						if(count++ % factor == 0)
//							System.out.print("....");
						int xPos = i * 4 + k;
						int zPos = j * 4 + l;
						Block block = new Block(xPos, zPos);
						
						if(xPos * dim + zPos < MEM_BLOCKS_SET)
						{
							currentBlocksSet[countSet]   = block;
							currentIDsSet[countSet][0]   = xPos;
							currentIDsSet[countSet++][1] = zPos;	
						}
						
						if(xPos * xPos < MEM_BLOCKS_GET && zPos * zPos < MEM_BLOCKS_GET)
						{
							currentBlocksGet[countGet]   = block;
							currentIDsGet[countGet][0]   = xPos;
							currentIDsGet[countGet++][1] = zPos;
						}
						
						// Fuellen der Bloecke mit Initialhoehe
						for(int m = 0; m < 256; m++)
						{
							
							for(int n = 0; n < 256; n++)
							{
								if(count++ % (factor * 16384) == 0)
									System.out.print(".");
								block.setInfo(m, n, 0, initialHeight);
						//		block.setInfo(m, n, 0, 0.1f);
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
	
	public void initRandom()
	{
		int dim = size / 256;
		
		blocks = new String[dim][dim];
		int countSet = 0;
		int countGet = 0;
		int count = 0;
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
//						if(count++ % factor == 0)
//							System.out.print("....");
						int xPos = i * 4 + k;
						int zPos = j * 4 + l;
						Block block = new Block(xPos, zPos);
						
						if(xPos * dim + zPos < MEM_BLOCKS_SET)
						{
							currentBlocksSet[countSet]   = block;
							currentIDsSet[countSet][0]   = xPos;
							currentIDsSet[countSet++][1] = zPos;	
						}
						
						if(xPos * xPos < MEM_BLOCKS_GET && zPos * zPos < MEM_BLOCKS_GET)
						{
							currentBlocksGet[countGet]   = block;
							currentIDsGet[countGet][0]   = xPos;
							currentIDsGet[countGet++][1] = zPos;
						}
						
						// Fuellen der Bloecke mit Initialhoehe
						for(int m = 0; m < 256; m++)
						{
							
							for(int n = 0; n < 256; n++)
							{
								if(count++ % (factor * 16384) == 0)
									System.out.print(".");
								block.setInfo(m, n, 0, 0.1f);
							}
						}
						
						// auf Festplatte schreiben
						blocks[xPos][zPos] = (BlockUtil.writeBlockData(block)).getName();						
					}
				}
			}
		}
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
			// wenn nicht, neue Bloecke reinladen
			boolean test = false;
			int n = 0;
			while(n < MEM_BLOCKS_SET)
			{
				if(currentIDsSet[n][0] == idX && currentIDsSet[n][1] == idZ)
				{
					test = true;
					break;
				}
				n++;
			}
			
			if(!test)
			{
				updateBlocksSet(x, z);
				n = 0;
				while(n < MEM_BLOCKS_SET)
				{
					if(currentIDsSet[n][0] == idX && currentIDsSet[n][1] == idZ)
					{
						break;
					}
					n++;
				}
			}

			Block myBlock = currentBlocksSet[n];
			myBlock.setInfo(blockX, blockZ, pos, value);
			BlockUtil.writeBlockData(myBlock);
			
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
		// wenn nicht, neue Bloecke reinladen
		boolean test = false;
		int n = 0;
		while((n+1) < MEM_BLOCKS_GET)
		{
			if(currentIDsGet[n][0] == idX && currentIDsGet[n][1] == idZ)
			{
				test = true;
				break;
			}
			n++;
		}
		
		if(!test)
		{
			updateBlocksGet(x, z);
			n = 0;
			while(n < MEM_BLOCKS_GET)
			{
				if(currentIDsGet[n][0] == idX && currentIDsGet[n][1] == idZ)
				{
					break;
				}
				n++;
			}
		}
		
		Block myBlock = currentBlocksSet[n];
		return myBlock.getInfo(blockX, blockZ, pos);
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
	public static int getSize() {
		return size;
	}
	
	/**
	 * Hilfsmethode, updated das Block[] fuer set-Aufrufe
	 * @param x x-Koordinate im Terrain
	 * @param z z-Koordinate im Terrain
	 */
	private void updateBlocksSet(int x, int z)
	{
		int idX = x / 256;
		int idZ = z / 256;
		
		for(int i = 0; i < MEM_BLOCKS_SET; i++)
		{
			int getX = (idX + i) % (size / 256);
			int getZ = (idZ + (idX + i) / (size / 256)) % (size / 256);
			currentBlocksSet[i] = BlockUtil.readBlockData(getX, getZ);
			currentIDsSet[i][0] = getX; currentIDsSet[i][1] = getZ;  
		}
	}
	
	/**
	 * Hilfsmethode, updated das Block[] fuer get-Aufrufe
	 * @param x x-Koordinate im Terrain
	 * @param z z-Koordinate im Terrain
	 */
	private void updateBlocksGet(int x, int z)
	{
		int idX = x / 256;
		int idZ = z / 256;
		int count = 0;
		
		for(int i = 0; i * i < MEM_BLOCKS_GET; i++)
		{
			for(int j = 0; j * j < MEM_BLOCKS_GET; j++)
			{
				currentBlocksGet[count] = BlockUtil.readBlockData(idX + i, (idZ + j) % (size / 256));
				currentIDsGet[count][0] = idX + i;
				currentIDsGet[count][1] = (idZ + j) % (size / 256);
				count++;
			}
		}
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
