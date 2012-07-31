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
    
    
    /** Erstellt Grid der Dimension x*y
     * 
     * @param x Breite
     * @param y Länge
     * @return Gridgeometrie
     */
    public static Geometry createGrid(int x, int y){
    	int vaid = glGenVertexArrays();
    	glBindVertexArray(vaid);        
    	
    	float [][][] image = Util.getImageContents("./face2face_usa_heightmap.jpg");
    	System.out.println("Image.length x: " + image.length);
    	System.out.println("Image.length y: " + image[0].length);
    	System.out.println("Image.length z: " + image[0][0].length);
    	System.out.println("Image Wert 0: " + image[0][0][0]);
    	System.out.println("Image Wert 1: " + image[0][0][1]);
    	System.out.println("Image Wert 2: " + image[0][0][2]);
    	float[] vertices = new float[3*x*y];
    	float damping = 0.1f;
    	int count = 0;
    	for(int i = 0; i<x; i++){
    		for(int j = 0; j<y; j++){
    			vertices[count++] = i*damping;
    			vertices[count++] = image[(image.length / x) * i][(image[0].length / y) * j][0];
    			vertices[count++] = j*damping;
    		}
    	}
    	
    	int[] indices = new int[6*x*y];
    	count = 0;
    	for(int i = 0; i<(y-1); i++){
    		for(int j = 0; j<(x-1); j++){
    			indices[count++] = i+j*(x);  // 0 + 0*10 = 0
//    			System.out.println("1. : "+(i+j*(x)));
    			indices[count++] = i+(j+1)*(x); // 0+1*10 = 10
//    			System.out.println("2. : "+(i+(j+1)*(x)));
    			indices[count++] = i+1+(j+1)*(x); // 1+1*10 = 11
//    			System.out.println("3. : "+(i+1+(j+1)*(x)));
    			
    			indices[count++] = i+j*(x);  // 0 + 0*10 = 0
//    			System.out.println("4. : "+(i+j*(x)));
    			indices[count++] = i+1+(j+1)*(x); // 1+1*10 = 11
//    			System.out.println("5. : "+(i+1+(j+1)*(x)));
    			indices[count++] = i+j*(x)+1;  // 0 + 0*10 = 0
//    			System.out.println("6. : "+(i+j*(x)+1));
       		}
    	}
    	
    	FloatBuffer fbu = BufferUtils.createFloatBuffer(vertices.length);
    	IntBuffer ibu = BufferUtils.createIntBuffer(indices.length);
    	
    	fbu.put(vertices); fbu.flip();
    	ibu.put(indices); ibu.flip();
    	
    	Geometry geo = new Geometry();
    	geo.setVertices(fbu);
    	geo.setIndices(ibu, GL_TRIANGLES);
    	geo.addVertexAttribute(ShaderProgram.ATTR_POS, 3, 0);
    	
    	return geo;
    }
}