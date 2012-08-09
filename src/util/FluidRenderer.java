package util;

import static opengl.GL.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * This class provides water textures for particles.
 * @author S. Hoeffner, K. Schmidt, A. Werner
 */
public class FluidRenderer {
	private Geometry testWaterParticles = GeometryFactory.createTestParticles(1024 * 8);
	private int 	 textureUnit = 0;
	private Camera   cam;
	private Vector3f lightPos;
	private Matrix4f viewProj;
	
    private ShaderProgram drawTextureSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/CopyTexture_FS.glsl");
    private Geometry screenQuad = GeometryFactory.createScreenQuad();    
    private Geometry cube 		= GeometryFactory.createCube();
    private Geometry plane 		= GeometryFactory.createPlane();
    private Texture  planeTex 	= Texture.generateTexture("Marble.jpg", textureUnit++);
    private Texture  cubemap    = new Texture(GL_TEXTURE_CUBE_MAP, textureUnit++);
    
    // Depth
    private ShaderProgram depthSP      = new ShaderProgram("./shader/fluid/Depth_VS.glsl", "./shader/fluid/Depth_FS.glsl");
    private FrameBuffer depthFB 	   = new FrameBuffer();
    private FrameBuffer depthFBLQ 	   = new FrameBuffer();
    private Texture depthTex 		   = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture depthTexLQ 		   = new Texture(GL_TEXTURE_2D, textureUnit++);
   
    // Normals
    private ShaderProgram normalSP      = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/fluid/Normal_FS.glsl");
    private FrameBuffer normalFB 		= new FrameBuffer();
    private FrameBuffer normalFBLQ 		= new FrameBuffer();
    private Texture normalTex 		    = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture normalTexLQ 	    = new Texture(GL_TEXTURE_2D, textureUnit++);

    // Thickness
    private ShaderProgram thicknessSP      = new ShaderProgram("./shader/fluid/Thickness_VS.glsl", "./shader/fluid/Thickness_FS.glsl");
	private FrameBuffer thicknessFB 	   = new FrameBuffer();
	private FrameBuffer thicknessFBLQ 	   = new FrameBuffer();
    private Texture thicknessTex 		   = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture thicknessTexLQ 		   = new Texture(GL_TEXTURE_2D, textureUnit++);

    // Blur filter
    private ShaderProgram blurSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/fluid/Blur_FS.glsl");
    	// depth
    private FrameBuffer depthHBlurFB   = new FrameBuffer();
    private FrameBuffer depthVBlurFB   = new FrameBuffer();
    private FrameBuffer depthHBlurFBLQ = new FrameBuffer();
    private FrameBuffer depthVBlurFBLQ = new FrameBuffer();
    private Texture depthHBlurTex 	   = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture depthVBlurTex 	   = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture depthHBlurTexLQ    = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture depthVBlurTexLQ    = new Texture(GL_TEXTURE_2D, textureUnit++);
    	// normals
    private FrameBuffer normalHBlurFB 	= new FrameBuffer();
    private FrameBuffer normalVBlurFB 	= new FrameBuffer();
    private FrameBuffer normalHBlurFBLQ	= new FrameBuffer();
    private FrameBuffer normalVBlurFBLQ	= new FrameBuffer();
    private Texture normalHBlurTex 	    = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture normalVBlurTex 	    = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture normalHBlurTexLQ    = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture normalVBlurTexLQ    = new Texture(GL_TEXTURE_2D, textureUnit++);
    	// thickness
    private FrameBuffer thicknessHBlurFB   = new FrameBuffer();
    private FrameBuffer thicknessVBlurFB   = new FrameBuffer();
    private FrameBuffer thicknessHBlurFBLQ = new FrameBuffer();
    private FrameBuffer thicknessVBlurFBLQ = new FrameBuffer();
    private Texture thicknessHBlurTex	   = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture thicknessVBlurTex 	   = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture thicknessHBlurTexLQ	   = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture thicknessVBlurTexLQ	   = new Texture(GL_TEXTURE_2D, textureUnit++);
    
    // Lighting TODO
    private ShaderProgram lightingSP = new ShaderProgram("./shader/fluid/Lighting_VS.glsl", "./shader/fluid/Lighting_FS.glsl");
	private FrameBuffer lightingFB   = new FrameBuffer();
    private Texture lightingTex      = new Texture(GL_TEXTURE_2D, textureUnit++);

    private ShaderProgram finalImageSP = new ShaderProgram("./shader/fluid/Lighting_VS.glsl", "./shader/fluid/FinalImage_FS.glsl");
    private FrameBuffer finalImageFB   = new FrameBuffer();
    private Texture finalImageTex      = new Texture(GL_TEXTURE_2D, textureUnit++);

    private ShaderProgram testPlaneSP = new ShaderProgram("./shader/fluid/TestPlane_VS.glsl", "./shader/fluid/TestPlane_FS.glsl");
    private FrameBuffer testPlaneFB   = new FrameBuffer();
    private Texture testPlaneTex      = new Texture(GL_TEXTURE_2D, textureUnit++);
    
    // Interpolation between
    private ShaderProgram interpolationSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/fluid/Interpolation_FS.glsl");
    private FrameBuffer depthIntFB        = new FrameBuffer();
    private FrameBuffer normalIntFB       = new FrameBuffer();
    private FrameBuffer thicknessIntFB    = new FrameBuffer();
    private Texture depthIntTex           = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture normalIntTex          = new Texture(GL_TEXTURE_2D, textureUnit++);
    private Texture thicknessIntTex       = new Texture(GL_TEXTURE_2D, textureUnit++);

    public FluidRenderer(Camera camTmp, Vector3f light) {
    	cam = camTmp;
    	lightPos = light;
    	
    	// init shaderPrograms, frameBuffers, ...
    	glPointSize(GL_POINT_SIZE);
    	glPointParameteri(GL_POINT_SIZE_MIN, 1);
        glPointParameteri(GL_POINT_SIZE_MAX, 1000);
        
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(4);
        floatBuffer.put(new float[]{1.0f, 1.0f, 0.0f, 0.0f});
        floatBuffer.position(0);
        glPointParameter(GL_POINT_DISTANCE_ATTENUATION, floatBuffer);
        
        // init depth, normal, thickness
    	init(depthSP, depthFB, depthFBLQ, depthTex, depthTexLQ, "depth", true);
		init(normalSP, normalFB, normalFBLQ, normalTex, normalTexLQ);
    	init(thicknessSP, thicknessFB, thicknessFBLQ, thicknessTex, thicknessTexLQ);

    	// init lighting
    	init(lightingSP, lightingFB, lightingTex, "color", false, false, GL_RGBA8);
    	init(finalImageSP, finalImageFB, finalImageTex, "color", false, false, GL_RGBA8);
    	init(testPlaneSP, testPlaneFB, testPlaneTex, "color", false, false, GL_RGBA8);
    	
    	// init interpolation
    	init(interpolationSP, depthIntFB,     depthIntTex);
    	init(interpolationSP, normalIntFB,    normalIntTex);
    	init(interpolationSP, thicknessIntFB, thicknessIntTex);
    	
    	// init blur
    	init(blurSP, new FrameBuffer[]{depthHBlurFB,    depthVBlurFB,    normalHBlurFB,    normalVBlurFB,    thicknessHBlurFB,    thicknessVBlurFB}, 
    				 new Texture[]    {depthHBlurTex,   depthVBlurTex,   normalHBlurTex,   normalVBlurTex,   thicknessHBlurTex,   thicknessVBlurTex});
    	init(blurSP, new FrameBuffer[]{depthHBlurFBLQ,  depthVBlurFBLQ,  normalHBlurFBLQ,  normalVBlurFBLQ,  thicknessHBlurFBLQ,  thicknessVBlurFBLQ}, 
    				 new Texture[]    {depthHBlurTexLQ, depthVBlurTexLQ, normalHBlurTexLQ, normalVBlurTexLQ, thicknessHBlurTexLQ, thicknessVBlurTexLQ}, true);

    	createCubeMap();
    	
	} 
	
	public void render() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Clear color must be black and alpha 0!
		viewProj = Util.mul(null, cam.getProjection(), cam.getView());
		
		// depth
		depth();
		
		// normals
		normals(); // Wenn normals(true), dann werden die Normalen mit der geblurrten depth berechnet. Aufpassen!
		
		// thickness
		thickness();
		
		// blur
		blur(depthTexLQ, depthHBlurFB, depthVBlurFB, 1.0f);
		blur(depthTexLQ, depthHBlurFBLQ, depthVBlurFBLQ, 1.0f);
		blur(normalTex, normalHBlurFB, normalVBlurFB, 1.0f);
		blur(normalTexLQ, normalHBlurFBLQ, normalVBlurFBLQ, 1.0f);
		blur(thicknessTexLQ, thicknessHBlurFB, thicknessVBlurFB, 1.0f);
		blur(thicknessTexLQ, thicknessHBlurFBLQ, thicknessVBlurFBLQ, 1.0f);
		
		// interpolation
		interpolate(depthVBlurTex, depthVBlurTexLQ, depthIntFB);
		interpolate(normalVBlurTex, normalVBlurTexLQ, normalIntFB);
		interpolate(thicknessVBlurTex, thicknessVBlurTexLQ, thicknessIntFB); // möglicherweise tun's hier auch die normale 
																			 // und die normal große geblurrte Texture, dann 
																			 // könnte man sich den gesamten LQ-Kram sparen
		// lighting
		testPlane();
		lighting();
		finalImage();

		drawTextureSP.use();
		
//		drawTextureSP.setUniform("image", depthTex);
//		drawTextureSP.setUniform("image", depthHBlurTex);
//		drawTextureSP.setUniform("image", depthVBlurTex);
//		drawTextureSP.setUniform("image", depthTexLQ);
//		drawTextureSP.setUniform("image", depthHBlurTexLQ);
//		drawTextureSP.setUniform("image", depthVBlurTexLQ);
//		drawTextureSP.setUniform("image", depthIntTex);
//		drawTextureSP.setUniform("image", normalTex);
//		drawTextureSP.setUniform("image", normalHBlurTex);
//		drawTextureSP.setUniform("image", normalVBlurTex);
//		drawTextureSP.setUniform("image", normalTexLQ);
//		drawTextureSP.setUniform("image", normalHBlurTexLQ);
//		drawTextureSP.setUniform("image", normalVBlurTexLQ);
//		drawTextureSP.setUniform("image", normalIntTex);
//		drawTextureSP.setUniform("image", thicknessTex);
//		drawTextureSP.setUniform("image", thicknessHBlurTex);
//		drawTextureSP.setUniform("image", thicknessVBlurTex);
//		drawTextureSP.setUniform("image", thicknessTexLQ);
//		drawTextureSP.setUniform("image", thicknessHBlurTexLQ);
//		drawTextureSP.setUniform("image", thicknessVBlurTexLQ);
//		drawTextureSP.setUniform("image", lightingTex);
		drawTextureSP.setUniform("image", finalImageTex);
//		drawTextureSP.setUniform("image", testPlaneTex);
		
		screenQuad.draw();
	}
	
	private void init(ShaderProgram sp, FrameBuffer fb, Texture tex) {
		init(sp, fb, tex, "color");
	}
	
	private void init(ShaderProgram sp, FrameBuffer fb, Texture tex, String attachmentName) {
		init(sp, fb, tex, attachmentName, false);
	}

	private void init(ShaderProgram sp, FrameBuffer fb, Texture tex, String attachmentName, boolean depthTest) {
		init(sp, fb, tex, attachmentName, depthTest, false);
	}
	
	private void init(ShaderProgram sp, FrameBuffer fb, Texture tex, String attachmentName, boolean depthTest, boolean low) {
		init(sp, fb, tex, attachmentName, depthTest, low, GL_RGBA16F);
	}

	private void init(ShaderProgram sp, FrameBuffer fb, Texture tex, String attachmentName, boolean depthTest, boolean low, int internalFormat) {
		fb.init(depthTest, WIDTH/(low?2:1), HEIGHT/(low?2:1));
    	fb.addTexture(tex, internalFormat, GL_RGBA);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    	glBindFragDataLocation(sp.getId(), 0, attachmentName);
    	fb.drawBuffers();
	}

	private void init(ShaderProgram sp, FrameBuffer fbHQ, FrameBuffer fbLQ, Texture texHQ, Texture texLQ) {
		init(sp, fbHQ, fbLQ, texHQ, texLQ, "color");
	}
	
	private void init(ShaderProgram sp, FrameBuffer fbHQ, FrameBuffer fbLQ, Texture texHQ, Texture texLQ, String attachmentName) {
		init(sp, fbHQ, fbLQ, texHQ, texLQ, attachmentName, false);
	}
	
	private void init(ShaderProgram sp, FrameBuffer fbHQ, FrameBuffer fbLQ, Texture texHQ, Texture texLQ, String attachmentName, boolean depthTest) {
		init(sp, fbHQ, fbLQ, texHQ, texLQ, attachmentName, depthTest, GL_RGBA16F);
	}
	
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
	
	private void init(ShaderProgram sp, FrameBuffer[] fb, Texture[] tex) {
		init(sp, fb, tex, false);
	}
	
	private void init(ShaderProgram sp, FrameBuffer[] fb, Texture[] tex, boolean low) {
		for(int i = 0; i < fb.length; i++) {
			fb[i].init(false, WIDTH/(low?2:1), HEIGHT/(low?2:1));
			fb[i].addTexture(tex[i], GL_RGBA16F, GL_RGBA);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			glBindFragDataLocation(sp.getId(), 0, "color");
			fb[i].drawBuffers();
		}
    }
	
	private void startPath(ShaderProgram sp, FrameBuffer fb) {
		sp.use();
		fb.bind();
		fb.clearColor();
	}
	
	private void startPath(ShaderProgram sp) {
		sp.use();
	}
	
	private void bindFB(FrameBuffer fb) {
		fb.bind();
		fb.clearColor();
	}
	
	private void endPath() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	private void depth() {
		depthSP.use();
		depthSP.setUniform("view", cam.getView());
		depthSP.setUniform("viewProj", viewProj);
		depthSP.setUniform("viewDistance", cam.getViewDistance());
        depthSP.setUniform("camPos", cam.getCamPos());
   	    
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);

        bindFB(depthFB);
	    testWaterParticles.draw();
	    
    	bindFB(depthFBLQ);
    	testWaterParticles.draw();
	    
        endPath();
	}
	
	private void normals() { normals(false); }
	private void normals(boolean source) {
		startPath(normalSP);
		normalSP.setUniform("depthTex", source?depthVBlurTex:depthTex);
		normalSP.setUniform("texSize", (float)WIDTH);
		normalSP.setUniform("camPos", cam.getCamPos());

		bindFB(normalFB);
		screenQuad.draw();
		
//		normalSP.setUniform("depthTex", source?depthVBlurTexLQ:depthTexLQ); // TODO probieren, ob diese Zeile doch rein muss
		bindFB(normalFBLQ);
		screenQuad.draw();
		
		endPath();
	}

	private void thickness() {
		
	    startPath(thicknessSP, thicknessFB);
	    thicknessSP.setUniform("viewProj", viewProj);
	    thicknessSP.setUniform("camera", cam.getCamPos());

        glBlendFunc(GL_ONE, GL_ONE);
        glEnable(GL_BLEND);
        glDisable(GL_DEPTH_TEST);

        testWaterParticles.draw();
        bindFB(thicknessFBLQ);
        testWaterParticles.draw();

        endPath();
    }

	private void lighting() {
        startPath(lightingSP, lightingFB);
        lightingSP.setUniform("view", cam.getView());
	    lightingSP.setUniform("depthTex", depthVBlurTex);   // TODO Sorry Kristin, ich habe das mal in geblurrten geändert, musst du evtl. anders machen!
	    lightingSP.setUniform("normalTex", normalVBlurTex);
	    lightingSP.setUniform("thicknessTex", thicknessTex);
        lightingSP.setUniform("cubeMap", cubemap);
        lightingSP.setUniform("plane", testPlaneTex);
        lightingSP.setUniform("lightPosW", lightPos);
        screenQuad.draw();
        
        endPath();
	}
	
	// TODO final image
	private void finalImage() {
		
		startPath(finalImageSP, finalImageFB);
        finalImageSP.setUniform("lightingTex", lightingTex);
        finalImageSP.setUniform("plane", testPlaneTex);
        finalImageSP.setUniform("thicknessTex", thicknessTex);
        screenQuad.draw();
        
        endPath();
	}
		
	private void testPlane() {
		
		startPath(testPlaneSP, testPlaneFB);
	    testPlaneSP.setUniform("viewProj", viewProj);
	    testPlaneSP.setUniform("colorTex", planeTex);
	    plane.draw();
	    
	    endPath();
	}
	
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
	
	private void blur(Texture tex, FrameBuffer hFB, FrameBuffer vFB, float offset) {
		blur(new Texture[] { tex }, new FrameBuffer[] { hFB }, new FrameBuffer[] { vFB }, new float[] { offset });
	}
	
	private void blur(Texture[] tex, FrameBuffer[] hFB, FrameBuffer[] vFB, float[] offset) {
		startPath(blurSP);
		runBlur(tex, hFB, vFB, offset);
		endPath();
	}
	
	private void runBlur(Texture[] tex, FrameBuffer[] hFB, FrameBuffer[] vFB, float[] offset) {
		blurSP.setUniform("dir", 1);
		for(int i = 0; i < vFB.length; i++) {
			blurSP.setUniform("tex", tex[i]);
			bindFB(hFB[i]);
			screenQuad.draw();
		}
		
		blurSP.setUniform("dir", 0);
		for(int i = 0; i < hFB.length; i++) {
			blurSP.setUniform("tex", hFB[i].getTexture(0));
			bindFB(vFB[i]);
			screenQuad.draw();
		}
	}
	
	// TODO Interpolation - evtl. depth neu skalieren?
	private void interpolate(Texture high, Texture low, FrameBuffer fb){
		startPath(interpolationSP, fb);
		interpolationSP.setUniform("highTex", high);
		interpolationSP.setUniform("lowTex", low);
		interpolationSP.setUniform("depthTex", depthTex);

        screenQuad.draw();
        
        endPath();
	}
}
