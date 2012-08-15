/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;
import java.io.Serializable;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import java.util.List;

/**
 * This class represents an entry in the Model map.
 * The model map is an two dimiensional array (array[x][z]), that stores the models that
 * will be placed on the terrain.
 * The implementation is serializable and type secure.
 * @author Floh1111
 */
public class ModelMapEntry implements Serializable {
    Matrix4f position;
    Matrix4f scale;
    List<ModelPart> modelList;
    
    public Matrix4f getPosition() {
        return this.position;
    }
    
    public Matrix4f getScale() {
        return this.scale;
    }
    
    public List<ModelPart> getModelList() {
        return this.modelList;
    }
    
    public void setPosition(Matrix4f position) {
        this.position = position;
    }
    
    public void setScale(Matrix4f scale) {
        this.scale = scale;
    }
    
    public void setModelList(List<ModelPart> modelList) {
        this.modelList = modelList;
    }
}