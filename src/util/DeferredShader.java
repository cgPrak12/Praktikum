package util;

import static opengl.GL.GL_COLOR_BUFFER_BIT;
import static opengl.GL.GL_DEPTH_BUFFER_BIT;
import static opengl.GL.glClear;

import java.nio.FloatBuffer;

import opengl.GL;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Matrix4f;

/**
 *
 * @author nico3000
 */
public class DeferredShader {
    private ShaderProgram drawTextureSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/CopyTexture_FS.glsl");
    private ShaderProgram fboSP         = new ShaderProgram("./shader/Main_VS.glsl", "./shader/Main_FS.glsl");
    private Geometry screenQuadGeo = GeometryFactory.createScreenQuad();
    private Geometry testCube 	   = GeometryFactory.createCube();
    
    private FrameBuffer frameBuffer = new FrameBuffer();
    
    // TODO: ADD different textures
	private Texture texPosition;
	private Texture texNormal;
	private Texture texVertexColor;

    private Camera cam;
    
    public DeferredShader(Camera camTmp) {
    	cam = camTmp;
    }
   
    public void init() {
    	
    	frameBuffer.init();
    	
        // generate frame buffer object
    	frameBufferObjectId = GL30.glGenFramebuffers();
    	
    	// generate textures
    	// TODO: ADD different textures
    	texPosition = 	 new Texture(GL11.GL_TEXTURE_2D, 0);
    	texVertexColor = new Texture(GL11.GL_TEXTURE_2D, 0);
    	texNormal = 	 new Texture(GL11.GL_TEXTURE_2D, 0);
    	
    	// bind frame buffer object
//    	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferObjectId);
    	
    	// bind textures to attachments
    	// TODO: BIND added textures
//    	bindTexture(texPosition, 	GL30.GL_COLOR_ATTACHMENT0, 8);
//    	bindTexture(texVertexColor, GL30.GL_COLOR_ATTACHMENT1, 8);
//    	bindTexture(texNormal, 		GL30.GL_COLOR_ATTACHMENT2, 8);
    	
    	frameBuffer.addTexture(texPosition, 8);
    	frameBuffer.addTexture(texVertexColor, 8);
    	frameBuffer.addTexture(texNormal, 8);
    	
    	// draw buffers (fbo): activate texture, fragdatalocation (sp) oder sowas in der Art
    	
    	
    	// unbind frame buffer object
//    	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }
    
    public void prepareRendering() {
   		fboSP.use();

    	Matrix4f modelMatrix = new Matrix4f();
    	
    	Matrix4f modelIT = Util.transposeInverse(modelMatrix, null);
    	fboSP.setUniform("model", 	 modelMatrix);
    	fboSP.setUniform("modelIT",  modelIT);
    	fboSP.setUniform("viewProj", Util.mul(null, cam.getProjection(), cam.getView()));
    	fboSP.setUniform("camPos", 	 cam.getCamPos());
    	
    	// bind Frame Buffer Object
    	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferObjectId);

    	// set clear color
    	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    	// draw to texture
    	testCube.draw();
    	
    	// unbind Frame Buffer Object
    	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }
    
    //Hinweis: In der w-Komponente der Koordinaten steht der Abstand des Punktes zur Kamera
    public Texture getWorldTexture() {
        return frameBuffer.getWorldTexture();
    }
    
    public Texture getNormalTexture() {
        return frameBuffer.getNormalTexture();
    }
    
    public Texture getDiffuseTexture() {
        return frameBuffer.getDiffuseTexture();
    }
    
    public void DrawTexture(Texture tex) {
        drawTextureSP.use();
        drawTextureSP.setUniform("image", tex);
        screenQuadGeo.draw();
    }
    
    public void delete() {
        drawTextureSP.delete();
        screenQuadGeo.delete();
        
        fboSP.delete();
        // TODO: delete textures
        texPosition.delete();
        texNormal.delete();
        texVertexColor.delete();
    }
    

}
