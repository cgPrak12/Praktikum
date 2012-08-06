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
    private static int textureUnitCounter = 0;
    
    public String materialLibraryName;
    public String materialName;
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
    public Texture textureDiffuseRefColorMap;
    public String dissolveFactColorMap; //map_d, File name of the file
                                        //containing  a color map (i.e. an image
                                        //file
                                        //During rendering, the map_d value is
                                        //multiplied by the d value.
    public Texture textureDissolveFactColorMap;
    public String specularRefColorMap; //map_d, File name of the file
                                        //containing  a color map (i.e. an image
                                        //file
                                        //During rendering, the map_d value is
                                        //multiplied by the d value.
    public Texture textureSpecularRefColorMap;    
    
    public void loadTextures() {
        String texturePath = "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\";
        if(this.diffuseRefColorMap != null && !this.diffuseRefColorMap.isEmpty()) {
            try {
                this.textureDiffuseRefColorMap = Texture.generateTexture(texturePath+diffuseRefColorMap, textureUnitCounter++);
            } catch(IllegalArgumentException e) {
                System.out.println("Failed to load Texture "+texturePath+diffuseRefColorMap);
            }
        }
        if(this.dissolveFactColorMap != null && !this.dissolveFactColorMap.isEmpty()) {
            try {
                this.textureDissolveFactColorMap = Texture.generateTexture(texturePath+dissolveFactColorMap, textureUnitCounter++);
            } catch(IllegalArgumentException e) {
                System.out.println("Failed to load Texture "+texturePath+dissolveFactColorMap);
            }
        }
        if(this.specularRefColorMap != null && !this.specularRefColorMap.isEmpty()) {
            try {
                this.textureSpecularRefColorMap = Texture.generateTexture(texturePath+specularRefColorMap, textureUnitCounter++);
            } catch(IllegalArgumentException e) {
                System.out.println("Failed to load Texture "+texturePath+specularRefColorMap);
            }
        }
    }
    
    @Override
    public String toString() {
        return "materialLibraryName: "+materialLibraryName+"\n"
                +"materialName: "+materialName+"\n"
                +"specularEx: "+specularEx+"\n"
                +"ambientRef: "+ambientRef+"\n"
                +"diffuseRef: "+diffuseRef+"\n"
                +"specularRef: "+specularRef+"\n"
                +"opticalDens: "+opticalDens+"\n"
                +"dissolveFact: "+dissolveFact+"\n"
                +"illuminationModel: "+illuminationModel+"\n"
                +"diffuseRefColorMap: "+diffuseRefColorMap+"\n"
                +"textureDiffuseRefColorMap: "+textureDiffuseRefColorMap+"\n"
                +"dissolveFactColorMap: "+dissolveFactColorMap+"\n"
                +"textureDissolveFactColorMap: "+textureDissolveFactColorMap+"\n"
                +"specularRefColorMap: "+specularRefColorMap+"\n"
                +"textureSpecularRefColorMap: "+textureSpecularRefColorMap+"\n\n";
    }
}