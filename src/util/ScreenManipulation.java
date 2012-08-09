package util;

import static opengl.GL.GL_RGBA;
import static opengl.GL.GL_TEXTURE_2D;

import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class ScreenManipulation {
	
	//frame buffer
	private static FrameBuffer fboBlur0;
	private static FrameBuffer fboBlur1;
	private static FrameBuffer fboBlur2;
	private static FrameBuffer fboBlur3;
	private static FrameBuffer fboBlur20;
	private static FrameBuffer fboBrightness;
	private static FrameBuffer fboBloom;
	private static FrameBuffer fboTone;
	private static FrameBuffer fboPhong;
	private static FrameBuffer fboHalf;
	private static FrameBuffer fboQuad;
	private static FrameBuffer fboShadow;
	private static FrameBuffer fboShadowPhong;
	private static FrameBuffer brightness; 
	private static FrameBuffer blured1;
	private static FrameBuffer blured2;
	private static FrameBuffer blured3;
	private static FrameBuffer blured4;
	
	//shader programs
	private static ShaderProgram spoBlur;
	private static ShaderProgram spoBrightness;
	private static ShaderProgram spoBloom;
	private static ShaderProgram spoTone;
	private static ShaderProgram spoPhong;
	private static ShaderProgram spoHalf;
	private static ShaderProgram spoQuad;
	private static ShaderProgram spoShadow;
	private static ShaderProgram spoShadowPhong;

	
    private static Vector2f[] tc_offset_5;

    
    //screen quad
    private Geometry screenQuad;
	private Geometry highScreenQuad;
    
    /**
     * Initialize the ScreenManipulation
     * @param vertexShader VertexShader to be used
     * @param fragmentShaderBlur FragmentShader for Blur
     * @param fragmentShaderBrightness FragmentShader for Brightness
     * @param fragmentShaderBloom FragmentShader for Bloom
     * @param fragmentShaderTone FragmentShader for ToneMapping
     * @param fragmentShaderPhong FragmentShader for PhongLighting
     * @param width Width of the FrameBufferObjects
     * @param height Height of the FrameBufferObjects
     */
	public void init(String vertexShader, String fragmentShaderBlur, String fragmentShaderBrightness, String fragmentShaderBloom, String fragmentShaderTone, String fragmentShaderPhong, int unitOffset, int width, int height) {
		
		//initialize pixel offset
		tc_offset_5 = generateTCOffset(5);
		
		//initialize screen quad
		screenQuad = GeometryFactory.createScreenQuad();
		highScreenQuad = GeometryFactory.createScreenQuad();
		
		//initialize all the FrameBufferObjects
		fboBlur0 = new FrameBuffer();
		fboBlur0.init(false, width, height);
        fboBlur0.addTexture(new Texture(GL_TEXTURE_2D, unitOffset + 0), GL30.GL_RGBA16F, GL_RGBA);
        
    	fboBlur1 = new FrameBuffer();
		fboBlur1.init(false, width, height);
        fboBlur1.addTexture(new Texture(GL_TEXTURE_2D, unitOffset + 1), GL30.GL_RGBA16F, GL_RGBA);
        
        fboBlur2 = new FrameBuffer();
		fboBlur2.init(false, width, height);
        fboBlur2.addTexture(new Texture(GL_TEXTURE_2D, unitOffset + 2), GL30.GL_RGBA16F, GL_RGBA);
        
    	fboBlur3 = new FrameBuffer();
		fboBlur3.init(false, width, height);
        fboBlur3.addTexture(new Texture(GL_TEXTURE_2D, unitOffset + 3), GL30.GL_RGBA16F, GL_RGBA);
        
		fboBrightness = new FrameBuffer();
        fboBrightness.init(false, width, height);
        fboBrightness.addTexture(new Texture(GL_TEXTURE_2D, unitOffset + 4), GL30.GL_RGBA16F, GL_RGBA);
        
		fboBloom = new FrameBuffer();
        fboBloom.init(false, width, height);
        fboBloom.addTexture(new Texture(GL_TEXTURE_2D, unitOffset + 5), GL30.GL_RGBA16F, GL_RGBA);
        
        fboTone = new FrameBuffer();	
        fboTone.init(false, width, height);
        fboTone.addTexture(new Texture(GL_TEXTURE_2D, unitOffset + 6), GL30.GL_RGBA16F, GL_RGBA);
        
        fboPhong = new FrameBuffer();
		fboPhong.init(false, width, height);
        fboPhong.addTexture(new Texture(GL_TEXTURE_2D, unitOffset + 7), GL30.GL_RGBA16F, GL_RGBA);
        
        fboHalf = new FrameBuffer();
        fboHalf.init(false, width, height);
        fboHalf.addTexture(new Texture(GL_TEXTURE_2D, unitOffset + 8), GL30.GL_RGBA16F, GL_RGBA);
        
        brightness = new FrameBuffer();
        brightness.init(false, width, height);
        brightness.addTexture(new Texture(GL_TEXTURE_2D, unitOffset + 9), GL30.GL_RGBA16F, GL_RGBA);
        
        blured1 = new FrameBuffer();
        blured1.init(false, width, height);
        blured1.addTexture(new Texture(GL_TEXTURE_2D, unitOffset + 10), GL30.GL_RGBA16F, GL_RGBA);
        
        blured2 = new FrameBuffer();
        blured2.init(false, width, height);
        blured2.addTexture(new Texture(GL_TEXTURE_2D, unitOffset + 11), GL30.GL_RGBA16F, GL_RGBA);
        
        blured3 = new FrameBuffer();
        blured3.init(false, width, height);
        blured3.addTexture(new Texture(GL_TEXTURE_2D, unitOffset + 12), GL30.GL_RGBA16F, GL_RGBA);
        
        blured4 = new FrameBuffer();
        blured4.init(false, width, height);
        blured4.addTexture(new Texture(GL_TEXTURE_2D, unitOffset + 13), GL30.GL_RGBA16F, GL_RGBA);
        
        fboBlur20 = new FrameBuffer();
		fboBlur20.init(false, width, height);
        fboBlur20.addTexture(new Texture(GL_TEXTURE_2D, unitOffset + 16), GL30.GL_RGBA16F, GL_RGBA);
              
        fboQuad = new FrameBuffer();
        fboQuad.init(false, width, height);
        fboQuad.addTexture(new Texture(GL_TEXTURE_2D, 14), GL30.GL_RGBA16F, GL_RGBA);
        
        fboShadow = new FrameBuffer();
        fboShadow.init(false, 2048, 2048);
        fboShadow.addTexture(new Texture(GL_TEXTURE_2D, 15), GL30.GL_RGBA32F, GL_RGBA);
        
        fboShadowPhong = new FrameBuffer();
        fboShadowPhong.init(false, width, height);
        fboShadowPhong.addTexture(new Texture(GL_TEXTURE_2D, 16), GL30.GL_RGBA16F, GL_RGBA);
        
        //initialize all the FragmentShaderPrograms
		spoBlur = new ShaderProgram(vertexShader, fragmentShaderBlur);
		spoBrightness = new ShaderProgram(vertexShader, fragmentShaderBrightness);
		spoBloom = new ShaderProgram(vertexShader, fragmentShaderBloom);
		spoTone = new ShaderProgram(vertexShader, fragmentShaderTone);
		spoPhong = new ShaderProgram(vertexShader, fragmentShaderPhong);
		spoHalf = new ShaderProgram(vertexShader, "./shader/Half_FS.glsl");
		spoQuad = new ShaderProgram(vertexShader, "./shader/Quad_FS.glsl");
		spoShadow = new ShaderProgram(vertexShader, "./shader/Shadow_FS.glsl");
		spoShadowPhong = new ShaderProgram(vertexShader, "./shader/ShadowLighting_FS.glsl");
	}
	
	/**
	 * Returns FrameBuffer with lighted Image
	 * @param shader Shader to light
	 * @param camPos Position of the camera
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
		spoPhong.setUniform("specularTex", shader.getSpecTexture());
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
		//fill FrameBuffers (necessary for bloom)
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
		//Set necessary texture for bloomed ToneMapping
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
	
	/**
	 * Splits screen in two halfs
	 * @param image1 left half image
	 * @param image2 right half image
	 * @return FrameBuffer with half image on each side
	 */
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
	 * Generates FrameBuffer with Quad Image View
	 * @param image1 First image
	 * @param image2 Second image
	 * @param image3 Third image
	 * @param image4 Forth image
	 * @return FrameBuffer with all four images
	 */
	public FrameBuffer getQuadScreenView(FrameBuffer image1, FrameBuffer image2, FrameBuffer image3, FrameBuffer image4) {
		fboQuad.bind();
		
		spoQuad.use();
		spoQuad.setUniform("leftTopImage", image1.getTexture(0));
		spoQuad.setUniform("rightTopImage", image2.getTexture(0));
		spoQuad.setUniform("leftDownImage", image3.getTexture(0));
		spoQuad.setUniform("rightDownImage", image4.getTexture(0));
		
		this.screenQuad.draw();
		
		fboQuad.unbind();
		
		return fboQuad;
	}
	
	/**
	 * Generates ShadowMap
	 * @param shadowImage Texture with depth-informations(GlobalTexture)
	 * @return FrameBuffer FrameBuffer with depth-informations
	 */
	public FrameBuffer getShadowMix(Texture worldTex, Texture shadowCoordsTex, Texture shadowTex, Vector3f sunDir) {
		fboShadow.bind();
		
		spoShadow.use();
		spoShadow.setUniform("worldTex", worldTex);
		spoShadow.setUniform("shadowCoordsTex", shadowCoordsTex);
		spoShadow.setUniform("shadowTex", shadowTex);
		spoShadow.setUniform("sunDir", sunDir);
		
		this.highScreenQuad.draw();
		
		fboShadow.unbind();
		
		return fboShadow;
	}
	
	public FrameBuffer getShadowLighting(DeferredShader shader, Vector3f camPos, Vector3f sunDirection, FrameBuffer shadowTex) {				
	
		fboShadowPhong.bind();
		
		spoShadowPhong.use();
		spoShadowPhong.setUniform("normalTex",  shader.getNormalTexture());
		spoShadowPhong.setUniform("worldTex",   shader.getWorldTexture());
		spoShadowPhong.setUniform("diffuseTex", shader.getDiffuseTexture());
		spoShadowPhong.setUniform("specularTex", shader.getSpecTexture());
		spoShadowPhong.setUniform("shadowTex", 	 shadowTex.getTexture(0));
		spoShadowPhong.setUniform("shadowCoordsTex", shader.getShadowTexture());
		spoShadowPhong.setUniform("camPos",     camPos);
		spoShadowPhong.setUniform("sunDir",	 	sunDirection);
		
		this.screenQuad.draw();
		
		fboShadowPhong.unbind();
		
		return fboShadowPhong;
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
		spoHalf.delete();
		spoQuad.delete();
		spoShadow.delete();
//		spoShadowPhong.delete();
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