package util;

import static opengl.GL.GL_FRAGMENT_SHADER;
import static opengl.GL.GL_VERTEX_SHADER;
import static opengl.GL.glAttachShader;
import static opengl.GL.glBindAttribLocation;
import static opengl.GL.glCompileShader;
import static opengl.GL.glCreateProgram;
import static opengl.GL.glCreateShader;
import static opengl.GL.glGetProgramInfoLog;
import static opengl.GL.glGetShaderInfoLog;
import static opengl.GL.glGetUniformLocation;
import static opengl.GL.glLinkProgram;
import static opengl.GL.glShaderSource;
import static opengl.GL.glUniform1i;
import static opengl.GL.glUniformMatrix4;
import static opengl.GL.glUseProgram;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author nico3000
 */
public class ShaderProgram {
    private int id, vs, fs;
    
    public ShaderProgram(String vertexShader, String fragmentShader) {
        this.createShaderProgram(vertexShader, fragmentShader);
    }
    
    public void use() {
        glUseProgram(this.id);
    }            
    
    public int getID(){
    	return this.id;
    }
    
    /**
     * Hilfsmethode, um eine Matrix in eine Uniform zu schreiben. Das
     * zugehoerige Programmobjekt muss aktiv sein.
     * @param matrix Quellmatrix
     * @param varName Zielvariable im Shader
     */
    public void setUniform(String varName, Matrix4f matrix) {
        int loc = glGetUniformLocation(this.id, varName);
        if(loc != -1) {
            Util.MAT_BUFFER.position(0);
            matrix.store(Util.MAT_BUFFER);
            Util.MAT_BUFFER.position(0);
            glUniformMatrix4(loc, false, Util.MAT_BUFFER);
            Util.MAT_BUFFER.position(0);
        } else {
            System.err.println(varName);
        }
            
    }
    
    public void setUniform(String varName, Vector3f vec) {
    	int loc = glGetUniformLocation(this.id, varName);
    	if(loc != -1) {
    		GL20.glUniform3f(loc, vec.x, vec.y, vec.z);
    	} else {
            System.err.println(varName);
        }
    	
    }
    
    /**
     * Hilfsmethode, um eine Textur in eine Uniform zu schreiben. Das
     * zugehoerige Programmobjekt muss aktiv sein.
     * @param texture Textur
     * @param varName Zielvariable im Shader
     */
    public void setUniform(String varName, Texture texture) {
        int loc = glGetUniformLocation(this.id, varName);
        if(loc != -1) {
            texture.bind();
            glUniform1i(loc, texture.getUnit());
        }
    }
    
    /**
     * Attribut Index von positionMC
     */
    public static final int ATTR_POS = 0;

    /**
     * Attribut Index von normalMC
     */
    public static final int ATTR_NORMAL = 1;

    /**
     * Attribut Index von vertexColor
     */
    public static final int ATTR_COLOR = 2;
    
    /**
     * Attribut Index von vertexColor2
     */
    public static final int ATTR_COLOR2 = 3;
    
    /**
     * Attribut Index von tex
     */
    public static final int ATTR_TEX = 4;
    
    /**
     * Attribut Index von instance
     */
    public static final int ATTR_INSTANCE = 5;
    
    /**
     * Erzeugt ein ShaderProgram aus einem Vertex- und Fragmentshader.
     * @param vs Pfad zum Vertexshader
     * @param fs Pfad zum Fragmentshader
     * @return ShaderProgram ID
     */
    private void createShaderProgram(String vs, String fs) {
        this.id = glCreateProgram();
        
        this.vs = glCreateShader(GL_VERTEX_SHADER);
        this.fs = glCreateShader(GL_FRAGMENT_SHADER);
        
        glAttachShader(this.id, this.vs);
        glAttachShader(this.id, this.fs);
        
        String vertexShaderContents = Util.getFileContents(vs);
        String fragmentShaderContents = Util.getFileContents(fs);
        
        glShaderSource(this.vs, vertexShaderContents);
        glShaderSource(this.fs, fragmentShaderContents);
        
        glCompileShader(this.vs);
        glCompileShader(this.fs);
        
        String log;
        log = glGetShaderInfoLog(this.vs, 1024);
        System.out.print(log);
        log = glGetShaderInfoLog(this.fs, 1024);
        System.out.print(log);
        
        glBindAttribLocation(this.id, ATTR_POS, "positionMC");
        glBindAttribLocation(this.id, ATTR_NORMAL, "normalMC");        
        glBindAttribLocation(this.id, ATTR_COLOR, "vertexColor");
        glBindAttribLocation(this.id, ATTR_COLOR2, "vertexColor2");
        glBindAttribLocation(this.id, ATTR_TEX, "vertexTexCoords");
        glBindAttribLocation(this.id, ATTR_INSTANCE, "instancedData");
        
        glLinkProgram(this.id);        
        
        log = glGetProgramInfoLog(this.id, 1024);
        System.out.print(log);
    }
    
    public void delete() {
        GL20.glDetachShader(this.id, this.fs);
        GL20.glDetachShader(this.id, this.vs);
        GL20.glDeleteShader(this.fs);
        GL20.glDeleteShader(this.vs);
        GL20.glDeleteProgram(this.id);
    }
}
