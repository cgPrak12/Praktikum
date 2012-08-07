package util;

public abstract class TerrainGrid {
	
	private Block[][] blocks;
	private int maxX;
	private int maxZ;
	
	public TerrainGrid(int size)
	{
		size = getPow2(size);
		maxX = maxZ = size;
		int dim = size / 256;
		blocks = new Block[dim][dim];
	}
	
	public void setBlock(int blockCol, int blockLin, Block block)
	{
		blocks[blockCol][blockLin] = block;
	}
	
	public Block getBlock(Camera cam)
	{
		return null;
	}
	
	
	public Block getBlock(int blockCol, int blockLin)
	{
		return blocks[blockCol][blockLin];
	}
		
	public void setVertex(int x, int z, int pos, float info)
	{
		if(x >= 0 && z >= 0 && pos >= 0 &&x < maxX && z < maxZ && pos < 5)
		{
			int terX = x / 256;
			int blockX = x % 256;
			int terZ = z / 256;
			int blockZ = z % 256;
			
			blocks[terX][terZ].setInfo(blockX, blockZ, pos, info);
		}
	}
	
	public float getVertex(int x, int z)
	{
		int terX = x / 256;
		int blockX = x % 256;
		int terZ = z / 256;
		int blockZ = z % 256;
		
		return blocks[terX][terZ].getInfo(blockX, blockZ)[0];
		
	}
	
	private static int getPow2(int num)
	{
		if(num < 1024) num = 1024;		
		int i = 256;
		while(i <= num) i*= 2;
		return i;
	}

}
