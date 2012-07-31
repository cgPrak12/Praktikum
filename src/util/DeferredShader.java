package util;

import static opengl.GL.GL_COLOR_BUFFER_BIT;
import static opengl.GL.GL_DEPTH_BUFFER_BIT;
import static opengl.GL.glClear;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import opengl.GL;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
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
    
    private int frameBufferObjectId;
    
    // TODO: ADD different textures
	private Texture texPosition;
	private Texture texVertexColor;
	private Texture texNormal;
    private Camera cam;
    
    public DeferredShader(Camera camTmp) {
    	cam = camTmp;
    }
   
    public void init() {
        // generate frame buffer object
    	frameBufferObjectId = GL30.glGenFramebuffers();
    	
    	// generate textures
    	// TODO: ADD different textures
    	texPosition = 	 new Texture(GL11.GL_TEXTURE_2D, 0);
    	texVertexColor = new Texture(GL11.GL_TEXTURE_2D, 0);
    	texNormal = 	 new Texture(GL11.GL_TEXTURE_2D, 0);
    	
    	// bind frame buffer object
    	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferObjectId);
    	
    	// bind textures to attachments
    	// TODO: BIND added textures
    	bindTexture(texPosition, 	GL30.GL_COLOR_ATTACHMENT0, 8);
    	bindTexture(texVertexColor, GL30.GL_COLOR_ATTACHMENT1, 8);
    	bindTexture(texNormal, 		GL30.GL_COLOR_ATTACHMENT2, 8);
    	
    	// draw buffers
    	int[] buffersArray = {
    			GL30.GL_COLOR_ATTACHMENT0,
    			GL30.GL_COLOR_ATTACHMENT1,
    			GL30.GL_COLOR_ATTACHMENT2
    	};
    	IntBuffer buffers = BufferUtils.createIntBuffer(buffersArray.length);
    	buffers.put(buffersArray);
    	GL20.glDrawBuffers(buffers);
    	
    	// unbind frame buffer object
    	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }
    
    public void prepareRendering() {
   		fboSP.use();
   		GL30.glBindFragDataLocation(fboSP.getId(), GL30.GL_COLOR_ATTACHMENT0, "position");
   		GL30.glBindFragDataLocation(fboSP.getId(), GL30.GL_COLOR_ATTACHMENT1, "normal");
   		GL30.glBindFragDataLocation(fboSP.getId(), GL30.GL_COLOR_ATTACHMENT2, "color");

   		
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
    
    public Texture getWorldTexture() {
        return texPosition;
    }
    
    public Texture getNormalTexture() {
        return texNormal;
    }
    
    public Texture getDiffuseTexture() {
        return texVertexColor;
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
    
    /**
     * Binds a texture to the current Frame Buffer Object.
     * @param Texture texture to bind
     * @param attachment Must be GL_COLOR_ATTACHMENT0-15, binding point
     * @param depth color depth (8, 16 or 32 [bit])
     */
    private void bindTexture(Texture texture, int attachment, int depth) {
    	// get color format
    	int internalFormat;
    	switch(depth) {
    		case 32:          internalFormat = GL30.GL_RGBA32F; break;
    		case 16:          internalFormat = GL30.GL_RGBA16F; break;
    		case  8: default: internalFormat = GL11.GL_RGBA8; break;
    	}
    	
    	// bind texture
    	texture.bind();
    	
    	// add filters
    	GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
    	GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    	
    	// set texture information
    	GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, internalFormat, GL.HEIGHT, GL.WIDTH, 0, GL11.GL_RGBA, GL11.GL_FLOAT, (FloatBuffer) null);
    	
    	// attach texture to framebuffer
    	GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, GL11.GL_TEXTURE_2D, texture.getId(), 0);
    	
    }
}
