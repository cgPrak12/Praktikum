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
        FloatBuffer vertexData = BufferUtils.createFloatBuffer((3+3+3+2)*4); // world coords, color
        vertexData.put(new float[] {
            // position          // normal          // tangent         // tex coords
            -1.0f, -1.0f, 0.0f,  0.0f, 0.0f, 1.0f,  1.0f, 0.0f, 0.0f,  0.0f, 0.0f,
            +1.0f, -1.0f, 0.0f,  0.0f, 0.0f, 1.0f,  1.0f, 0.0f, 0.0f,  1.0f, 0.0f,
            -1.0f, +1.0f, 0.0f,  0.0f, 0.0f, 1.0f,  1.0f, 0.0f, 0.0f,  0.0f, 1.0f,
            +1.0f, +1.0f, 0.0f,  0.0f, 0.0f, 1.0f,  1.0f, 0.0f, 0.0f,  1.0f, 1.0f,
        });
    	
        vertexData.position(0);
                  
        IntBuffer indexData = BufferUtils.createIntBuffer(4);
        indexData.put(new int[] { 0, 1, 2, 3 });
        indexData.position(0);
        
        Geometry geo = new Geometry();
        geo.setVertices(vertexData);
        geo.setIndices(indexData, GL_TRIANGLE_STRIP);
        
        geo.addVertexAttribute(Util.ATTR_POS, 3, 0);
        geo.addVertexAttribute(Util.ATTR_NORMAL, 3, 12);
        geo.addVertexAttribute(Util.ATTR_TANGENT, 3, 24);
        geo.addVertexAttribute(Util.ATTR_TEX, 2, 36);
        return geo;
    }
}

