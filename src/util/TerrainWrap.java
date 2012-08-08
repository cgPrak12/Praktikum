package util;

public class TerrainWrap
{
	private String[][] blocks;
	int size;
	
	public TerrainWrap(int sizeX, int sizeZ, int initialHeight)
	{
		size = sizeX;
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
						blocks[xPos][zPos] = (BlockUtil.writeBlockData(block)).getName();						
					}
				}
			}
		}
	}
	
	public boolean set(int x, int z, int pos, float value)
	{
		if(x >= 0 && z >= 0 && pos >= 0 &&x < size && z < size && pos < 5)
		{
			int terX = x / 256;
			int blockX = x % 256;
			int terZ = z / 256;
			int blockZ = z % 256;
			
			Block myBlock = BlockUtil.readBlockData(terX, terZ);
			myBlock.setInfo(blockX, blockZ, pos, value);
			BlockUtil.writeBlockData(myBlock);
			
			return true;
		}
		return false;
	}
	
	public float get(int x, int z, int pos)
	{
		int terX   = x / 256;
		int blockX = x % 256;
		int terZ   = z / 256;
		int blockZ = z % 256;
		
		Block myBlock = BlockUtil.readBlockData(terX, terZ);
		return myBlock.getInfo(blockX, blockZ, pos);
	}

	
//	
//	public float[][][] getBlock()
//	{
//		return values;
//	}
	
	public void add(int x, int z, int pos, float dValue)
	{
		set(x, z, pos, get(x, z, pos) + dValue);
	}
}
