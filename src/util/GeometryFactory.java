package util;

import static opengl.GL.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
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
    
    
    static public Geometry createTerrainFromMap(String map, float amplitude) {
        // vertex array id
        int vaid = glGenVertexArrays();
        glBindVertexArray(vaid);

        // load height map
        //Util.ImageContents ic = Util.loadImage(map);
        float[][][] ic = Util.getImageContents(map);
        
/*        
        FloatBuffer vertexData = BufferUtils.createFloatBuffer(ic.width*ic.height*6);
        for (int h = 0; h < ic.height; h++) {
            for (int w = 0; w < ic.width; w++) {
                vertexData.put(new float[]{w/(float)ic.width,
                                            amplitude*ic.data.get(),
                                            h/(float)ic.height});
                // put normals
                vertexData.put(new float[]{0,0,0});
                
                //ic.data.get(); ic.data.get();
                ic.data.position(ic.data.position()+2);
            }
        }
        vertexData.position(0);
        
        // indexbuffer
        IntBuffer indexData = BufferUtils.createIntBuffer((ic.height-1)*2*ic.width+(ic.height-2));
        for (int y = 0; y < ic.height-1; y++) {
            for (int x = 0; x < ic.width; x++) {
                indexData.put(y*ic.width + x);
                indexData.put((y+1)*ic.width + x);
                
            }
            if (y < ic.height-2)
                indexData.put(-1);
        }
        indexData.position(0);
  */      
        
        float[][] env = new float[3][3];
        FloatBuffer vertexData = BufferUtils.createFloatBuffer(ic[0].length*ic.length*6);
        for (int h = 0; h < ic.length; h++) {
            for (int w = 0; w < ic[0].length; w++) {
                vertexData.put(new float[]{w/(float)ic[0].length,
                                            amplitude*ic[h][w][0],
                                            h/(float)ic.length});
                
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
  
                // put normals
                Vector3f norm = new Vector3f(2.0f * gx, 0.5f * (float)Math.sqrt(1.0f - gx*gx - gz*gz), 2.0f * gz);
                vertexData.put(norm.x);
                vertexData.put(norm.y);
                vertexData.put(norm.z);
                
                //ic.data.get(); ic.data.get();

            }
        }        vertexData.position(0);     
        
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
        
        Geometry geo = new Geometry();
        geo.setIndices(indexData, GL_TRIANGLE_STRIP);
        geo.setVertices(vertexData);
        geo.addVertexAttribute(ShaderProgram.ATTR_POS, 3, 0);
        geo.addVertexAttribute(ShaderProgram.ATTR_NORMAL, 3, 12);

        return geo;
    }
}