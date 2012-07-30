package util;

import static opengl.GL.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * Kapselt ein Vertexarray Object.
 * @author Sascha Kolodzey, Nico Marniok
 */
public class Geometry {
    private int vaid = -1;                  // vertex array id
    private FloatBuffer vertexValueBuffer;  // vertex buffer values
    private IntBuffer indexValueBuffer;     // index buffer values
    private int topology;                   // index topology
    private int indexCount;                 // number of indices
    private int vbid;                       // geometry vertex buffer
    private int ibid;                       // geometry index buffer
    private final List<VertexAttribute> attributes = new LinkedList<>();

    /**
     * Setzt den IntBuffer, der die Indexdaten dieser Geometrie beinhaltet und
     * die zugehoerige Topologie.
     * <b>Obacht:</b> Diese Methode erzeugt <i>keinen</i> Buffer auf der GPU.
     * @param indices Buffer, der die Indexdaten beinhaltet
     * @param topology Zugehoerige Topologie
     */
    public void setIndices(IntBuffer indices, int topology) {
        indexValueBuffer = indices;
        indexCount = indices.capacity();
        this.topology = topology;
    }

    /**
     * Setzt den FloatBuffer, der die Indexdaten dieser Geometrie beinhaltet.
     * <b>Obacht:</b> Diese Methode erzeugt <i>keinen</i> Buffer auf der GPU.
     * @param vertices 
     */
    public void setVertices(FloatBuffer vertices) {
        vertexValueBuffer = vertices;
    }
    
    /**
     * Erweitert die Geometrie um ein Vertexattribut vom Typ GL_FLOAT.
     * @param index Index / Location des Attributs
     * @param size Anzahl der Komponenten des Attributs
     * @param offset Offset im Vertex gemessen in Byte
     */
    public void addVertexAttribute(int index, int size, int offset) {
        VertexAttribute attr = new VertexAttribute();
        attr.index = index;
        attr.size = size;
        attr.offset = offset;
        attributes.add(attr);
    }
    
    /**
     * Loescht alle Attribute, die mittels <code>addVertexAttribute</code>
     * hinzugefuegt wurden.
     */
    public void clearVertexAttributes() {
        attributes.clear();
    }
    
    /**
     * Erzeugt aus den gesetzten Vertex- und Indexdaten ein Vertexarray Object,
     * das die zugoerige Topologie beinhaltet.
     */
    public void construct() {
        if(vertexValueBuffer == null || indexValueBuffer == null) {
            throw new UnsupportedOperationException("Vertex- und Indexbuffer wurden noch nicht gesetzt!");
        }
        
        this.vaid = glGenVertexArrays();
        glBindVertexArray(this.vaid);
        
        this.vbid = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vbid);
        glBufferData(GL_ARRAY_BUFFER, this.vertexValueBuffer, GL_STATIC_DRAW);
        
        this.ibid = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.ibid);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, this.indexValueBuffer, GL_STATIC_DRAW);
        
        int stride = 0;
        for(VertexAttribute attr : attributes) {
            stride += 4 * attr.size;
            glEnableVertexAttribArray(attr.index);
        }
        for(VertexAttribute attr : attributes) {
            glVertexAttribPointer(attr.index, attr.size, GL_FLOAT, false, stride, attr.offset);
        }
    }
    
    /**
     * Loescht alle GPU Resourcen dieser Geometrie, die in
     * <code>construct()</code> generiert wurden (Indexbuffer, Vertexbuffer und 
     * das Vertexarray Object).
     */
    public void delete() {
        glDeleteBuffers(vbid);
        vbid = -1;
        glDeleteBuffers(ibid);
        ibid = -1;
        glDeleteVertexArrays(vaid);
        vaid = -1;
    }
    
    /**
     * Erzeugt die Geometrie, falls noch nicht geschehen und zeichnet sie
     * anschließend.
     */
    public void draw() {
        if(vaid == -1) {
            construct();
        }
        glBindVertexArray(vaid);
        glDrawElements(topology, indexCount, GL_UNSIGNED_INT, 0);
    }
    
    private class VertexAttribute {
        /**
         * Attribut index / location
         */
        private int index;
        
        /**
         * Anzahl der Elemente des Attributs
         */
        private int size;
        
        /**
         * Offset des Attributs in einem Vertex, gemessen in Byte
         */
        private int offset;
    }
}
