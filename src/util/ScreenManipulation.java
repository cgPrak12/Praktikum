package util;

import static opengl.GL.GL_RGBA;
import static opengl.GL.GL_TEXTURE_2D;

import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class ScreenManipulation {
	
	//framebuffer
	private static FrameBuffer fboBlur0;
	private static FrameBuffer fboBlur1;
	private static FrameBuffer fboBlur2;
	private static FrameBuffer fboBlur3;
	private static FrameBuffer fboBrightness;
	private static FrameBuffer fboBloom;
	private static FrameBuffer fboTone;
	private static FrameBuffer fboPhong;
	private static FrameBuffer fboHalf;
	private static FrameBuffer brightness; 
	private static FrameBuffer blured1;
	private static FrameBuffer blured2;
	private static FrameBuffer blured3;
	private static FrameBuffer blured4;
	
	//shaderprograms
	private static ShaderProgram spoBlur;
	private static ShaderProgram spoBrightness;
	private static ShaderProgram spoBloom;
	private static ShaderProgram spoTone;
	private static ShaderProgram spoPhong;
	private static ShaderProgram spoHalf;
	
    private static Vector2f[] tc_offset_5;
    
    //screenquad
    private Geometry screenQuad;
    
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
		//initalize pixeloffset
		tc_offset_5 = generateTCOffset(5);
		
		//initalize screenquad
		screenQuad = GeometryFactory.createScreenQuad();
		
		//initialize all the FrameBufferObjects
		fboBlur0 = new FrameBuffer();
		fboBlur0.init(false, width, height);
        fboBlur0.addTexture(new Texture(GL_TEXTURE_2D, 0), GL30.GL_RGBA16F, GL_RGBA);
        
    	fboBlur1 = new FrameBuffer();
		fboBlur1.init(false, width, height);
        fboBlur1.addTexture(new Texture(GL_TEXTURE_2D, 0), GL30.GL_RGBA16F, GL_RGBA);
        
        fboBlur2 = new FrameBuffer();
		fboBlur2.init(false, width, height);
        fboBlur2.addTexture(new Texture(GL_TEXTURE_2D, 0), GL30.GL_RGBA16F, GL_RGBA);
        
    	fboBlur3 = new FrameBuffer();
		fboBlur3.init(false, width, height);
        fboBlur3.addTexture(new Texture(GL_TEXTURE_2D, 0), GL30.GL_RGBA16F, GL_RGBA);
        
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
        
        fboHalf = new FrameBuffer();
        fboHalf.init(false, width, height);
        fboHalf.addTexture(new Texture(GL_TEXTURE_2D, 0), GL30.GL_RGBA16F, GL_RGBA);
        
        brightness = new FrameBuffer();
        brightness.init(false, width, height);
        brightness.addTexture(new Texture(GL_TEXTURE_2D, 0), GL30.GL_RGBA16F, GL_RGBA);
        
        blured1 = new FrameBuffer();
        blured1.init(false, width, height);
        blured1.addTexture(new Texture(GL_TEXTURE_2D, 0), GL30.GL_RGBA16F, GL_RGBA);
        
        blured2 = new FrameBuffer();
        blured2.init(false, width, height);
        blured2.addTexture(new Texture(GL_TEXTURE_2D, 0), GL30.GL_RGBA16F, GL_RGBA);
        
        blured3 = new FrameBuffer();
        blured3.init(false, width, height);
        blured3.addTexture(new Texture(GL_TEXTURE_2D, 0), GL30.GL_RGBA16F, GL_RGBA);
        
        blured4 = new FrameBuffer();
        blured4.init(false, width, height);
        blured4.addTexture(new Texture(GL_TEXTURE_2D, 0), GL30.GL_RGBA16F, GL_RGBA);
        
        //initialize all the FragmentShaderPrograms
		spoBlur = new ShaderProgram(vertexShader, fragmentShaderBlur);
		spoBrightness = new ShaderProgram(vertexShader, fragmentShaderBrightness);
		spoBloom = new ShaderProgram(vertexShader, fragmentShaderBloom);
		spoTone = new ShaderProgram(vertexShader, fragmentShaderTone);
		spoPhong = new ShaderProgram(vertexShader, fragmentShaderPhong);
		spoHalf = new ShaderProgram(vertexShader, "./shader/Half_FS.glsl");
	}
	
	/**
	 * Returns FrameBuffer with lighted Image
	 * @param shader Shader to enlight
	 * @param camPos Position of the Camera
	 * @param sunDirection Direction of the sun
	 * @param screenQuad screenQuad
	 * @return FrameBuffer with lighted Image
	 */
	public FrameBuffer getLighting(DeferredShader shader, Vector3f camPos, Vector3f sunDirection) {
		fboPhong.bind();
		
		spoPhong.use();
		spoPhong.setUniform("normalTex",  shader.getNormalTexture());
		spoPhong.setUniform("worldTex",   shader.getWorldTexture());
		spoPhong.setUniform("diffuseTex", shader.getDiffuseTexture());
		spoPhong.setUniform("camPos",     camPos);
		spoPhong.setUniform("sunDir",	 sunDirection);
		
		this.screenQuad.draw();
		
		fboPhong.unbind();
		
		return fboPhong;
	}
	
	/**
	 * Returns a FrameBuffer with blured image
	 * @param toBlur FrameBuffer to blur
	 * @param screenQuad screenQuad
	 * @return FrameBuffer with blured image
	 */
	public FrameBuffer getBlur51(FrameBuffer toBlur) {
		fboBlur0.bind();
		
		spoBlur.use();
		spoBlur.setUniform("colorTex", toBlur.getTexture(0));
		spoBlur.setUniform("tc_offset", tc_offset_5);
		  
		this.screenQuad.draw();
		
		fboBlur0.unbind();
		
		return fboBlur0;
	}
	
	/**
	 * Returns a FrameBuffer with blured image
	 * @param toBlur FrameBuffer to blur
	 * @param screenQuad screenQuad
	 * @return FrameBuffer with blured image
	 */
	public FrameBuffer getBlur52(FrameBuffer toBlur) {
		fboBlur1.bind();
		
		spoBlur.use();
		spoBlur.setUniform("colorTex", toBlur.getTexture(0));
		spoBlur.setUniform("tc_offset", tc_offset_5);
		  
		this.screenQuad.draw();
		
		fboBlur1.unbind();
		
		return fboBlur1;
	}
	
	/**
	 * Returns a FrameBuffer with blured image
	 * @param toBlur FrameBuffer to blur
	 * @param screenQuad screenQuad
	 * @return FrameBuffer with blured image
	 */
	public FrameBuffer getBlur53(FrameBuffer toBlur) {
		fboBlur2.bind();
		
		spoBlur.use();
		spoBlur.setUniform("colorTex", toBlur.getTexture(0));
		spoBlur.setUniform("tc_offset", tc_offset_5);
		  
		this.screenQuad.draw();
		
		fboBlur2.unbind();
		
		return fboBlur2;
	}	
	/**
	 * Returns a FrameBuffer with blured image
	 * @param toBlur FrameBuffer to blur
	 * @param screenQuad screenQuad
	 * @return FrameBuffer with blured image
	 */
	public FrameBuffer getBlur54(FrameBuffer toBlur) {
		fboBlur3.bind();
		
		spoBlur.use();
		spoBlur.setUniform("colorTex", toBlur.getTexture(0));
		spoBlur.setUniform("tc_offset", tc_offset_5);
		  
		this.screenQuad.draw();
		
		fboBlur3.unbind();
		
		return fboBlur3;
	}	

	/**
	 * Returns FrameBuffer with Brightnessinformations
	 * @param toLight FrameBuffer to get brightness from
	 * @param brightnessFactor Factor for getting Brightnessinformations
	 * @param screenQuad screenQuad
	 * @return FrameBuffer with Brightnessinformations
	 */
	public FrameBuffer getBrightness(FrameBuffer toLight, Vector4f brightnessFactor) {
		fboBrightness.bind();
		
    	spoBrightness.use();
    	spoBrightness.setUniform("colorTex", toLight.getTexture(0));
    	spoBrightness.setUniform("colorFactor", brightnessFactor);
		
    	this.screenQuad.draw();
    	
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
	public FrameBuffer getBloom(FrameBuffer origImage, float bloomLevel, Vector4f brightnessFactor) {
		//fill FrameBuffers neccesary for Bloom
		brightness = getBrightness(origImage, brightnessFactor);
    	blured1 = getBlur51(origImage);
    	blured2 = getBlur52(blured1);
    	blured3 = getBlur53(blured2);
    	blured4 = getBlur54(blured2);
    	
    	fboBloom.bind();
    	
    	spoBloom.use();
    	spoBloom.setUniform("origImage", origImage.getTexture(0));
    	spoBloom.setUniform("bloomLevel", bloomLevel);
    	spoBloom.setUniform("brightImage", brightness.getTexture(0));
    	spoBloom.setUniform("blur1", blured1.getTexture(0));
    	spoBloom.setUniform("blur2", blured2.getTexture(0));
    	spoBloom.setUniform("blur3", blured3.getTexture(0));
    	spoBloom.setUniform("blur4", blured4.getTexture(0));
    	
    	this.screenQuad.draw();
    	
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
	public FrameBuffer getToneMapped(FrameBuffer origImage, float exposure) {
		fboTone.bind();
		
		spoTone.use();
    	spoTone.setUniform("lightedTex", origImage.getTexture(0));
    	spoTone.setUniform("exposure", exposure);
       	spoTone.setUniform("tc_offset", tc_offset_5);
       	
       	this.screenQuad.draw();
       	
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
	public FrameBuffer getToneMappedBloomed(FrameBuffer origImage, float bloomLevel, Vector4f brightnessFactor, float exposure) {
		//Set neccesary Texutre for bloomed ToneMapping
		Texture bloomed = getBloom(origImage, bloomLevel, brightnessFactor).getTexture(0);
		
		fboTone.bind();
		
		spoTone.use();
    	spoTone.setUniform("lightedTex", bloomed);
    	spoTone.setUniform("exposure", exposure);
       	spoTone.setUniform("tc_offset", tc_offset_5);
       	
       	this.screenQuad.draw();
       	
       	fboTone.unbind();
       	
       	return fboTone;
	}
	
	public FrameBuffer getHalfScreenView(FrameBuffer image1, FrameBuffer image2) {
		fboHalf.bind();
		
		spoHalf.use();
		spoHalf.setUniform("leftImage", image1.getTexture(0));
		spoHalf.setUniform("rightImage", image2.getTexture(0));
		
		this.screenQuad.draw();
		
		fboHalf.unbind();
		
		return fboHalf;
		
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