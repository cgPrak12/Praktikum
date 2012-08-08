package util;

import opengl.GL;
import static opengl.GL.*;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

/**
 *
 * @author nico3000
 */
public class ShaderProgram {
    private int id, vs, fs;
    
    private int printed = 0;
    
    public ShaderProgram(String vertexShader, String fragmentShader) {
        this.createShaderProgram(vertexShader, fragmentShader);
    }
    
    public void use() {
        glUseProgram(this.id);
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
            if(printed <= 10) {
            	System.err.println("location of " + varName + " is -1");
            	++printed;
            }
        }            
    }          
    
    /**
     * Hilfsmethode, um eine Matrix in eine Uniform zu schreiben. Das
     * zugehoerige Programmobjekt muss aktiv sein.
     * @param matrix Quellmatrix
     * @param varName Zielvariable im Shader
     */
    public void setUniform(String varName, float val) {
        int loc = glGetUniformLocation(this.id, varName);
        if(loc != -1) {
        	GL20.glUniform1f(loc, val);
        } else {
        	if(printed <= 10) {
            	System.err.println("location of " + varName + " is -1");
            	++printed;
            }
        }            
    }
    
    public void setUniform(String varName, Vector3f vector) {
        int loc = glGetUniformLocation(this.id, varName);
        if(loc != -1) {
            glUniform3f(loc, vector.x, vector.y, vector.z);
        }
    }
    
    public void setUniform(String varName, Vector4f vector) {
        int loc = glGetUniformLocation(this.id, varName);
        if(loc != -1) {
            GL20.glUniform4f(loc, vector.x, vector.y, vector.z, vector.w);
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
        } else {
        	if(printed <= 10) {
            	System.err.println("location of " + varName + " is -1");
            	++printed;
            }
        }            
    }
    
    public void setUniform(String varName, Vector2f[] vectorarray) {
    	GL.checkError("");
    	for(int i=0; i < vectorarray.length; ++i) {
    		int loc = glGetUniformLocation(this.id, varName + "[" + i + "]");
    		if(loc != -1) {
    			GL20.glUniform2f(loc, vectorarray[i].x, vectorarray[i].y);
    		} else {
    			if(printed <= 10) {
                	System.err.println("location of " + varName + " is -1");
                	++printed;
                }
    		}
    	}
    	GL.checkError("");
    	
//    	FloatBuffer val = BufferUtils.createFloatBuffer(vectorarray.length * 2);
//        if(loc != -1) {
//        	for(int i = 0; i < vectorarray.length; ++i) {
//        		val.put(vectorarray[i].x);
//        		val.put(vectorarray[i].y);
//        	}
//        	GL20.glUniform2(loc, val);
//        }	
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
    public static final int ATTR_TANGENT = 6;
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
        glBindAttribLocation(this.id, ATTR_TANGENT, "tangentMC");
        //glBindAttribLocation(this.id, ATTR_COLOR2, "vertexColor2");
        glBindAttribLocation(this.id, ATTR_TEX, "texCoords");
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
    
    public int getId() {
    	return this.id;
    }
}
