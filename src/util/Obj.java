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
    public List<Vector3f> vertexList;
    public List<Vector3f> vertexTextureList;
    public List<Vector3f> vertexNormalList;
    public List<Face> faceListe;
    
    public Obj() {
        this.vertexList = new LinkedList();
        this.vertexTextureList = new LinkedList();
        this.vertexNormalList = new LinkedList();
        this.faceListe = new LinkedList();        
    }
}