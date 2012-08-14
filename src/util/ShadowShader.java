package util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;

import opengl.GL;

public class ShadowShader {
	//fixed opengl values
	private static final int TEXTURE = GL11.GL_TEXTURE_2D;
	private static final int RGBA = GL11.GL_RGBA;
	
	//shader program
	private ShaderProgram drawShadowSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/CopyTexture_FS.glsl");
	
	//screenquad for drawing
	private Geometry screenQuad = GeometryFactory.createScreenQuad();
	
	//framebuffer
	private FrameBuffer frameBuffer = new FrameBuffer();
	
	//texture
	private Texture texShaderPosition;
	
	/**
	 * Constructor
	 */
	public ShadowShader() {
	}
	
	/**
	 * init method
	 * @param unitOffset offset for texture units
	 */
	public void init(int unitOffset) {
		this.init(unitOffset, GL.WIDTH, GL.HEIGHT);
	}
	
	/**
	 * init method
	 * @param unitOffset offset for texture units
	 * @param width framebuffer width
	 * @param height framebuffer height
	 */
	public void init(int unitOffset, int width, int height) {
		frameBuffer.init(true, width, height);
		
		texShaderPosition = new Texture(TEXTURE, unitOffset + 0);
		
		//clamping
		texShaderPosition.bind();
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
    	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
    	
    	frameBuffer.addTexture(texShaderPosition, GL30.GL_RGBA32F, RGBA);
    	frameBuffer.drawBuffers();
	}
	
	/**
	 * bind framebuffer
	 */
	public void bind() {
   		frameBuffer.bind();
    }
    
	/**
	 * register a shaderProgram
	 * @param shaderProgram shaderProgram to register
	 */
    public void registerShaderProgram(ShaderProgram shaderProgram) {
    	shaderProgram.use();
        frameBuffer.BindFragDataLocations(shaderProgram, "position");        
    }
    
    /**
     * clear the framebuffer
     */
    public void clear() {
    	frameBuffer.clearColor(); 
    }
    
    /**
     * unbind the framebuffer
     */
    public void finish() {
    	frameBuffer.unbind();
    }
    
    /**
     * shadow map
     * @return texture with shadow map
     */
    public Texture getTexture() {
        return frameBuffer.getTexture(0);
    }
    
    /**
     * draw a texture
     * @param tex
     */
    public void DrawTexture(Texture tex) {
    	drawShadowSP.use();
    	drawShadowSP.setUniform("image", tex);
    	screenQuad.draw();
    }
    
    /**
     * delete all stuff
     */
    public void delete() {
    	drawShadowSP.delete();
    	screenQuad.delete();
        
    	texShaderPosition.delete();
    }
}