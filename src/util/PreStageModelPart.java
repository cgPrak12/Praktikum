/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import static opengl.GL.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import opengl.GL;
import opengl.OpenCL;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import util.*;
import java.util.*;

/**
 *
 * @author Floh1111
 */
public class PreStageModelPart {
    public String materialLibrarayName;
    public String materialName;
    public int smoothingGroup;
    public FloatBuffer vertexBuffer;
    public IntBuffer indexBuffer;
    
    public ModelPart createModelPart(List materialList) {
        System.out.println("Create ModelPart...");

        int vaid = glGenVertexArrays();
        glBindVertexArray(vaid);
        
        ModelPart modelPart = new ModelPart();
        Geometry geo = new Geometry();

        geo.setIndices(this.indexBuffer, GL_TRIANGLES);
        geo.setVertices(this.vertexBuffer);
        geo.addVertexAttribute(ShaderProgram.ATTR_POS, 3, 0);
        geo.addVertexAttribute(ShaderProgram.ATTR_TEX, 3, 12);
        geo.addVertexAttribute(ShaderProgram.ATTR_NORMAL, 3, 24);
        modelPart.geometry = geo;
        Iterator<Material> materialListIterator = materialList.listIterator();
        while(materialListIterator.hasNext()) {
            Material currentMaterial = materialListIterator.next();
            if(currentMaterial.materialName.equals(this.materialName))
                modelPart.material = currentMaterial;
        }
        
        return modelPart;
    }
    
    public void createBuffers( List<Face>faceList, List<Vector3f>vertexList,
                               List<Vector3f>vertexTextureList,
                               List<Vector3f>vertexNormalList) {
        System.out.println("Create Buffers...");
        //Erzeuge vertex Buffer
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(faceList.size()*3*3*3);
        //Erzeuge index Buffer
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(faceList.size()*3);
        //Schreibe Indexdaten aus der Index Liste in den Index Buffer
        Iterator<Face> faceIterator = faceList.listIterator();

        //Loop through all faces
        int counter= 0;
        Vector3f[] vertexArray = new Vector3f[faceList.size()*3*3*3];
        int position = 0;
        ThreadGroup bufferCreatorThreadGroup = new ThreadGroup("buffer creator threads");
        while(faceIterator.hasNext()) {
            BufferConstructor bufferConstrucot = new BufferConstructor(faceIterator.next(), vertexArray, position, vertexBuffer, vertexList, vertexTextureList, vertexNormalList);
            Thread threadObject = new Thread(bufferCreatorThreadGroup, bufferConstrucot);
            threadObject.start();
            position += 9;
        }
        
        for(int i=0; i<faceList.size()*3; i++) {
            indexBuffer.put(i);
        }
        
        //wait if the buffer array us fully created (each thread finished)
        while(bufferCreatorThreadGroup.activeCount()>0);
        
        //sore vertex array into buffer
        for(int i=0; i<vertexArray.length; i++) {
            if(vertexArray[i]!=null)
                vertexArray[i].store(vertexBuffer);
        }
        
        
        vertexBuffer.position(0);
        indexBuffer.position(0);

        this.vertexBuffer = vertexBuffer;
        this.indexBuffer = indexBuffer;        
    }

    public String toString() {
        return "materialLibrarayName: "+materialLibrarayName+"\n"
                +"materialName: "+materialName+"\n"
                +"vertexBuffer: "+vertexBuffer+"\n"
                +"indexBuffer: "+indexBuffer+"\n\n";
    }
}