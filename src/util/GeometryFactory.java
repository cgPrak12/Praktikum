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
	 * Erzeugt ein Viereck in der xy-Ebene. (4 Indizes)
	 * 
	 * @return VertexArrayObject ID
	 * 
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

	/**
	 * Erzeugt ein MxNGrid in der XZ-Ebene
	 * 
	 * @param m
	 *            breite
	 * @param n
	 *            länge
	 * @return Grid : Geometry
	 */
	public static Geometry createMxNGrid(int m, int n) {
		int vaid = glGenVertexArrays();
		glBindVertexArray(vaid);

		// VertexBufferArray erstellen
		float[] vertices = new float[5 * m * n];
		int count = 0;

		for (int y = 0; y < n; y++) {
			for (int x = 0; x < m; x++) {
				vertices[count++] = y;
				vertices[count++] = x;
				vertices[count++] = 0.35f;
				vertices[count++] = 0.5f;
				vertices[count++] = 0.35f;
			}
		}

		// IndexBufferArray erstellen
		int[] indices = new int[((m - 1) * 2 + 3) * (n - 1)];

		count = 0;

		for (int i = 0; i < n - 1; i++) {
			for (int j = 0; j < m; j++) {

				indices[count++] = i * (m) + j + m;
				indices[count++] = i * (m) + j;

			}
			indices[count++] = -1;

		}

		// Buffer erzeugen
		FloatBuffer fbu = BufferUtils.createFloatBuffer(vertices.length);
		IntBuffer ibu = BufferUtils.createIntBuffer(indices.length);

		fbu.put(vertices);
		fbu.flip();
		ibu.put(indices);
		ibu.flip();

		// Geometry erzeugen und setzen
		Geometry geo = new Geometry();
		geo.setVertices(fbu);
		geo.setIndices(ibu, GL_TRIANGLE_STRIP);
		geo.addVertexAttribute(ShaderProgram.ATTR_POS, 2, 0);
		geo.addVertexAttribute(ShaderProgram.ATTR_COLOR, 3, 8);

		return geo;

	}

	/**
	 * Erzeugt ein Grid in der XZ-Ebene
	 * 
	 * @param m
	 *            breite
	 * @param n
	 *            länge
	 * @return Grid : Geometry
	 */
	public static Geometry createGrid(int x, int y) {
		int vaid = glGenVertexArrays();
		glBindVertexArray(vaid);

		float[] vertices = new float[5 * x * y];
		int count = 0;

		for (int j = 0; j < y; j++) {
			for (int i = 0; i < x; i++) {
				vertices[count++] = i;
				vertices[count++] = j;
				vertices[count++] = 0.8f;
				vertices[count++] = 0.7f;
				vertices[count++] = 0.4f;
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
		geo.addVertexAttribute(ShaderProgram.ATTR_COLOR, 3, 8);

		return geo;
	}

	/**
	 * Erzeugt ein Grid in der XZ-Ebene
	 * 
	 * @param m
	 *            breite
	 * @param n
	 *            länge
	 * @return Grid : Geometry
	 */
	public static Geometry createGridTex(int x, int y) {
		int vaid = glGenVertexArrays();
		glBindVertexArray(vaid);

		float[] vertices = new float[6 * x * y];
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

	/**
	 * Erzeugt ein L-Grid in der XZ-Ebene Kante des Ls liegt "oben rechts"
	 * 
	 * @param length
	 *            Länge einer Kante
	 * @return TopRightL Geometrie
	 */

	public static Geometry createTopRight(int length){

		int vaid = glGenVertexArrays();
		glBindVertexArray(vaid);

		// Vertex und Index Arrays mit passender Größe erzeugen
		float[] vertices = new float[10 * length + 10 * (length - 1)];
		int[] indices = new int[2 * length + (length - 2) * 4 + (length - 2)
				* 10];
		int count = 0;

		// VertexBufferArray beschreiben
		for (int x = 0; x < length; x++) {
			for (int y = 0; y < 2; y++) {
				vertices[count++] = y;
				vertices[count++] = x;
				vertices[count++] = 0.5f;
				vertices[count++] = 0.5f;
				vertices[count++] = 0.7f;
			}
		}

		for (int x = length - 2; x < length; x++) {
			for (int y = 2; y < length; y++) {
				vertices[count++] = y;
				vertices[count++] = x;
				vertices[count++] = 0.5f;
				vertices[count++] = 0.5f;
				vertices[count++] = 0.7f;
			}
		}

		// IndexBufferArray beschreiben als TRIANGLE_STRIP

		int icount = 0;
		for (int i = 0; i < 2 * length; i += 2) {
			indices[icount++] = i + 1;
			indices[icount++] = i;
		}
		indices[icount++] = -1;
		indices[icount++] = 2 * length;
		indices[icount++] = 2 * length - 3;
		indices[icount++] = 3 * length - 2;
		indices[icount++] = 2 * length - 1;

		indices[icount++] = -1;
		for (int j = 0; j < length - 3; j++) {
			for (int i = 1; i >= 0; i--) {
				indices[icount++] = 2 * length + i + j;
			}
			for (int i = 1; i >= 0; i--) {
				indices[icount++] = 3 * length + i + j - 2;
			}
			indices[icount++] = -1;
		}

		// Buffer erstellen
		FloatBuffer fbu = BufferUtils.createFloatBuffer(vertices.length);
		IntBuffer ibu = BufferUtils.createIntBuffer(indices.length);
		fbu.put(vertices);
		fbu.flip();
		ibu.put(indices);
		ibu.flip();

		// Geometry erzeugen
		Geometry geo = new Geometry();

		geo.setVertices(fbu);
		geo.setIndices(ibu, GL_TRIANGLE_STRIP);
		geo.addVertexAttribute(ShaderProgram.ATTR_POS, 2, 0);
		geo.addVertexAttribute(ShaderProgram.ATTR_COLOR, 3, 8);

		return geo;
	}

	/**
	 * Erzeugt ein L-Grid in der XZ-Ebene Kante des Ls liegt "unten rechts"
	 * 
	 * @param length
	 *            Länge "einer" Kante
	 * @return BottomRechtsL Geometrie
	 */

	public static Geometry createBottomRight(int length){

		int vaid = glGenVertexArrays();
		glBindVertexArray(vaid);

		float[] vertices = new float[10 * length + 10 * (length - 1)];
		int[] indices = new int[2 * length + (length - 2) * 4 + (length - 2)
				* 10];
		int count = 0;

		for (int x = 0; x < length; x++) {
			for (int y = 0; y < 2; y++) {
				vertices[count++] = y;
				vertices[count++] = x;
				vertices[count++] = 0.5f;
				vertices[count++] = 0.5f;
				vertices[count++] = 0.7f;
			}
		}

		for (int x = 0; x < 2; x++) {
			for (int y = 2; y < length; y++) {
				vertices[count++] = y;
				vertices[count++] = x;
				vertices[count++] = 0.5f;
				vertices[count++] = 0.5f;
				vertices[count++] = 0.7f;
			}
		}

		int icount = 0;
		for (int i = 0; i < 2 * length; i += 2) {
			indices[icount++] = i + 1;
			indices[icount++] = i;
		}
		indices[icount++] = -1;
		indices[icount++] = 2 * length;
		indices[icount++] = 1;
		indices[icount++] = 3 * length - 2;
		indices[icount++] = 3;

		indices[icount++] = -1;
		for (int j = 0; j < length - 3; j++) {
			for (int i = 1; i >= 0; i--) {
				indices[icount++] = 2 * length + i + j;
			}
			for (int i = 1; i >= 0; i--) {
				indices[icount++] = 3 * length + i + j - 2;
			}
			indices[icount++] = -1;
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
		geo.addVertexAttribute(ShaderProgram.ATTR_COLOR, 3, 8);

		return geo;
	}

	/**
	 * Erzeugt ein L-Grid in der XZ-Ebene Kante des Ls liegt "oben links"
	 * 
	 * @param length
	 *            Länge einer Kante
	 * @return TopLeftL Geometrie
	 */
	public static Geometry createTopLeft(int length){

		int vaid = glGenVertexArrays();
		glBindVertexArray(vaid);

		float[] vertices = new float[10 * length + 10 * (length - 1)];
		int[] indices = new int[(length - 2) * 5 + 2 * length + 1];
		int count = 0;

		for (int x = 0; x < length; x++) {
			for (int y = 0; y < 2; y++) {
				vertices[count++] = y;
				vertices[count++] = x;
				vertices[count++] = 0.5f;
				vertices[count++] = 0.5f;
				vertices[count++] = 0.7f;
			}
		}

		for (int x = length - 2; x < length; x++) {
			for (int y = -1; y > -length + 1; y--) {
				vertices[count++] = y;
				vertices[count++] = x;
				vertices[count++] = 0.5f;
				vertices[count++] = 0.5f;
				vertices[count++] = 0.7f;
			}
		}

		int icount = 0;
		for (int i = 0; i < 2 * length; i += 2) {
			indices[icount++] = i + 1;
			indices[icount++] = i;
		}
		indices[icount++] = -1;

		indices[icount++] = 2 * length - 4;
		indices[icount++] = 2 * length;
		indices[icount++] = 2 * length - 2;
		indices[icount++] = 3 * length - 2;

		indices[icount++] = -1;
		for (int j = 0; j < length - 3; j++) {
			for (int i = 0; i < 2; i++) {
				indices[icount++] = 2 * length + i + j;
			}
			for (int i = 0; i < 2; i++) {
				indices[icount++] = 3 * length + i + j - 2;
			}
			indices[icount++] = -1;
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
		geo.addVertexAttribute(ShaderProgram.ATTR_COLOR, 3, 8);

		return geo;
	}

	/**
	 * Erzeugt ein L-Grid in der XZ-Ebene Kante des Ls liegt "unten links"
	 * 
	 * @param length
	 *            Länge einer Kante
	 * @return BottomLeftL Geometrie
	 */

	public static Geometry createBottomLeft(int length){

		int vaid = glGenVertexArrays();
		glBindVertexArray(vaid);

		float[] vertices = new float[10 * length + 10 * (length - 1)];
		int[] indices = new int[(length - 2) * 5 + 2 * length + 1];
		int count = 0;

		for (int x = 0; x < length; x++) {
			for (int y = 0; y < 2; y++) {
				vertices[count++] = y;
				vertices[count++] = x;
				vertices[count++] = 0.5f;
				vertices[count++] = 0.5f;
				vertices[count++] = 0.7f;
			}
		}

		for (int x = 0; x < 2; x++) {
			for (int y = -1; y > -length + 1; y--) {
				vertices[count++] = y;
				vertices[count++] = x;
				vertices[count++] = 0.5f;
				vertices[count++] = 0.5f;
				vertices[count++] = 0.7f;
			}
		}

		int icount = 0;
		for (int i = 0; i < 2 * length; i += 2) {
			indices[icount++] = i + 1;
			indices[icount++] = i;
		}
		indices[icount++] = -1;

		indices[icount++] = 0;
		indices[icount++] = 2 * length;
		indices[icount++] = 2;
		indices[icount++] = 3 * length - 2;

		indices[icount++] = -1;
		for (int j = 0; j < length - 3; j++) {
			for (int i = 0; i < 2; i++) {
				indices[icount++] = 2 * length + i + j;
			}
			for (int i = 0; i < 2; i++) {
				indices[icount++] = 3 * length + i + j - 2;
			}
			indices[icount++] = -1;
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
		geo.addVertexAttribute(ShaderProgram.ATTR_COLOR, 3, 8);

		return geo;
	}
	
	public static Geometry outerTriangle(int length){

		int vaid = glGenVertexArrays();
		glBindVertexArray(vaid);

		float[] vertices = new float[4*length*2];
		int[] indices = new int[4*length+4];
		int count = 0;

		for (int y=0; y<length ; y +=length-1){
		for(int i=0; i< length;i++){
			vertices[count++]= i;
			vertices[count++]= y;
		}}

		
		for(int y=0; y<length; y+=length-1){
		for(int i=0; i<length;i++){
			vertices[count++]=y;
			vertices[count++]=i;
		}}
		
		
		//Indices
		int icount=0;
		    
		int mul=1;
		int k =0;
		for(int j=0; j<4;j++){
			for(int i=k; i<mul*length;i++){
				indices[icount++]=i;
			}
			indices[icount++]=-1;
			k=mul*length;
			mul++;
			
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

}