package util;

/**
 * Holds the information of the terrain for each (x|y)-point in a VertexInfo-object
 * @author LZELLER
 *
 */
public class Map {
	
	// vertex information
	private VertexInfo[][] info;
	int x, y;
	
	/**
	 * Ctor, per default 1024x1024
	 */
	public Map()
	{
		this(1024, 1024);
	}
	
	/**
	 * Ctor
	 * @param x x-dimension
	 * @param y y-dimension
	 */
	public Map(int x, int y)
	{
		this.x = x;
		this.y = y;
		info = new VertexInfo[x][y];
	}

	/**
	 * Getter
	 * @param x x-position
	 * @param y y-position
	 * @return vertex information
	 */
	public VertexInfo getInfo(int x, int y)
	{
		if(x >= 0 && x < this.x && y >=0 && y < this.y)
		{
			return info[x][y];
		}
		// ueberarbeiten, wer will ;-)
		else return null;
	}

	/**
	 * Setter
	 * @param x x-position
	 * @param y y-position
	 * @param vi new vertex information
	 */
	public void setInfo(int x, int y, VertexInfo vi)
	{
		if(x >= 0 && x < this.x && y >=0 && y < this.y)
		{
			info[x][y] = vi;
		}
		else System.err.println("error");
	}
	
	public int getXDim()	{	return x; }
	
	public int getZDim()	{	return y; }

}
