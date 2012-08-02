/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author Floh1111
 */
public class Object {
    public String name; //name ob object
    public String material; //material name
    public int smothingGroup; //0 for off
    public FloatBuffer vertexBuffer;
    public IntBuffer indexBuffer;
}