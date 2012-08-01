package util;

import static opengl.GL.*;

import java.nio.FloatBuffer;

import opengl.GL;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

public class FluidRenderer {
	
	private Geometry testWaterParticles = GeometryFactory.createTestParticles(1024);
	private int textureUnit = 50;
	private Camera cam;
	
    private ShaderProgram drawTextureSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/CopyTexture_FS.glsl");
    private Geometry screenQuadGeo 		= GeometryFactory.createScreenQuad();
    
    // TiefenTextur
    private FrameBuffer depthFrameBuffer = new FrameBuffer();
    private ShaderProgram depthSP = new ShaderProgram("./shader/fluid/Depth_Texture_VS.glsl", "./shader/fluid/Depth_Texture_FS.glsl");
    private Texture depthTexture = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);
    
    // Normal-Path
    private FrameBuffer normalFrameBuffer = new FrameBuffer();
    private ShaderProgram normalSP = new ShaderProgram("./shader/fluid/Normal_VS.glsl", "./shader/fluid/Normal_FS.glsl");
    private Texture normalTexture = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);

    // Thickness-Path
	private FrameBuffer thicknessFrameBuffer = new FrameBuffer();
    private ShaderProgram thicknessSP = new ShaderProgram("./shader/fluid/Thickness_VS.glsl", "./shader/fluid/Thickness_FS.glsl");
    private Texture thicknessTexture = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);

    // ThicknessBlur-Path
	private FrameBuffer thicknessBlurFrameBuffer = new FrameBuffer();
    private ShaderProgram thicknessBlurSP = new ShaderProgram("./shader/fluid/ThicknessBlur_VS.glsl", "./shader/fluid/ThicknessBlur_FS.glsl");
    private Texture thicknessBlurTexture = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);
    private FrameBuffer thicknessBlurFrameBuffer2 = new FrameBuffer();
    private ShaderProgram thicknessBlurSP2 = new ShaderProgram("./shader/fluid/ThicknessBlur_VS.glsl", "./shader/fluid/ThicknessBlur2_FS.glsl");
    private Texture thicknessBlurTexture2 = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);

    // Final Image
    private FrameBuffer finalImageFB = new FrameBuffer();
    private ShaderProgram finalImageSP = new ShaderProgram("./shader/fluid/Complete_VS.glsl", "./shader/fluid/Complete_FS.glsl");
    private Texture finalImage = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);
    
    private Texture[] textures = { thicknessTexture, depthTexture };
    private String[] textureNames = { "thickness", "depth" };
    

    public FluidRenderer(Camera camTmp) {

    	cam = camTmp;
    	
    	// init shaderPrograms, frameBuffers, ...
    	GL11.glPointSize(GL11.GL_POINT_SIZE);
    	init(depthSP, depthFrameBuffer, "depth", depthTexture);
    	init(normalSP, normalFrameBuffer, "color", normalTexture);
    	init(thicknessSP, thicknessFrameBuffer, "color", thicknessTexture);
    	init(thicknessBlurSP, thicknessBlurFrameBuffer, "color", thicknessBlurTexture);
    	init(thicknessBlurSP2, thicknessBlurFrameBuffer2, "color", thicknessBlurTexture2);
    	init(finalImageSP, finalImageFB, "color", finalImage);
	} 
	
	public void render() {
		// fluid depth
		depthTexture();
		// fluid normals
		fluidNormals();
		// fluid thickness
		fluidThickness();
		// fluid thicknessBlur
		fluidThicknessBlur();		
		
		// combine images to final image
		createFinalImage();
		
		// Draws image (will be removed later)
        glDisable(GL_BLEND);
		drawTextureSP.use();
        drawTextureSP.setUniform("image", depthTexture);
        screenQuadGeo.draw();
        
        // resets buffers
        depthFrameBuffer.reset();
		normalFrameBuffer.reset();
        thicknessFrameBuffer.reset();
        thicknessBlurFrameBuffer.reset();
        thicknessBlurFrameBuffer2.reset();
        
        finalImageFB.reset();
	}
	
	private void init(ShaderProgram sp, FrameBuffer fb, String attachmentName, Texture tex) {
    	fb.addTexture(tex, GL11.GL_RGBA8, GL11.GL_RGBA);
    	GL30.glBindFragDataLocation(sp.getId(), 0, attachmentName);
    	fb.drawBuffers();
	}
	
	private void init(ShaderProgram sp, FrameBuffer fb, String[] attachmentNames, Texture[] textures) {
    	if(attachmentNames.length != textures.length) throw new RuntimeException("Anzahl attachmentNames und Texturen stimmt nicht ueberein!");
		
    	for(int i = 0; i < textures.length; i++) { 
			fb.addTexture(textures[i], GL11.GL_RGBA8, GL11.GL_RGBA);
			GL30.glBindFragDataLocation(sp.getId(), i, attachmentNames[i]);
		}
    	fb.drawBuffers();
	}
	
	private void startPath(ShaderProgram sp, FrameBuffer fb) {
		sp.use();
		sp.setUniform("viewProj", Util.mul(null, cam.getProjection(), cam.getView()));
		fb.bind();
		fb.clearColor();
	}
	    
	private void endPath(FrameBuffer fb) {
		fb.unbind();
	}
    
	private void depthTexture() {
		
		depthSP.use();
		
		depthSP.setUniform("view", cam.getView());
		depthSP.setUniform("proj", cam.getProjection());
		
        depthSP.setUniform("camPos", cam.getCamPos());
   	    depthFrameBuffer.bind();
   	    depthFrameBuffer.clearColor();
    
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        
        testWaterParticles.draw();
        depthFrameBuffer.unbind();
        
	}
	
	private void fluidNormals() {
		startPath(normalSP, normalFrameBuffer);
		normalSP.setUniform("depthTex", depthTexture);
		normalSP.setUniform("texSize", GL.WIDTH);
		
	    glDisable(GL_BLEND);
	    glDisable(GL_DEPTH_TEST);
		screenQuadGeo.draw();
		endPath(normalFrameBuffer);
	}
	
	private void fluidThickness() {  //TODO

	    startPath(thicknessSP, thicknessFrameBuffer);

	    thicknessSP.setUniform("camera", cam.getCamPos());

        glBlendFunc(GL_ONE, GL_ONE);
        glEnable(GL_BLEND);
        glDisable(GL_DEPTH_TEST);

    	// evtl. auslagern (vllt. sogar TerrainMain? Auf jeden Fall hier in den Konstruktor!)
        GL14.glPointParameteri(GL14.GL_POINT_SIZE_MIN, 1);
        GL14.glPointParameteri(GL14.GL_POINT_SIZE_MAX, 1000);
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(4);
        floatBuffer.put(1.0f);
        floatBuffer.put(1.0f);
        floatBuffer.put(0.0f);
        floatBuffer.put(0.0f);
        floatBuffer.position(0);
        GL14.glPointParameter(GL14.GL_POINT_DISTANCE_ATTENUATION, floatBuffer);


        testWaterParticles.draw();

        thicknessFrameBuffer.unbind();
        
    }
	
	private void fluidThicknessBlur() {  //TODO
		startPath(thicknessBlurSP, thicknessBlurFrameBuffer);
	    thicknessBlurSP.setUniform("thickness", thicknessTexture);
        screenQuadGeo.draw();
        thicknessBlurFrameBuffer.unbind();
	        
	    startPath(thicknessBlurSP2, thicknessBlurFrameBuffer2);
	    thicknessBlurSP2.setUniform("thickness", thicknessBlurTexture);
        screenQuadGeo.draw();
        thicknessBlurFrameBuffer2.unbind();
    }

	private void createFinalImage() {
		if(textureNames.length != textures.length) throw new RuntimeException("Anzahl names und textures stimmt nicht ueberein!");
		
		startPath(finalImageSP, finalImageFB);

		for(int i = 0; i < textures.length; i++) {
			finalImageSP.setUniform(textureNames[i], textures[i]);
		}

		screenQuadGeo.draw();
		endPath(finalImageFB);
	} 
}
