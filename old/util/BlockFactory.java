package util;

public class BlockFactory {
	
	private static Block[][] blocks;
	private int maxX;
	private int maxZ;
	
	public BlockFactory(int size)
	{
		maxX = maxZ = size;
		int dim = size / 256;
		blocks = new Block[dim][dim];
	}
	
	public void setBlock(int blockCol, int blockLin, Block block)
	{
		blocks[blockCol][blockLin] = block;
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
	
	public static Block getBlock(Camera cam)
	{
		int x = Math.round(cam.getCamPos().x);
		int z = Math.round(cam.getCamPos().z);
		
		return blocks[x / 256][z / 256];		
	}
	
	
	public Block getBlock(int blockCol, int blockLin)
	{
		return blocks[blockCol][blockLin];
	}
	
	public float get(int x, int z, int pos)
	{
		int terX   = x / 256;
		int blockX = x % 256;
		int terZ   = z / 256;
		int blockZ = z % 256;
		
		return blocks[terX][terZ].getInfo(blockX, blockZ, pos);		
	}
}
