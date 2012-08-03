/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Class which represents an geometric object Object
 * @author Floh1111
 */
public class Object {
    public String name; //name ob object
    public String materialLibrary; //material name
    public String material; //material name
    public int smothingGroup; //0 for off
    public FloatBuffer vertexBuffer;
    public IntBuffer indexBuffer;

    public String toString() {
        return "name: "+name+"\n"
                +"materialLibrary: "+materialLibrary+"\n"
                +"material: "+material+"\n"
                +"smothingGroup: "+smothingGroup+"\n"
                +"vertexBuffer: "+vertexBuffer+"\n"
                +"indexBuffer: "+indexBuffer+"\n\n";
    }
}