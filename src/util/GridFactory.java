package util;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.BufferUtils;

public class GridFactory {
        
        //test final
        private final static int maxCamHeight = 100;
        private final static int detailsteps = 20;

//     // Festgesetzte Variablen fuer Ausgabe
//    	private final static int maxCamHeight = 100;
//    	private final static int detailsteps = 20;
//    	private final static int standardBlock = 5;
//    	private final static float[] dummy = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
//    	
//    	// bisherige float-Array an den darunter beschriebenen Kamerapositionen
//    	private static float[][][][] result;
//    	private static int camPosX;
//    	private static int camPosHeight;
//    	private static int camPosZ;
//    	
//    	/**
//    	 * minimized Grid and use a default value
//    	 * @param src
//    	 * @param cam
//    	 * @return
//    	 */
//    	public static FloatBuffer[] minGrid(float[][][] src, Camera cam)
//    	{
//    		return minGrid(src,cam,standardBlock);
//    	}
//    	
//    	/**
//    	 * minimized Grid 
//    	 * @param src
//    	 * @param cam
//    	 * @param size number of Blocks (1-10) 
//    	 * @return
//    	 */
//    	public static FloatBuffer[] minGrid(float[][][] src, Camera cam, int size)
//    	{
//    		// false-values
//    		if(src == null){
//    			return new FloatBuffer[0];
//    		}
//    		if(cam == null){
//    			return new FloatBuffer[0];
//    		}
//    		if(size < 0 || size >10){
//    			size = standardBlock;
//    		}
//    		
//    		// die KameraPostion haengt mit dem uebergebenden float[][][] zusammen, heiﬂt:
//    		// hier muss definitv eine ueberarbeitung stattfinden
//          // die Kamera Position ist naemlich nicht an den Stellen wie in der src
//    		camPosX = (int)cam.getCamPos().x;
//    		camPosHeight = (int)cam.getCamPos().y;
//    		camPosZ = (int)cam.getCamPos().z;
//    		
//    		// set result[][][][]
//    		int density = updateHeight(src);
//    		result = new float[size][2*density+1][2*density+1][5];
//    		
//    		for(int i = 0; i < size; i++){
//    			result[i] = getBlock(src,i+1,density);
//    		}
//    		
//    		return getFloatBufferGrid(result);
//    	}
//
//    	/**
//    	 * write a float[][][][] to a FloatBuffer[]
//    	 * @param minGrid
//    	 * @return
//    	 */
//    	private static FloatBuffer[] getFloatBufferGrid(float[][][][] minGrid){
//    		
//    		int length0 = minGrid.length;
//    		int length1 = minGrid[0].length;
//    		int length2 = minGrid[0][0].length;
//    		int length3 = minGrid[0][0][0].length;
//    		
//    		FloatBuffer[] end = new FloatBuffer[length0];
//    		for(int size = 0; size < length0; size++)
//    		{
//    			end[size] = BufferUtils.createFloatBuffer(length1 * length2 * length3);
//    			for(int x = 0; x < length1; x++){
//    				for(int z = 0; z < length2; z++){
//    					end[size].put(minGrid[size][x][z]);
//    				}
//    			}
//    		}
//    		return end;
//    	}
//    	
//    	/**
//    	 * get the needed Values of a block 
//    	 * @param src
//    	 * @param size
//    	 * @param density
//    	 * @return
//    	 */
//    	private static float[][][] getBlock(float[][][] src, int size, int density){
//    		
//    		float[][][] area = new float[2*density+1][2*density+1][5];
//
//    		//values for run through area
//    		int i = 0; //for the x-coordinates
//    		int j = 0; //for the z-coordinates
//    		
//    		// (2^(size-1)) is the number of steps to the next vertex
//    		//ist noch nicht ganz richtig
//    		for(int x = camPosX-density * size-1; x < camPosX + density * size; x += (int) Math.pow(2, size-1)){
//    			j=0;
//    			for(int z = camPosZ-density * size-1; z < camPosZ + density* size; z += (int) Math.pow(2, size-1)){
//    				
//    				// dummy
//    				if(x < 0 || z < 0 || x >= src.length || z >= src[0].length){
//    					area[i][j] = dummy;
//    				}
//    				else{
//    					area[i][j] = src[x][z];
//    				}
//    				j++;
//    			}
//    			i++;
//    		}
//    		return area;
//    	}
//    	
//    	/**
//    	 * calculate the density; use final values and the camera-position
//    	 * @param src
//    	 * @return
//    	 */
//        private static int updateHeight(float[][][] src) 
//        {  	
//        	// Hoehendifferenz
//        	float heightDiff = camPosHeight - src[camPosX][camPosZ][0];
//        	
//        	if(heightDiff < 0 || camPosHeight > maxCamHeight)
//        	{
//        		System.err.println("ATTENTION: Camera out of area!");
//        		return 0;
//        	}
//    	
//        	return ((int)heightDiff - (int)heightDiff % detailsteps) * (-4) / detailsteps + maxCamHeight / detailsteps * 4 + 4;	
//        }
//        
//        
        /**
         * minimizeGrid mit Standardwerten
         * @param src
         * @param cam
         * @return Floatbuffer[]
         */
        public static FloatBuffer[] minimizeGrid(Terrain src, Camera cam)
        {
                return minimizeGrid(src, cam, 5);
        }
        
        /**
         * Liefert ein FloatBuffer-Array mit allen Detail-Bloecken
         * result[0] ist dabei der innerste Block
         * quantity gibt die Anzahl der Bloecke an (1 nur der innerste, 10 liefert 9 weitere)
         * 
         * @param src Karte
         * @param cam Kamera
         * @param size Unterteilung (auf einer Skala von 1 bis 10)
         * @param density Dichte (auf einer Skala von 1 bis 10)
         * @return
         */
        public static FloatBuffer[] minimizeGrid(Terrain src, Camera cam, int size)
        {
                if(size <= 0 || size > 10) 
                { 
                        System.err.println("ERROR: wrong quantity!");
                        size = 5; 
                }
                
                int density = updateHeight(src,cam);
                
//                if(density  <= 0 || density  > 10) 
//                { 
//                        System.err.println("ERROR: wrong quantity!");
//                        density  = 8;
//                }
                
//                density *= 5;
//                density -= density % 4;

                return minimizeGrid(src, (int) cam.getCamPos().x, (int) cam.getCamPos().z, size,
density);
        }
                
        /**
         * Hilfsmethode
         * @param src Karte
         * @param p x-Koordinate der Kameraposition
         * @param q y-Koordinate der Kameraposition
         * @param size Unterteilung (auf einer Skala von 1 bis 10)
         * @return result
         */
        private static FloatBuffer[] minimizeGrid(Terrain src, int p, int q, int size, int
density)
        {
                FloatBuffer[] result = new FloatBuffer[size];
                
                if(p < 0 || q < 0 || p >= src.getXDim() || q >= src.getZDim())
                {
                        System.err.println("ERROR: Camera out of map!");
                        for(int i = 0; i < size; i++)
                        {
                                result[i] = BufferUtils.createFloatBuffer(0);
                        }
                        return result;
                }
                
                List<float[]> vertexList01 = new LinkedList<float[]>();
                
                for(int i = -density/2 ; i <= density/2; i ++)
                {
                        for(int j = -density/2; j <= density/2; j ++)
                        {
                                if(p + i >= 0 && p + i < src.getXDim() && q + j >= 0 && q + j < src.getZDim())
                                {
                                        // tmpFuerTest ist nur fuer Testzwecke!
                                        // ansonsten nur folgendes verwenden!
                                        //vertexList01.add(src.getInfo(p+i, q+j));
                                        
                                        //testblock
                                        float[] Test = new float[7];
                                        Test[0] = p + i; 
                                        Test[1] = src.getInfo(p+i, q+j)[0];
                                        Test[2] = q + j;
                                        Test[3] = src.getInfo(p+i, q+j)[1];
                                        Test[4] = src.getInfo(p+i, q+j)[2];  // wird sp??ter ausgelassen
                                        Test[5] = src.getInfo(p+i, q+j)[3];
                                        Test[6] = src.getInfo(p+i, q+j)[4];
                                        vertexList01.add(Test);
                                        //testblock ende

                                }
                        }
                }
                
                // Fuelle den ersten FloatBuffer mit Vertices aus dem innersten Block
                FloatBuffer fb01 = BufferUtils.createFloatBuffer(7 * vertexList01.size());
                for(float[] fa : vertexList01)
                {
                        fillList(fb01, fa);
                }
                
                result[0] = fb01;
                
                for(int i = 1; i < size; i++)
                {
                        result[i] = getOuterGrids(src, p, q, size - 1, density)[i-1];
                }
                return result;
        }
        
        
        /**
         * 
         * @param src        Urspruengliches Grid 
         * @param p
         * @param q
         * @param size
         * @return
         */
        private static FloatBuffer[] getOuterGrids(Terrain src, int p, int q, int size, int
density)
        {
                FloatBuffer[] result = new FloatBuffer[size];
                
                @SuppressWarnings("unchecked")
                List<float[]>[] help = (LinkedList<float[]>[]) new LinkedList[size];
                
                for(int i = 0; i < size; i++)
                {
                        help[i] = new LinkedList<float[]>();
                }

                int step = 2;
                int count = 0;
                while(count < size && ((p - density)/2 >= 0 || (p + density)/2 < src.getXDim() ||
(q - density)/2 >= 0 || (q + density)/2 < src.getZDim()))
                {        
                        for(int i = -density; i <= density; i += step)
                        {
                                for(int j = -density; j <= density; j += step)
                                {
                                        if(p + i >= 0 && p + i < src.getXDim() && q + j >= 0 && q + j <
src.getZDim())// && 
                                                        //!(i < density / 2 && i > -density / 2 && j < density / 2 && j > -density / 2))
                                        {
                                                // Test ist nur fuer Test.java!
                                                // ansonsten nur folgendes benutzen!
                                                //help[count].add(src.getInfo(p + i, q + j));
                                                
                                                // testblock
                                                float[] Test = new float[7];
                                                Test[0] = p + i; 
                                                Test[1] = src.getInfo(p+i, q+j)[0];
                                                Test[2] = q + j;
                                                Test[3] = src.getInfo(p+i, q+j)[1];
                                                Test[4] = src.getInfo(p+i, q+j)[2];  // wird sp??ter ausgelassen
                                                Test[5] = src.getInfo(p+i, q+j)[3];
                                                Test[6] = src.getInfo(p+i, q+j)[4];
                                                help[count].add(Test);
                                                //testblock ende

                                        }
                                }
                        }
                        density *= 2;
                        step *= 2;
                        count++;
                }                
                
                for(int i = 0; i < size; i++)
                {
                        FloatBuffer fb = BufferUtils.createFloatBuffer(7 * help[i].size());
                        for(float[] vi : help[i])
                        {
                                fillList(fb, vi);
                        }
                        result[i] = fb;
                }
                
                return result;
        }        
        
        /**
         * Hilfsmethode
         * @param fb
         * @param vi
         */
    private static void fillList(FloatBuffer fb, float[] fa)
    {
            for(int i = 0; i < fa.length; i++)
            {
                    // ToDo:  
                    // soll sp??ter y_normal auslassen
                    // <y, nx, nz, mat> soll das Vertex Layout sein
                    fb.put(fa[i]);
            }
                        
    }
    
    /**
     * 
     * @param src
     * @param cam
     * @return
     */
    private static int updateHeight(Terrain src, Camera cam) 
    {          
            float heightDiff = cam.getCamPos().y - src.getInfo((int)cam.getCamPos().x,
(int)cam.getCamPos().z)[0];
            
            if(heightDiff < 0 || cam.getCamPos().y > maxCamHeight)
            {
                    System.err.println("ATTENTION: Camera out of area!");
                    return 0;
            }
        
            return ((int)heightDiff - (int)heightDiff % detailsteps) * (-4) / detailsteps +
maxCamHeight / detailsteps * 4 + 4;        
    }
}