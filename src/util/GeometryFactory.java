package util;

import static opengl.GL.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import opengl.GL;

import org.lwjgl.BufferUtils;
import java.util.*;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Stellt Methoden zur Erzeugung von Geometrie bereit.
 * @author Sascha Kolodzey, Nico Marniok
 */
public class GeometryFactory {    
    /**
     * Erzeugt ein Vierexk in der xy-Ebene. (4 Indizes)
     * @return VertexArrayObject ID
     */
    public static Geometry createScreenQuad() {        
        int vaid = glGenVertexArrays();
        glBindVertexArray(vaid);        
        
        // vertexbuffer
        FloatBuffer vertexData = BufferUtils.createFloatBuffer(8);
        vertexData.put(new float[] {
            -1.0f, -1.0f,
            +1.0f, -1.0f,
            -1.0f, +1.0f,
            +1.0f, +1.0f,
        });
        vertexData.position(0);
        
        // indexbuffer
        IntBuffer indexData = BufferUtils.createIntBuffer(4);
        indexData.put(new int[] { 0, 1, 2, 3, });
        indexData.position(0);
        
        Geometry geo = new Geometry();
        geo.setIndices(indexData, GL_TRIANGLE_STRIP);
        geo.setVertices(vertexData);
        geo.addVertexAttribute(ShaderProgram.ATTR_POS, 2, 0);
        return geo;
    }
    
    /**
     * Erzeugt eine Kugel mit Texturekoordinaten und Normalen.
     * @param r Radius der Kugel
     * @param n Anzahl der vertikalen Streifen
     * @param k Anzahl der horizontalen Streifen
     * @return Geometrie der Kugel
     */
    public static Geometry createSkyDome(float r, int n, int k) {
        FloatBuffer fb = BufferUtils.createFloatBuffer((3+3+3+2) * (n+1)*(k+1));
        
        float dTheta = Util.PI / (float)k;
        float dPhi = Util.PI_MUL2 / (float)n;
        float theta = 0;
        for(int j=0; j <= k; ++j) {
            float sinTheta = (float)Math.sin(theta);
            float cosTheta = (float)Math.cos(theta);
            float phi = 0;
            for(int i=0; i <= n; ++i) {
                float sinPhi = (float)Math.sin(phi);
                float cosPhi = (float)Math.cos(phi);
                
                // position
                fb.put(r*sinTheta*cosPhi);  
                fb.put(r*cosTheta);
                fb.put(r*sinTheta*sinPhi);
                
                // normal
                fb.put(0.0f);    
                fb.put(0.0f);
                fb.put(0.0f);
                
                //tangent
                fb.put(0);    
                fb.put(0);
                fb.put(0);
                
                // tex coords
                fb.put(phi / Util.PI_MUL2);
                fb.put(theta / Util.PI);
                                
                phi += dPhi;
            }
            theta += dTheta;
        }
        fb.position(0);
        
        IntBuffer ib = BufferUtils.createIntBuffer(k*(2*(n+1)+1));
        for(int j=0; j < k; ++j) {
            for(int i=0; i <= n; ++i) {
                
                ib.put(j*(n+1) + i);
                ib.put((j+1)*(n+1) + i);
            }
            ib.put(RESTART_INDEX);
        }
        ib.position(0);
        
        Geometry sphere = new Geometry();
        sphere.setIndices(ib, GL_TRIANGLE_STRIP);
        sphere.setVertices(fb);
        sphere.addVertexAttribute(ShaderProgram.ATTR_POS, 3, 0);
        sphere.addVertexAttribute(ShaderProgram.ATTR_NORMAL, 3, 12);
        sphere.addVertexAttribute(ShaderProgram.ATTR_TANGENT, 3, 24);
        sphere.addVertexAttribute(ShaderProgram.ATTR_TEX, 2, 36);
        return sphere;
    }   
    
    /**
     * Erzeugt eine Kugel mit Texturekoordinaten und Normalen.
     * @param r Radius der Kugel
     * @param n Anzahl der vertikalen Streifen
     * @param k Anzahl der horizontalen Streifen
     * @return Geometrie der Kugel
     */
    public static Geometry createSphere(float r, int n, int k) {
        FloatBuffer fb = BufferUtils.createFloatBuffer((3+3+3+2) * (n+1)*(k+1));
        
        float dTheta = Util.PI / (float)k;
        float dPhi = Util.PI_MUL2 / (float)n;
        float theta = 0;
        for(int j=0; j <= k; ++j) {
            float sinTheta = (float)Math.sin(theta);
            float cosTheta = (float)Math.cos(theta);
            float phi = 0;
            for(int i=0; i <= n; ++i) {
                float sinPhi = (float)Math.sin(phi);
                float cosPhi = (float)Math.cos(phi);
                
                // position
                fb.put(r*sinTheta*cosPhi);  
                fb.put(r*cosTheta);
                fb.put(r*sinTheta*sinPhi);
                
                // normal
                fb.put(sinTheta*cosPhi);    
                fb.put(cosTheta);
                fb.put(sinTheta*sinPhi);
             
                //tangent
                fb.put(-sinPhi);    
                fb.put(cosPhi);
                fb.put(0);
                
                // tex coords
                fb.put(phi / Util.PI_MUL2);
                fb.put(theta / Util.PI);
                                
                phi += dPhi;
            }
            theta += dTheta;
        }
        fb.position(0);
        
        IntBuffer ib = BufferUtils.createIntBuffer(k*(2*(n+1)+1));
        for(int j=0; j < k; ++j) {
            for(int i=0; i <= n; ++i) {
            	ib.put((j+1)*(n+1) + i);
                ib.put(j*(n+1) + i);
                
            }
            ib.put(RESTART_INDEX);
        }
        ib.position(0);
        
        Geometry sphere = new Geometry();
        sphere.setIndices(ib, GL_TRIANGLE_STRIP);
        sphere.setVertices(fb);
        sphere.addVertexAttribute(ShaderProgram.ATTR_POS, 3, 0);
        sphere.addVertexAttribute(ShaderProgram.ATTR_NORMAL, 3, 12);
        sphere.addVertexAttribute(ShaderProgram.ATTR_TANGENT, 3, 24);
        sphere.addVertexAttribute(ShaderProgram.ATTR_TEX, 2, 36);
        return sphere;
    }
    
    /**
     * Erzeugt ein Vierexk in der xy-Ebene. (4 Indizes)
     * @return VertexArrayObject ID
     */
    public static Geometry createQuad() {        
        // vertexbuffer
        FloatBuffer vertexData = BufferUtils.createFloatBuffer((3+3+3+2)*4);
        vertexData.put(new float[] {
            // position          // normal          // tangent         // tex coords
            0.0f, -1.0f, -1.0f,  0.0f, 0.0f, 0.0f,  0.0f, 0.0f, 1f, 0f, 0f,
            0.0f, -1.0f, 1.0f,   0.0f, 0.0f, 0.0f,  1.0f, 0.0f, 1f, 0f, 0f,
            0.0f, +1.0f, -1.0f,  0.0f, 0.0f, 0.0f,  0.0f, 1.0f, 1f, 0f, 0f,
            0.0f, +1.0f, 1.0f,   0.0f, 0.0f, 0.0f,  1.0f, 1.0f, 1f, 0f, 0f
            
        });
    	
        vertexData.position(0);
                  
        IntBuffer indexData = BufferUtils.createIntBuffer(4);
        indexData.put(new int[] { 0, 1, 2, 3 });
        indexData.position(0);
        
        Geometry geo = new Geometry();
        geo.setVertices(vertexData);
        geo.setIndices(indexData, GL_TRIANGLE_STRIP);
        
        geo.addVertexAttribute(ShaderProgram.ATTR_POS, 3, 0);
        geo.addVertexAttribute(ShaderProgram.ATTR_NORMAL, 3, 12);
        geo.addVertexAttribute(ShaderProgram.ATTR_TEX, 2, 24);
        geo.addVertexAttribute(ShaderProgram.ATTR_TANGENT, 3, 32);
        return geo;
    }
    
    public static Geometry createWhiteScreenQuad() {        
        int vaid = glGenVertexArrays();
        glBindVertexArray(vaid);        
        
        // vertexbuffer
        FloatBuffer vertexData = BufferUtils.createFloatBuffer(4*(3+3+3+4+2));
        vertexData.put(new float[] {
            -1.0f, -1.0f, 0.0f,   0.0f, 0.0f, 1.0f,   10.0f, 10.0f, 10.0f, 1.0f,   1.0f,0.0f,0.0f,   -1.0f, -1.0f,
            +1.0f, -1.0f, 0.0f,   0.0f, 0.0f, 1.0f,   10.0f, 10.0f, 10.0f, 1.0f,   1.0f,0.0f,0.0f,   +1.0f, -1.0f,
            -1.0f, +1.0f, 0.0f,   0.0f, 0.0f, 1.0f,   10.0f, 10.0f, 10.0f, 1.0f,   1.0f,0.0f,0.0f,   -1.0f, +1.0f,
            +1.0f, +1.0f, 0.0f,   0.0f, 0.0f, 1.0f,   10.0f, 10.0f, 10.0f, 1.0f,   1.0f,0.0f,0.0f,   +1.0f, +1.0f
            
        });
        vertexData.position(0);
        
        // indexbuffer
        IntBuffer indexData = BufferUtils.createIntBuffer(4);
        indexData.put(new int[] { 0, 1, 2, 3, });
        indexData.position(0);
        
        Geometry geo = new Geometry();
        geo.setIndices(indexData, GL_TRIANGLE_STRIP);
        geo.setVertices(vertexData);
        geo.addVertexAttribute(ShaderProgram.ATTR_POS, 3, 0);
        geo.addVertexAttribute(ShaderProgram.ATTR_NORMAL, 3, 3*4);
        geo.addVertexAttribute(ShaderProgram.ATTR_COLOR, 4, 3*4+3*4);
        geo.addVertexAttribute(ShaderProgram.ATTR_TANGENT, 3, 3*4+3*4+4*4);
        geo.addVertexAttribute(ShaderProgram.ATTR_TEX, 2, 3*4+3*4+4*4+3*4);
        return geo;
    }
    
    public static Geometry createCube() {
    	float[] cubeVertices  = {
         	 0.5f,  0.5f,  0.5f,  0.0f, 0.0f, 1.0f,  		1.0f, 0.0f, 0.0f, 1.0f,  1.0f, 0.0f, 0.0f, 1.0f, 1.0f,// front top right
       		-0.5f,  0.5f,  0.5f,  0.0f, 0.0f, 1.0f,  	 	0.0f, 1.0f, 0.0f, 1.0f,  1.0f, 0.0f, 0.0f, 0.0f,1.0f,// front top left
       		 0.5f, -0.5f,  0.5f,  0.0f, 0.0f, 1.0f,  		0.0f, 0.0f, 1.0f, 1.0f,  1.0f, 0.0f, 0.0f, 1.0f, 0.0f,// front bottom right
       		-0.5f, -0.5f,  0.5f,  0.0f, 0.0f, 1.0f,  		0.0f, 1.0f, 1.0f, 1.0f,  1.0f, 0.0f, 0.0f, 0.0f, 0.0f,// front bottom left
           		
       		 0.5f,  0.5f, -0.5f,  0.0f, 0.0f, -1.0f,  		10.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,// back top right
       		 0.5f, -0.5f, -0.5f,  0.0f, 0.0f, -1.0f,  		0.0f, 10.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f,  0.0f,1.0f,// back top left
       		-0.5f,  0.5f, -0.5f,  0.0f, 0.0f, -1.0f,  		1.0f, 0.0f, 20.0f, 1.0f, 1.0f, 0.0f, 0.0f,1.0f, 0.0f,// back bottom right
       		-0.5f, -0.5f, -0.5f,  0.0f, 0.0f, -1.0f,  		0.0f, 0.0f, 30.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,// back bottom left		

       		 0.5f, 0.5f,  0.5f,  0.0f, 1.0f, 0.0f,  		20.0f, 2.0f, 20.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,// front top right
       		 0.5f, 0.5f, -0.5f,  0.0f, 1.0f, 0.0f,  		0.0f, 2.0f, 2.0f, 1.0f,   0.0f, 0.0f, 1.0f, 0.0f,1.0f,// front top left
       		-0.5f, 0.5f,  0.5f,  0.0f, 1.0f, 0.0f,  		30.0f, 0.0f, 2.0f, 1.0f,  0.0f, 0.0f, 1.0f,1.0f, 0.0f,// front bottom right
       		-0.5f, 0.5f, -0.5f,  0.0f, 1.0f, 0.0f,  		0.0f, 0.0f, 5.0f, 1.0f,   0.0f, 0.0f, 1.0f,0.0f, 0.0f,// front bottom left
               		
       		 0.5f, -0.5f,  0.5f,  0.0f, -1.0f, 0.0f,  		2.0f, 50.0f, 2.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,// front top right
	    	-0.5f, -0.5f,  0.5f,  0.0f, -1.0f, 0.0f,  		0.0f, 2.0f, 20.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,1.0f,// front top left
			 0.5f, -0.5f, -0.5f,  0.0f, -1.0f, 0.0f,  		20.0f, 0.0f, 2.0f, 1.0f, 0.0f, 0.0f, 1.0f,1.0f, 0.0f,// front bottom right
	 		-0.5f, -0.5f, -0.5f,  0.0f, -1.0f, 0.0f,  		0.0f, 0.0f, 2.0f, 1.0f,  0.0f, 0.0f, 1.0f,0.0f, 0.0f,// front bottom left
               		
       		 0.5f,  0.5f,  0.5f,  1.0f, 0.0f, 0.0f,  		0.5f, 1.0f, 0.0f, 1.0f,  0.0f, 1.0f, 0.0f, 1.0f, 1.0f,// front top right
       		 0.5f, -0.5f,  0.5f,  1.0f, 0.0f, 0.0f,  		0.0f, 1.0f, 0.5f, 1.0f,  0.0f, 1.0f, 0.0f, 0.0f,1.0f,// front top left
       		 0.5f,  0.5f, -0.5f,  1.0f, 0.0f, 0.0f,  		2.0f, 0.0f, 0.5f, 1.0f,  0.0f, 1.0f, 0.0f,1.0f, 0.0f,// front bottom right
       		 0.5f, -0.5f, -0.5f,  1.0f, 0.0f, 0.0f,  		0.0f, 0.0f, 0.1f, 1.0f,  0.0f, 1.0f, 0.0f,0.0f, 0.0f,// front bottom left
           		
       		-0.5f,  0.5f,  0.5f, -1.0f, 0.0f, 0.0f,  		0.2f, 0.7f, 1.0f, 1.0f,  0.0f, 1.0f, 0.0f, 1.0f, 1.0f,// front top right
       		-0.5f,  0.5f, -0.5f, -1.0f, 0.0f, 0.0f,  		0.0f, 2.0f, 0.2f, 1.0f,  0.0f, 1.0f, 0.0f, 0.0f,1.0f,// front top left
       		-0.5f, -0.5f,  0.5f, -1.0f, 0.0f, 0.0f,  		20.0f, 0.0f, 2.0f, 1.0f, 0.0f, 1.0f, 0.0f,1.0f, 0.0f, // front bottom right
       		-0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f,  		5.0f, 10.0f, 2.0f, 1.0f, 0.0f, 1.0f, 0.0f,0.0f, 0.0f// front bottom left
   	};
       
       int[] cubeIndices = {
    		 0,  1,  2,  3, GL.RESTART_INDEX,
    		 4,  5,  6,  7, GL.RESTART_INDEX,
    		 8,  9, 10, 11, GL.RESTART_INDEX,
    		12, 13, 14, 15, GL.RESTART_INDEX,
    		16, 17, 18, 19, GL.RESTART_INDEX,
    		20, 21, 22, 23, GL.RESTART_INDEX,
       };
       
       FloatBuffer cubeVertBuf = BufferUtils.createFloatBuffer(cubeVertices.length);
       IntBuffer cubeIndBuf = BufferUtils.createIntBuffer(cubeIndices.length);
       cubeVertBuf.put(cubeVertices);
       cubeVertBuf.flip();
       cubeIndBuf.put(cubeIndices);
       cubeIndBuf.flip();
       
       Geometry geo = new Geometry();
       geo.setIndices(cubeIndBuf, GL_TRIANGLE_STRIP);
       geo.setVertices(cubeVertBuf);
       geo.addVertexAttribute(ShaderProgram.ATTR_POS, 3, 0);
       geo.addVertexAttribute(ShaderProgram.ATTR_NORMAL, 3, 12);
       geo.addVertexAttribute(ShaderProgram.ATTR_COLOR, 4, 24);
       geo.addVertexAttribute(ShaderProgram.ATTR_TANGENT, 3, 40);
       geo.addVertexAttribute(ShaderProgram.ATTR_TEX, 2, 52);
       return geo;
    }
    
    public static Geometry createTestParticles(int num) {
    	FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(num*3);
    	float amp = 0.9f;
    	double freq = 5;//1/(double)num;
    	int d = (int)Math.sqrt(num);
    	for(int i = 0; i < num; i++) {
    		vertexBuffer.put((float)Math.random() * 3.0f - 1.5f);
    		vertexBuffer.put((float)Math.random() * 0.4f);
    		vertexBuffer.put((float)Math.random() * 3.0f - 1.5f);
    		
    		/*for(int j = 0; j < d; ++j) {
        		vertexBuffer.put(-2 + 4*i/(float)d);
        		vertexBuffer.put(amp*(float)((Math.sin(freq * i) * Math.cos(freq * j))));
        		vertexBuffer.put(-2 + 4*j/(float)d);
    		}*/
    	}
    	vertexBuffer.position(0);
    	
    	IntBuffer indexBuffer = BufferUtils.createIntBuffer(num);
    	for(int i = 0; i < num; i++) {
    		indexBuffer.put(i);
    	}
    	indexBuffer.position(0);
    	
    	Geometry geo = new Geometry();
    	geo.setVertices(vertexBuffer);
    	geo.setIndices(indexBuffer, GL_POINTS);
    	geo.addVertexAttribute(ShaderProgram.ATTR_POS, 3, 0);
    	return geo;
    }
    
    public static Geometry createPlane() {        
        int vaid = glGenVertexArrays();
        glBindVertexArray(vaid);        
        
        // vertexbuffer
        FloatBuffer vertexData = BufferUtils.createFloatBuffer(12);
        vertexData.put(new float[] {
            -2.0f, 0.0f, -2.0f,
            +2.0f, 0.0f, -2.0f,
            -2.0f, 0.0f, +2.0f,
            +2.0f, 0.0f, +2.0f
        });
        vertexData.position(0);
        
        // indexbuffer
        IntBuffer indexData = BufferUtils.createIntBuffer(4);
        indexData.put(new int[] { 0, 2, 1, 3, });
        indexData.position(0);
        
        Geometry geo = new Geometry();
        geo.setIndices(indexData, GL_TRIANGLE_STRIP);
        geo.setVertices(vertexData);
        geo.addVertexAttribute(ShaderProgram.ATTR_POS, 3, 0);
        return geo;
    }

    /**
     * Erzeugt ein einfaches Terrain mit Sinus/Cosinus
     */
    public static Geometry createTerrain(int width, int height, int mode)
    {
        // vertex array id
        int vaid = glGenVertexArrays();
        glBindVertexArray(vaid);
        
        // fill vertex buffer
        FloatBuffer vertexData = BufferUtils.createFloatBuffer(width*height*3);
        
        float lenX = 2.0f/(float)(width-1);
        float lenY = 2.0f/(float)(height-1);
        float h;
        double sinf = 0.2;
        float ampli = 0.2f;
        Random r = new Random();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                // terrain modus
                switch (mode) {
                    case 1: // random
                        h = (float) (r.nextDouble());
                        break;
                    case 2:
                        //h = ampli*2 + (float)(Math.sin(x*sinf)+Math.sin(y*sinf))*ampli;
                        h = 0.5f + 0.5f*(float)(Math.sin(2*(Math.PI*2/height)*y)+Math.sin((Math.PI*2/width)*x));
                        break;
                    case 3:
                        h = ampli*2 * (float)(Math.sin(x*r.nextDouble()*sinf)+Math.sin(y*r.nextDouble()*sinf))*ampli;
                        break;
                    default: // plane
                        h = 0;
                }
                vertexData.put(new float[]{x*lenX, h, y*lenY});
            }
        }

        vertexData.position(0);     
        
        // indexbuffer
        IntBuffer indexData = BufferUtils.createIntBuffer((height-1)*2*width+(height-2));
        for (int y = 0; y < height-1; y++) {
            for (int x = 0; x < width; x++) {
                indexData.put(y*width + x);
                indexData.put((y+1)*width + x);
                
            }
            if (y < height-2)
                indexData.put(-1);
        }
        indexData.position(0);
        
        Geometry geo = new Geometry();
        geo.setIndices(indexData, GL_TRIANGLE_STRIP);
        geo.setVertices(vertexData);
        geo.addVertexAttribute(ShaderProgram.ATTR_POS, 3, 0);
        return geo;
    }
    
    final static public int NORMALTEX_UNIT = 2;
    final static public int HEIGHTTEX_UNIT = 3;
    
    static public Geometry createTerrainFromMap(String map, float amplitude) {
        // vertex array id
        int vaid = glGenVertexArrays();
        glBindVertexArray(vaid);

        // load height map
        float[][][] ic = Util.getImageContents(map);
        float[][] env = new float[3][3];
        FloatBuffer vertexData = BufferUtils.createFloatBuffer(ic[0].length*ic.length*3);
        FloatBuffer normalTexBuf = BufferUtils.createFloatBuffer(ic[0].length*ic.length*4);
        FloatBuffer heightTexBuf = BufferUtils.createFloatBuffer(ic[0].length*ic.length*4);
        for (int h = 0; h < ic.length; h++) {
            for (int w = 0; w < ic[0].length; w++) {
                vertexData.put(new float[]{w/(float)ic[0].length,
                                            amplitude*ic[h][w][0],
                                            h/(float)ic.length});
                heightTexBuf.put(amplitude*ic[h][w][0]);
                heightTexBuf.put(new float[]{0,0,0});
                
                // set environment
                env[0][0] = ic[h-1 >= 0 ? h-1 : h]
                                [w-1 >= 0 ? w-1 : w][0];
                env[0][1] = ic[h]
                                [w-1 >= 0 ? w-1 : w][0];
                env[0][2] = ic[h+1 < ic.length ? h+1 : h]
                                [w-1 >= 0 ? w-1 : w][0];
                env[1][0] = ic[h-1 >= 0 ? h-1 : h]
                                [w][0];
                env[1][1] = ic[h][w][0];

                env[1][2] = ic[h+1 < ic.length ? h+1 : h]
                                [w][0];
                env[2][0] = ic[h-1 >= 0 ? h-1 : h]
                                [w+1 < ic[0].length ? w+1 : w][0];
                env[2][1] = ic[h]
                                [w+1 < ic[0].length ? w+1 : w][0];
                env[2][2] = ic[h+1 < ic.length ? h+1 : h]
                                [w+1 < ic[0].length ? w+1 : w][0];

                float gx = env[0][0]+2*env[0][1]+env[0][2]
                            -env[2][0]-2*env[2][1]-env[2][2];
                
                float gz = env[0][0]+2*env[1][0]+env[2][0]
                            -env[0][2]-2*env[1][2]-env[2][2];
  
                
                // put normals to normalTexBuffer
                Vector3f norm = new Vector3f(2.0f * gx,
                        0.5f * (float)Math.sqrt(1.0f - gx*gx - gz*gz),
                        2.0f * gz);
                normalTexBuf.put(norm.x);
                normalTexBuf.put(norm.y);
                normalTexBuf.put(norm.z);
                normalTexBuf.put(0);
            }
        }
        vertexData.position(0);
        normalTexBuf.position(0);
        heightTexBuf.position(0);
        
        // indexbuffer
        IntBuffer indexData = BufferUtils.createIntBuffer((ic.length-1)*2*ic[0].length+(ic.length-2));
        for (int y = 0; y < ic.length-1; y++) {
            for (int x = 0; x < ic[0].length; x++) {
                indexData.put(y*ic[0].length + x);
                indexData.put((y+1)*ic[0].length + x);
                
            }
            if (y < ic.length-2)
                indexData.put(-1);
        }
        indexData.position(0);
        
        // create normal texture from normaltexturebuffer
        Texture tex = new Texture(GL_TEXTURE_2D, NORMALTEX_UNIT);
        tex.bind();
        glTexImage2D(GL_TEXTURE_2D,
                0,
                GL_RGBA8,
                ic[0].length,
                ic.length,
                0,
                GL_RGBA,
                GL_FLOAT,
                normalTexBuf);
        glGenerateMipmap(GL_TEXTURE_2D);        
        
        // create height texture
        Texture hTex = new Texture(GL_TEXTURE_2D, HEIGHTTEX_UNIT);
        hTex.bind();
        glTexImage2D(GL_TEXTURE_2D,
                0,
                GL_RGBA8,
                ic[0].length,
                ic.length,
                0,
                GL_RGBA,
                GL_FLOAT,
                heightTexBuf);
        glGenerateMipmap(GL_TEXTURE_2D);        
        
        // create geometry
        Geometry geo = new Geometry();
        geo.setIndices(indexData, GL_TRIANGLE_STRIP);
        geo.setVertices(vertexData);
        geo.addVertexAttribute(ShaderProgram.ATTR_POS, 3, 0);

        geo.setNormalTex(tex);
        geo.setHeightTex(hTex);
        //geo.addVertexAttribute(ShaderProgram.ATTR_NORMAL, 3, 12);
        

        return geo;
    }
}