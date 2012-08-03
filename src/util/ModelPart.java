/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Class which represents an geometric ModelPart
 * @author Floh1111
 */
public class ModelPart {
    public String modelPartName; //name of model part
    public int smothingGroup; //0 for off
    public Geometry geometry;
    public Material material;
    
    public String toString() {
        return "modelPartName: "+modelPartName+"\n"
                +"smothingGroup: "+smothingGroup+"\n"
                +"geometry: "+geometry+"\n"
                +"material: "+material+"\n\n";
    }
}