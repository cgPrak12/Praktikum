package terrain;

/**
 * Klasse kapselt ein komplettes Terrain als float[][][]
 * 
 * @author daniel, lukas, mareike
 */

public class Terrain
{
	private static final int MEM_BLOCKS = 16;
	
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
				
		/* Pruefen, ob ein bereits existierendes Terrain von Festplatte gelesen werden kann */
		if(test && !overwrite && this.size == (BlockUtil.readDataInfo()))
		{
			// Block Objekte liegen bereits vor
			this.initialHeight = initHeight;
			System.out.println("Loading blocks");
			
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
					if(count % 10000 == 0) System.out.print(".");
				}
			}
		}
		else
		{
			// Block Objekte muessen neu erstellt werden
			System.out.println("0%      +++       Bloecke werden initiiert        +++       100%");
			this.size = getLastPow2(size);
			this.initialHeight = initHeight;
			currentBlocks = new Block[MEM_BLOCKS];
			currentIDs = new int[MEM_BLOCKS][2];
			
			init();
			BlockUtil.writeDataInfo(this.size);
		}
	}
	
	/* Konstruktoren mit Standardwerten */
	public Terrain(int size, float initHeight)	
	{ 
		this(size, initHeight, false);		// overwrite = false 
	}
	
	public Terrain(int size, boolean overwrite)	
	{ 
		this(size, 0.0f, overwrite);  		// initHeight = 0.0f 
	}
	
	public Terrain(boolean overwrite)
	{ 
		this(512, overwrite); 				// size = 512
	}       	
	
	public Terrain()	
	{ 
		this(false); 						// overwrite = false
	}                  

	
	/**
	 * Setze alle Vertices initial auf die gleiche Hoehe
	 * Initialisiere dabei Block[] fuer schnelleren Zugriff bei get- und set-Aufrufen
	 */
	private void init()
	{		
		int count = 0;
		int countDot = 0;
		int factor = (size / 512) * (size / 512);
		
		/* default Unterteilung in 1024x1024er Bloecke */
		for(int i = 0; i < size / 1024; i++)
		{
			for(int j = 0; j < size / 1024; j++)
			{				
				/* default Unterteilung in 256x256er Bloecke */
				for(int k = 0; k < 4; k++)
				{
					for(int l = 0; l < 4; l++)
					{
						int xPos = i * 4 + k;
						int zPos = j * 4 + l;
						Block block = new Block(xPos, zPos, 512, 5);
						
						if(xPos * xPos < MEM_BLOCKS && zPos * zPos < MEM_BLOCKS)
						{
							currentBlocks[count]   = block;
							currentIDs[count][0]   = xPos;
							currentIDs[count++][1] = zPos;
						}
						
						// Fuellen der Bloecke mit Initialhoehe
						for(int m = 0; m < 512; m++)
						{
							for(int n = 0; n < 512; n++)
							{
								if(countDot++ % (factor * 16384) == 0)
									System.out.print(".");
								block.setInfo(m, n, 0, initialHeight);
							}
						}
						
						// auf Festplatte schreiben
						BlockUtil.writeBlockData(block);						
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
		if(x >= 0 && z >= 0 && pos >= 0 && x < size && z < size && pos < 5)
		{
			int idX    = x / 512; // x-Koordinate im Terrain
			int blockX = x % 512; // x-Koordinate im Block
			int idZ    = z / 512; // z-Koordinate im Terrain
			int blockZ = z % 512; // z-Koordinate im Block
			
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
		int idX    = x / 512; // x-Koordinate im Terrain
		int blockX = x % 512; // x-Koordinate im Block
		int idZ    = z / 512; // z-Koordinate im Terrain
		int blockZ = z % 512; // z-Koordinate im Block
				
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
	public int getSize()
	{
		return size;
	}
	
	/**
	 * Speichert die Daten auf der Festplatte
	 */
	public void save()
	{
		for(int i = 0; i < size / 512; i++)
		{
			for(int j = 0; j < size / 512; j++)
			{
				BlockUtil.writeBlockData(getBlock(i,j));
			}
		}
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
		int dim = size / 512;
		int idX = dim + x / 512;
		int idZ = dim + z / 512;
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
		for(int i = 0; i < 512; i++)
		{
			for(int j = 0; j < 512; j++)
			{
				int blockX = idX * 512 + i;
				int blockZ = idZ * 512 + j;
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
		if(n < 512) 
		{
			return 512;
		}		
		int i = 512;
		while(i <= n)
		{
			i *= 2;
		}	
		i /= 2;
		return i;
	}
}
