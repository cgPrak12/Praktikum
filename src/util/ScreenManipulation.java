package util;

import static opengl.GL.GL_RGBA;
import static opengl.GL.GL_TEXTURE_2D;

import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import util.*;

public class ScreenManipulation {
	
	private static FrameBuffer fbo;
	private static ShaderProgram spoBlur;
	private static ShaderProgram spoBrightness;
	private static ShaderProgram spoBloom;
	private static ShaderProgram spoTone;
	private static ShaderProgram spoPhong;
    private static Vector2f[] tc_offset_5;

	
	public void init(String vertexShader, String fragmentShaderBlur, String fragmentShaderBrightness, String fragmentShaderBloom, String fragmentShaderTone, String fragmentShaderPhong, int width, int height) {
		tc_offset_5 = generateTCOffset(5);
		fbo = new FrameBuffer();
		fbo.init(false, width, height);
        fbo.addTexture(new Texture(GL_TEXTURE_2D, 0), GL30.GL_RGBA16F, GL_RGBA);
		spoBlur = new ShaderProgram(vertexShader, fragmentShaderBlur);
		spoBrightness = new ShaderProgram(vertexShader, fragmentShaderBrightness);
		spoBloom = new ShaderProgram(vertexShader, fragmentShaderBloom);
		spoTone = new ShaderProgram(vertexShader, fragmentShaderTone);
		spoPhong = new ShaderProgram(vertexShader, fragmentShaderPhong);
	}
	
	public FrameBuffer getLighting(DeferredShader shader, Vector3f camPos, Vector4f sunDirection, Geometry screenQuad) {
		fbo.bind();
		spoPhong.use();
		spoPhong.setUniform("normalTex",  shader.getNormalTexture());
		spoPhong.setUniform("worldTex",   shader.getWorldTexture());
		spoPhong.setUniform("diffuseTex", shader.getDiffuseTexture());
		spoPhong.setUniform("camPos",     camPos);
		spoPhong.setUniform("sunDir",	 new Vector3f(sunDirection.x, sunDirection.y, sunDirection.z));
		
		screenQuad.draw();
		fbo.unbind();
		return fbo;

	}
	
	public FrameBuffer getBlur5(FrameBuffer toBlur, Geometry screenQuad) {
		fbo.bind();
		spoBlur.use();
		spoBlur.setUniform("colorTex", toBlur.getTexture(0));
		spoBlur.setUniform("tc_offset", tc_offset_5);
		  
		screenQuad.draw();
		fbo.unbind();
		return fbo;
	}
		
	public FrameBuffer getBrightness(FrameBuffer toLight, Vector4f brightnessFactor, Geometry screenQuad) {
		fbo.bind();
    	spoBrightness.use();
    	spoBrightness.setUniform("colorTex", toLight.getTexture(0));
    	spoBrightness.setUniform("colorFactor", brightnessFactor);
		
    	screenQuad.draw();
    	
    	fbo.unbind();
		return fbo;
	}
	
	public FrameBuffer getBloom(FrameBuffer origImage, float bloomLevel, Vector4f brightnessFactor, Geometry screenQuad) {
		Texture brightTex = getBrightness(origImage, brightnessFactor, screenQuad).getTexture(0);
    	FrameBuffer blured1 = getBlur5(origImage, screenQuad);
    	FrameBuffer blured2 = getBlur5(blured1, screenQuad);
    	FrameBuffer blured3 = getBlur5(blured2, screenQuad);
    	FrameBuffer blured4 = getBlur5(blured2, screenQuad);
    	fbo.bind();
    	spoBloom.use();
    	spoBloom.setUniform("origImage", origImage.getTexture(0));
    	spoBloom.setUniform("bloomLevel", bloomLevel);
    	spoBloom.setUniform("brightImage", brightTex);
    	spoBloom.setUniform("blur1", blured1.getTexture(0));
    	spoBloom.setUniform("blur2", blured2.getTexture(0));
    	spoBloom.setUniform("blur3", blured3.getTexture(0));
    	spoBloom.setUniform("blur4", blured4.getTexture(0));
    	
    	screenQuad.draw();
    	fbo.unbind();
    	return blured4;//fbo;
	}

	public FrameBuffer getToneMapped(FrameBuffer origImage, float exposure, Geometry screenQuad) {
		fbo.bind();
		spoTone.use();
    	spoTone.setUniform("lightedTex", origImage.getTexture(0));
    	spoTone.setUniform("exposure", exposure);
       	spoTone.setUniform("tc_offset", tc_offset_5);
       	screenQuad.draw();
       	fbo.unbind();
       	return fbo;
	}
	
	public FrameBuffer getToneMappedBloomed(FrameBuffer origImage, float bloomLevel, Vector4f brightnessFactor, float exposure, Geometry screenQuad) {
		Texture bloomed = getBloom(origImage, bloomLevel, brightnessFactor, screenQuad).getTexture(0);
		fbo.bind();
		spoTone.use();
    	spoTone.setUniform("lightedTex", bloomed);
    	spoTone.setUniform("exposure", exposure);
       	spoTone.setUniform("tc_offset", tc_offset_5);
       	screenQuad.draw();
       	fbo.unbind();
       	return fbo;
	}
	
	public void delete() {
		spoBlur.delete();
		spoBloom.delete();
		spoBrightness.delete();
	}


	private static Vector2f[] generateTCOffset(int size) {
	    Vector2f[] tc_offset = new Vector2f[size*size];
		int arraycounter = 0;
		for(int i = (-size/2); i <= (size/2); ++i) {
			for(int j = (-size/2); j <= (size/2); ++j) {
				tc_offset[arraycounter] = new Vector2f(i, j);
				++arraycounter;
			}
		}
	    return tc_offset;
	}
}