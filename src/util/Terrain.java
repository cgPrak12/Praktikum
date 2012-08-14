package util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix2f;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 * 
 * Terrain data in float[][] with noisemaps, normalMap, biomeMap and materialMap
 * can generate and smooth a terrain
 * @author ARECKNAG, FMAESCHIG
 *
 */
public class Terrain {

	private TerrainGrid terra;
	private float[][] noiseMap = new float [32][32];
	private float[][] mountainMap1 = new float [32][32];
	private float[][] mountainMap2 = new float [32][32];
	private float[][] mountainMap3 = new float [32][32];
	private float[][] riverMap = new float [32][32];
	private float[][] desertMap1 = new float [32][32], desertMap2 = new float [32][32];
	private float[][] seaMap = new float [32][32];
	private float[][] gauss17 = new float[17][17];
	private float[][] slopeMap = new float [32][32];
	private float[][] diamondMap = new float[513][513];
	private Random random;
	private int maxX, maxZ;
	private final int MAXHEIGHT;
	private int vertexInfoCount = 5;
	private float SCALE;

        public TerrainGrid getTerrainGrid() {
            return this.terra;
        }
        
	/**
	 * 
	 * @param initialHeight
	 * @param maxX
	 * @param maxZ 
	 * @param seed
	 */

	public Terrain(float initialHeight, int maxX, int maxZ, int seed){

		this.terra = new TerrainGrid(maxX, maxZ, vertexInfoCount);
		this.MAXHEIGHT = 20;
		this.random = new Random(seed);
		this.maxX = maxX;
		this.maxZ= maxZ; 

		System.out.println("Initialising noiseMaps");

		// Gen flat terra, with no norms and material info
		for(int x=0; x < maxX; ++x) {
			for(int z=0; z < maxZ; ++z) {
				this.terra.set(x,z,0,initialHeight);
			}
		} 		
		//TODO method to fill terra with inital height(block by block) 
		//this.terra.setInitialHeight;

		// Getting diamondMap
		this.diamondMap = Util.diamondSquare(9, 1);
		
		// Gen Noisemap       
		for(int x=0; x < noiseMap.length; ++x) {
			for(int z=0; z < noiseMap[0].length; ++z) {
				this.noiseMap[x][z] = this.random.nextFloat()*2-1;
			}
		}
		gauss17[0][0] = gauss17[16][0] = gauss17[0][16] = gauss17[16][16] = 1;
		gauss17[0][1] = gauss17[1][0] = gauss17[16][1] = gauss17[1][16] = gauss17[16][15] = gauss17[15][16] = gauss17[0][15] = gauss17[15][0] = 16;
		gauss17[0][2] = gauss17[2][0] = gauss17[16][2] = gauss17[2][16] = gauss17[16][14] = gauss17[14][16] = gauss17[0][14] = gauss17[14][0] = 120;
		gauss17[0][3] = gauss17[3][0] = gauss17[16][3] = gauss17[3][16] = gauss17[16][13] = gauss17[13][16] = gauss17[0][13] = gauss17[13][0] = 560;
		gauss17[0][4] = gauss17[4][0] = gauss17[16][4] = gauss17[4][16] = gauss17[16][12] = gauss17[12][16] = gauss17[0][12] = gauss17[12][0] = 1820;
		gauss17[0][5] = gauss17[5][0] = gauss17[16][5] = gauss17[5][16] = gauss17[16][11] = gauss17[11][16] = gauss17[0][11] = gauss17[11][0] = 4360;
		gauss17[0][6] = gauss17[6][0] = gauss17[16][6] = gauss17[6][16] = gauss17[16][10] = gauss17[10][16] = gauss17[0][10] = gauss17[10][0] = 8008;
		gauss17[0][7] = gauss17[7][0] = gauss17[16][7] = gauss17[7][16] = gauss17[16][9] = gauss17[9][16] = gauss17[0][9] = gauss17[9][0] = 11440;
		gauss17[0][8] = gauss17[8][0] = gauss17[16][8] = gauss17[8][16] = 12870;


		for(int x=1; x < gauss17.length-1; ++x) {
			for(int z=1; z < gauss17[0].length-1; ++z) {
				this.gauss17[x][z] = this.gauss17[0][z] * this.gauss17[x][0];
			}
		}
		float maxVal = this.gauss17[8][8];
		for(int x=0; x < gauss17.length; ++x) {
			for(int z=0; z < gauss17[0].length; ++z) {
				this.gauss17[x][z] /= maxVal;
			}
		}


		//Gen MountainMap
		for(int x=0; x < 32; ++x) {
			for(int z=0; z < 32; ++z) {

				this.mountainMap1[x][z] = ((16f-(Math.abs(-(x-16f))))/16f*(16f-(Math.abs(-(z-16f))))/16f);
				this.mountainMap2[x][z] = (float) (((Math.cos(((x-16)/12.9f)*((x-16)/12.9f)) *
						Math.cos(((z-16)/12.9f)*((z-16)/12.9f)))));
				this.mountainMap3[x][z] =  (mountainMap1[x][z] +(mountainMap2[x][z] * mountainMap2[x][z] * mountainMap2[x][z] * mountainMap2[x][z] * mountainMap2[x][z]))/2f;
				this.slopeMap[x][z] = (float) (-0.05f + Math.sin((float)x / 64 *Math.PI)/2f);
				//	Mt.slope 	(float) (((16-(Math.abs(-(x-16))))/16f*(16-(Math.abs(-(z-16))))/16f)* (1f+(3f* (0.05f*(((this.random.nextFloat()*2-1)/200)+ (((Math.cos((x/6f)*Math.PI))))+(((Math.cos((z/12f)*Math.PI)))))))));


			}
		}
		//Gen RiverMap
		for(int x=0; x < 32; ++x) {
			for(int z=0; z < 32; ++z) {
				this.riverMap[x][z] = (float)(1f- Math.sin(((z-16)/13f)*((z-16)/13f)));
			}
		}

		//Gen DesertMap1
		for(int x=0; x < 32; ++x) {
			for(int z=0; z < 32; ++z) {
				this.desertMap1[x][z] = (float) (((16-(Math.abs(-(x-16))))/16f*(16-(Math.abs(-(z-16))))/16f)* (((0.05f*(((this.random.nextFloat()*2-1)/200)+ (((Math.cos((x/6f)*Math.PI))))+(((Math.cos((z/12f)*Math.PI)))))))));		
			}	
		}

		//Gen DesertMap2
		for(int x=0; x < 32; ++x) {
			for(int z=0; z < 32; ++z) {
				this.desertMap2[x][z] = (float) (((Math.sin(((x/16f)-1)*Math.PI ) 
						* Math.cos(((z*z/480.5f)-1) * Math.sqrt(Math.PI/2d))+2f)/2f* mountainMap3[x][z])/1.1180068f);

			}	
		}
		//		for(int x=0; x < 32; ++x) {
		//			for(int z=0; z < 32; ++z) {
		//				this.noiseMap[x][z] = (float) ((this.random.nextFloat()*2-1)-Math.sin(z/32*6.282f)*3+Math.cos(x/32*6.282f)*0.25);
		//			}
		//		}

		System.out.println("Done");

	}


	/**
	 * With Seed = 0
	 * 
	 * @param initialHeight
	 * @param maxX
	 * @param maxZ
	 */
	public Terrain(float initialHeight, int maxX, int maxZ){
		this(initialHeight, maxX, maxZ, 0);
	}

	/**
	 * With Seed = 0
	 * With initialHeight = 0.5
	 * 
	 * @param maxX
	 * @param maxZ
	 */
	public Terrain(int maxX, int maxZ){
		this(0.5f, maxX, maxZ);
	}

	/**
	 * With Seed = 0
	 * With initialHeight = 0.5
	 * With maxX = maxZ = 2048
	 * 
	 */
	public Terrain(){
		this(2048, 2048);
	}


	/**
	 * 
	 * @return Terra's vertices info
	 */
	public float[][][] getTerra(){		
		return this.terra.getBlock();		
	}


	/**
	 * 
	 * @return Terra's heightMap
	 */
	public float[][] getHeightMap(){

		float[][] heightMap = new float [maxX][maxZ];

		for(int x=0; x<maxX; x++){
			for(int z=0; z<maxZ; z++){
				heightMap[x][z] = this.terra.get(x, z, 0);
			}
		}
		return heightMap;
	}









	

	/**
	 * method for testing terrain generation.
	 * @param form
	 */
	public void genTerrain(int form){
		
//		int startX = 50;
//		int startZ = 50;
//		for(int i = 0; i<513; i++){
//			for(int j = 0; j<513; j++){
//				terra.set(i+startX, j+startZ, 0, terra.get(i+startX, j+startZ, 0)+this.diamondMap[i][j]);
//			}
//		}
//				this.terraform(8, 1);
		//		putMountain(mountainMap1, 1, 256, 256, 100);

		//		putTest(riverMap,1,512,512,300);
				this.terraform(1, 1, 0.1f);	
		//				this.testForm();
//				setMaterialsFromHeight(0, maxX, 0, maxZ);
//				this.putRiver(2, 100, 100, 800, 800, 0f);

//				this.testForm();
		//		this.flatten(1024, 1024, 1024, 1, 2);
//				this.putLake(46.0f, 0f, (int)(1.0f*640), (int)(1.0f*640));
//				this.putLake(20.0f, 0f, 170, 170);
		//		this.putLake(20.0f, 0f, (int)(0.5f*640), (int)(0.7f*640));

				this.flattenAll(20);				
				this.smooth();
				this.checkNormals();
	}

	/**
	 * Sets the materials of the map dependent on height values.
	 * The parameters are the position on the map (minX - maxX and minZ - maxZ)
	 * @param minX
	 * @param maxX
	 * @param minZ
	 * @param maxZ
	 */
	private void setMaterialsFromHeight(int minX, int maxX, int minZ, int maxZ){
		System.out.println("Setting materials");

		float compare;
		// Gen Materials from height
		for(int x=minX; x<maxX; x++){
			for(int z=minZ; z<maxZ; z++){
				compare = this.terra.get(x,z,0);
				if(compare<0 *SCALE){
					this.terra.set(x, z, 4, 1);							//1 = sea
				}
				else{													//2 = river(only biome)
					compare += (random.nextFloat()/10f);
					if(compare<1.5f *SCALE){
						this.terra.set(x, z, 4, 3);						//3 = beach
					}
					else{
						if(compare<2.5f *SCALE){
							this.terra.set(x, z, 4, 4);					//4 = earth
						}
						else{	
							if(compare<3.5f *SCALE){
								this.terra.set(x, z, 4, 5);				//5 = light grass
							}
							else{										
								if(compare<4f *SCALE){				
									this.terra.set(x, z, 4, 6);			//6 = dark grass
								}else{										
									if(compare<4.5f *SCALE){
										this.terra.set(x, z, 4, 7);		//7 = stone
									}
									else{	
										if(compare<6.5f *SCALE){
											this.terra.set(x, z, 4, 8);		//8 = rock
										}
										else{	
											if(compare<7.5f *SCALE){
												this.terra.set(x, z, 4, 9);	//9 = light snow
											}
											else{	

												this.terra.set(x, z, 4, 10);	//10 = heavy snow

											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		System.out.println("Done");

	}



	/**
	 * check vertices in the whole map for their normals and writes them in position 1 - 3.
	 */ 
	private void checkNormals(){

		System.out.println("Checking normals");

		// List of triangles for a grid of vertices, one bigger on each side to account context
		int[] triangles = new int[3*2*(this.maxX+1)*(this.maxZ+1)];
		int count = maxZ;
		for(int z=1; z<maxZ; z++){
			for(int x=1; x<maxX; x++){
				triangles[count++] = (z * this.maxX + x);
				triangles[count++] = ((z + 1) * this.maxX + x + 1);
				triangles[count++] = (z * this.maxX + x + 1);

				triangles[count++] = (z * this.maxX + x);
				triangles[count++] = ((z + 1) * this.maxX + x);
				triangles[count++] = ((z + 1) * this.maxX + x + 1);
			}
		}



		//TODO get rid of floatbuffer and write norms directly into terra
		//Gen normalMap
		int vertexSize = 6;   	    	
		FloatBuffer vertices = BufferUtils.createFloatBuffer(vertexSize*this.maxX*this.maxZ);

		// Gen Vbuffer
		for(int z=0; z < this.maxZ; ++z) {
			for(int x=0; x < this.maxX; ++x) {
				vertices.put(1e-2f * (float)x);
				vertices.put(terra.get(x, z, 0));
				vertices.put(1e-2f * (float)z);
				vertices.put(0);	// norm.x
				vertices.put(0);	// norm.y
				vertices.put(0);	// norm.z
			}                	    
		}

		// Gen IndexBuffer
		IntBuffer indices = BufferUtils.createIntBuffer(3 * 2 * (this.maxX - 1) * (this.maxZ - 1));
		for(int z=0; z < this.maxZ - 1; ++z) {
			for(int x=0; x < this.maxX - 1; ++x) {
				indices.put(z * this.maxX + x);
				indices.put((z + 1) * this.maxX + x + 1);
				indices.put(z * this.maxX + x + 1);

				indices.put(z * this.maxX + x);
				indices.put((z + 1) * this.maxX + x);
				indices.put((z + 1) * this.maxX + x + 1);
			}
		}

		// Gen norms
		indices.position(0);
		for(int i=0; i < indices.capacity();) {
			int index0 = indices.get(i++);
			int index1 = indices.get(i++);
			int index2 = indices.get(i++);

			vertices.position(vertexSize * index0);
			Vector3f p0 = new Vector3f();
			p0.load(vertices);
			vertices.position(vertexSize * index1);
			Vector3f p1 = new Vector3f();
			p1.load(vertices);
			vertices.position(vertexSize * index2);
			Vector3f p2 = new Vector3f();
			p2.load(vertices);

			Vector3f a = Vector3f.sub(p1, p0, null);
			Vector3f b = Vector3f.sub(p2, p0, null);
			Vector3f normal = Vector3f.cross(a, b, null);
			normal.normalise();

			vertices.position(vertexSize * index0 + 3);
			normal.store(vertices);
		}
		vertices.position(0);
		indices.position(0);

		for(int x=1; x<=this.maxX; x++){
			for(int z=0; z<this.maxZ; z++){

				vertices.position(4+(vertexSize*x*z));

				//		        		tmp.load(vertices);
				//		        		this.normalMap[x-1][z] = tmp;
				//		        		this.terra[x-1][z][1] = tmp.x;
				//		        		this.terra[x-1][z][2] = tmp.y;
				//		        		this.terra[x-1][z][3] = tmp.z;

				this.terra.set(x-1,z,1, vertices.get());
				this.terra.set(x-1,z,2, vertices.get());
				this.terra.set(x-1,z,3, vertices.get());	        		
			}        	
		}
		System.out.println("Done");

	}


	/**
	 * For all used bioForms, it has to hold that edges must be 0.
	 * 
	 * @param bioForm the biome which is to be put
	 * @param amp the amplitude with which it is put
	 * @param x the position
	 * @param z the position
	 * @param range the size of area it covers
	 */
	private void putMountain(float amp, int x, int z, int range){

		System.out.println("Putting mountain"+x+" / "+z);

		int mountainX = mountainMap3.length;
		int mountainZ = mountainMap3[0].length;
		int pX, pZ;
		float a, b;
		float dX, dZ;

		for(int i=0; i < 2*range-1; i++){

			a = (float) mountainX / (float) (2f*range) * (float) i;
			pX = (int) a;
			dX = a - pX;

			for(int j=0; j < 2*range-1; j++){

				b = (float) mountainZ / (float) (2f*range) * (float) j;
				pZ = (int) b;
				dZ = b - pZ;

				//interpolate height values
				//				biomeMap[x-range+i][z-range+j] = MOUNTAIN;
				this.terra.add(x-range+i, z-range+j, 0, amp * 
						(Util.iPol
								(Util.iPol(
										mountainMap3[pX % mountainX][pZ % mountainZ], 
										mountainMap3[pX % mountainX][(pZ+1)%mountainZ],
										dZ),
										Util.iPol(
												mountainMap3[(pX+1)%mountainX][pZ % mountainZ], 
												mountainMap3[(pX+1)%mountainX][(pZ+1)%mountainZ],
												dZ),
												dX)));

			}

		}
		setMaterialsFromHeight(x-range, x+range, z-range, z+range);
		//		makeNoise(range, x, z, 2, 0.05f, 5);
		System.out.println("Done");

	}


	/**
	 * Puts a desert within a certain range.
	 * 
	 * @param amp
	 * @param x
	 * @param z
	 * @param range
	 */
	private void putDesert(float amp, int x, int z, int range){
		
		System.out.println("Putting desert at"+x+" / "+z);

		int desertX = desertMap2.length;
		int desertZ = desertMap2[0].length;
		int pX, pZ;
		float a, b;
		float dX, dZ;

		this.flatten(x, z, range, 0.25f, 1, 3);
		//		this.setMaterialsFromHeight(x-range, x+range, z-range, z+range);
		amp *= 0.8f;
		for(int i=0; i < 2*range-1; i++){

			a = (float) desertX / (float) (2f*range) * (float) i;
			pX = (int) a;
			dX = a - pX;

			for(int j=0; j < 2*range-1; j++){

				b = (float) desertZ / (float) (2f*range) * (float) j;
				pZ = (int) b;
				dZ = b - pZ;

				//interpolate height values

				this.terra.add(x-range+i, z-range+j, 0, amp * 
						(Util.iPol
								(Util.iPol(
										desertMap2[pX % desertX][pZ % desertZ], 
										desertMap2[pX % desertX][(pZ+1)%desertZ],
										dZ),
										Util.iPol(
												desertMap2[(pX+1)%desertX][pZ % desertZ], 
												desertMap2[(pX+1)%desertX][(pZ+1)%desertZ],
												dZ),
												dX)));
			}

		}
		//		makeNoise(range, x, z, 2, 0.05f, 5);
		System.out.println("Done");
	}


	
	/**
	 * puts a lake with scale depth at point xz
	 * 
	 * @param scale
	 * @param depth
	 * @param x
	 * @param z
	 */
	private void putLake(float scale, float depth, int x, int z){
		
		System.out.println("Putting lake at "+x+" / "+z);
		// relict
		float rotation = 0;

		int riverX = gauss17.length-1;
		int riverZ = gauss17[0].length-1;
		int pX, pZ;
		float dX, dZ;
		float cosa = (float)Math.cos(rotation);
		float sina = (float)Math.sin(rotation);
		int range = (int)(scale * Math.ceil(Math.sqrt(riverX * riverX + riverZ * riverZ)));

		Matrix3f transformation = new Matrix3f();
		transformation.m20 = x;
		transformation.m21 = z;
		transformation.m00 = 0.5f * scale * riverX * cosa;
		transformation.m01 = 0.5f * scale * riverX * sina;
		transformation.m10 = 0.5f * scale * riverZ * -sina;
		transformation.m11 = 0.5f * scale * riverZ * cosa;
		transformation.invert();
		Vector3f vec = new Vector3f();
		vec.z = 1.0f;
		float riverVal;

		for(int i = x - range/2; i < x + range/2; i++){

			for(int j = z - range/2; j < z + range/2; j++){

				// set material to "river" if depth at that point is close to lvl

				if (i>0 && i<maxX && j>0 && j<maxZ) {
					vec.x = i;
					vec.y = j;
					Matrix3f.transform(transformation, vec, vec);
					if (vec.x < -1 || vec.x >= 1 || vec.y < -1
							|| vec.y >= 1)
						continue;
					vec.x = 0.5f + 0.5f * vec.x;
					vec.y = 0.5f + 0.5f * vec.y;
					dX = (float) riverX * vec.x;
					pX = (int) Math.floor(dX);
					dX -= pX;
					dZ = (float) riverZ * vec.y;
					pZ = (int) Math.floor(dZ);
					dZ -= pZ;
					//					interpolate height values
					riverVal = (Util.iPol(
							Util.iPol(this.gauss17[pX][pZ],
									  this.gauss17[pX][(pZ + 1) % riverZ],
									  dZ),
							Util.iPol(this.gauss17[(pX + 1) % riverX][pZ],
									  this.gauss17[(pX + 1) % riverX][(pZ + 1) % riverZ],
									  dZ),
							dX));
					
					this.terra.set(i, j, 0, Util.iPol(this.terra.get(i, j, 0), depth, riverVal));
					
					//set materials for lake
					if (riverVal > 0.20f * (float) scale / 20f) {
						if (this.terra.get(i, j, 0) < (depth + 0.8f)) {
							this.terra.set(i, j, 4, 2);
						}	
					}
						//set materials for earth around lake
					if (((riverVal + ((random.nextFloat()))/scale) > 0.1f ) 
						  && this.terra.get(i, j, 4) !=2) {
						this.terra.set(i, j, 4, 4);								
							
						
					}
				}
			}
		}		
		System.out.println("Done");
	}



	/**
	 * First, a "riverflow" is computed and put in an array, where the first dimension size is the length of the river,
	 * and the second dimension is 2, one for each coordinate. Fluctuation is currently changed at /5 of river length.
	 * Mountains can either make a river disappear or rearrange the riverflow.
	 * 
	 * @param scale should not exceed 40
	 * @param x starting point x
	 * @param z starting point z
	 * @param dstX destination  point x
	 * @param dstZ destination  point z
	 * @param depth how deep the river bed will be 
	 */
	private void putRiver(float scale, int x, int z, int dstX, int dstZ, float depth){
		System.out.println("Putting river from "+x+" / "+z+" to "+dstX+" / "+dstZ);


		//int[][] riverFlow =  new int[(int) Math.round(3.14159 * Math.sqrt((x-dstX)*(x-dstX)+(z-dstZ)*(z-dstZ)))][2];


		//TODO get a riverflows (aka making sexy curves) in soviet russia, river flows in you!
		float negFreq = 1*scale; // depends in some way on depth and scale
		int randRad = (int) (100*Math.sqrt(Math.sqrt(scale))); // defines the weight of randomness. TotalRand = [-1, 1] * radRad
		float dist = (float) Math.sqrt((x-dstX)*(x-dstX)+(z-dstZ)*(z-dstZ));

		float deltaX = Math.abs(x - dstX);
		float deltaZ = Math.abs(z - dstZ);
		float[][] bezierPts = new float [(int) Math.ceil(Math.sqrt(dist)*3f)][2];

		// Fill the array with suitable bezier Points
		bezierPts[0][0] = x;
		bezierPts[0][1] = z;
		bezierPts[bezierPts.length-1][0] = dstX;
		bezierPts[bezierPts.length-1][1] = dstZ;
		for(int i=2; i<=bezierPts.length-1; i++){
			bezierPts[i-1][0] = ((2*random.nextFloat()-1) * randRad) + x + ((float)i/bezierPts.length * deltaX);

			bezierPts[i-1][1] = ((2*random.nextFloat()-1) * randRad) + z + ((float)i/bezierPts.length * deltaZ); 
		}

		//Draw Lines
		float[][][] interPols = new float[bezierPts.length-1][bezierPts.length-1][2];
		int iPolIndex;
		for(float var = 0; var<1.0001; var+=(negFreq/dist)){
			iPolIndex = 0;
			for(int linePos = 0; linePos< interPols.length; linePos++){
				interPols[iPolIndex][linePos][0] = Util.iPol(bezierPts[linePos][0], bezierPts[linePos+1][0], var);
				interPols[iPolIndex][linePos][1] = Util.iPol(bezierPts[linePos][1], bezierPts[linePos+1][1], var);			 
			}
			for(iPolIndex = 1; iPolIndex<bezierPts.length-1; iPolIndex++){
				for(int linePos = 0; linePos< interPols.length-iPolIndex; linePos++){
					interPols[iPolIndex][linePos][0] = Util.iPol(interPols[iPolIndex-1][linePos][0], interPols[iPolIndex-1][linePos+1][0], var);
					interPols[iPolIndex][linePos][1] = Util.iPol(interPols[iPolIndex-1][linePos][1], interPols[iPolIndex-1][linePos+1][1], var);			 
				}
			}

			putLake(scale, depth, (int)Math.round(interPols[interPols.length-1][0][0]), (int)Math.round(interPols[interPols.length-1][0][1]));

		}
		//		int[][] riverFlow = new int[riverFlow.size()/2][2];
		//
		//		
		//		float fluct = 1, formerFluct = 1, rel = -0.000001f;
		//		
		//		for(int i = 0; i<riverFlow.length; i++){
		//			if(i > rel){
		//				rel += ((float) (riverFlow.length) / 5f);
		//				formerFluct = fluct;
		//				fluct *= (random.nextFloat()/3f + 0.83333334f);
		//			}
		//			System.out.println();
		////			this.putLake(10 * Util.iPol(fluct, formerFluct, rel/5f), depth, riverFlow[i][0], riverFlow[i][1]);	
		//			this.putLake(10, depth, riverFlow[i][0], riverFlow[i][1]);			
		//			
		//		}
		
		System.out.println("Done");
	}




	//TODO putSea
	private void putSea(float[][]bioForm, int x, int z, int range){

	}

	/**
	 * Flattens at a certain point within a range and also sets material in this range.
	 * 
	 * @param x position of flattening centre
	 * @param z position of flattening centre
	 * @param lvl the height-level to which you want to flatten
	 * @param range the range of area in which you want to flatten
	 * @param type the type of flattening process used [1 for mountain1, 2 for mountain2]
	 * @param the material which will be found in the middle of the area
	 */
	private void flatten(int x, int z, int range, float lvl,  int type, int material){
		System.out.println("flatten at "+x+" "+z+" with range "+range);

		float xf, zf, dX, dZ, flatVal;
		int xi, zi;
		float[][] flatVals;
		if(type == 1){
			flatVals = this.mountainMap3;
		}else{
			flatVals = this.mountainMap2;
		}

		for(int i=0; i<2*range; i++ ){

			xf = 32f / (2f * range) * (float) i;
			xi = (int) xf;
			dX = xf-xi;

			for(int j=0; j<2*range; j++ ){

				zf =  32f / (2f * range) * (float) j;
				zi = (int) zf;
				dZ = zf-zi;

				flatVal = 0.95f * (Util.iPol
						(Util.iPol(
								flatVals[xi % 32][zi % 32], 
								flatVals[xi % 32][(zi+1) % 32],
								dZ),
								Util.iPol(
										flatVals[(xi+1) % 32][zi % 32], 
										flatVals[(xi+1) % 32][(zi+1) % 32],
										dZ),
										dX));
				this.terra.set(x-range+i, z-range+j, 0, Util.iPol(this.terra.get(x-range+i, z-range+j, 0), lvl, flatVal));

				if((flatVal + 0.85 + (random.nextFloat()/10f))>1f){
					this.terra.set(x-range+i, z-range+j, 4, material);
				}
			}

		}
		System.out.println("Done");
	}

	/**
	 * Flats terra dependent on material.
	 * @param range
	 */
	private void flattenAll(int range){
		
		System.out.println("Flattening whole map");
		float flatVal;
		float lvl = -50,  contextWeight = 0; 

		for(int i=0;i<maxX;i++){
			
			if(i%100 == 0){
				System.out.println(i + " / "+ maxX);
			}
			for(int j=0;j<maxZ;j++){
				
  
				
				lvl = -50;
				switch(Math.round(this.terra.get(i, j, 4))){
				case 1: lvl = -0.15f; contextWeight = 0.6f;break;
				case 3: lvl = 1.5f; contextWeight = 1f;break;
				case 4: lvl = 2.5f; contextWeight = 0.7f;break;
				case 5: lvl = 3.5f; contextWeight = 0.6f;break;
				case 6: lvl = 4f; contextWeight = 0.5f;break;
				//				case 9:lvl = 7.5f;scale = 0.8f;break;
				//				case 10:lvl = 9f;scale = 0.8f;break;
				}
				if(lvl > -10){
					flatVal = getDistToEdge(i, j, range)/(float)range;
					this.terra.set(i, j, 0, Util.iPol(this.terra.get(i, j, 0), lvl * SCALE , flatVal * contextWeight));
					
	            }
				
			}
		}
		System.out.println("Done");
	}

	/**
	 * Gives distance to nearest not identical material.
	 * 
	 * @param pX the point
	 * @param pZ the point
	 * @param range the range around the point
	 * @return an integer which gives the layer where another material was found, it is between 0 and range.
	 */
	private int getDistToEdge(int pX, int pZ, int range){	
		float rangeScale = 1;
		int material = Math.round(this.terra.get(pX, pZ, 4));
		switch(material){
		case 1: rangeScale = 0.9f;break;
		case 3: rangeScale = 0.5f;break;
		case 4: rangeScale = 0.7f;break;
		case 5: rangeScale = 0.9f;break;
		case 6: rangeScale = 1f;break;
		case 9: rangeScale = 0.8f;break;
		case 10: rangeScale = 0.8f;break;
		}
		range = Math.round(rangeScale * range);
		for(int i=1; i<range; i++){

			for(int idx = -i; idx < i; idx++){
				if((pZ-i)>=0 && (pZ+i)<maxZ && (pX-i)>=0 && (pX+i)<maxX){

					if(Math.round(this.terra.get(pX+idx, pZ+i, 4)) != material || 
							Math.round(this.terra.get(pX+idx, pZ-i, 4)) != material ||
							Math.round(this.terra.get(pX+i, pZ+idx, 4)) != material ||
							Math.round(this.terra.get(pX-i, pZ+idx, 4)) != material){				
						return i;
					}
				}
			}
		}

		return range;
	}

	/**
	 * smoothing of terra
	 */
	private void smooth(){
		
		System.out.println("Smoothing");
		
		for(int x=0; x<maxX; x++){
			for(int z=0; z<maxZ; z++){


				Util.smooth(this.terra.getBlock(), x, z);
			}
		}
		System.out.println("Done");
		
	}

	/**
	 * Method to give terrain more structure or noise within x and z boundaries.
	 * @param range
	 * @param x
	 * @param z
	 * @param freq
	 * @param amp
	 * @param rep
	 */
	private void makeNoise(int range, int x, int z, float freq, float amp, int rep){


		int noiseX = this.noiseMap.length;
		int noiseZ = this.noiseMap[0].length;
		int pX, pZ;
		float a, b;
		float dX, dZ;

		for(int k=0; k<rep; k++){
			freq *= (2+(random.nextFloat()/5f-0.2f));
			amp /= (2+(random.nextFloat()/5f-0.2f));
			for(int i = 0; i < 2*range-1; i++){

				a = (float) noiseX / (float) (2f*range)* freq * (float) i;
				pX = (int) a;
				dX = a - pX;

				for(int j=0; j < 2*range-1; j++){

					b = (float) noiseZ / (float) (2f*range) * freq * (float) j;
					pZ = (int) b;
					dZ = b - pZ;

					//interpolate height values
					this.terra.add(x-range+i, z-range+j, 0, amp * 
							(Util.iPol
									(Util.iPol(
											this.noiseMap[pX % noiseX][pZ % noiseZ], 
											this.noiseMap[pX % noiseX][(pZ+1) % noiseZ],
											dZ),
											Util.iPol(
													this.noiseMap[(pX+1) % noiseX][pZ % noiseZ], 
													this.noiseMap[(pX+1) % noiseX][(pZ+1) % noiseZ],
													dZ),
													dX)));
				}
			}

		}		
	}


	/**
	 * 
	 * @param surfaceWrink: Something from 0 to 25 to get some noise
	 * @param macroStructure: Defines Macrostructure as follows:
	 * @param 1 -> Noisy
	 * @param 2 -> Desert hills
	 * @param 3 -> Mountain 1
	 * @param 4 -> Mountain 2
	 * @param 5 -> Mountain 3
	 */
	private void terraform(int surfaceWrink, int macroStructure, float scale){
		
		this.SCALE = scale;
		System.out.println("Terraforming");
		

		switch(macroStructure){
		case 1:	Util.biLinIpol(this.terra.getBlock(), this.noiseMap, 0.5f*SCALE, 10f*SCALE);
		Util.biLinIpol(this.terra.getBlock(), this.noiseMap, 1f*SCALE, 5.05f*SCALE);break;
		case 2: Util.biLinIpol(this.terra.getBlock(), this.desertMap1, 10f*SCALE, 4f*SCALE);break;
		case 3: Util.biLinIpol(this.terra.getBlock(), this.mountainMap1, 8f*SCALE, 20f*SCALE);break;
		case 4: Util.biLinIpol(this.terra.getBlock(), this.mountainMap2, 10f*SCALE, 4f*SCALE);break;
		case 5: Util.biLinIpol(this.terra.getBlock(), this.mountainMap3, 10f*SCALE, 10f*SCALE);break;
		case 6: Util.biLinIpol(this.terra.getBlock(), this.slopeMap, 10f*SCALE, 10f*SCALE);break;
		}



		float freq=1f, amp=0.9f*SCALE;
		for(int i=0; i<surfaceWrink; i++){


			if(i>30) freq = 27f+(random.nextFloat()/2f);

			Util.biLinIpol(this.terra.getBlock(), this.noiseMap, freq, amp);
			freq*=(2+(random.nextFloat()/5f-0.2f));
			amp/=(2+(random.nextFloat()/5f-0.2f));

		}


		this.setMaterialsFromHeight(0,this.maxX,0,this.maxZ);
		
		System.out.println("Done");

	}

	/**
	 * method to place biomes (testing).
	 * 
	 */
	private void testForm(){
		this.putTest(this.diamondMap, 1, 512, 512, 300);
		//		putMountain(map, 2f, 512, 512, 256);
		//	putDesert(1, 512, 512, 300);
		//		this.setMaterialsFromHeight(0,this.maxX,0,this.maxZ);

	}


	private void putTest(float[][]testMap,float amp, int x, int z, int range){

		int testX = testMap.length;
		int testZ = testMap[0].length;
		int pX, pZ;
		float a, b;
		float dX, dZ;

		for(int i=0; i < 2*range-1; i++){

			a = (float) testX / (float) (2f*range) * (float) i;
			pX = (int) a;
			dX = a - pX;

			for(int j=0; j < 2*range-1; j++){

				b = (float) testZ / (float) (2f*range) * (float) j;
				pZ = (int) b;
				dZ = b - pZ;

				//interpolate height values
				this.terra.add(x-range+i, z-range+j, 0, amp * 
						(Util.iPol
								(Util.iPol(
										testMap[pX % testX][pZ % testZ], 
										testMap[pX % testX][(pZ+1)%testZ],
										dZ),
										Util.iPol(
												testMap[(pX+1)%testX][pZ % testZ], 
												testMap[(pX+1)%testX][(pZ+1)%testZ],
												dZ),
												dX)));

			}

		}
		setMaterialsFromHeight(x-range, x+range, z-range, z+range);
		//		makeNoise(range, x, z, 2, 0.05f, 5);
	}


}
