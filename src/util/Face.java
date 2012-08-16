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
    
    public Face(Vector3f vertexIndizies, Vector3f textureIndizies, Vector3f normalIndizies) {
        
        this.vertexIndizies = vertexIndizies;
        this.vertexTextureIndizies = textureIndizies;
        this.vertexNormalIndizies = normalIndizies;
    }
    
    public Face(Vector3f vertexIndizies) {
        this(vertexIndizies, new Vector3f(), new Vector3f());
    }
}