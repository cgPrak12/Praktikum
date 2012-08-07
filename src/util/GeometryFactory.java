package util;

import static opengl.GL.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
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
                fb.put(cosTheta*cosPhi);    
                fb.put(cosTheta*sinPhi);
                fb.put(-sinTheta);
                
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
                fb.put(cosTheta*cosPhi);    
                fb.put(cosTheta*sinPhi);
                fb.put(-sinTheta);
                
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
    
    public static Geometry createCube() {
    	float[] cubeVertices  = {
    		 //pos                    //color                 //normale         //tangente        //tex
       		 0.5f,  0.5f,  0.5f,	1.0f, 1.0f, 1.0f, 1.0f,   0.0f,0.0f,1.0f,   1.0f,0.0f,0.0f,   1.0f,1.0f, // front top right
       		-0.5f,  0.5f,  0.5f, 	0.0f, 1.0f, 1.0f, 1.0f,   0.0f,0.0f,1.0f,   1.0f,0.0f,0.0f,   0.0f,1.0f,// front top left
       		 0.5f, -0.5f,  0.5f,	1.0f, 0.0f, 1.0f, 1.0f,   0.0f,1.0f,1.0f,   1.0f,0.0f,0.0f,   1.0f,0.0f,// front bottom right
       		-0.5f, -0.5f,  0.5f, 	0.0f, 0.0f, 1.0f, 1.0f,   0.0f,1.0f,1.0f,   1.0f,0.0f,0.0f,   0.0f,0.0f,// front bottom left
       		
       		 0.5f,  0.5f, -0.5f,	1.0f, 1.0f, 0.0f, 1.0f,   0.0f,0.0f,1.0f,   1.0f,0.0f,0.0f,   0.0f,1.0f,// back top right
       		-0.5f,  0.5f, -0.5f,	0.0f, 1.0f, 0.0f, 1.0f,   0.0f,0.0f,1.0f,   1.0f,0.0f,0.0f,   1.0f,1.0f,// back top left
       		 0.5f, -0.5f, -0.5f,	1.0f, 0.0f, 0.0f, 1.0f,   0.0f,0.0f,1.0f,   1.0f,0.0f,0.0f,   0.0f,0.0f,// back bottom right
       		-0.5f, -0.5f, -0.5f,	0.0f, 0.0f, 0.0f, 1.0f,   0.0f,0.0f,1.0f,   1.0f,0.0f,0.0f,   1.0f,0.0f// back bottom left		
       };
       
       int[] cubeIndices = {
    		5, 4, 7, 6, 2, 4, 0, 
    		5, 1, 7, 3, 2, 1, 0
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
       geo.addVertexAttribute(ShaderProgram.ATTR_COLOR, 4, 12);
       geo.addVertexAttribute(ShaderProgram.ATTR_NORMAL, 3, 28);
       geo.addVertexAttribute(ShaderProgram.ATTR_TANGENT, 3, 40);
       geo.addVertexAttribute(ShaderProgram.ATTR_TEX, 2, 52);
       return geo;
    }
    
    public static Geometry getGrid(String heightmap) {
        float heightfield[][][] = Util.getImageContents(heightmap);
        int maxX = heightfield[0].length/2;
        int maxZ = heightfield.length/2;
        
        int vertexSize = 3+3+3+2;
        FloatBuffer vertices = BufferUtils.createFloatBuffer(vertexSize * maxX/2 * (maxZ/2));
        for(int z=0; z < maxZ; ++z) {
            for(int x=0; x < maxX; ++x) {
                vertices.put(1e-2f * (float)x); // 1e-2 = 1 * 10 ^ -2 = 0.01
                vertices.put(heightfield[z][x][0]);
                vertices.put(1e-2f * (float)z);
                //normale
                vertices.put(0);
                vertices.put(1);
                vertices.put(0);
                //tangente
                vertices.put(1);
                vertices.put(0);
                vertices.put(0);
                //tex
                vertices.put(x/maxX);
                vertices.put(z/maxZ);
                
            }
        }
        vertices.position(0);
        
        IntBuffer indices = BufferUtils.createIntBuffer(3 * 2 * (maxX - 1) * (maxZ - 1));
        for(int z=0; z < maxZ - 1; ++z) {
            for(int x=0; x < maxX - 1; ++x) {
                indices.put(z * maxX + x);
                indices.put((z + 1) * maxX + x + 1);
                indices.put(z * maxX + x + 1);
                
                indices.put(z * maxX + x);
                indices.put((z + 1) * maxX + x);
                indices.put((z + 1) * maxX + x + 1);
            }
        }
        
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
            
            //System.out.println(p0 + " " + p1 + " " + p2);
            
            Vector3f a = Vector3f.sub(p1, p0, null);
            Vector3f b = Vector3f.sub(p2, p0, null);
            Vector3f normal = Vector3f.cross(a, b, null);
            normal.normalise();
            
            vertices.position(vertexSize * index0 + 3);
            normal.store(vertices);
        }
        
        vertices.position(0);
        indices.position(0);
        Geometry geo = new Geometry();
        geo.setIndices(indices, GL_TRIANGLES);
        geo.setVertices(vertices);
        geo.addVertexAttribute(ShaderProgram.ATTR_POS, 3, 0);
        geo.addVertexAttribute(ShaderProgram.ATTR_NORMAL, 3, 12);
        geo.addVertexAttribute(ShaderProgram.ATTR_TANGENT, 3, 24);
        geo.addVertexAttribute(ShaderProgram.ATTR_TEX, 2, 36);
        return geo;
    }
}
