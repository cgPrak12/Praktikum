package util;

/**
 *
 * @author nico3000
 */
public class DeferredShader {
    private ShaderProgram drawTextureSP = new
ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/CopyTexture_FS.glsl");
    private Geometry screenQuadGeo = GeometryFactory.createScreenQuad();
    
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
    
    public void DrawTexture(Texture tex) {
        drawTextureSP.use();
        drawTextureSP.setUniform("image", tex);
        screenQuadGeo.draw();
    }
    
    public void delete() {
        //drawTextureSP.
        screenQuadGeo.delete();
    }
}