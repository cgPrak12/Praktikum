package util;

import static opengl.GL.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import java.util.*;

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
}