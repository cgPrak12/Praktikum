/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;
import java.nio.FloatBuffer;
import java.util.*;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Floh1111
 */
public class BufferConstructor implements Runnable {
    private Face currentFace;
    private List<Vector3f> vertexList;
    private List<Vector3f> vertexTextureList;
    private List<Vector3f> vertexNormalList;
    private FloatBuffer vertexBuffer;
    private int position;
    private Vector3f[] vertexArray;
    
    public BufferConstructor(Face currentFace, Vector3f[] vertexArray, int position, FloatBuffer vertexBuffer, List<Vector3f> vertexList, List<Vector3f> vertexTextureList, List<Vector3f> vertexNormalList) {
        this.currentFace = currentFace;
        this.vertexArray = vertexArray;
        this.position = position;
        this.vertexBuffer = vertexBuffer;
        this.vertexList = vertexList;
        this.vertexTextureList = vertexTextureList;
        this.vertexNormalList = vertexNormalList;
    }
    
    public void run() {
        //Get vertex coordinates of the first vertex of the triangle
        vertexArray[position++] = vertexList.get((int)currentFace.vertexIndizies.x);
        //check if the model has texture coordinates
        if(currentFace.vertexTextureIndizies.length()!=0)
           vertexArray[position++] = new Vector3f(vertexTextureList.get((int)currentFace.vertexTextureIndizies.x).x,
                                         vertexTextureList.get((int)currentFace.vertexTextureIndizies.x).y*-1.0f,
                                         vertexTextureList.get((int)currentFace.vertexTextureIndizies.x).z);
        else
            vertexArray[position++] = new Vector3f();
        //check if the model has normals
        if(currentFace.vertexNormalIndizies.length()!=0)
            vertexArray[position++] = vertexNormalList.get((int)currentFace.vertexNormalIndizies.x);
        else
            vertexArray[position++] = new Vector3f();
        //----------------------------------------------
        //Get vertex coordinates of the first vertex of the triangle
        vertexArray[position++] = vertexList.get((int)currentFace.vertexIndizies.y);
        //check if the model has texture coordinates
        if(currentFace.vertexTextureIndizies.length()!=0)
            vertexArray[position++] = new Vector3f(vertexTextureList.get((int)currentFace.vertexTextureIndizies.y).x,
                                          vertexTextureList.get((int)currentFace.vertexTextureIndizies.y).y*-1.0f,
                                          vertexTextureList.get((int)currentFace.vertexTextureIndizies.y).z);
        else
            vertexArray[position++] = new Vector3f();
        //check if the model has normals
        if(currentFace.vertexNormalIndizies.length()!=0)
            vertexArray[position++] = vertexNormalList.get((int)currentFace.vertexNormalIndizies.y);
        else
            vertexArray[position++] = new Vector3f();
        //----------------------------------------------
        //Get vertex coordinates of the first vertex of the triangle
        vertexArray[position++] = vertexList.get((int)currentFace.vertexIndizies.z);
        //check if the model has texture coordinates
        if(currentFace.vertexTextureIndizies.length()!=0)
            vertexArray[position++] = new Vector3f(vertexTextureList.get((int)currentFace.vertexTextureIndizies.z).x,
                                          vertexTextureList.get((int)currentFace.vertexTextureIndizies.z).y*-1.0f,
                                          vertexTextureList.get((int)currentFace.vertexTextureIndizies.z).z);
        else
            vertexArray[position++] = new Vector3f();
        //check if the model has normals
        if(currentFace.vertexNormalIndizies.length()!=0)
            vertexArray[position++] = vertexNormalList.get((int)currentFace.vertexNormalIndizies.z);
        else
            vertexArray[position++] = new Vector3f();
    }
}
