package util;

public class BlockFactory {
	
	private static Block[][] blocks;
	private int maxX;
	private int maxZ;
	
	/**
	 * Ctor, size = terrain size (muss quadratisch sein)
	 * @param size
	 */
	public BlockFactory(int size)
	{
		maxX = maxZ = size;
		int dim = size / 256;
		blocks = new Block[dim][dim];
	}
	
	/**
	 * sets an entire block
	 * @param blockCol idX
	 * @param blockLin idZ
	 * @param block new block
	 */
	public void setBlock(int blockCol, int blockLin, Block block)
	{
		blocks[blockCol][blockLin] = block;
	}
	
	/**
	 * sets a value in the responsible block
	 * @param x x position in terrain
	 * @param z z position in terrain
	 * @param pos vertexinfo position
	 * @param info new value
	 */
	public void set(int x, int z, int pos, float info)
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
	
	/**
	 * gets the responsible block for current camera position
	 * @param cam camera
	 * @return responsible block
	 */
	public static Block getBlock(Camera cam)
	{
		int x = Math.round(cam.getCamPos().x);
		int z = Math.round(cam.getCamPos().z);
		
		return blocks[x / 256][z / 256];		
	}
	
	/**
	 * gets block at given ID
	 * @param blockCol idX
	 * @param blockLin idZ
	 * @return block at given ID
	 */
	public Block getBlock(int blockCol, int blockLin)
	{
		return blocks[blockCol][blockLin];
	}
	
	/**
	 * gets vertexinfo value at vertex (x|z) at position pos
	 * @param x x position in terrain
	 * @param z z position in terrain
	 * @param pos vertexinfo position
	 * @return vertexinfo at position
	 */
	public float get(int x, int z, int pos)
	{
		int terX   = x / 256;
		int blockX = x % 256;
		int terZ   = z / 256;
		int blockZ = z % 256;
		
		return blocks[terX][terZ].getInfo(blockX, blockZ, pos);		
	}
}
