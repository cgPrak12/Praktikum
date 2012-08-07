package util;

public class TerrainGrid {
	private float values[][][];
	
	// myGrid.get(10000, 10000, 5);
	// myGrid.get(10000, 10000)[5];
	
	public TerrainGrid(int sizeX, int sizeZ, int vertexInfo) {
		this.values = new float[sizeX][sizeZ][vertexInfo];
	}
	
	public void set(int x, int z, int vertexInfo, float value) {
		this.values[x][z][vertexInfo] = value;
	}
	
	public float get(int x, int z, int vertexInfo) {
		return this.values[x][z][vertexInfo];
	}
	
	public float[][][] getBlock(){
		return values;
		
	}
	
	public void add(int x, int z, int vertexInfo, float dValue) {
		this.values[x][z][vertexInfo] += dValue;
	}	
}
