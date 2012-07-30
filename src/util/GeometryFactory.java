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
       		1, 3, 0, 2, // front side
       		4, 6,		// right side
       		5, 7,		// back side
       		1, 3,		// left side
       	   -1,			
       		5, 1, 4, 0, // top
       	   -1,
       	    2, 3, 6, 7  // bottom	
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