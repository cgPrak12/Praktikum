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
 * @author arecknagel, fmaeschig
 *
 */
public class Terrain {

        private float[][][] terra;
        private float[][] noiseMap = new float [32][32];
        //private float[][] biome;
        private Random random;
        private int maxX, maxZ;
        private int vertexInfoCount = 5;

        /**
         * 
         * @param initialHeight
         * @param maxX
         * @param maxZ 
         * @param seed
         * @param noiseType
         * @param noiseMap
         */
        public Terrain(float initialHeight, int maxX, int maxZ, int seed, int noiseType,
float[][]noiseMap){

                this.terra = new float[maxX][maxZ][vertexInfoCount];
                this.random = new Random(seed);

                this.maxX = maxX;
                this.maxZ= maxZ; 

                // Gen flat terra and empty biome
                for(int x=0; x < maxX; ++x) {
                        for(int z=0; z < maxZ; ++z) {
                                this.terra[x][z][0] = initialHeight;
                        }
                }                 

                // Gen Noisemap       
                if (noiseMap != null){
                        if(noiseMap.length!=32 || noiseMap[0].length!=32) throw new
RuntimeException("noiseMap out of bounds");
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
                                                this.noiseMap[x][z] = (float)
((this.random.nextFloat()*2-1)-Math.sin(z/32*6.282f)*3+Math.cos(x/32*6.282f)*0.25);
                                        }
                                }
                                break;                
                        }
                }

                //Gen normalMap

            int vertexSize = 6;                       
               FloatBuffer vertices =
BufferUtils.createFloatBuffer(vertexSize*this.maxX*this.maxZ);
            
               // Gen Vbuffer
               for(int z=0; z < this.maxZ; ++z) {
            for(int x=0; x < this.maxX; ++x) {
                    vertices.put(1e-2f * (float)x);
                    vertices.put(terra[x][z][0]);
                    vertices.put(1e-2f * (float)z);
                    vertices.put(0);        // norm.x
                    vertices.put(0);        // norm.y
                    vertices.put(0);        // norm.z

            }                            
               }
        
               // Gen IndexBuffer
        IntBuffer indices = BufferUtils.createIntBuffer(3 * 2 * (this.maxX - 1) *
(this.maxZ - 1));
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

//                        tmp.load(vertices);
//                        this.normalMap[x-1][z] = tmp;
//                        this.terra[x-1][z][1] = tmp.x;
//                        this.terra[x-1][z][2] = tmp.y;
//                        this.terra[x-1][z][3] = tmp.z;
                        
                        this.terra[x-1][z][1] = vertices.get();
                        this.terra[x-1][z][2] = vertices.get();
                        this.terra[x-1][z][3] = vertices.get();                                
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
        public Terrain(float initialHeight, int maxX, int maxZ, int noiseType,
float[][]noisyMap){
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

        }
        /**
         * Function for transforming the terrain. Calls Material- and Biome- changing
functions by default
         * 
         * @param surfaceWrink: The level of detail
         */
        public void terraform(int surfaceWrink){

                float amp=4, freq=0.05f;
                for(int i=1; i<=surfaceWrink; i++){

                        if(i==3) freq = 0.5f;
                        Util.biLinIpol(this.terra, this.noiseMap, freq, amp);
                        freq*=(2+(random.nextFloat()/5f-0.2f));
                        amp/=(2+(random.nextFloat()/5f-0.2f));

                }

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
//        public void updateMaterialFromHeight(int range){
//
//                // Fill material map
//                float[][] hMM = new float[terra.length+range][terra[0].length+range];
//                for(int x=0; x<terra.length; x++){
//                        for(int z=0; z<terra[0].length; z++){
//                                if(this.heightMap[x][z]<0f){
//                                        hMM[x+range][z+range] = 1;
//                                        this.terra[x][z][4] = 1;
//                                }
//                                else{
//                                        if(this.heightMap[x][z]<0.5f){
//                                                hMM[x+range][z+range] = 2;
//                                                this.terra[x][z][4] = 2;
//                                        }
//                                        else{
//                                                if(this.heightMap[x][z]<2f){
//                                                        hMM[x+range][z+range] = 3;
//                                                        this.terra[x][z][4] = 3;
//                                                }
//                                                else{        
//                                                        hMM[x+range][z+range] = 4;
//                                                        this.terra[x][z][4] = 4;
//                                                }
//                                        }
//                                }
//                        }
//                }

                //TODO Fill help map corners
//                
//                int helpX = terra.length;
//                int helpZ = terra[0].length;
//                for(int x=0; x<helpX; x++){
//                        if(x<range || x>helpX-range){
//                                for(int z=0; z<helpZ; z++ ){
//                                        if(z>=range){
//
//                                        }else{
//                                                hMM[x][z] = terra[x][range][4];                                                
//                                        }
//                                }
//                        }else{
//                                for(int z=0; z<range; z++){
//
//                                }
//                                for(int z=0; z<range; z++){
//
//                                }
//
//                        }
//                }


                // Check for biomes, one for each material
                //checkBiome(range);


        /**
         * 
         * @param material
         * @param range
         */
        
        //TODO biome checker
        private void checkBiome(int range){
                int material;
                for(int x=0; x<maxX; x++){
                        for(int z=0; z<maxZ; z++){

                        }
                }
        }
        
        //TODO material checker for biome
        private void checkMaterial(){
                                
                for(int x=0; x<terra.length; x++){
                        for(int z=0; z<terra[0].length; z++){
                                
                                if(this.terra[x][z][0]<0){
                                        this.terra[x][z][4] = 1;
                                }
                                else{
                                        if(this.terra[x][z][0]<0.5f){
                                                this.terra[x][z][4] = 2;
                                        }
                                        else{
                                                if(this.terra[x][z][0]<2f){
                                                        this.terra[x][z][4] = 3;
                                                }
                                                else{        
                                                                this.terra[x][z][4] = 4;
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
         * test Terrain (to be deleted)
         */
        public void genTestTerrain(){
                this.terraform(25);
                this.smooth(7,1);


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
        
        public int getXDim(){
                return maxX;         
        }
        
        public int getZDim(){        
                return maxZ; 
        }
}