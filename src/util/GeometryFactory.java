package util;

import static opengl.GL.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;

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
            -1.0f, -1.0f, 0.0f,  0.0f, 0.0f, 1.0f,  0.0f, 0.0f, 1f, 0f, 0f,
            +1.0f, -1.0f, 0.0f,  0.0f, 0.0f, 1.0f,  1.0f, 0.0f, 1f, 0f, 0f,
            -1.0f, +1.0f, 0.0f,  0.0f, 0.0f, 1.0f,  0.0f, 1.0f, 1f, 0f, 0f,
            +1.0f, +1.0f, 0.0f,  0.0f, 0.0f, 1.0f,  1.0f, 1.0f, 1f, 0f, 0f
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
    public static Geometry createSphere(float r, int n, int k) {
        FloatBuffer fb = BufferUtils.createFloatBuffer((3+3+2) * (n+1)*(k+1));
        
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
        sphere.addVertexAttribute(ShaderProgram.ATTR_TEX, 2, 24);
        return sphere;
    }   


    
    public static Geometry createCube() {
    	float[] cubeVertices  = {
       		 0.5f,  0.5f,  0.5f,	1.0f, 1.0f, 1.0f, 1.0f, // front top right
       		-0.5f,  0.5f,  0.5f, 	0.0f, 1.0f, 1.0f, 1.0f, // front top left
       		 0.5f, -0.5f,  0.5f,	1.0f, 0.0f, 1.0f, 1.0f, // front bottom right
       		-0.5f, -0.5f,  0.5f, 	0.0f, 0.0f, 1.0f, 1.0f, // front bottom left
       		
       		 0.5f,  0.5f, -0.5f,	1.0f, 1.0f, 0.0f, 1.0f, // back top right
       		-0.5f,  0.5f, -0.5f,	0.0f, 1.0f, 0.0f, 1.0f, // back top left
       		 0.5f, -0.5f, -0.5f,	1.0f, 0.0f, 0.0f, 1.0f, // back bottom right
       		-0.5f, -0.5f, -0.5f,	0.0f, 0.0f, 0.0f, 1.0f  // back bottom left		
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
       return geo;
    }
}
