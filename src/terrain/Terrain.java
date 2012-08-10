package terrain;

public class Terrain implements TerrainData{
	
	private final float[][] landscape;
	private final int[][] material;
	
	public Terrain(int x, int y){
		this.landscape = new float[x][y];
		this.material = new int[x][y];
	}
	
	@Override
	public int getDimensionX() {
		return landscape.length;
	}

	@Override
	public int getDimensionY() {
		return landscape[0].length;
	}

	@Override
	public float getHeight(int x, int y) {
		return landscape[x][y];
	}

	@Override
	public void setHeight(int x, int y, float height) {
		landscape[x][y] = height;
	}

	@Override
	public int getMaterial(int x, int y) {
		return material[x][y];
	}

	@Override
	public void setMaterial(int x, int y, int material) {
		this.material[x][y] = material;
	}
}
