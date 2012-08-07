package util;

public class TerrainView {

	private Block[][] myBl;
	private Camera cam;
	private int[] middle;
	
	public TerrainView(Camera c)
	{		
		myBl = new Block[9][9];
		cam =  c;
		init();
	}
	
	private void init()
	{
		myBl[4][4] = BlockFactory.getBlock(cam);
		middle = myBl[4][4].getID();
		
		int idI = middle[0]-1;
		int idJ = middle[1]-1;
		
		//hier fehlt noch die Fehlerbehandlung am Rand
		//dummy-Block wenn man am/über dem Rand ist 
		for(int i=0; i<3; i++)
		{
			for(int j=0; j<3; j++)
			{
				myBl[i][j] = BlockParser.readBlockData("block_"+(idI+i)+"_"+(idJ+j)+".bf");
			}
		}
	}

	public void updateTerrainView()
	{		
		int diffX = (BlockFactory.getBlock(cam)).getID()[0] - middle[0];
		int diffY = (BlockFactory.getBlock(cam)).getID()[1] - middle[1];
		
		if(Math.abs(diffX) > 2 || Math.abs(diffX) > 2)
		{
			init();
		}
		else
		{
			for(int i=0; i<3; i++)
			{
				for(int j=0; j<3; j++)
				{

					if( i+diffX<0 || i+diffX>2 || j+diffY<0 || j+diffY>2)
					{
						String file = "block_" + (myBl[i][j].getID()[0] + diffX) 
								         + "_" + (myBl[i][j].getID()[0] + diffX) + ".bf";
						myBl[i][j] = BlockParser.readBlockData(file);
					}
					else
					{
						myBl[i][j] = myBl[i+diffX][j+diffY];
					}
				}
			}
		}
	}

}
