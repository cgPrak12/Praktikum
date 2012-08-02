/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;
import org.lwjgl.util.vector.Vector3f;

/**
 * Class which represents an Material specified in an MTL file. Watch this
 * website for specification: http://paulbourke.net/dataformats/mtl/
 * @author Floh1111
 */
public class Material {
    public String name;
    public float specularEx; //Ns, Specular exponent
    public Vector3f ambientRef; //Ka, ambient reflectance
    public Vector3f diffuseRef; //Kd, diffuse reflectance
    public Vector3f specularRef; //Ks, specular reflectance
    public float opticalDens; //Ni, optical density
    public float dissolveFact; //d, dissolve factor (scalar)
    public int illuminationModel; //illum, illumination model used for material
    public String diffuseRefColorMap; //map_Kd, File name of the file containing
                                      //a color map (i.e. an image file).
                                      //During rendering, the map_Kd value is
                                      //multiplied by the Kd value.
    public String dissolveFactColorMap; //map_d, File name of the file
                                        //containing  a color map (i.e. an image
                                        //file
                                        //During rendering, the map_d value is
                                        //multiplied by the d value.
    
    public Material(String name, float specularEx, Vector3f ambientRef,
                    Vector3f diffuseRef, Vector3f specularRef,
                    float opticalDens, float dissolveFact,
                    int illuminationModel, String diffuseRefColorMap,
                    String dissolveFactColorMap) {
        this.name = name;
        this.specularEx = specularEx;
        this.ambientRef = ambientRef;
        this.diffuseRef = diffuseRef;
        this.specularRef = specularRef;
        this.opticalDens = opticalDens;
        this.dissolveFact = dissolveFact;
        this.illuminationModel = illuminationModel;
        this.diffuseRefColorMap = diffuseRefColorMap;
        this.dissolveFactColorMap = dissolveFactColorMap;
    }
}