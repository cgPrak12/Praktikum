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
    public String materialLibraryName;
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
    
    @Override
    public String toString() {
        return "materialLibraryName: "+materialLibraryName+"\n"
                +"name: "+name+"\n"
                +"specularEx: "+specularEx+"\n"
                +"ambientRef: "+ambientRef+"\n"
                +"diffuseRef: "+diffuseRef+"\n"
                +"specularRef: "+specularRef+"\n"
                +"opticalDens: "+opticalDens+"\n"
                +"dissolveFact: "+dissolveFact+"\n"
                +"illuminationModel: "+illuminationModel+"\n"
                +"diffuseRefColorMap: "+diffuseRefColorMap+"\n"
                +"dissolveFactColorMap: "+dissolveFactColorMap+"\n\n";
    }
}