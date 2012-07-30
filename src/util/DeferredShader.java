package util;

import opengl.GL;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

/**
 *
 * @author nico3000
 */
public class DeferredShader {
    private ShaderProgram drawTextureSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/CopyTexture_FS.glsl");
    private ShaderProgram fboSP = new ShaderProgram("./shader/Main_VS.glsl", "./shader/Main_FS.glsl");
    private Geometry screenQuadGeo = GeometryFactory.createScreenQuad();
    
    private int frameBufferObjectId;
    
    private int texPosition;
    private int texVertexColor;
    private int texNormal;
    private Camera cam;
    
    public DeferredShader(Camera camTmp) {
    	cam = camTmp;
    }
    
    public void init() {
        // generate frame buffer object
    	frameBufferObjectId = GL30.glGenFramebuffers();
    	
    	// generate textures
    	texPosition = 	 GL11.glGenTextures();
    	texVertexColor = GL11.glGenTextures();
    	texNormal = 	 GL11.glGenTextures();
    	
    	// bind frame buffer object
    	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferObjectId);
    	
    	// bind textures
    	bindTexture(texPosition, GL30.GL_COLOR_ATTACHMENT0);
    	bindTexture(texVertexColor, GL30.GL_COLOR_ATTACHMENT1);
    	bindTexture(texNormal, GL30.GL_COLOR_ATTACHMENT2);
    	
    	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }
    
    public void prepareRendering() {
        // TODO: Aktiviere Framebuffer zum Rendern der Szene
    	
    	Matrix4f modelMatrix = cam.getProjection();
    	Matrix4f modelIT = Util.transposeInverse(modelMatrix, null);
    	fboSP.setUniform("model", modelMatrix);
    	fboSP.setUniform("modelIT", modelIT);
    	fboSP.setUniform("viewProj", cam.getView());
    	fboSP.setUniform("camPos", cam.getCamPos());
    	
    }
    
    public Texture getWorldTexture() {
        return texPosition; // TODO
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
    
    private void bindTexture(int id, int attachment) {
    	GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
    	GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
    	GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    	GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, GL.HEIGHT, GL.WIDTH, 0, GL11.GL_RGBA, GL11.GL_FLOAT, 0);
    	GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, GL11.GL_TEXTURE_2D, id, 0);
    	GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }
}
