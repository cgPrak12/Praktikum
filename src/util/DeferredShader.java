package util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

/**
 * Usage:
 * - ShaderProgram.use()
 * - set Uniforms
 * - DeferredShader.prepareRendering(ShaderProgram )
 * - (optional: DeferredShader.clear() )
 * - draw-Aufruf der Geometry
 * - DeferredShader.finish()
 * - DeferredShader.drawTexture(Texture ...)
 * FragmentShader braucht als out-Variablen position, normal, color!
 * @author nico3000
 */
public class DeferredShader {
    private ShaderProgram drawTextureSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/CopyTexture_FS.glsl");
    
    private Geometry screenQuadGeo = GeometryFactory.createScreenQuad();
    
    private FrameBuffer frameBuffer = new FrameBuffer();
    
	private Texture texPosition;
	private Texture texNormal;
	private Texture texVertexColor;

    private Camera cam;
    
    public DeferredShader(Camera camTmp) {
    	cam = camTmp;
    }
   
    public void init() {
    	
    	// generate textures
    	texPosition = 	 new Texture(GL11.GL_TEXTURE_2D, 0);
    	texVertexColor = new Texture(GL11.GL_TEXTURE_2D, 0);
    	texNormal = 	 new Texture(GL11.GL_TEXTURE_2D, 0);
    	    	
    	frameBuffer.addTexture(texPosition, GL11.GL_RGBA8, GL11.GL_RGBA);
    	frameBuffer.addTexture(texVertexColor, GL11.GL_RGBA8, GL11.GL_RGBA);
    	frameBuffer.addTexture(texNormal, GL11.GL_RGBA8, GL11.GL_RGBA);
    	
    	frameBuffer.drawBuffers();
    }
    
    public void prepareRendering(ShaderProgram shaderProgram) {
    	shaderProgram.use();
   		GL30.glBindFragDataLocation(shaderProgram.getId(), 0, "position");
   		GL30.glBindFragDataLocation(shaderProgram.getId(), 1, "normal");
   		GL30.glBindFragDataLocation(shaderProgram.getId(), 2, "color");
    	shaderProgram.setUniform("camPos", 	 cam.getCamPos());
   		
   		frameBuffer.bind();
    }
    
    public void clear() {
    	frameBuffer.clearColor(); 
    }
    
    public void finish() {
    	frameBuffer.unbind();
    }
    
    // Hinweis: In der w-Komponente der World-Koordinaten steht der Abstand des Punktes zur Kamera
    public Texture getWorldTexture() {
        return frameBuffer.getTexture(0);
    }
    
    public Texture getNormalTexture() {
        return frameBuffer.getTexture(1);
    }
    
    public Texture getDiffuseTexture() {
        return frameBuffer.getTexture(2);
    }
    
    public void DrawTexture(Texture tex) {
        drawTextureSP.use();
        drawTextureSP.setUniform("image", tex);
        screenQuadGeo.draw();
    }
    
    public void delete() {
        drawTextureSP.delete();
        screenQuadGeo.delete();
        
        texPosition.delete();
        texNormal.delete();
        texVertexColor.delete();
    }

}
