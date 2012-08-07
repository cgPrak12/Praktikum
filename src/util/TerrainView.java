package util;

public class TerrainView {
	
	private Block[][] myBlocks;
	//private Camera cam;
	
	public TerrainView(Camera cam)
	{
		myBlocks = new Block[9][9];
		myBlocks[4][4] = BlockFactory.getBlock(cam);
		
	}

}
