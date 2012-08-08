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

	private int frameBufferObjectId = -1;
	private List<Texture> textureList = new LinkedList<Texture>();
	private int count;
	private int renderBufferObjectId;
    private int width, height;
	
	
	public void init(boolean depthTest, int width, int height) {
        this.width = width;
        this.height = height;
        count = 0;
        frameBufferObjectId = GL30.glGenFramebuffers();
        
        if(depthTest) {
            this.bind();
            renderBufferObjectId = GL30.glGenRenderbuffers();
            GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderBufferObjectId);
            GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH_COMPONENT32F, GL.WIDTH, GL.HEIGHT);
            GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
            GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, renderBufferObjectId);
            this.unbind();
            this.checkForErrors();
        }
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
    	// set clear color
    	this.bind();
    	FloatBuffer color = BufferUtils.createFloatBuffer(4);
    	color.put(new float[] { 0, 0, 0, 0 });
    	color.position(0);
    	for(int i=0; i < this.textureList.size(); ++i) {
    		GL30.glClearBuffer(GL11.GL_COLOR, i, color);
    	}
    	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
    
    public void bind() {
    	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferObjectId);
        GL11.glViewport(0, 0, this.width, this.height);
        if(renderBufferObjectId == -1) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        } else {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }
    }
    
    public void unbind() {
    	GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }
    
    public void BindFragDataLocations(ShaderProgram program, String ...fsOutVarNames) {
        for(int i=0; i < fsOutVarNames.length; ++i) {
            GL30.glBindFragDataLocation(program.getId(), i, fsOutVarNames[i]);
        }
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
    	GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
    	GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
    	
    	// set texture information
    	GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, internalFormat, this.width, this.height, 0, format, GL11.GL_FLOAT, (FloatBuffer) null);
    	
    	// attach texture to framebuffer
    	GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, attachment, GL11.GL_TEXTURE_2D, texture.getId(), 0);
    	
        this.checkForErrors();
    }
    
    public void checkForErrors() {
        int error = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
        switch(error) {
            case GL30.GL_FRAMEBUFFER_COMPLETE: break;
            case GL30.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT: System.err.println("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT"); break;
            case GL30.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER: System.err.println("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER"); break;
            case GL30.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT: System.err.println("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT"); break;
            case GL30.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE: System.err.println("GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE"); break;
            case GL30.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER: System.err.println("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER"); break;
        }
    }
    
}
