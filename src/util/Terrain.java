package util;

import java.util.Random;

public class Terrain {

	private float[][] terra;
	private float[][] noiseMap = new float [32][32];
	private float[][] biome;
	private float[][] materialMap;
	private Random random;
	
	/**
	 * 
	 * @param initialHeight
	 * @param maxX: Should be devisable by 4
	 * @param maxZ: Should be devisable by 4
	 * @param seed
	 * @param noiseType
	 * @param noiseMap
	 */
	public Terrain(float initialHeight, int maxX, int maxZ, int seed, int noiseType, float[][]noiseMap){

		this.random = new Random(seed);
		this.terra = new float[maxX][maxZ];
		this.biome = new float[maxX][maxZ];
		this.materialMap= new float[maxX][maxZ];
		
		// Gen flat terra and empty biome
		for(int x=0; x < maxX; ++x) {
			for(int z=0; z < maxZ; ++z) {
				this.terra[x][z] = initialHeight;
			}
		} 		
		
		// Gen Noisemap       
		if (noiseMap != null){
			if(noiseMap.length!=32 || noiseMap[0].length!=32) throw new RuntimeException("Are you retarded?");
			this.noiseMap = noiseMap;
		}
		else{
			switch (noiseType){
			case 1: 
				for(int x=0; x < 32; ++x) {
					for(int z=0; z < 32; ++z) {
						this.noiseMap[x][z] = this.random.nextFloat()*2-1;
					}
				}
				break;

			case 2:
				for(int x=0; x < 32; ++x) {
					for(int z=0; z < 32; ++z) {
						this.noiseMap[x][z] = (float) ((this.random.nextFloat()*2-1)-Math.sin(z/32*6.282f)*3+Math.cos(x/32*6.282f)*0.25);
					}
				}
				break;		
			}
		}
		
	}

	/**
	 * With Seed = 0
	 * 
	 * @param initialHeight
	 * @param maxX
	 * @param maxZ
	 * @param noiseType
	 * @param noise
	 * @param noisyMap
	 * @param surfaceWrink
	 */
	public Terrain(float initialHeight, int maxX, int maxZ, int noiseType, float[][]noisyMap){
		this(initialHeight, maxX, maxZ, 0, noiseType,  noisyMap);
	}

	/**
	 * With Seed = 0
	 * With initialHeight = 0.5
	 * 
	 * @param maxX
	 * @param maxZ
	 * @param noiseType
	 * @param noisyMap
	 * @param surfaceWrink
	 */
	public Terrain(int maxX, int maxZ,  int noiseType, float[][]noisyMap){
		this(0.5f, maxX, maxZ, noiseType, noisyMap);
	}

	/**
	 * With Seed = 0
	 * With initialHeight = 0.5
	 * With random noisyMap
	 * 
	 * @param maxX
	 * @param maxZ
	 * @param noiseType
	 * @param surfaceWrink
	 */
	public Terrain(int maxX, int maxZ,  int noiseType){
		this(maxX, maxZ, noiseType, null);
	}

	/**
	 * With Seed = 0
	 * With initialHeight = 0.5
	 * With random noisyMap
	 * With maxX = maxZ = 2048
	 * 
	 * @param noiseType
	 * @param surfaceWrink
	 */
	public Terrain(int noiseType){
		this(2048, 2048, noiseType);
	}

	/**
	 * Default Map(noisy)
	 */
	public Terrain(){
		this(1);
	}
/**
 * 
 * @param fieldSize (3,5,7)
 * @param smoothLevel must be positive
 */
	public void smooth(int fieldSize, int smoothLevel){

		switch(fieldSize){
		case 3: for(int i=0; i<smoothLevel; i++)
			Util.smoothGauss3(this.terra);
		break;

		case 5: for(int i=0; i<smoothLevel; i++)
			Util.smoothGauss5(this.terra);
		break;
		
		case 7: for(int i=0; i<smoothLevel; i++)
			Util.smoothGauss7(this.terra);
		break;
		
		}
		updateBiomeFromHeight(5);
	}
	/**
	 * Function for transforming the terrain. Calls Material- and Biome- changing fuctions by default
	 * 
	 * @param surfaceWrink: The level of detail
	 */
	public void terraform(int surfaceWrink){

		float amp=2, freq=0.05f;
		for(int i=1; i<=surfaceWrink; i++){

			if(i==3) freq = 0.5f;
			Util.biLinIpol(this.terra, this.noiseMap, freq, amp);
			freq*=(2+(random.nextFloat()/5f-0.2f));
			amp/=(2+(random.nextFloat()/5f-0.2f));

		}
		updateBiomeFromHeight(5);
	}

	public float[][] getBiome(){		
		return this.biome;		
	}
	/**
	 * MaterialMap Legend:
	 * 0 = undefined/default
	 * 1 = under water
	 * 2 = sand
	 * 3 = earth/grass
	 * 4 = stone/snow
	 * 
	 * @param range: The Width in which is checked if materials are the same
	 */
	private void updateBiomeFromHeight(int range){
		int maxX = this.terra.length;
		int maxZ = this.terra[0].length;
		
		// Fill material map
		for(int x=0; x<maxX; x++){
			for(int z=0; z<maxZ; z++){
				if(this.terra[x][z]<0f) this.materialMap[x][z] = 1;
				else{
					if(this.terra[x][z]<0.5f) this.materialMap[x][z] = 2;
					else{
						if(this.terra[x][z]<2f) this.materialMap[x][z] = 3;
						else{	
							this.materialMap[x][z] = 4;
						}
					}
				}
			}
		}
		
		// Check for biomes, one for each material
		for(int i=1; i<5; i++){
			checkBiome(i, range);
		}
	}
	/**
	 * 
	 * @param material
	 * @param range
	 */
	private void checkBiome(int material, int range){
		int maxX = this.materialMap.length;
		int maxZ = this.materialMap[0].length;
		for(int x=0; x<maxX; x++){
			for(int z=0; z<maxZ; z++){
				
			}
		}
			
	}
	/**
	 * 
	 * @return this terrains heightmap
	 */
	public float[][] getTerra(){		
		return this.terra;		
	}
	public void bitchPLEASE(){
		this.terraform(25);
		this.smooth(7, 1);
		this.smooth(5,2);
        this.smooth(3,3);
        
	}

}
