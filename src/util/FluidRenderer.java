package util;

import static opengl.GL.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;


public class FluidRenderer {
	
	private Geometry testWaterParticles;
	private Camera cam;
	
    private ShaderProgram drawTextureSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/CopyTexture_FS.glsl");
    private Geometry screenQuadGeo 		= GeometryFactory.createScreenQuad();
    
    // Thickness-Path
	private FrameBuffer thicknessFrameBuffer = new FrameBuffer();
    private ShaderProgram thicknessSP = new ShaderProgram("./shader/FluidThickness_VS.glsl", "./shader/FluidThickness_FS.glsl");
    private Texture thicknessTex = new Texture(GL11.GL_TEXTURE_2D, 0);

	public FluidRenderer(Camera camTmp) {

    	testWaterParticles = GeometryFactory.createTestParticles(1024);
    	cam = camTmp;
	} 
	    
    public Texture fluidThickness() {

        thicknessSP.use();
        thicknessSP.setUniform("viewProj", Util.mul(null, cam.getProjection(), cam.getView()));
        thicknessSP.setUniform("camera", cam.getCamPos());
            	
    	thicknessFrameBuffer.addTexture(thicknessTex, GL11.GL_RGBA8, GL11.GL_RGBA);
    	thicknessFrameBuffer.drawBuffers();
    	
   		GL30.glBindFragDataLocation(thicknessSP.getId(), 0, "color");
   		thicknessFrameBuffer.bind();
   		
    	thicknessFrameBuffer.clearColor();
        
        glBlendFunc(GL_ONE, GL_ONE);
        glEnable(GL_BLEND);
        glDisable(GL_DEPTH_TEST);
        GL14.glPointParameteri(GL14.GL_POINT_SIZE_MIN, 100);
        GL14.glPointParameteri(GL14.GL_POINT_SIZE_MAX, 500);
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(4);
        floatBuffer.put(1.0f);
        floatBuffer.put(1.0f);
        floatBuffer.put(0.0f);
        floatBuffer.put(0.0f);
        floatBuffer.position(0);
        GL14.glPointParameter(GL14.GL_POINT_DISTANCE_ATTENUATION, floatBuffer);
        testWaterParticles.draw();
        GL11.glPointSize(GL11.GL_POINT_SIZE);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        thicknessFrameBuffer.unbind();
        
        drawTextureSP.use();
        drawTextureSP.setUniform("image", thicknessTex);
        screenQuadGeo.draw();
        
        thicknessFrameBuffer.renew();
        
        return thicknessFrameBuffer.getTexture(0);
        
    }
    
}
