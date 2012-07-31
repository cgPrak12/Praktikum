package util;

import static opengl.GL.GL_COLOR_BUFFER_BIT;
import static opengl.GL.GL_DEPTH_BUFFER_BIT;
import static opengl.GL.glClear;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;

import opengl.GL;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class FrameBuffer {

	private int frameBufferObjectId;
	private List<Texture> textureList = new LinkedList<Texture>();
	private int count;
	
	public FrameBuffer() {
		frameBufferObjectId = GL30.glGenFramebuffers();
		count = 0;
	}
	
	
	public void addTexture(Texture tex, int internalFormat, int format) {
		this.bind();
		bindTexture(tex, GL30.GL_COLOR_ATTACHMENT0 + count, internalFormat, format);
		textureList.add(tex);
		count++;	

		this.unbind();
	}
	
	public void drawBuffers() {
    	// draw buffers
    	int[] buffersArray = new int[count]; 
    	for(int i = 0; i < buffersArray.length; i++) {
    		buffersArray[i] = GL30.GL_COLOR_ATTACHMENT0 + i;
    	}
    	IntBuffer buffers = BufferUtils.createIntBuffer(buffersArray.length);
    	buffers.put(buffersArray);
    	buffers.position(0);
    	
    	this.bind();
    	GL20.glDrawBuffers(buffers);
    	this.unbind();
	}
	
    public Texture getTexture(int i) {
        return textureList.get(i);
    }
    
    public void clearColor() {
    	// bind Frame Buffer Object
    	this.bind();

    	// set clear color
    	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
   	
    }
    
    public void bind() {
    	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferObjectId);
    }
    
    public void unbind() {
    	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }
    
    /**
     * Binds a texture to the current Frame Buffer Object.
     * @param Texture texture to bind
     * @param attachment Must be GL_COLOR_ATTACHMENT0-15, binding point
     * @param internalFormat GL internal Format
     * @param format GL Format
     */
    private void bindTexture(Texture texture, int attachment, int internalFormat, int format) {
    	// bind texture
    	texture.bind();
    	
    	// add filters
    	GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
    	GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    	
    	// set texture information
    	GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, internalFormat, GL.HEIGHT, GL.WIDTH, 0, format, GL11.GL_FLOAT, (FloatBuffer) null);
    	
    	// attach texture to framebuffer
    	GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, GL11.GL_TEXTURE_2D, texture.getId(), 0);
    	
    }
    
}
