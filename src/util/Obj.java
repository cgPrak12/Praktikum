/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;
import java.util.List;
import java.util.LinkedList;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Floh1111
 */
public class Obj {
    public List<Face> faceListe;
    public List<Vector3f> vertexList;
    public List<Vector3f> vertexNormalList;
    public List<Vector3f> vertexTextureList;

    
    public Obj() {
        this.faceListe = new LinkedList();
        this.vertexList = new LinkedList();
        this.vertexNormalList = new LinkedList();
        this.vertexTextureList = new LinkedList();
    }
}