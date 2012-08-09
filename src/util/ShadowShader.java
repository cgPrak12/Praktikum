package util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import opengl.GL;

public class ShadowShader {
	private static final int TEXTURE = GL11.GL_TEXTURE_2D;
	private static final int RGBA = GL11.GL_RGBA;
	
	private ShaderProgram drawShadowSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/CopyTexture_FS.glsl");
	private Geometry screenQuad = GeometryFactory.createScreenQuad();
	
	private FrameBuffer frameBuffer = new FrameBuffer();
	
	private Texture texShaderPosition;

	public ShadowShader() {
	}
	
	public void init(int unitOffset) {
		this.init(unitOffset, GL.WIDTH, GL.HEIGHT);
	}
	
	public void init(int unitOffset, int width, int height) {
		frameBuffer.init(true, width, height);
		
		texShaderPosition = new Texture(TEXTURE, unitOffset + 0);
    	frameBuffer.addTexture(texShaderPosition, GL30.GL_RGBA32F, GL11.GL_RGBA);
    	frameBuffer.drawBuffers();
	}
	
	public void bind() {
   		frameBuffer.bind();
    }
    
    public void registerShaderProgram(ShaderProgram shaderProgram) {
    	shaderProgram.use();
        frameBuffer.BindFragDataLocations(shaderProgram, "position");        
    }
    
    public void clear() {
    	frameBuffer.clearColor(); 
    }
    
    public void finish() {
    	frameBuffer.unbind();
    }
    
    // Hinweis: In der w-Komponente der World-Koordinaten steht der Abstand des Punktes zur Kamera
    public Texture getTexture() {
        return frameBuffer.getTexture(0);
    }

    public void DrawTexture(Texture tex) {
    	drawShadowSP.use();
    	drawShadowSP.setUniform("image", tex);
    	screenQuad.draw();
    }
    
    public void delete() {
    	drawShadowSP.delete();
    	screenQuad.delete();
        
    	texShaderPosition.delete();
    }
}