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
	
	public static Geometry getGrid(String heightmap) {
        float heightfield[][][] = Util.getImageContents(heightmap);
        int maxX = heightfield[0].length;
        int maxZ = heightfield.length;
        
        int vertexSize = 6;
        FloatBuffer vertices = BufferUtils.createFloatBuffer(vertexSize * maxX * maxZ);
        for(int z=0; z < maxZ; ++z) {
            for(int x=0; x < maxX; ++x) {
                vertices.put(1e-2f * (float)x); // 1e-2 = 1 * 10 ^ -2 = 0.01
                vertices.put(0.75f*heightfield[z][x][0]);
                vertices.put(1e-2f * (float)z);
                
                vertices.put(0);
                vertices.put(0);
                vertices.put(0);
            }
        }
        
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
        return geo;
    }
    
    
    
    
        public static Geometry genTerrain(float[][] terra) {
        	
        	int vertexSize = 7;
        	int maxX = terra.length;
        	int maxZ = terra[0].length; 	
        	
           	FloatBuffer vertices = BufferUtils.createFloatBuffer(vertexSize*maxX*maxZ);

        	
        	
           	// Gen Vbuffer
           	for(int z=0; z < maxZ; ++z) {
                for(int x=0; x < maxX; ++x) {
                	vertices.put(1e-2f * (float)x);
                	vertices.put(terra[x][z]);
                	vertices.put(1e-2f * (float)z);
                	
                	vertices.put(0);	// norm.x
                	vertices.put(0);	// norm.y
                	vertices.put(0);	// norm.z
                	vertices.put(0);	// material (0=Earth)
                }                	    
           	}
           	
           	
           	// Gen IndexBuffer
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
            Geometry geo = new Geometry();
            geo.setIndices(indices, GL_TRIANGLES);
            geo.setVertices(vertices);
            return geo;	
        }
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
}