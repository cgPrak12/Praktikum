package util;

import static opengl.GL.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;

/**
 * This class provides water textures for particles.
 * @author S. Hoeffner, K. Schmidt, A. Werner
 */
public class FluidRenderer {
	private Geometry testWaterParticles = GeometryFactory.createTestParticles(1024);
	private int 	 textureUnit = 0;
	private Camera   cam;
	private Matrix4f viewProj;
	private boolean  skipLow = false;	// if true, all LQ Textures and functions based on them are skipped
	
    private ShaderProgram drawTextureSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/CopyTexture_FS.glsl");
    private Geometry screenQuad = GeometryFactory.createScreenQuad();    
    private Geometry cube 		= GeometryFactory.createCube();
    private Geometry plane 		= GeometryFactory.createPlane();
    private Texture  planeTex 	= Texture.generateTexture("Marble.jpg", textureUnit++);
    private Texture  cubemap    = new Texture(GL_TEXTURE_CUBE_MAP, textureUnit++);
    
    // Depth
    private ShaderProgram depthSP      = new ShaderProgram("./shader/fluid/Depth_VS.glsl",	   "./shader/fluid/Depth_FS.glsl");
    private ShaderProgram depthHBlurSP = new ShaderProgram("./shader/fluid/DepthBlur_VS.glsl", "./shader/fluid/DepthHBlur_FS.glsl");
    private ShaderProgram depthVBlurSP = new ShaderProgram("./shader/fluid/DepthBlur_VS.glsl", "./shader/fluid/DepthVBlur_FS.glsl");
    private FrameBuffer depthFB 	   = new FrameBuffer();
    private FrameBuffer depthHBlurFB   = new FrameBuffer();
    private FrameBuffer depthVBlurFB   = new FrameBuffer();
    private FrameBuffer depthFBLQ 	   = new FrameBuffer();
    private FrameBuffer depthHBlurFBLQ = new FrameBuffer();
    private FrameBuffer depthVBlurFBLQ = new FrameBuffer();
    private Texture depthTex 		   = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture depthHBlurTex 	   = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture depthVBlurTex 	   = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture depthTexLQ 		   = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture depthHBlurTexLQ    = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture depthVBlurTexLQ    = new Texture(GL_TEXTURE_2D, textureUnit++);
   
    // Normals
    private ShaderProgram normalSP      = new ShaderProgram("./shader/fluid/Normal_VS.glsl",	 "./shader/fluid/Normal_FS.glsl");
    private ShaderProgram normalHBlurSP = new ShaderProgram("./shader/fluid/NormalBlur_VS.glsl", "./shader/fluid/NormalHBlur_FS.glsl");
    private ShaderProgram normalVBlurSP = new ShaderProgram("./shader/fluid/NormalBlur_VS.glsl", "./shader/fluid/NormalVBlur_FS.glsl");
    private FrameBuffer normalFB 		= new FrameBuffer();
    private FrameBuffer normalHBlurFB 	= new FrameBuffer();
    private FrameBuffer normalVBlurFB 	= new FrameBuffer();
    private FrameBuffer normalFBLQ 		= new FrameBuffer();
    private FrameBuffer normalHBlurFBLQ	= new FrameBuffer();
    private FrameBuffer normalVBlurFBLQ	= new FrameBuffer();
    private Texture normalTex 		    = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture normalHBlurTex 	    = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture normalVBlurTex 	    = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture normalTexLQ 	    = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture normalHBlurTexLQ    = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture normalVBlurTexLQ    = new Texture(GL_TEXTURE_2D, textureUnit++);

    // Thickness
    private ShaderProgram thicknessSP      = new ShaderProgram("./shader/fluid/Thickness_VS.glsl",	   "./shader/fluid/Thickness_FS.glsl");
    private ShaderProgram thicknessHBlurSP = new ShaderProgram("./shader/fluid/ThicknessBlur_VS.glsl", "./shader/fluid/ThicknessHBlur_FS.glsl");
    private ShaderProgram thicknessVBlurSP = new ShaderProgram("./shader/fluid/ThicknessBlur_VS.glsl", "./shader/fluid/ThicknessVBlur_FS.glsl");
	private FrameBuffer thicknessFB 	   = new FrameBuffer();
	private FrameBuffer thicknessHBlurFB   = new FrameBuffer();
	private FrameBuffer thicknessVBlurFB   = new FrameBuffer();
    private Texture thicknessTex 		   = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture thicknessHBlurTex	   = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture thicknessVBlurTex 	   = new Texture(GL_TEXTURE_2D, textureUnit++);

    // Lighting TODO
    private ShaderProgram lightingSP = new ShaderProgram("./shader/fluid/Lighting_VS.glsl", "./shader/fluid/Lighting_FS.glsl");
	private FrameBuffer lightingFB   = new FrameBuffer();
    private Texture lightingTex      = new Texture(GL_TEXTURE_2D, textureUnit++);

    private ShaderProgram colorSP    = new ShaderProgram("./shader/fluid/Lighting_VS.glsl", "./shader/fluid/Color_FS.glsl");
    private FrameBuffer colorFB      = new FrameBuffer();
    private Texture colorTex         = new Texture(GL_TEXTURE_2D, textureUnit++);

    private ShaderProgram cubeMapSP = new ShaderProgram("./shader/fluid/Lighting_VS.glsl", "./shader/fluid/CubeMap_FS.glsl");
    private FrameBuffer cubeMapFB   = new FrameBuffer();
    private Texture cubeMapTex      = new Texture(GL_TEXTURE_2D, textureUnit++);

    private ShaderProgram testPlaneSP = new ShaderProgram("./shader/fluid/TestPlane_VS.glsl", "./shader/fluid/TestPlane_FS.glsl");
    private FrameBuffer testPlaneFB   = new FrameBuffer();
    private Texture testPlaneTex      = new Texture(GL_TEXTURE_2D, textureUnit++);
    
    // Interpolation between
    private ShaderProgram interpolationSP = new ShaderProgram("./shader/fluid/Interpolation_VS.glsl", "./shader/fluid/Interpolation_FS.glsl");
    private FrameBuffer depthIntFB        = new FrameBuffer();
    private FrameBuffer normalIntFB       = new FrameBuffer();
    private Texture depthIntTex           = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture normalIntTex          = new Texture(GL_TEXTURE_2D, textureUnit++);

    /**
     * Initializes the ShaderPrograms, FrameBuffers, Textures and sets some GL_STATES,
     * also creates the cubemap.
     * @param camTmp Camera from main program
     */
    public FluidRenderer(Camera camTmp) {
    	cam = camTmp;
    	
    	// init shaderPrograms, frameBuffers, ...
    	glPointSize(GL_POINT_SIZE);
    	glPointParameteri(GL_POINT_SIZE_MIN, 1);
        glPointParameteri(GL_POINT_SIZE_MAX, 1000);
        
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(4);
        floatBuffer.put(new float[]{1.0f, 1.0f, 0.0f, 0.0f});
        floatBuffer.position(0);
        glPointParameter(GL_POINT_DISTANCE_ATTENUATION, floatBuffer);
        
        // init depth
    	init(depthSP,       depthFB, 	  depthFBLQ, 	  depthTex, 	 depthTexLQ, "depth", true);
		init(depthHBlurSP,  depthHBlurFB, depthHBlurFBLQ, depthHBlurTex, depthHBlurTexLQ);
		init(depthVBlurSP,  depthVBlurFB, depthVBlurFBLQ, depthVBlurTex, depthVBlurTexLQ);
    	
		// init normals TODO: LQ?
		init(normalSP, 		normalFB, 	   normalFBLQ, 		normalTex, 		normalTexLQ);
    	init(normalHBlurSP, normalHBlurFB, normalHBlurFBLQ, normalHBlurTex, normalHBlurTexLQ);
    	init(normalVBlurSP, normalVBlurFB, normalVBlurFBLQ, normalVBlurTex, normalVBlurTexLQ);
    	
    	// init thickness TODO: LQ?
    	init(thicknessSP, 	   thicknessFB, 	 thicknessTex);
    	init(thicknessHBlurSP, thicknessHBlurFB, thicknessHBlurTex);
    	init(thicknessVBlurSP, thicknessVBlurFB, thicknessVBlurTex);
    	
    	// init lighting TODO: finish
    	init(lightingSP,  lightingFB,  lightingTex);
    	init(colorSP, 	  colorFB,     colorTex);
    	init(cubeMapSP,   cubeMapFB,   cubeMapTex);
    	init(testPlaneSP, testPlaneFB, testPlaneTex);
    	
    	// init interpolation TODO: correction
    	init(interpolationSP, depthIntFB,  depthIntTex);
    	init(interpolationSP, normalIntFB, normalIntTex);
    	
    	createCubeMap();
    	
    	// TODO this is DEBUG
    	skipLow = false;
	} 
	
    /**
     * Renders the current frame's water texture (and draws it for now). 
     */
	public void render() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Clear color must be black and alpha 0!
		viewProj = Util.mul(null, cam.getProjection(), cam.getView());
		// TODO: select pathes
		createDepth(2);
		createNormals(5, 1.5f);
		thickness();
		createLighting();
		cubeMap();
		createTestPlane();

		// TODO this is DEBUG DRAW
		drawTextureSP.use();
		
//		drawTextureSP.setUniform("image", depthTex);
//		drawTextureSP.setUniform("image", depthHBlurTex);
//		drawTextureSP.setUniform("image", depthVBlurTex);
//		drawTextureSP.setUniform("image", normalTex);
//		drawTextureSP.setUniform("image", normalHBlurTex);
//		drawTextureSP.setUniform("image", normalVBlurTex);
//		drawTextureSP.setUniform("image", depthTexLQ);
//		drawTextureSP.setUniform("image", depthHBlurTexLQ);
//		drawTextureSP.setUniform("image", depthVBlurTexLQ);
//		drawTextureSP.setUniform("image", normalTexLQ);
//		drawTextureSP.setUniform("image", normalHBlurTexLQ);
//		drawTextureSP.setUniform("image", normalVBlurTexLQ);
//		drawTextureSP.setUniform("image", thicknessTex);
//		drawTextureSP.setUniform("image", thicknessHBlurTex);
//		drawTextureSP.setUniform("image", thicknessVBlurTex);
//		drawTextureSP.setUniform("image", lightingTex);
//		drawTextureSP.setUniform("image", colorTex);
//		drawTextureSP.setUniform("image", cubeMapTex);
//		drawTextureSP.setUniform("image", testPlaneTex);
		
		screenQuad.draw();
	}
	
	/**
	 * Inits a ShaderProgram with a FrameBuffer and a Texture and
	 * binds it to "color".
	 * @param sp ShaderProgram
	 * @param fb FrameBuffer
	 * @param tex Texture
	 */
	private void init(ShaderProgram sp, FrameBuffer fb, Texture tex) {
		init(sp, fb, tex, "color");
	}
	
	/**
	 * Inits a ShaderProgram with a FrameBuffer, a Texture and an
	 * Attachment Name for the fragment shader.
	 * @param sp ShaderProgram
	 * @param fb FrameBuffer
	 * @param tex Texture
	 * @param attachmentName Attachment Name (has to be "out" in fragment shader)
	 */
	private void init(ShaderProgram sp, FrameBuffer fb, Texture tex, String attachmentName) {
		init(sp, fb, tex, attachmentName, false);
	}

	/**
	 * Inits a ShaderProgram with a FrameBuffer, a Texture, an
	 * Attachment Name for the fragment shader and a given 
	 * DepthTest State.
	 * @param sp ShaderProgram
	 * @param fb FrameBuffer
	 * @param tex Texture
	 * @param attachmentName Attachment Name (has to be "out" in fragment shader)
	 * @param depthTest DepthTest en-/dis-abled
	 */
	private void init(ShaderProgram sp, FrameBuffer fb, Texture tex, String attachmentName, boolean depthTest) {
		init(sp, fb, tex, attachmentName, depthTest, false);
	}
	
	/**
	 * Inits a ShaderProgram with a FrameBuffer, a Texture, an
	 * Attachment Name for the fragment shader, a given DepthTest 
	 * State and a switch for HQ/LQ-Textures.
	 * @param sp ShaderProgram
	 * @param fb FrameBuffer
	 * @param tex Texture
	 * @param attachmentName Attachment Name (has to be "out" in fragment shader)
	 * @param depthTest DepthTest en-/dis-abled
	 * @param low HQ/LQ-Textures
	 */
	private void init(ShaderProgram sp, FrameBuffer fb, Texture tex, String attachmentName, boolean depthTest, boolean low) {
		init(sp, fb, tex, attachmentName, depthTest, low, GL_RGBA16F);
	}

	/**
	 * Inits a ShaderProgram with a FrameBuffer, a Texture, an
	 * Attachment Name for the fragment shader, a given DepthTest 
	 * State, a switch for HQ/LQ-Textures and a switch for
	 * OpenGL's internal format used in the texture.
	 * @param sp ShaderProgram
	 * @param fb FrameBuffer
	 * @param tex Texture
	 * @param attachmentName Attachment Name (has to be "out" in fragment shader)
	 * @param depthTest DepthTest en-/dis-abled
	 * @param low HQ/LQ-Textures
	 * @param internalFormat GL internal Format
	 */
	private void init(ShaderProgram sp, FrameBuffer fb, Texture tex, String attachmentName, boolean depthTest, boolean low, int internalFormat) {
		fb.init(depthTest, WIDTH/(low?2:1), HEIGHT/(low?2:1));
    	fb.addTexture(tex, internalFormat, GL_RGBA);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    	glBindFragDataLocation(sp.getId(), 0, attachmentName);
    	fb.drawBuffers();
	}

	/**
	 * Inits a ShaderProgram for each, a HQ and LQ Texture.
	 * @param sp ShaderProgram
	 * @param fbHQ FrameBuffer HQ Texture
	 * @param fbLQ FrameBuffer LQ Texture
	 * @param texHQ HQ Texture
	 * @param texLQ LQ Texture
	 */
	private void init(ShaderProgram sp, FrameBuffer fbHQ, FrameBuffer fbLQ, Texture texHQ, Texture texLQ) {
		init(sp, fbHQ, fbLQ, texHQ, texLQ, "color");
	}
	
	/**
	 * Inits a ShaderProgram for each, a HQ and LQ Texture, and applies
	 * the textures to a specific attachment name.
	 * @param sp ShaderProgram
	 * @param fbHQ FrameBuffer HQ Texture
	 * @param fbLQ FrameBuffer LQ Texture
	 * @param texHQ HQ Texture
	 * @param texLQ LQ Texture
	 * @param attachmentName Attachment Name (has to be "out" in fragment shader)
	 */
	private void init(ShaderProgram sp, FrameBuffer fbHQ, FrameBuffer fbLQ, Texture texHQ, Texture texLQ, String attachmentName) {
		init(sp, fbHQ, fbLQ, texHQ, texLQ, attachmentName, false);
	}
	
	/**
	 * Inits a ShaderProgram for each, a HQ and LQ Texture, applies
	 * the textures to a specific attachment name and adds a DepthTest switch.
	 * @param sp ShaderProgram
	 * @param fbHQ FrameBuffer HQ Texture
	 * @param fbLQ FrameBuffer LQ Texture
	 * @param texHQ HQ Texture
	 * @param texLQ LQ Texture
	 * @param attachmentName Attachment Name (has to be "out" in fragment shader)
	 * @param depthTest En-/Dis-able DepthTest
	 */
	private void init(ShaderProgram sp, FrameBuffer fbHQ, FrameBuffer fbLQ, Texture texHQ, Texture texLQ, String attachmentName, boolean depthTest) {
		init(sp, fbHQ, fbLQ, texHQ, texLQ, attachmentName, depthTest, GL_RGBA16F);
	}
	
	/**
	 * Inits a ShaderProgram for each, a HQ and LQ Texture, applies
	 * the textures to a specific attachment name, adds a DepthTest switch and
	 * specifies an OpenGL internal format.
	 * @param sp ShaderProgram
	 * @param fbHQ FrameBuffer HQ Texture
	 * @param fbLQ FrameBuffer LQ Texture
	 * @param texHQ HQ Texture
	 * @param texLQ LQ Texture
	 * @param attachmentName Attachment Name (has to be "out" in fragment shader)
	 * @param depthTest En-/Dis-able DepthTest
	 * @param internalFormat OpenGL internal Format
	 */
	private void init(ShaderProgram sp, FrameBuffer fbHQ, FrameBuffer fbLQ, Texture texHQ, Texture texLQ, String attachmentName, boolean depthTest, int internalFormat) {
		fbHQ.init(depthTest, WIDTH, HEIGHT);
		fbHQ.addTexture(texHQ, internalFormat, GL_RGBA);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glBindFragDataLocation(sp.getId(), 0, attachmentName);
		fbHQ.drawBuffers();

		fbLQ.init(depthTest, WIDTH/2, HEIGHT/2);
		fbLQ.addTexture(texLQ, internalFormat, GL_RGBA);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glBindFragDataLocation(sp.getId(), 0, attachmentName);
		fbLQ.drawBuffers();
    }
	
	/**
	 * Starts a path by using a ShaderProgram, binding a FrameBuffer and
	 * setting the clearColor.
	 * @param sp ShaderProgram
	 * @param fb FrameBuffer
	 */
	
	private void startPath(ShaderProgram sp, FrameBuffer fb) {
		sp.use();
		fb.bind();
		fb.clearColor();
	}
	
	/**
	 * Starts a path by using a ShaderProgram.
	 * @param sp ShaderProgram
	 */
	private void startPath(ShaderProgram sp) {
		sp.use();
	}
	
	/**
	 * Binds a FrameBuffer and sets the clear color.
	 * @param fb FrameBuffer
	 */
	private void bindFB(FrameBuffer fb) {
		fb.bind();
		fb.clearColor();
	}
	
	/**
	 * Unbinds the current FrameBuffer.
	 */
	    
	private void endPath() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	/**
	 * Creates a clean depth texture.
	 */
	private void depth() {
		createDepth(0);
	}
	
	/**
	 * Creates clean and blurred depth textures.
	 * @param blurCount number of blur iterations
	 */
	private void createDepth(int blurCount) {
		depthSP.use();
		depthSP.setUniform("view", cam.getView());
		depthSP.setUniform("viewProj", viewProj);
		depthSP.setUniform("viewDistance",cam.getViewDistance());
        depthSP.setUniform("camPos", cam.getCamPos());
   	    
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);

      /*
        testWaterParticles.draw();
        depthFrameBuffer.unbind();

        blur(hBlurFrameBuffer,hBlurSP,vBlurFrameBuffer,vBlurSP, depthTexture, 0,false);
        blur(low_h_BlurFrameBuffer,low_h_BlurSP,low_v_BlurFrameBuffer,low_v_BlurSP, depthTexture, 0,true);  
   */
        bindFB(depthFB);
	    testWaterParticles.draw();
	    
	    if(!skipLow) {
	    	bindFB(depthFBLQ);
	    	testWaterParticles.draw();
	    }
	    
        for(int i = 0; i <= blurCount-1; i++) {
        	depthHBlurSP.use();
        	depthHBlurSP.setUniform("scene", i==0?depthTex:depthVBlurTex);

			bindFB(depthHBlurFB);	    
	    	screenQuad.draw();	
	    	if(!skipLow) {
	    		depthHBlurSP.setUniform("scene", i==0?depthTexLQ:depthVBlurTexLQ);
		    	bindFB(depthHBlurFBLQ);	    
		    	screenQuad.draw();	
	    	}
	    	
        	depthVBlurSP.use();
        	depthVBlurSP.setUniform("scene", depthHBlurTex);
        	
        	bindFB(depthVBlurFB);
        	screenQuad.draw();
        	if(!skipLow) {
        		depthVBlurSP.setUniform("scene", depthHBlurTexLQ);
		    	bindFB(depthVBlurFBLQ);
		    	screenQuad.draw();
        	}
		}        
        
        if(!skipLow)
        	interpolate(depthVBlurTex, depthVBlurTexLQ, depthIntFB);
        
        endPath();
	}
	
	/**
	 * Creates a clean normal texture.
	 */
	private void normals() {
		normals(0);
	}
	
	/**
	 * Creates clean and blurred normal textures.
	 * @param blurCount number of blur iterations
	 */
	private void normals(int blurCount) {
		createNormals(blurCount, 1.0f);
	}
	
	/**
	 * Creates clean and blurred normal textures. 
	 * @param blurCount number of blur iterations
	 * @param offsetValue blur factor
	 */
	private void createNormals(int blurCount, float offsetValue) {
		startPath(normalSP);
		normalSP.setUniform("depthTex", depthTex);
		normalSP.setUniform("texSize", (float)WIDTH);
		normalSP.setUniform("camPos", cam.getCamPos());

		bindFB(normalFB);
		screenQuad.draw();
		if(!skipLow) {
			bindFB(normalFBLQ);
			screenQuad.draw();
		}
		
		for(int i = 0; i <= blurCount-1; i++){
			normalHBlurSP.use();
			normalHBlurSP.setUniform("normalTex", i==0?normalTex:normalVBlurTex);
			normalHBlurSP.setUniform("texSize", (float)WIDTH);
			normalHBlurSP.setUniform("offsetValue", offsetValue);

			bindFB(normalHBlurFB);
			screenQuad.draw();
			if(!skipLow) {
				normalHBlurSP.setUniform("normalTex", i==0?normalTexLQ:normalVBlurTexLQ);
				bindFB(normalHBlurFBLQ);
				screenQuad.draw();
			}
			
			normalVBlurSP.use(); 
			normalVBlurSP.setUniform("normalTex", normalHBlurTex);
			normalVBlurSP.setUniform("texSize", (float)WIDTH);
			normalVBlurSP.setUniform("offsetValue", offsetValue);
			
			bindFB(normalVBlurFB);
			screenQuad.draw();
			if(!skipLow) {
				normalVBlurSP.setUniform("normalTex", normalHBlurTexLQ);
				bindFB(normalVBlurFBLQ);
				screenQuad.draw();
			}
		}
		endPath();
	}

	/** 
	 * Creates a clean thickness textures.
	 */
	private void thickness() {
		thickness(0);
	}
	
	/**
	 * Creates clean and blurred thickness textures.
	 * @param blurCount number of blur iterations.
	 */
	private void thickness(int blurCount) {
		
	    startPath(thicknessSP, thicknessFB);
	    thicknessSP.setUniform("viewProj", viewProj);
	    thicknessSP.setUniform("camera", cam.getCamPos());

        glBlendFunc(GL_ONE, GL_ONE);
        glEnable(GL_BLEND);
        glDisable(GL_DEPTH_TEST);

        testWaterParticles.draw();
        
        for(int i = 0; i <= blurCount-1; i++){
	        startPath(thicknessHBlurSP, thicknessHBlurFB);
		    thicknessHBlurSP.setUniform("thickness", i==0?thicknessTex:thicknessVBlurTex);
		    thicknessHBlurSP.setUniform("depth", depthTex);
		    thicknessHBlurSP.setUniform("width", WIDTH);
	        screenQuad.draw();
	        
	        startPath(thicknessVBlurSP, thicknessVBlurFB);
	        thicknessVBlurSP.setUniform("thickness", thicknessHBlurTex);
	        thicknessVBlurSP.setUniform("depth", depthTex);
	        thicknessVBlurSP.setUniform("height", HEIGHT);
	        screenQuad.draw();
        }
        endPath();
    }
	
	// TODO LIGHTING
	/**
	 * Creates some lighting.
	 */
	private void createLighting() {
		
		startPath(colorSP, colorFB);
        colorSP.setUniform("thicknessTex", thicknessTex);
        screenQuad.draw();
        
        startPath(lightingSP, lightingFB);
	    lightingSP.setUniform("depthTex", depthTex);
	    lightingSP.setUniform("normalTex", normalVBlurTex);
	    lightingSP.setUniform("camPos", cam.getCamPos());
	    lightingSP.setUniform("view", cam.getView());
	    lightingSP.setUniform("thicknessTex", thicknessTex);
	    lightingSP.setUniform("colorTex", colorTex);
        screenQuad.draw();
        
        endPath();
	}
	
	// TODO MORE LIGHTING
	/**
	 * Creates advanced lighting effects
	 */
	private void cubeMap() {
		
		startPath(cubeMapSP, cubeMapFB);
        cubeMapSP.setUniform("normalTex", normalVBlurTex);
        cubeMapSP.setUniform("depthTex", depthTex);
        cubeMapSP.setUniform("lightingTex", lightingTex);
        cubeMapSP.setUniform("plane", testPlaneTex);
        cubeMapSP.setUniform("thicknessTex", thicknessTex);
        cubeMapSP.setUniform("cubeMap", cubemap);
        cubeMapSP.setUniform("view", cam.getView());
        screenQuad.draw();
        
        endPath();
	}
		
	/** 
	 * Draws a plane to the ground. 
	 */
	private void createTestPlane() {
		
		startPath(testPlaneSP, testPlaneFB);
	    testPlaneSP.setUniform("viewProj", viewProj);
	    testPlaneSP.setUniform("colorTex", planeTex);
	    plane.draw();
	    
	    endPath();
	}
	
	/**
	 * Initializes a cubemap.
	 * @param cubeMap cubemap name
	 */
	private void createCubeMap() {
		
		String[] cubeMapFileName = {"cubemap/sky_right.jpg", "cubemap/sky_left.jpg", "cubemap/sky_top.jpg",
				"cubemap/sky_bottom.jpg", "cubemap/sky_front.jpg", "cubemap/sky_back.jpg"};

		int[] cubeMapTargets = {GL_TEXTURE_CUBE_MAP_POSITIVE_X, GL_TEXTURE_CUBE_MAP_NEGATIVE_X, GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
				GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, GL_TEXTURE_CUBE_MAP_POSITIVE_Z, GL_TEXTURE_CUBE_MAP_NEGATIVE_Z};
       
        cubemap.bind();
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        for(int i = 0; i < 6; i++) {
		
        	Util.ImageContents contents = Util.loadImage(cubeMapFileName[i]);
        	int format = 0;
        	int internalFormat = 0;
        	switch(contents.colorComponents) {
            	case 1: internalFormat = GL_R8;    format = GL_RED;  break;
            	case 2: internalFormat = GL_RG8;   format = GL_RG;   break;
            	case 3: internalFormat = GL_RGB8;  format = GL_RGB;  break;
            	case 4: internalFormat = GL_RGBA8; format = GL_RGBA; break;
        	}
        	glTexImage2D(cubeMapTargets[i], 0, internalFormat, contents.width, contents.height, 0, format, GL_FLOAT, contents.data);
        	
		}
		glGenerateMipmap(GL_TEXTURE_CUBE_MAP);
	}
	
	private void blur(FrameBuffer hFB,ShaderProgram hSP,FrameBuffer vFB,ShaderProgram vSP, Texture scene, int counter, boolean low){
		
		hSP.use();
		
		hSP.setUniform("viewProj",Util.mul(null, cam.getProjection(), cam.getView()));
		hSP.setUniform("scene", scene);

//   		GL30.glBindFragDataLocation(depthSP.getId(), 0, "color");
		hFB.bind();
		hFB.clearColor();
    
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        
        screenQuad.draw();
        //testWaterParticles.draw();
        hFB.unbind();
// Vertical Blur
		vSP.use();
		
		vSP.setUniform("viewProj",Util.mul(null, cam.getProjection(), cam.getView()));
		if(low){
		vSP.setUniform("scene", depthHBlurTexLQ);
		}else{
		vSP.setUniform("scene", depthHBlurTex);
		}
		vFB.bind();
		vFB.clearColor();
    
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        
        screenQuad.draw();
 
        vFB.unbind();
		for(int i = 0; i < counter; i++) {
			startPath(hSP, hFB);	    
			if(low){
				vSP.setUniform("scene", depthVBlurTexLQ);
				}else{
				vSP.setUniform("scene", depthVBlurTex);
				}
	    	screenQuad.draw();	
	    	hFB.unbind();	        
	        
        	startPath(vSP, vFB);
        	if(low){
				vSP.setUniform("scene", depthHBlurTexLQ);
				}else{
				vSP.setUniform("scene", depthHBlurTex);
				}
        	screenQuad.draw();
        	vFB.unbind();
		}
	}
	
	private void interpolate(Texture high, Texture low, FrameBuffer fb){
		interpolationSP.use();
		
		interpolationSP.setUniform("viewProj", viewProj);
		interpolationSP.setUniform("highTex", high);
		interpolationSP.setUniform("lowTex", low);

//   		GL30.glBindFragDataLocation(depthSP.getId(), 0, "color");
		fb.bind();
		fb.clearColor();
    
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        
        screenQuad.draw();
        //testWaterParticles.draw();
        fb.unbind();

	}
}
