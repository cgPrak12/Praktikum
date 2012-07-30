package util;

import static opengl.GL.*;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author nico3000
 */
public class ShaderProgram {
    private int id = -1;
    private final Map<String, Integer> uniformLocations = new HashMap<>();
    
    public void compile(String vertexShader, String fragmentShader) {
        this.id = Util.createShaderProgram(vertexShader, fragmentShader);
    }
    
    public void use() {
        glUseProgram(this.id);
    }
    
    private int getUniformLocation(String name) {
        this.use();
        if(!this.uniformLocations.containsKey(name)) {
            int location = glGetUniformLocation(this.id, name);
            this.uniformLocations.put(name, location);
        }
        return this.uniformLocations.get(name);
    }
    
    public void setUniform(String name, float val) {
        int location = this.getUniformLocation(name);
        if(location != -1) {
            glUniform1f(location, val);
        }
    }
    
    public void setUniform(String name, Vector3f vec) {
        int location = this.getUniformLocation(name);
        if(location != -1) {
            glUniform3f(location, vec.x, vec.y, vec.z);
        }
    }
    
    public void setUniform(String name, Matrix4f mat) {
        int location = this.getUniformLocation(name);
        if(location != -1) {
            Util.MAT_BUFFER.position(0);
            mat.store(Util.MAT_BUFFER);
            Util.MAT_BUFFER.position(0);
            glUniformMatrix4(location, false, Util.MAT_BUFFER);
            Util.MAT_BUFFER.position(0);
        }
    }
    
    public void setUniform(String name, Texture texture) {
        int location = this.getUniformLocation(name);
        if(location != -1) {
            glUniform1i(location, texture.getSlot());
        }        
    }
}
