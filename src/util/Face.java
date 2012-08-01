/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Floh1111
 */
public class Face {
    Vector3f vertexIndizies;
    Vector3f vertexTextureIndizies;
    Vector3f vertexNormalIndizies;
    
    public Face(int vertexOne, int vertexTwo, int vertexThree, int normalOne, int normalTwo, int normalThree) {
        this.vertexIndizies = new Vector3f((float)vertexOne, (float)vertexTwo, (float)vertexThree);
        this.vertexNormalIndizies = new Vector3f((float)normalOne, (float)normalTwo, (float)normalThree);
    }
    
    public Face(int vertexOne, int vertexTwo, int vertexThree) {
        this.vertexIndizies = new Vector3f((float)vertexOne, (float)vertexTwo, (float)vertexThree);
    }
}