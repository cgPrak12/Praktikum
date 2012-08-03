package util;

/**
 *
 * @author nico3000
 */
public class DeferredShader {
    private ShaderProgram drawTextureSP;

    public DeferredShader(ShaderProgram shader) {
        this.drawTextureSP = shader;
    }
    
    public void init() {
        // TODO
    }
    
    public void prepareRendering() {
        // TODO: Aktiviere Framebuffer zum Rendern der Szene
    }
    
    public Texture getWorldTexture() {
        return null; // TODO
    }
    
    public Texture getNormalTexture() {
        return null; // TODO
    }
    
    public Texture getDiffuseTexture() {
        return null; // TODO
    }
    
    public void draw(Geometry geometry, Texture texture) {
        drawTextureSP.use();
        drawTextureSP.setUniform("image", texture);
        geometry.draw();
    }
    
/*    public void DrawTexture(Geometry geometry, Texture texture) {
        drawTextureSP.use();
        drawTextureSP.setUniform("image", texture);
        geometry.draw();
    }*/
    
    public void delete() {
        //drawTextureSP.
        //screenQuadGeo.delete();
    }
}
