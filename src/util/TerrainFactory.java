package util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector3f;

import terrain.Terrain;

/**
 * 
 * Terrain data in float[][] with noisemaps, normalMap, biomeMap and materialMap
 * can generate and smooth a terrain
 * @author ARECKNAG, FMAESCHIG
 *
 */
public class TerrainFactory {

	private static float[][] noiseMap = new float [32][32];
	private static float[][] mountainMap1 = new float [32][32];
	private static float[][] mountainMap2 = new float [32][32];
	private static float[][] mountainMap3 = new float [32][32];
	private static float[][] riverMap = new float [32][32];
	private static float[][] desertMap1 = new float [32][32];
	private static float[][] desertMap2 = new float [32][32];
	private static float[][] gauss17 = new float[17][17];
	private static float[][] slopeMap = new float [32][32];
	
	private static Random random;
	private static final float SCALE = 0.1f;

	/**
	 * 
	 * @param initialHeight
	 * @param terra.getSize()
	 * @param terra.getSize() 
	 * @param seed
	 * @return 
	 */
	public static void init(){

		System.out.println("Initialising noiseMaps...");
		random = new Random();

		
		// Gen Noisemap       
		for(int x=0; x < noiseMap.length; ++x) {
			for(int z=0; z < noiseMap[0].length; ++z) {
				noiseMap[x][z] = random.nextFloat()*2-1;
			}
		}
		
		// Gen gaussMap17
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
				gauss17[x][z] = gauss17[0][z] * gauss17[x][0];
			}
		}

		float maxVal = gauss17[8][8];
		for(int x=0; x < gauss17.length; ++x) {
			for(int z=0; z < gauss17[0].length; ++z) {
				gauss17[x][z] /= maxVal;
			}
		}
		
		//Gen MountainMaps
		for(int x=0; x < 32; ++x) {
			for(int z=0; z < 32; ++z) {
				mountainMap1[x][z] = ((16f-(Math.abs(-(x-16f))))/16f*(16f-(Math.abs(-(z-16f))))/16f);
				mountainMap2[x][z] = (float) (((Math.cos(((x-16)/12.9f)*((x-16)/12.9f)) 
											  * Math.cos(((z-16)/12.9f)*((z-16)/12.9f)))));
				mountainMap3[x][z] =  (mountainMap1[x][z] +(mountainMap2[x][z] * mountainMap2[x][z] * mountainMap2[x][z] * mountainMap2[x][z] * mountainMap2[x][z]))/2f;
				slopeMap[x][z] = (float) (-0.05f + Math.sin((float)x / 64 *Math.PI)/2f);
			}
		}
		
		//Gen RiverMap
		for(int x=0; x < 32; ++x) {
			for(int z=0; z < 32; ++z) {
				riverMap[x][z] = (float)(1f- Math.sin(((z-16)/13f)*((z-16)/13f)));
			}
		}

		//Gen DesertMaps
		for(int x=0; x < 32; ++x) {
			for(int z=0; z < 32; ++z) {
				desertMap1[x][z] = (float) (((16-(Math.abs(-(x-16))))/16f*(16-(Math.abs(-(z-16))))/16f)
											* (((0.05f*(((random.nextFloat()*2-1)/200)
											+ (((Math.cos((x/6f)*Math.PI))))
											+(((Math.cos((z/12f)*Math.PI)))))))));		
				desertMap2[x][z] = (float) (((Math.sin(((x/16f)-1)*Math.PI ) 
											* Math.cos(((z*z/480.5f)-1) 
											* Math.sqrt(Math.PI/2d))+2f)/2f
											* mountainMap3[x][z])/1.1180068f);
			}	
		}
		System.out.println("Done");
		System.out.println();

	}
	
	/**
		 * method for testing terrain generation.
		 * @param form
		 */
		public static void genTerrain(Terrain terra, int form){
			
			terraform(terra, form, 1);
			flattenAllBiomes(terra, 10);
			smooth(terra);
			checkNormals(terra);

		}


	/**
	 * check vertices in the whole map for their normals and writes them in position 1 - 3.
	 */ 
	public static void checkNormals(Terrain terra){
		// List of triangles for a grid of vertices, one bigger on each side to account context
		
		System.out.println("Checking normals");
		int vertexSize = 6;
		
		//Gen normalMap
		   	    	
		FloatBuffer vertices = BufferUtils.createFloatBuffer(vertexSize*terra.getSize()*terra.getSize());

		// Gen Vbuffer
		for(int z=0; z < terra.getSize(); ++z) {
			for(int x=0; x < terra.getSize(); ++x) {
				vertices.put(1e-2f * (float)x);
				vertices.put(terra.get(x, z, 0));
				vertices.put(1e-2f * (float)z);
				vertices.put(0);	// norm.x
				vertices.put(0);	// norm.y
				vertices.put(0);	// norm.z
			}                	    
		}
		
		System.out.println("Generating IndexBuffer");
		// Gen IndexBuffer
		IntBuffer indices = BufferUtils.createIntBuffer(3 * 2 * (terra.getSize() - 1) * (terra.getSize() - 1));
		for(int z=0; z < terra.getSize() - 1; ++z) {
			for(int x=0; x < terra.getSize() - 1; ++x) {
				indices.put(z * terra.getSize() + x);
				indices.put((z + 1) * terra.getSize() + x + 1);
				indices.put(z * terra.getSize() + x + 1);

				indices.put(z * terra.getSize() + x);
				indices.put((z + 1) * terra.getSize() + x);
				indices.put((z + 1) * terra.getSize() + x + 1);
			}
		}
		
		System.out.println("Calculating normals");
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
		
		System.out.println("Storing normals");
		
		for(int x=1; x<=terra.getSize(); x++){
			for(int z=0; z<terra.getSize(); z++){

				vertices.position(4+(vertexSize*x*z));

				terra.set(x-1,z,1, vertices.get());
				terra.set(x-1,z,2, vertices.get());
				terra.set(x-1,z,3, vertices.get());	   
				
			}        	
		}
		System.out.println("Done");
		System.out.println();
	}

	/**
	 * smoothing of terra
	 */
	public static void smooth(Terrain terra){
		
		for(int x=0; x<terra.getSize(); x++){
			for(int z=0; z<terra.getSize(); z++){

				Util.smooth(terra, x, z);
				
			}
		}
	}

	/**
	 * Sets the materials of the map dependent on height values.
	 * The parameters are the position on the map (minX - terra.getSize() and minZ - terra.getSize())
	 * @param minX
	 * @param terra.getSize()
	 * @param minZ
	 * @param terra.getSize()
	 */
	public static void setMaterialsFromHeight(Terrain terra, int minX, int maxX, int minZ, int maxZ){	
		
		System.out.println("Setting materials");
		
		float compare;
		
		// Gen Materials from height
		for(int x=minX; x<maxX; x++){
			for(int z=minZ; z<maxZ; z++){
				compare = terra.get(x,z,0);
				if(compare<0 *SCALE){
					terra.set(x, z, 4, 1);							//1 = sea
				}
				else{												//2 = river(only biome)
					compare += (random.nextFloat()/10f);
					if(compare<1.5f *SCALE){
						terra.set(x, z, 4, 3);						//3 = beach
					}
					else{
						if(compare<2.5f *SCALE){
							terra.set(x, z, 4, 4);					//4 = earth
						}
						else{	
							if(compare<3.5f *SCALE){
								terra.set(x, z, 4, 5);				//5 = light grass
							}
							else{										
								if(compare<4f *SCALE){				
									terra.set(x, z, 4, 6);			//6 = dark grass
								}else{										
									if(compare<4.5f *SCALE){
										terra.set(x, z, 4, 7);		//7 = stone
									}
									else{	
										if(compare<6.5f *SCALE){
											terra.set(x, z, 4, 8);		//8 = rock
										}
										else{	
											if(compare<7.5f *SCALE){
												terra.set(x, z, 4, 9);	//9 = light snow
											}
											else{	

												terra.set(x, z, 4, 10);	//10 = heavy snow

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
		System.out.println();
	}

	/**
	 * 
	 * @return Terra's heightMap
	 */
	public static float[][] getHeightMap(Terrain terra){

		float[][] heightMap = new float [terra.getSize()][terra.getSize()];

		for(int x=0; x<terra.getSize(); x++){
			for(int z=0; z<terra.getSize(); z++){
				heightMap[x][z] = terra.get(x, z, 0);
			}
		}
		return heightMap;
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
	public static void putMountain(Terrain terra, float amp, int x, int z, int range){
		
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
				terra.add(x-range+i, z-range+j, 0, amp * 
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
		setMaterialsFromHeight(terra, x-range, x+range, z-range, z+range);
		System.out.println("Done");
		System.out.println();
	}


	/**
	 * for test purposes only
	 * @param terra
	 * @param biome
	 * @param amp
	 * @param x
	 * @param z
	 * @param range
	 */
	public static void putTest(Terrain terra, float[][]biome,float amp, int x, int z, int range){

		int mountainX = biome.length;
		int mountainZ = biome[0].length;
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
				terra.add(x-range+i, z-range+j, 0, amp * 
						(Util.iPol
								(Util.iPol(
										biome[pX % mountainX][pZ % mountainZ], 
										biome[pX % mountainX][(pZ+1)%mountainZ],
										dZ),
										Util.iPol(
												biome[(pX+1)%mountainX][pZ % mountainZ], 
												biome[(pX+1)%mountainX][(pZ+1)%mountainZ],
												dZ),
												dX)));

			}

		}
		setMaterialsFromHeight(terra, x-range, x+range, z-range, z+range);
		//		makeNoise(range, x, z, 2, 0.05f, 5);
	}
	/**
	 * Puts a desert within a certain range.
	 * 
	 * @param amp
	 * @param x
	 * @param z
	 * @param range
	 */
	public static void putDesert(Terrain terra, float amp, int x, int z, int range){
		
		System.out.println("putting Desert");

		int desertX = desertMap2.length;
		int desertZ = desertMap2[0].length;
		int pX, pZ;
		float a, b;
		float dX, dZ;

		flatten(terra, x, z, range, 0.25f, 1, 3);

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
//				biomeMap[x-range+i][z-range+j] = DESERT;

				terra.add(x-range+i, z-range+j, 0, amp * 
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
		System.out.println("Done");
		System.out.println();
	}
	
	/**
	 * puts a lake with scale depth at point xz
	 * 
	 * @param terra 
	 * @param scale
	 * @param depth
	 * @param x
	 * @param z
	 */
	public static void putLake(Terrain terra, float scale, float depth, int x, int z){

		System.out.println("Putting lake at "+x+" / "+z);
		
		// relict
		float rotation = 0;

		int maxX = terra.getSize();
		int maxZ = maxX;
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
							Util.iPol(gauss17[pX][pZ],
									  gauss17[pX][(pZ + 1) % riverZ],
									  dZ),
							Util.iPol(gauss17[(pX + 1) % riverX][pZ],
									  gauss17[(pX + 1) % riverX][(pZ + 1) % riverZ],
									  dZ),
							dX));
					
					terra.set(i, j, 0, Util.iPol(terra.get(i, j, 0), depth, riverVal));
					
					//set materials for lake
					if (riverVal > 0.20f * (float) scale / 20f) {
						if (terra.get(i, j, 0) < (depth + 0.8f)) {
							terra.set(i, j, 4, 2);
						}	
					}
						//set materials for earth around lake
					if (((riverVal + ((random.nextFloat()))/scale) > 0.1f ) 
						  && terra.get(i, j, 4) !=2) {
						terra.set(i, j, 4, 4);								
							
						
					}
				}
			}
		}	
		System.out.println("Done");
		System.out.println();
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
	public static void putRiver(Terrain terra, float scale, int x, int z, int dstX, int dstZ, float depth){
		
		System.out.println("Putting river from "+x+" / "+z+" to "+dstX+" / "+dstZ);
		
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
			putLake(terra, scale, depth, (int)Math.round(interPols[interPols.length-1][0][0]), (int)Math.round(interPols[interPols.length-1][0][1]));
		}
		System.out.println("Done");
		System.out.println();
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
	public static void flatten(Terrain terra, int x, int z, int range, float lvl,  int type, int material){
		
		System.out.println("flatten at "+x+" "+z+" with range "+range);

		float xf, zf, dX, dZ, flatVal;
		int xi, zi;
		float[][] flatVals;
		if(type == 1){
			flatVals = mountainMap3;
		}else{
			flatVals = mountainMap2;
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
				terra.set(x-range+i, z-range+j, 0, Util.iPol(terra.get(x-range+i, z-range+j, 0), lvl, flatVal));

				if((flatVal + 0.85 + (random.nextFloat()/10f))>1f){
					terra.set(x-range+i, z-range+j, 4, material);
				}
			}
		}
		System.out.println("Done");
		System.out.println();
	}

	/**
	 * Flattens terra dependent on material.
	 * @param range
	 */
	public static void flattenAllBiomes(Terrain terra, int range){
		
		System.out.println("Flatten Whole Terrain");
		
		float flatVal;
		float lvl = -50,  contextWeight = 0; 
		//		float delta;
		//		int idx;
		for(int i=0;i<terra.getSize();i++){
			for(int j=0;j<terra.getSize();j++){
				lvl = -50;
				switch(Math.round(terra.get(i, j, 4))){
				case 1: lvl = -0.15f; contextWeight = 0.6f;break;
				case 3: lvl = 1.5f; contextWeight = 1f;break;
				case 4: lvl = 2.5f; contextWeight = 0.7f;break;
				case 5: lvl = 3.5f; contextWeight = 0.6f;break;
				case 6: lvl = 4f; contextWeight = 0.5f;break;
//				case 9:lvl = 7.5f;scale = 0.8f;break;
//				case 10:lvl = 9f;scale = 0.8f;break;
				}
				if(lvl > -10){

//					delta = (getDistToEdge(i, j, range)/(float)range)*15;
//					idx = (int) delta;
//					delta -= idx;					
//					flatVal = Util.iPol(mountainMap2[15][idx], mountainMap2[15][idx+1], delta);

					flatVal = getDistToEdge(terra, i, j, range)/(float)range;

					terra.set(i, j, 0, Util.iPol(terra.get(i, j, 0), lvl * SCALE , flatVal * contextWeight));
				}
			}
		}
		System.out.println("Done");
		System.out.println();
	}

	/**
	 * Gives distance to nearest not identical material.
	 * 
	 * @param pX the point
	 * @param pZ the point
	 * @param range the range around the point
	 * @return an integer which gives the layer where another material was found, it is between 0 and range.
	 */
	public static int getDistToEdge(Terrain terra, int pX, int pZ, int range){	
		float rangeScale = 1;
		int material = Math.round(terra.get(pX, pZ, 4));
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
				if((pZ-i)>=0 && (pZ+i)<terra.getSize() && (pX-i)>=0 && (pX+i)<terra.getSize()){

					if(Math.round(terra.get(pX+idx, pZ+i, 4)) != material || 
							Math.round(terra.get(pX+idx, pZ-i, 4)) != material ||
							Math.round(terra.get(pX+i, pZ+idx, 4)) != material ||
							Math.round(terra.get(pX-i, pZ+idx, 4)) != material){				
						return i;
					}
				}
			}
		}

		return range;
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
	public static void makeNoise(Terrain terra, int range, int x, int z, float freq, float amp, int rep){

		int noiseX = noiseMap.length;
		int noiseZ = noiseMap[0].length;
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
					terra.add(x-range+i, z-range+j, 0, amp * 
							(Util.iPol
									(Util.iPol(
											noiseMap[pX % noiseX][pZ % noiseZ], 
											noiseMap[pX % noiseX][(pZ+1) % noiseZ],
											dZ),
											Util.iPol(
													noiseMap[(pX+1) % noiseX][pZ % noiseZ], 
													noiseMap[(pX+1) % noiseX][(pZ+1) % noiseZ],
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
	public static void terraform(Terrain terra, int surfaceWrink, int macroStructure){

		System.out.print("Terraforming");

		switch(macroStructure){
		case 1:	Util.biLinIpol(terra, noiseMap, 0.05f, 1f);
		Util.biLinIpol(terra, noiseMap, 0.1f, 0.505f);break;
		case 2: Util.biLinIpol(terra, desertMap1, 1f, 0.4f);break;
		case 3: Util.biLinIpol(terra, mountainMap1, 0.8f, 2f);break;
		case 4: Util.biLinIpol(terra, mountainMap2, 1f, 0.4f);break;
		case 5: Util.biLinIpol(terra, mountainMap3, 1f, 1f);break;
		}

		float freq=1f, amp=0.09f;
		
		for(int i=0; i<surfaceWrink; i++){

			if(i>30) freq = 27f+(random.nextFloat()/2f);

			Util.biLinIpol(terra, noiseMap, freq, amp);
			freq*=(2+(random.nextFloat()/5f-0.2f));
			amp/=(2+(random.nextFloat()/5f-0.2f));	
		}
		setMaterialsFromHeight(terra, 0,terra.getSize(),0,terra.getSize());
		System.out.print("Done");
	}

	/**
	 * method to place biomes (testing).
	 * 
	 */
	public static void testForm(Terrain terra){
		putTest(terra, gauss17, 1, 512, 512, 300);
		//		putMountain(map, 2f, 512, 512, 256);
	//	putDesert(1, 512, 512, 300);
		//		this.setMaterialsFromHeight(0,terra.getSize(),0,terra.getSize());

	}


}
