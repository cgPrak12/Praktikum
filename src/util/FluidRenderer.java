package util;

import static opengl.GL.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;


public class FluidRenderer {
	
	private Geometry testWaterParticles = GeometryFactory.createTestParticles(1024);
	private int textureUnit = 50;
	private Camera cam;
	
    private ShaderProgram drawTextureSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/CopyTexture_FS.glsl");
    private Geometry screenQuadGeo 		= GeometryFactory.createScreenQuad();
    
    // Thickness-Path
	private FrameBuffer thicknessFB = new FrameBuffer();
    private ShaderProgram thicknessSP = new ShaderProgram("./shader/fluid/Renderer_VS.glsl", "./shader/fluid/Thickness_FS.glsl");
    private Texture thicknessTexture = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);
    
    // Final Image
    private FrameBuffer finalImageFB = new FrameBuffer();
    private ShaderProgram finalImageSP = new ShaderProgram("./shader/fluid/Complete_VS.glsl", "./shader/fluid/Complete_FS.glsl");
    private Texture finalImage = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);
    
    private Texture[] textures = { thicknessTexture };
    private String[] textureNames = { "thickness" }; 
    
    public FluidRenderer(Camera camTmp) {
    	cam = camTmp;
    	
    	// init shaderPrograms, frameBuffers, ...
    	init(thicknessSP, thicknessFB, "color", thicknessTexture);
    	init(finalImageSP, finalImageFB, "color", finalImage);
	} 
	
	public void render() {
		// get fluid thickness
		fluidThickness();
		
		// combine images to final image
		createFinalImage();
		
        
		// Draws image
		drawTextureSP.use();
        drawTextureSP.setUniform("image", finalImage);
        screenQuadGeo.draw();
        
        // resets buffers
        thicknessFB.reset();
        
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
    
	private void fluidThickness() {
    	startPath(thicknessSP, thicknessFB);
    	

        glBlendFunc(GL_ONE, GL_ONE);
        glEnable(GL_BLEND);
        glDisable(GL_DEPTH_TEST);
        
        GL11.glPointSize(15);
        testWaterParticles.draw();
        GL11.glPointSize(GL11.GL_POINT_SIZE);
        
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_COLOR);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        
        endPath(thicknessFB);
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
