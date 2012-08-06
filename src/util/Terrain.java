package util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;

/**
 * 
 * Terrain data in float[][] with noisemaps, normalMap, biomeMap and materialMap
 * can generate and smooth a terrain
 * @author ARECKNAG, FMAESCHIG
 *
 */
public class Terrain {

	private float[][][] terra;
	private float[][] noiseMap = new float [32][32];
	private float[][] mountainMap1 = new float [32][32];
	private float[][] mountainMap2 = new float [32][32];
	private float[][] mountainMap3 = new float [32][32];
	private float[][] riverMap = new float [32][32];
	private float[][] desertMap = new float [32][32];
	private float[][] seaMap = new float [32][32];
	private float[][] ravineMap = new float [32][32];
	private float[][] slopeMap = new float [32][32];
	private float[][] biomeMap;
	private Random random;
	private int maxX, maxZ;
	private final int MAXHEIGHT;
	private int vertexInfoCount = 5;
	private static final int UNKNOWN = 0;
	private static final int SEA = 1;
	private static final int RIVER = 2;
	private static final int HILLS = 3;
	private static final int DESERT = 4;
	private static final int OASIS = 5;
	private static final int LOWLANDS = 6;
	private static final int MOUNTAIN = 7;

	/**
	 * 
	 * @param initialHeight
	 * @param maxX
	 * @param maxZ 
	 * @param seed
	 */
	public Terrain(float initialHeight, int maxX, int maxZ, int seed){

		this.MAXHEIGHT = 20;
		this.terra = new float[maxX][maxZ][vertexInfoCount];
		this.biomeMap = new float[maxX][maxZ];
		this.random = new Random(seed);

		this.maxX = maxX;
		this.maxZ= maxZ; 

		// Gen flat terra, with no norms and material info
		for(int x=0; x < maxX; ++x) {
			for(int z=0; z < maxZ; ++z) {
				this.terra[x][z][0] = initialHeight;
			}
		} 		

		// Gen Noisemap       

		for(int x=0; x < 32; ++x) {
			for(int z=0; z < 32; ++z) {
				this.noiseMap[x][z] = this.random.nextFloat()*2-1;
			}
		}

		//Gen MountainMap
		for(int x=0; x < 32; ++x) {
			for(int z=0; z < 32; ++z) {

				this.mountainMap1[x][z] = ((16-(Math.abs(-(x-16))))/16f*(16-(Math.abs(-(z-16))))/16f);
				this.mountainMap2[x][z] = (float) (((Math.cos(((x-16)/12.9f)*((x-16)/12.9f)) *
													 Math.cos(((z-16)/12.9f)*((z-16)/12.9f)))));
				if(x==15) System.out.println(mountainMap2[x][z]);
				this.mountainMap3[x][z] = (float) (((Math.cos(((x-16)/12.9f)) *
													 Math.cos(((z-16)/12.9f)))));
				this.slopeMap[x][z] = (float) (-0.05f + Math.sin((float)x / 64 *Math.PI)/2f);
				//	Mt.slope 	(float) (((16-(Math.abs(-(x-16))))/16f*(16-(Math.abs(-(z-16))))/16f)* (1f+(3f* (0.05f*(((this.random.nextFloat()*2-1)/200)+ (((Math.cos((x/6f)*Math.PI))))+(((Math.cos((z/12f)*Math.PI)))))))));

									
			}
		}

		//Gen RiverMap
		for(int x=0; x < 32; ++x) {
			for(int z=0; z < 32; ++z) {
				this.riverMap[x][z] = (float) Math.sin(((z-16)/13f)*((z-16)/13f));
			}
		}

		//Gen DesertMap
		for(int x=0; x < 32; ++x) {
			for(int z=0; z < 32; ++z) {
				this.desertMap[x][z] = (float) (((16-(Math.abs(-(x-16))))/16f*(16-(Math.abs(-(z-16))))/16f)* (((0.05f*(((this.random.nextFloat()*2-1)/200)+ (((Math.cos((x/6f)*Math.PI))))+(((Math.cos((z/12f)*Math.PI)))))))));
			}
		}

		//		for(int x=0; x < 32; ++x) {
		//			for(int z=0; z < 32; ++z) {
		//				this.noiseMap[x][z] = (float) ((this.random.nextFloat()*2-1)-Math.sin(z/32*6.282f)*3+Math.cos(x/32*6.282f)*0.25);
		//			}
		//		}

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



	private void checkNormals(){
		//TODO get rid of floatbuffer and write norms directly into terra
		//Gen normalMap
		int vertexSize = 6;   	    	
		FloatBuffer vertices = BufferUtils.createFloatBuffer(vertexSize*this.maxX*this.maxZ);

		// Gen Vbuffer
		for(int z=0; z < this.maxZ; ++z) {
			for(int x=0; x < this.maxX; ++x) {
				vertices.put(1e-2f * (float)x);
				vertices.put(terra[x][z][0]);
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

				this.terra[x-1][z][1] = vertices.get();
				this.terra[x-1][z][2] = vertices.get();
				this.terra[x-1][z][3] = vertices.get();	        		
			}        	
		}

	}

	/**
	 * smoothing of terra
	 */
	public void smooth(){
		for(int x=0; x<maxX; x++){
			for(int z=0; z<maxZ; z++){


				Util.smooth(this.terra, x, z);
			}
		}
		//checkNormals();
	}

	/**
	 * Function for transforming the terrain. Calls Material- and Biome- changing functions by default
	 * 
	 * @param surfaceWrink: The level of detail
	 */
	public void terraform(int surfaceWrink){

		float amp=1f, freq=0.05f;
		for(int i=1; i<=surfaceWrink; i++){

			if(i==3) freq = 0.5f;
			if(i>30) freq = 27f+(random.nextFloat()/2f);

			Util.biLinIpol(this.terra, this.noiseMap, freq, amp);
			freq*=(2+(random.nextFloat()/5f-0.2f));
			amp/=(2+(random.nextFloat()/5f-0.2f));

		}


		this.setMaterialsFromHeight(0,this.maxX,0,this.maxZ);

	}


	//	public void updateMaterialFromHeight(int range){
	//
	//		// Fill material map
	//		float[][] hMM = new float[terra.length+range][terra[0].length+range];
	//		for(int x=0; x<terra.length; x++){
	//			for(int z=0; z<terra[0].length; z++){
	//				if(this.heightMap[x][z]<0f){
	//					hMM[x+range][z+range] = 1;
	//					this.terra[x][z][4] = 1;
	//				}
	//				else{
	//					if(this.heightMap[x][z]<0.5f){
	//						hMM[x+range][z+range] = 2;
	//						this.terra[x][z][4] = 2;
	//					}
	//					else{
	//						if(this.heightMap[x][z]<2f){
	//							hMM[x+range][z+range] = 3;
	//							this.terra[x][z][4] = 3;
	//						}
	//						else{	
	//							hMM[x+range][z+range] = 4;
	//							this.terra[x][z][4] = 4;
	//						}
	//					}
	//				}
	//			}
	//		}

	// Fill help map corners
	//		
	//		int helpX = terra.length;
	//		int helpZ = terra[0].length;
	//		for(int x=0; x<helpX; x++){
	//			if(x<range || x>helpX-range){
	//				for(int z=0; z<helpZ; z++ ){
	//					if(z>=range){
	//
	//					}else{
	//						hMM[x][z] = terra[x][range][4];						
	//					}
	//				}
	//			}else{
	//				for(int z=0; z<range; z++){
	//
	//				}
	//				for(int z=0; z<range; z++){
	//
	//				}
	//
	//			}
	//		}


	// Check for biomes, one for each material
	//checkBiome(range);


	/**
	 * 
	 */
	private void setBiomesFromMaterial(){

		int range = 5;		// now checks in a 10x10 square around the target
		int material;
		int compareMaterial;
		boolean isBiome;
		for(int x=0; x<maxX; x++){
			for(int z=0; z<maxZ; z++){
				material = Math.round(terra[x][z][4]);
				isBiome = true;

				for(int i=-range; i<range; i++){					
					for(int j=-range; j<range; j++){
						if(x+i<0 || j+z<0 || x+i>=maxX || z+j>=maxZ){
							compareMaterial = 0;
						}else{
							compareMaterial = Math.round(terra[x+i][z+j][4]);
						}
						if(material != compareMaterial)
							isBiome = false;					
					}
				}

				if(isBiome) biomeMap[x][z] = material;
			}
		}
	}


	private void setMaterialsFromHeight(int minX, int maxX, int minZ, int maxZ){	
		float scale = 0.1f;
		// Gen Materials from height
		for(int x=minX; x<maxX; x++){
			for(int z=minZ; z<maxZ; z++){

				if(this.terra[x][z][0]<0 *scale){
					this.terra[x][z][4] = 1;							//1 = sea
				}
				else{													//2 = river(only biome)
					if(this.terra[x][z][0]<1.5f *scale){
						this.terra[x][z][4] = 3;						//3 = beach
					}
					else{
						if(this.terra[x][z][0]<2.5f *scale){
							this.terra[x][z][4] = 4;					//4 = earth
						}
						else{	
							if(this.terra[x][z][0]<3.5f *scale){
								this.terra[x][z][4] = 5;				//5 = light grass
							}
							else{										
								if(this.terra[x][z][0]<4f *scale){				
									this.terra[x][z][4] = 6;			//6 = dark grass
								}else{										
									if(this.terra[x][z][0]<4.5f *scale){
										this.terra[x][z][4] = 7;		//7 = stone
									}
									else{	
										if(this.terra[x][z][0]<6.5f *scale){
											this.terra[x][z][4] = 8;		//8 = rock
										}
										else{	
											if(this.terra[x][z][0]<7.5f *scale){
												this.terra[x][z][4] = 9;	//9 = light snow
											}
											else{	

												this.terra[x][z][4] = 10;	//10 = heavy snow

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

	}



	/**
	 * 
	 * @return Terra's vertices info
	 */
	public float[][][] getTerra(){		
		return this.terra;		
	}

	/**
	 * 
	 * @return Terra's heightMap
	 */
	public float[][] getHeightMap(){

		float[][] heightMap = new float [maxX][maxZ];

		for(int x=0; x<maxX; x++){
			for(int z=0; z<maxZ; z++){
				heightMap[x][z] = terra[x][z][0];
			}
		}
		return heightMap;
	}


	/**
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	public float[] getInfo(int x, int z){

		if(x >= 0 && x < this.maxX && z >=0 && z < this.maxZ){

			return terra[x][z];
		}
		// ueberarbeiten, wer will ;-)
		else{ 
			System.err.println("error");
			return null;	 
		}
	}

	/**
	 * 
	 * @param x
	 * @param z
	 * @param vP
	 */
	public void setInfo(int x, int z, float[] vP)
	{
		if(x >= 0 && x < this.maxX && z >=0 && z < this.maxZ)
		{
			terra[x][z] = vP;
		}
		else System.err.println("error");
	}

	public int getMAXHEIGHT() {
		return MAXHEIGHT;
	}

	public int getXDim(){
		return maxX; 	
	}

	public int getZDim(){	
		return maxZ; 
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
	public void putMountain(float[][]bioForm, float amp, int x, int z, int range){

		int bioX = bioForm.length;
		int bioZ = bioForm[0].length;
		int pX, pZ;
		float a, b;
		float dX, dZ;

		for(int i=0; i < 2*range-1; i++){

			a = (float) bioX / (float) (2f*range) * (float) i;
			pX = (int) a;
			dX = a - pX;

			for(int j=0; j < 2*range-1; j++){

				b = (float) bioZ / (float) (2f*range) * (float) j;
				pZ = (int) b;
				dZ = b - pZ;

				//interpolate height values
				biomeMap[x-range+i][z-range+j] = MOUNTAIN;
				terra[x-range+i][z-range+j][0] += amp * 
						(Util.iPol
								(Util.iPol(
										bioForm[pX % bioX][pZ % bioZ], 
										bioForm[pX % bioX][(pZ+1)%bioZ],
										dZ),
										Util.iPol(
												bioForm[(pX+1)%bioX][pZ % bioZ], 
												bioForm[(pX+1)%bioX][(pZ+1)%bioZ],
												dZ),
												dX));

			}

		}
		setMaterialsFromHeight(x-range, x+range, z-range, z+range);
		makeNoise(range, x, z, 2, 0.05f, 5);
	}
	
	//TODO putDesert
	public void putDesert(float[][]bioForm, float amp, int x, int z, int range){

		int bioX = bioForm.length;
		int bioZ = bioForm[0].length;
		int pX, pZ;
		float a, b;
		float dX, dZ;
		
		//TODO Planieren
		
		for(int i=0; i < 2*range-1; i++){

			a = (float) bioX / (float) (2f*range) * (float) i;
			pX = (int) a;
			dX = a - pX;

			for(int j=0; j < 2*range-1; j++){

				b = (float) bioZ / (float) (2f*range) * (float) j;
				pZ = (int) b;
				dZ = b - pZ;

				//interpolate height values
				biomeMap[x-range+i][z-range+j] = DESERT;
				terra[x-range+i][z-range+j][0] += amp * 
						(Util.iPol
								(Util.iPol(
										bioForm[pX % bioX][pZ % bioZ], 
										bioForm[pX % bioX][(pZ+1)%bioZ],
										dZ),
										Util.iPol(
												bioForm[(pX+1)%bioX][pZ % bioZ], 
												bioForm[(pX+1)%bioX][(pZ+1)%bioZ],
												dZ),
												dX));

			}

		}
		//TODO Materials from Biome
		
		//TODO Noise for sand
//		makeNoise(range, x, z, 2, 0.05f, 5);
	}
	
	//TODO putRiver
	public void putRiver(float[][]bioForm, int x, int z, int dstX, int dstZ, int width){
		
	}
	
	//TODO putSea
	public void putSea(float[][]bioForm, int x, int z, int range){
		
	}
	
	/**
	 * 
	 * @param x position of flattening centre
	 * @param z position of flattening centre
	 * @param lvl the height-level to which you want to flatten
	 * @param range the range of area in which you want to flatten
	 * @param amp the grade of flattening to the lvl value [0,1]
	 * @param type the type of flattening process used [1 for mountain1, 2 for mountain2]
	 */
	private void flatten(int x, int z, int range, float amp, int type){
		
		float lvl = 1;
//		switch(Math.round(terra[x][z][4])){
//		case 1: lvl = -3;break;
//		case 2: lvl = -1;break;
//		case 3: lvl = 1;break;
//		case 4: lvl = 2;break;
//		case 5: lvl = 2.5f;break;
//		case 6: lvl = 3;break;
//		case 7: lvl = 5;break;
//		case 8: lvl = 5.5f;break;
//		case 9: lvl = 6;break;
//		case 10: lvl = 7;break;
//		
//		}
		float xf, zf, dX, dZ, flatVal;
		int xi, zi;
		amp *= 0.9;
		float[][] flatVals;
		if(type == 1){
			flatVals = this.mountainMap1;
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
				
				flatVal = amp * (Util.iPol
									(Util.iPol(
											flatVals[xi % 32][zi % 32], 
											flatVals[xi % 32][(zi+1) % 32],
											dZ),
									 Util.iPol(
											 flatVals[(xi+1) % 32][zi % 32], 
											 flatVals[(xi+1) % 32][(zi+1) % 32],
											 dZ),
									 dX));
//				System.out.println(flatVal);
				terra[x-range+i][z-range+j][0] = Util.iPol(terra[x-range+i][z-range+j][0], lvl, flatVal);
			}
	
		}

	}

	
	private void flattenAllBiomes(int range){
		float flatVal;
		float lvl = -50, scale = 0, delta;
		int idx;
		for(int i=0;i<maxX;i++){
			for(int j=0;j<maxZ;j++){
				lvl = -50;
				switch(Math.round(terra[i][j][4])){
				case 3: lvl = 0.15f;scale = 0.8f;break;
				case 4: lvl = 0.25f;scale = 0.7f;break;
				case 5: lvl = 0.35f;scale = 0.6f;break;
				case 6: lvl = 0.4f;scale = 0.5f;break;
				case 9:lvl = 0.65f;scale = 0.8f;break;
				case 10:lvl = 0.7f;scale = 0.8f;break;
				}
				if(lvl > -1){
					
//					delta = (getDistToEdge(i, j, range)/(float)range)*15;
//					idx = (int) delta;
//					delta -= idx;					
//					flatVal = Util.iPol(this.mountainMap2[15][idx], this.mountainMap2[15][idx+1], delta);
					
					flatVal = getDistToEdge(i, j, range)/(float)range;

					terra[i][j][0] = Util.iPol(terra[i][j][0], lvl, flatVal *scale);
				}
			}
		}
	}
		
	/**
	 * 
	 * @param pX the point
	 * @param pZ the point
	 * @param range the range around the point
	 * @return an integer which gives the layer where another material was found, it is between 0 and range.
	 */
	private int getDistToEdge(int pX, int pZ, int range){	
		float scale = 1;
		int material = Math.round(terra[pX][pZ][4]);
		switch(material){
		case 3: scale = 0.5f;break;
		case 4: scale = 0.7f;break;
		case 5: scale = 0.9f;break;
		case 6: scale = 1f;break;
		case 9: scale = 0.8f;break;
		case 10: scale = 0.8f;break;
		}
		range = Math.round(scale * range);
		for(int i=1; i<range; i++){
			
			for(int idx = -i; idx < i; idx++){
				if((pZ-i)>=0 && (pZ+i)<maxZ && (pX-i)>=0 && (pX+i)<maxX){
					
					if(Math.round(terra[pX+idx][pZ+i][4]) != material || 
					   Math.round(terra[pX+idx][pZ-i][4]) != material ||
					   Math.round(terra[pX+i][pZ+idx][4]) != material ||
					   Math.round(terra[pX-i][pZ+idx][4]) != material){				
						return i;
					}
				}
			}
			
//			for(int x = pX-i; x< pX+i; x++){	
//				if((pZ-i)>=0 && (pZ+i)<maxZ &&
//				      (x)>=0 && (x)<maxX){
//					if(Math.round(terra[x][pZ+i][4]) != material){
//						return i;
//					}
//				}
//			}
//			for(int x = pX-i; x< pX+i; x++){
//				if((pZ-i)>=0 && (pZ+i)<maxZ &&
//				      (x)>=0 && (x)<maxX){
//					if(Math.round(terra[x][pZ-i][4]) != material){
//						return i;
//					}
//				}
//			}
//		
//			for(int z = pZ-i; z< pZ+i; z++){	
//				if( (pX-i)>=0 && (pX+i)<maxX &&
//					   (z)>=0 && (z)<maxZ){
//					if(Math.round(terra[pX+i][z][4]) != material){
//						return i;
//					}
//				}
//			}
//			for(int z = pZ-i; z< pZ+i; z++){	
//				if((pX-i)>=0 && (pX+i)<maxX &&
//				      (z)>=0 && (z)<maxZ){
//					if(Math.round(terra[pX-i][z][4]) != material){
//						return i;
//					}
//				}
//			}
		}
		
		return range;
	}

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
					terra[x-range+i][z-range+j][0] += amp * 
							(Util.iPol
									(Util.iPol(
											this.noiseMap[pX % noiseX][pZ % noiseZ], 
											this.noiseMap[pX % noiseX][(pZ+1) % noiseZ],
											dZ),
									 Util.iPol(
											this.noiseMap[(pX+1) % noiseX][pZ % noiseZ], 
											this.noiseMap[(pX+1) % noiseX][(pZ+1) % noiseZ],
											dZ),
									 dX));
				}
			}

		}		
	}

	/**
	 * 
	 * @param form is the surfacewrinkle, more than 30 won't help..
	 */
	public void genTerrain(int form){
//		this.testForm(slopeMap);
		this.terraform(form);	
		//		this.setBiomesFromMaterial();
//		this.testForm(slopeMap);
//		this.flatten(1024, 1024, 1024, 1, 2);
//		flattenForMaterial();
		this.flattenAllBiomes(25);
		this.smooth();
		//		this.checkNormals();
	}
	public void testForm(float[][]map){

		putMountain(map, 0.5f, 512, 512, 512);
//		putDesert(map, 5, 1024,1024,400);
	
		
	}


}
