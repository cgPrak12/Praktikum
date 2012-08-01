package util;

import static opengl.GL.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;

/**
 * Stellt Methoden zur Erzeugung von Geometrie bereit.
 * 
 * @author Sascha Kolodzey, Nico Marniok
 */
public class GeometryFactory {
	/**
	 * Erzeugt ein Vierexk in der xy-Ebene. (4 Indizes)
	 * 
	 * @return VertexArrayObject ID
	 */
	public static Geometry createScreenQuad() {
		int vaid = glGenVertexArrays();
		glBindVertexArray(vaid);

		// vertexbuffer
		FloatBuffer vertexData = BufferUtils.createFloatBuffer(8);
		vertexData.put(new float[] { -1.0f, -1.0f, +1.0f, -1.0f, -1.0f, +1.0f,
				+1.0f, +1.0f, });
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

	public static Geometry createMxNGrid(int m, int n){
		int vaid = glGenVertexArrays();
		glBindVertexArray(vaid);
		
		float[] vertices = new float[2*m*n];
		int count = 0;
		
		for(int y=0; y < n; y++){
		for (int x=0; x < m; x++){
			vertices[count++] = x;
			vertices[count++] = y;
			}
		}
		
		int[]indices = new int[((m-1)*2+3)*(n-1)];
		
		count=0;
		
		for(int i=0; i<n-1;i++){
			for(int j=0; j<m;j++){
				
				indices[count++] = i*(m)+j;
		
				indices[count++] = i*(m)+j+m;
				
			}
			indices[count++] = -1;

		}
		
		FloatBuffer fbu = BufferUtils.createFloatBuffer(vertices.length);
		IntBuffer ibu = BufferUtils.createIntBuffer(indices.length);

		fbu.put(vertices);
		fbu.flip();
		ibu.put(indices);
		ibu.flip();

		Geometry geo = new Geometry();
		geo.setVertices(fbu);
		geo.setIndices(ibu, GL_TRIANGLE_STRIP);
		geo.addVertexAttribute(ShaderProgram.ATTR_POS, 2, 0);

		return geo;		
		

	}
	
	/**
	 * Erstellt Grid der Dimension x*y
	 * 
	 * @param x
	 *            Breite
	 * @param y
	 *            Länge
	 * @return Gridgeometrie
	 */
	public static Geometry createGrid(int x, int y) {
		int vaid = glGenVertexArrays();
		glBindVertexArray(vaid);

		float[] vertices = new float[4 * x * y];
		int count = 0;
		
		for (int j = 0; j < y; j++) {
			for (int i = 0; i < x; i++) {
				vertices[count++] = i;
				vertices[count++] = j;
			}
		}

		int[] indices = new int[6 * x * y];
		count = 0;
		for (int i = 0; i < (y-1); i++) {
			for (int j = 0; j < (x-1); j++) {
				indices[count++] = i + j * (x); // 0 + 0*10 = 0
				indices[count++] = i + (j + 1) * (x); // 0+1*10 = 10
				indices[count++] = i + 1 + (j + 1) * (x); // 1+1*10 = 11
				
				indices[count++] = i + j * (x); // 0 + 0*10 = 0
				indices[count++] = i + 1 + (j + 1) * (x); // 1+1*10 = 11				
				indices[count++] = i + j * (x) + 1; // 0 + 0*10+1 = 1
			}
		}

		FloatBuffer fbu = BufferUtils.createFloatBuffer(vertices.length);
		IntBuffer ibu = BufferUtils.createIntBuffer(indices.length);

		fbu.put(vertices);
		fbu.flip();
		ibu.put(indices);
		ibu.flip();

		Geometry geo = new Geometry();
		geo.setVertices(fbu);
		geo.setIndices(ibu, GL_TRIANGLES);
		geo.addVertexAttribute(ShaderProgram.ATTR_POS, 2, 0);

		return geo;
	}
	/**
	 * Erstellt Grid der Dimension x*y
	 * 
	 * @param x
	 *            Breite
	 * @param y
	 *            Länge
	 * @return Gridgeometrie
	 */
	public static Geometry createGridTex(int x, int y) {
		int vaid = glGenVertexArrays();
		glBindVertexArray(vaid);
		
		float[] vertices = new float[4 * x * y];
		int count = 0;
		
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				vertices[count++] = i;
				vertices[count++] = j;
				vertices[count++] = ((float) 1 / (float) x) * (float) i;
				vertices[count++] = ((float) 1 / (float) y) * (float) j;
			}
		}
		
		int[] indices = new int[6 * x * y];
		count = 0;
		for (int i = 0; i < (y - 1); i++) {
			for (int j = 0; j < (x - 1); j++) {
				indices[count++] = i + j * (x); // 0 + 0*10 = 0
				indices[count++] = i + (j + 1) * (x); // 0+1*10 = 10
				indices[count++] = i + 1 + (j + 1) * (x); // 1+1*10 = 11
				
				indices[count++] = i + j * (x); // 0 + 0*10 = 0
				indices[count++] = i + 1 + (j + 1) * (x); // 1+1*10 = 11
				indices[count++] = i + j * (x) + 1; // 0 + 0*10 = 0
			}
		}
		
		FloatBuffer fbu = BufferUtils.createFloatBuffer(vertices.length);
		IntBuffer ibu = BufferUtils.createIntBuffer(indices.length);
		
		fbu.put(vertices);
		fbu.flip();
		ibu.put(indices);
		ibu.flip();
		
		Geometry geo = new Geometry();
		geo.setVertices(fbu);
		geo.setIndices(ibu, GL_TRIANGLES);
		geo.addVertexAttribute(ShaderProgram.ATTR_POS, 2, 0);
		geo.addVertexAttribute(ShaderProgram.ATTR_TEX, 2, 2 * 4);
		
		return geo;
	}
	/** Creates L Geometry for ClipMap 
	 * 
	 * @param length 
	 * @param scase 0 = Bottom Right
	 * @param scase 1 = Bottom Left
	 * @param scase 2 = Top Left
	 * @param scase 3 = Top Right
	 * @return
	 */
	public static Geometry createL(int length, int scase) {
		int vaid = glGenVertexArrays();
		glBindVertexArray(vaid);

		float[] vertices = new float[4 * length + 4*(length- 1)];
		int[] indices = new int[(length*6) + ((length-1)*6)-6];
		int count = 0;
		switch (scase) {
		case 0:
			for (int i = length-1; i >= 0; i--) {
				for (int j = 0; j < 2; j++) {
					vertices[count++] = i;
					vertices[count++] = j;
				}
			}
			
			for (int j = 1; j < length; j++) {
				for (int i = 0; i < 2; i++) {
					vertices[count++] = i;
					vertices[count++] = j;
				}
			} break;
		case 1:			
			for (int i = length-1; i >= 0; i--) {
				for (int j = 0; j < 2; j++) {
					vertices[count++] = i;
					vertices[count++] = j;
				}
			}
			for (int j = 1; j < length; j++) {
				for (int i = 0; i < 2; i++) {
					vertices[count++] = i+length-2;
					vertices[count++] = j;
				}
			} break;
		case 2:
			for (int i = 0; i < length; i++) {
				for (int j = length-2; j < length; j++) {
					vertices[count++] = i;
					vertices[count++] = j;
				}
			}
			for (int j = length-2; j >= 0; j--) {
				for (int i = length - 2; i < length; i++) {
					vertices[count++] = i;
					vertices[count++] = j;
				}
			} break;
		case 3:
			for (int j = 0; j < length; j++) {
				for (int i = 0; i < 2; i++) {
					vertices[count++] = i;
					vertices[count++] = j;
				}
			}
			
			for (int i = 1 ; i < length; i++) {
				for (int j = length-2; j < length; j++) {
					vertices[count++] = i;
					vertices[count++] = j;
				}
			} break;
		}
		
		int i=0;
		count = 0;
		while(i< indices.length/2){
			indices[i++] = count+1;
			indices[i++] = count;
			indices[i++] = count+2;
			
			indices[i++] = count+1;
			indices[i++] = count+2;
			indices[i++] = count+3;
			
			count += 2;
		}
		while(i< indices.length){
			indices[i++] = count;
			indices[i++] = count+1;
			indices[i++] = count+2;
			
			indices[i++] = count+2;
			indices[i++] = count+1;
			indices[i++] = count+3;
			
			count += 2;
		}
		
		
		FloatBuffer fbu = BufferUtils.createFloatBuffer(vertices.length);
		IntBuffer ibu = BufferUtils.createIntBuffer(indices.length);
		fbu.put(vertices);	fbu.flip();
		ibu.put(indices);	ibu.flip();
		
		Geometry geo = new Geometry();
		
		geo.setVertices(fbu);
		geo.setIndices(ibu, GL_TRIANGLES);
		geo.addVertexAttribute(ShaderProgram.ATTR_POS, 2, 0);
		
		return geo;
	}
}