package util;

import static opengl.GL.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;


public class FluidRenderer {
	
	private Geometry testWaterParticles;
	private Camera cam;
	
    private ShaderProgram drawTextureSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/CopyTexture_FS.glsl");
    private Geometry screenQuadGeo 		= GeometryFactory.createScreenQuad();
    
    // Thickness-Path
	private FrameBuffer thicknessFrameBuffer = new FrameBuffer();
    private ShaderProgram thicknessSP = new ShaderProgram("./shader/WaterRenderer_VS.glsl", "./shader/FluidThickness_FS.glsl");

	public FluidRenderer(Camera camTmp) {

    	testWaterParticles = GeometryFactory.createTestParticles(1024);
    	cam = camTmp;
	} 
	    
    public Texture fluidThickness() {
    	

        thicknessSP.use();
        thicknessSP.setUniform("viewProj", Util.mul(null, cam.getProjection(), cam.getView()));
        
        
    	Texture tex = new Texture(GL11.GL_TEXTURE_2D, 0);    	
    	thicknessFrameBuffer.addTexture(tex, GL11.GL_RGBA8, GL11.GL_RGBA);
    	thicknessFrameBuffer.drawBuffers();
    	
//        waterShader.prepareRendering(waterSP);
//    	thicknessSP.use();
   		GL30.glBindFragDataLocation(thicknessSP.getId(), 0, "color");
   		thicknessFrameBuffer.bind();
   		
//        waterShader.clear();
    	thicknessFrameBuffer.clearColor();
        
        glBlendFunc(GL_ONE, GL_ONE);
        glEnable(GL_BLEND);
        glDisable(GL_DEPTH_TEST);
//        GL11.glEnable(GL11.GL_POINT_SMOOTH);
//        GL11.glEnable(GL20.GL_POINT_SPRITE);
        GL11.glPointSize(15);
        testWaterParticles.draw();
        GL11.glPointSize(GL11.GL_POINT_SIZE);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
//        waterShader.finish();
        thicknessFrameBuffer.unbind();
        
//        waterShader.DrawTexture(waterShader.getWorldTexture());
        drawTextureSP.use();
        drawTextureSP.setUniform("image", tex);
        screenQuadGeo.draw();
        
        return thicknessFrameBuffer.getTexture(0);
        
    }
    
}
