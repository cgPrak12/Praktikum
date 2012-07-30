package util;

import static opengl.GL.GL_ARRAY_BUFFER;
import static opengl.GL.GL_FLOAT;
import static opengl.GL.GL_STATIC_DRAW;
import static opengl.GL.GL_POINTS;
import static opengl.GL.glBindBuffer;
import static opengl.GL.glBindVertexArray;
import static opengl.GL.glBufferData;
import static opengl.GL.glEnableVertexAttribArray;
import static opengl.GL.glGenBuffers;
import static opengl.GL.glGenVertexArrays;
import static opengl.GL.glVertexAttribPointer;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;


public class Particle {
    
    private Vector3f position = new Vector3f();
    private Vector3f color = new Vector3f(0.0f,0.0f,1.0f);
    private float radius = 1.0f;
    //private float speed = 0.0f;
    
    // construct variables
    private int vaid = -1;                  // vertex array id
    private int vbid;                       // vertex buffer id
    private FloatBuffer vertexValueBuffer = BufferUtils.createFloatBuffer(7);  // vertex buffer values
    
    public void setPosition(Vector3f position) {
    	this.position = position;
    }
    
    
    public void setColor(Vector3f color) {
        this.color.set(color);
    }
    
    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setVertexBuffer() {
        vertexValueBuffer.put(position.x);
        vertexValueBuffer.put(position.y);
        vertexValueBuffer.put(position.z);
        vertexValueBuffer.put(color.x);
        vertexValueBuffer.put(color.y);
        vertexValueBuffer.put(color.z);
        vertexValueBuffer.put(radius);
        vertexValueBuffer.position(0);
    }
    
    /**
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    */
    
    public void draw() {
        
        if(vaid == -1) {
            construct();
        }
        glBindVertexArray(vaid);

        GL11.glDrawArrays(GL_POINTS, 0, 1); 

    }
    
    /**
     * Erzeugt aus den gesetzten Vertex- und Indexdaten ein Vertexarray Object,
     * das die zugoerige Topologie beinhaltet.
     */
    public void construct() {
        
    	setVertexBuffer();
    	
        this.vaid = glGenVertexArrays();
        glBindVertexArray(this.vaid);
        
        this.vbid = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vbid);
        glBufferData(GL_ARRAY_BUFFER, this.vertexValueBuffer, GL_STATIC_DRAW);
        
        glEnableVertexAttribArray(Util.ATTR_POS);
        glEnableVertexAttribArray(Util.ATTR_COLOR);
        glEnableVertexAttribArray(Util.ATTR_INSTANCE);

        glVertexAttribPointer(Util.ATTR_POS, 3, GL_FLOAT, false, 28, 0);
        glVertexAttribPointer(Util.ATTR_COLOR, 3, GL_FLOAT, false, 28, 12);
        glVertexAttribPointer(Util.ATTR_INSTANCE, 1, GL_FLOAT, false, 28, 24);

        glBindVertexArray(0);
    }
	
}
