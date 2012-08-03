package util;

import static opengl.GL.GL_RGBA;
import static opengl.GL.GL_TEXTURE_2D;

import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class ScreenManipulation {
	
	//framebuffer
	private static FrameBuffer fboBlur;
	private static FrameBuffer fboBrightness;
	private static FrameBuffer fboBloom;
	private static FrameBuffer fboTone;
	private static FrameBuffer fboPhong;
	
	//shaderprograms
	private static ShaderProgram spoBlur;
	private static ShaderProgram spoBrightness;
	private static ShaderProgram spoBloom;
	private static ShaderProgram spoTone;
	private static ShaderProgram spoPhong;
    private static Vector2f[] tc_offset_5;
    
    /**
     * Initialize the ScreenManipulation
     * @param vertexShader VertexShader witch is used
     * @param fragmentShaderBlur FragmentShader for Blur
     * @param fragmentShaderBrightness FragmentShader for Brightness
     * @param fragmentShaderBloom FragmentShader for Bloom
     * @param fragmentShaderTone FragmentShader for ToneMapping
     * @param fragmentShaderPhong FragmentShader for PhongLighting
     * @param width Width of the FrameBufferObjects
     * @param height Height of the FrameBufferObjects
     */
	
	public void init(String vertexShader, String fragmentShaderBlur, String fragmentShaderBrightness, String fragmentShaderBloom, String fragmentShaderTone, String fragmentShaderPhong, int width, int height) {
		tc_offset_5 = generateTCOffset(5);
		
		//initialize all the FrameBufferObjects
		fboBlur = new FrameBuffer();
		fboBlur.init(false, width, height);
        fboBlur.addTexture(new Texture(GL_TEXTURE_2D, 0), GL30.GL_RGBA16F, GL_RGBA);
        
		fboBrightness = new FrameBuffer();
        fboBrightness.init(false, width, height);
        fboBrightness.addTexture(new Texture(GL_TEXTURE_2D, 0), GL30.GL_RGBA16F, GL_RGBA);
        
		fboBloom = new FrameBuffer();
        fboBloom.init(false, width, height);
        fboBloom.addTexture(new Texture(GL_TEXTURE_2D, 0), GL30.GL_RGBA16F, GL_RGBA);
        
        fboTone = new FrameBuffer();	
        fboTone.init(false, width, height);
        fboTone.addTexture(new Texture(GL_TEXTURE_2D, 0), GL30.GL_RGBA16F, GL_RGBA);
        
        fboPhong = new FrameBuffer();
		fboPhong.init(false, width, height);
        fboPhong.addTexture(new Texture(GL_TEXTURE_2D, 0), GL30.GL_RGBA16F, GL_RGBA);
        
        //initialize all the FragmentShaderPrograms
		spoBlur = new ShaderProgram(vertexShader, fragmentShaderBlur);
		spoBrightness = new ShaderProgram(vertexShader, fragmentShaderBrightness);
		spoBloom = new ShaderProgram(vertexShader, fragmentShaderBloom);
		spoTone = new ShaderProgram(vertexShader, fragmentShaderTone);
		spoPhong = new ShaderProgram(vertexShader, fragmentShaderPhong);
	}
	
	/**
	 * Returns FrameBuffer with lighted Image
	 * @param shader Shader to enlight
	 * @param camPos Position of the Camera
	 * @param sunDirection Direction of the sun
	 * @param screenQuad screenQuad
	 * @return FrameBuffer with lighted Image
	 */
	public FrameBuffer getLighting(DeferredShader shader, Vector3f camPos, Vector4f sunDirection, Geometry screenQuad) {
		fboPhong.bind();
		
		spoPhong.use();
		spoPhong.setUniform("normalTex",  shader.getNormalTexture());
		spoPhong.setUniform("worldTex",   shader.getWorldTexture());
		spoPhong.setUniform("diffuseTex", shader.getDiffuseTexture());
		spoPhong.setUniform("camPos",     camPos);
		spoPhong.setUniform("sunDir",	 new Vector3f(sunDirection.x, sunDirection.y, sunDirection.z));
		
		screenQuad.draw();
		
		fboPhong.unbind();
		
		return fboPhong;

	}
	
	/**
	 * Returns a FrameBuffer with blured image
	 * @param toBlur FrameBuffer to blur
	 * @param screenQuad screenQuad
	 * @return FrameBuffer with blured image
	 */
	public FrameBuffer getBlur5(FrameBuffer toBlur, Geometry screenQuad) {
		fboBlur.bind();
		
		spoBlur.use();
		spoBlur.setUniform("colorTex", toBlur.getTexture(0));
		spoBlur.setUniform("tc_offset", tc_offset_5);
		  
		screenQuad.draw();
		
		fboBlur.unbind();
		
		return fboBlur;
	}
	
	/**
	 * Returns FrameBuffer with Brightnessinformations
	 * @param toLight FrameBuffer to get brightness from
	 * @param brightnessFactor Factor for getting Brightnessinformations
	 * @param screenQuad screenQuad
	 * @return FrameBuffer with Brightnessinformations
	 */
	public FrameBuffer getBrightness(FrameBuffer toLight, Vector4f brightnessFactor, Geometry screenQuad) {
		fboBrightness.bind();
		
    	spoBrightness.use();
    	spoBrightness.setUniform("colorTex", toLight.getTexture(0));
    	spoBrightness.setUniform("colorFactor", new Vector4f(1f, 1f, 1f, 1f));
		
    	screenQuad.draw();
    	
    	fboBrightness.unbind();
    	
		return fboBrightness;
	}
	
	/**
	 * Returns bloomed FrameBuffer
	 * @param origImage Image to bloom
	 * @param bloomLevel Strength of bloomeffect
	 * @param brightnessFactor Factor for getting Brightnessinformations
	 * @param screenQuad screenQuad
	 * @return bloomed FrameBuffer
	 */
	public FrameBuffer getBloom(FrameBuffer origImage, float bloomLevel, Vector4f brightnessFactor, Geometry screenQuad) {
		//fill FrameBuffers neccesary for Bloom
		FrameBuffer brightness = getBrightness(origImage, brightnessFactor, screenQuad);
    	FrameBuffer blured1 = getBlur5(origImage, screenQuad);
    	FrameBuffer blured2 = getBlur5(blured1, screenQuad);
    	FrameBuffer blured3 = getBlur5(blured2, screenQuad);
    	FrameBuffer blured4 = getBlur5(blured2, screenQuad);
    	
    	fboBloom.bind();
    	
    	spoBloom.use();
    	spoBloom.setUniform("origImage", origImage.getTexture(0));
    	spoBloom.setUniform("bloomLevel", bloomLevel);
    	spoBloom.setUniform("brightImage", brightness.getTexture(0));
    	spoBloom.setUniform("blur1", blured1.getTexture(0));
    	spoBloom.setUniform("blur2", blured2.getTexture(0));
    	spoBloom.setUniform("blur3", blured3.getTexture(0));
    	spoBloom.setUniform("blur4", blured4.getTexture(0));
    	
    	screenQuad.draw();
    	
    	fboBloom.unbind();
    	
    	return fboBloom;
	}
	
	/**
	 * Returns tonemapped FrameBuffer
	 * @param origImage Image to tonemap
	 * @param bloomLevel Strength of bloomeffect
	 * @param brightnessFactor Factor for getting Brightnessinformations
	 * @param exposure blendingfactor of the tonemapping
	 * @param screenQuad screenQuad
	 * @return tonemapped FrameBuffer
	 */
	public FrameBuffer getToneMapped(FrameBuffer origImage, float exposure, Geometry screenQuad) {
		fboTone.bind();
		
		spoTone.use();
    	spoTone.setUniform("lightedTex", origImage.getTexture(0));
    	spoTone.setUniform("exposure", exposure);
       	spoTone.setUniform("tc_offset", tc_offset_5);
       	
       	screenQuad.draw();
       	
       	fboTone.unbind();
       	
       	return fboTone;
	}
	
	/**
	 * Returns bloomed and tonemapped FrameBuffer
	 * @param origImage Image to tonemap
	 * @param exposure blendingfactor of the tonemapping
	 * @param screenQuad screenQuad
	 * @return bloomed and tonemapped FrameBuffer
	 */
	public FrameBuffer getToneMappedBloomed(FrameBuffer origImage, float bloomLevel, Vector4f brightnessFactor, float exposure, Geometry screenQuad) {
		//Set neccesary Texutre for bloomed ToneMapping
		Texture bloomed = getBloom(origImage, bloomLevel, brightnessFactor, screenQuad).getTexture(0);
		
		fboTone.bind();
		
		spoTone.use();
    	spoTone.setUniform("lightedTex", bloomed);
    	spoTone.setUniform("exposure", exposure);
       	spoTone.setUniform("tc_offset", tc_offset_5);
       	
       	screenQuad.draw();
       	
       	fboTone.unbind();
       	
       	return fboTone;
	}
	
	/**
	 * Delete all ShaderPrograms
	 */
	public void delete() {
		spoBlur.delete();
		spoBloom.delete();
		spoBrightness.delete();
		spoPhong.delete();
		spoTone.delete();
	}

	/**
	 * Generates Pixeloffset for size*size gaussian manipulation
	 * @param size size*size Matrix
	 * @return vector2f array with pixeloffset
	 */
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