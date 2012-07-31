package util;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;

import opengl.GL;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class FrameBuffer {

	private int frameBufferObjectId;
	private List<Texture> textureList = new LinkedList<Texture>();
	private int count;
	
	public FrameBuffer() {
		frameBufferObjectId = GL30.glGenFramebuffers();
		count = 0;
	}
	
	public void init() {
		
	}
	
	public void addTexture(Texture tex, int depth) {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferObjectId);
		bindTexture(tex, GL30.GL_COLOR_ATTACHMENT0 + count, depth);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		textureList.add(tex);
		count++;
	}
	
    public Texture getTexture(int i) {
        return textureList.get(i);
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
