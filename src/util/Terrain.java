package util;

public class Terrain
{
	private String[][] blocks;
	private int size;
	private float initialHeight;
	private int[][] currentIDs;
	private Block[] currentBlocks;
	
	/**
	 * Konstruktor mit konkreten Angaben zu Groesse und Initialhoehe
	 * @param size Groesse des Terrains
	 * @param initHeight Initialhoehe der Vertices
	 */
	public Terrain(int size, float initHeight)
	{
		this.size = size;
		this.initialHeight = initHeight;
		currentBlocks = new Block[4];
		currentIDs = new int[4][2];
		init();
		
		currentBlocks[0] = BlockUtil.getBlock(0,0);
		currentBlocks[1] = BlockUtil.getBlock(1,0);
		currentBlocks[2] = BlockUtil.getBlock(2,0);
		currentBlocks[3] = BlockUtil.getBlock(3,0);
		
		currentIDs[0][0] = 0; currentIDs[0][1] = 0;
		currentIDs[1][0] = 1; currentIDs[1][1] = 0;
		currentIDs[2][0] = 2; currentIDs[2][1] = 0;
		currentIDs[3][0] = 3; currentIDs[3][1] = 0;
		
	}
	
	/* Konstruktoren mit Standardwerten */
	public Terrain(int size) 			{ this(size, 0.0f); }
	public Terrain(float initHeight) 	{ this(1024, initHeight); }
	public Terrain() 					{ this(1024, 0.0f); }
	
	/**
	 * Setze alle Vertices initial auf die gleiche Hoehe
	 */
	public void init()
	{
		int dim = size / 256;
		blocks = new String[dim][dim];
		
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
						
						// Fuellen der Bloecke mit Initialhoehe
						for(int m = 0; m < 256; m++)
						{
							for(int n = 0; n < 256; n++)
							{
								block.setInfo(m, n, 0, initialHeight);
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
			while(n < 4)
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
				while(n < 4)
				{
					if(currentIDs[n][0] == idX && currentIDs[n][1] == idZ)
					{
						break;
					}
					n++;
				}
			}

			Block myBlock = currentBlocks[n];
//			Block myBlock = BlockUtil.readBlockData(idX, idZ);
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
		int terX   = x / 256;
		int blockX = x % 256;
		int terZ   = z / 256;
		int blockZ = z % 256;
		
		Block myBlock = BlockUtil.readBlockData(terX, terZ);
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
	
	private void updateBlocks(int x, int z)
	{
		int idX = getLastMul4(x / 256);
		int idZ = z / 256;
		
		Block block = BlockUtil.readBlockData(idX, idZ);
		currentBlocks[0] = block;
		currentBlocks[1] = BlockUtil.readBlockData(idX + 1, idZ);
		currentBlocks[2] = BlockUtil.readBlockData(idX + 2, idZ);
		currentBlocks[3] = BlockUtil.readBlockData(idX + 3, idZ);
		
		currentIDs[0][0] = idX    ; currentIDs[0][1] = idZ;
		currentIDs[1][0] = idX + 1; currentIDs[1][1] = idZ;
		currentIDs[2][0] = idX + 2; currentIDs[2][1] = idZ;
		currentIDs[3][0] = idX + 3; currentIDs[3][1] = idZ;
	}
	
	private int getLastMul4(int n)
	{
		if(n < 0) return 0;
		int i = 0;
		while(i < n) i += 4;
		return i;
	}
}
