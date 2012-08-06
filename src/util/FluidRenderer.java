package util;

import static opengl.GL.*;

import java.nio.FloatBuffer;

import opengl.GL;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
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
    
    // Horizontal_Blur Tiefentextur
    private FrameBuffer hBlurFrameBuffer = new FrameBuffer();
    private ShaderProgram hBlurSP = new ShaderProgram("./shader/fluid/Blur_Texture_VS.glsl", "./shader/fluid/Horizontal_Blur_Texture_FS.glsl");
    private Texture hBlurTexture = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);
   
    //Vertical_Blur    
    private FrameBuffer vBlurFrameBuffer = new FrameBuffer();
    private ShaderProgram vBlurSP = new ShaderProgram("./shader/fluid/Blur_Texture_VS.glsl", "./shader/fluid/Vertical_Blur_Texture_FS.glsl");
    private Texture vBlurTexture = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);
   
    // Horizontal_Blur Tiefentextur
    private FrameBuffer low_h_BlurFrameBuffer = new FrameBuffer();
    private ShaderProgram low_h_BlurSP = new ShaderProgram("./shader/fluid/Blur_Texture_VS.glsl", "./shader/fluid/Horizontal_Blur_Texture_FS.glsl");
    private Texture low_h_BlurTexture = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);
   
    //Vertical_Blur
    
    private FrameBuffer low_v_BlurFrameBuffer = new FrameBuffer();
    private ShaderProgram low_v_BlurSP = new ShaderProgram("./shader/fluid/Blur_Texture_VS.glsl", "./shader/fluid/Vertical_Blur_Texture_FS.glsl");
    private Texture low_v_BlurTexture = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);
    
    // Normal-Path
    private FrameBuffer normalFrameBuffer = new FrameBuffer();
    private ShaderProgram normalSP = new ShaderProgram("./shader/fluid/Normal_VS.glsl", "./shader/fluid/Normal_FS.glsl");
    private Texture normalTexture = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);
    private FrameBuffer normalHBlurFrameBuffer = new FrameBuffer();
    private ShaderProgram normalHBlurSP = new ShaderProgram("./shader/fluid/NormalBlur_VS.glsl", "./shader/fluid/NormalHBlur_FS.glsl");
    private Texture normalHBlurTexture = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);
    private FrameBuffer normalVBlurFrameBuffer = new FrameBuffer();
    private ShaderProgram normalVBlurSP = new ShaderProgram("./shader/fluid/NormalBlur_VS.glsl", "./shader/fluid/NormalVBlur_FS.glsl");
    private Texture normalBlurTexture = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);

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

    // FluidLighting-Path
	private FrameBuffer lightingFrameBuffer = new FrameBuffer();
    private ShaderProgram lightingSP = new ShaderProgram("./shader/fluid/FluidLighting_VS.glsl", "./shader/fluid/FluidLighting_FS.glsl");
    private Texture lightingTexture = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);

    private FrameBuffer colorFrameBuffer = new FrameBuffer();
    private ShaderProgram colorSP = new ShaderProgram("./shader/fluid/FluidLighting_VS.glsl", "./shader/fluid/FluidColor_FS.glsl");
    private Texture colorTexture = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);

    private FrameBuffer cubeMapFrameBuffer = new FrameBuffer();
    private ShaderProgram cubeMapSP = new ShaderProgram("./shader/fluid/FluidLighting_VS.glsl", "./shader/fluid/FluidCubeMap_FS.glsl");
    private Texture cubeMapTexture = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);

    private FrameBuffer testPlaneFrameBuffer = new FrameBuffer();
    private ShaderProgram testPlaneSP = new ShaderProgram("./shader/fluid/TestPlane_VS.glsl", "./shader/fluid/TestPlane_FS.glsl");
    private Texture testPlaneTexture = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);

    
    // Final Image
    private FrameBuffer finalImageFB = new FrameBuffer();
    private ShaderProgram finalImageSP = new ShaderProgram("./shader/fluid/Complete_VS.glsl", "./shader/fluid/Complete_FS.glsl");
    private Texture finalImage = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);
    
    private Texture[] textures = { thicknessBlurTexture2, vBlurTexture, lightingTexture };
    private String[] textureNames = { "thickness", "depth", "light" };
    
    private Texture cubemap;
    private Geometry cube = GeometryFactory.createCube();
    private Geometry plane = GeometryFactory.createPlane();
    private Texture planeTex = Texture.generateTexture("Marble.jpg", textureUnit++);
    

    public FluidRenderer(Camera camTmp) {
    	cam = camTmp;
    	
    	// init shaderPrograms, frameBuffers, ...
    	GL11.glPointSize(GL11.GL_POINT_SIZE);
    	
    	init(depthSP, depthFrameBuffer, "depth", depthTexture, true);
		init(hBlurSP, hBlurFrameBuffer, "color", hBlurTexture);
		init(vBlurSP, vBlurFrameBuffer, "color", vBlurTexture);
		lowinit(low_h_BlurSP, low_h_BlurFrameBuffer, "low_h_BlurTexture", low_h_BlurTexture);
		lowinit(low_v_BlurSP, low_v_BlurFrameBuffer, "low_v_BlurTexture", low_v_BlurTexture);
    	init(normalSP, normalFrameBuffer, "color", normalTexture);
    	init(normalHBlurSP, normalHBlurFrameBuffer, "color", normalHBlurTexture);
    	init(normalVBlurSP, normalVBlurFrameBuffer, "color", normalBlurTexture);
    	init(thicknessSP, thicknessFrameBuffer, "color", thicknessTexture);
    	init(thicknessBlurSP, thicknessBlurFrameBuffer, "color", thicknessBlurTexture);
    	init(thicknessBlurSP2, thicknessBlurFrameBuffer2, "color", thicknessBlurTexture2);
    	init(lightingSP, lightingFrameBuffer, "color", lightingTexture);
    	init(colorSP, colorFrameBuffer, "color", colorTexture);
    	init(cubeMapSP, cubeMapFrameBuffer, "color", cubeMapTexture);
    	init(testPlaneSP, testPlaneFrameBuffer, "color", testPlaneTexture);
    	init(finalImageSP, finalImageFB, "color", finalImage);
    	createCubeMap();    	
	} 
	
	public void render() {
		// fluid depth
		depthTexture();
		// fluid normals
		fluidNormals(5, 1.5f);
		// fluid thickness
		fluidThickness();
		// fluid thicknessBlur
		fluidThicknessBlur();
		// fluid lighting
		fluidLighting();
		// fluid cubeMap
		fluidCubeMap();
		// test underground
		fluidTestPlane();
		// fluid normalBlur

		
		// combine images to final image
//		createFinalImage();
		
		// Draws image (will be removed later)
        glDisable(GL_BLEND);
		drawTextureSP.use();
//        drawTextureSP.setUniform("image", depthTexture);
//        drawTextureSP.setUniform("image", hBlurTexture);
//        drawTextureSP.setUniform("image", vBlurTexture);
//        drawTextureSP.setUniform("image", low_h_BlurTexture);
//        drawTextureSP.setUniform("image", low_v_BlurTexture);
//        drawTextureSP.setUniform("image", normalTexture);
//        drawTextureSP.setUniform("image", normalBlurTexture);
//        drawTextureSP.setUniform("image", thicknessTexture);
//        drawTextureSP.setUniform("image", thicknessBlurTexture);
//        drawTextureSP.setUniform("image", thicknessBlurTexture2);
//        drawTextureSP.setUniform("image", lightingTexture);
//        drawTextureSP.setUniform("image", colorTexture);
        drawTextureSP.setUniform("image", cubeMapTexture);
//        drawTextureSP.setUniform("image", testPlaneTexture);
//        drawTextureSP.setUniform("image", normalBlurTexture2);

//        drawTextureSP.setUniform("image", finalImage);
        screenQuadGeo.draw();
	}
	
	private void init(ShaderProgram sp, FrameBuffer fb, String attachmentName, Texture tex) {
		init(sp, fb, attachmentName, tex, false);
	}
	
	private void init(ShaderProgram sp, FrameBuffer fb, String attachmentName, Texture tex, boolean depthTest) {
		fb.init(depthTest, GL.WIDTH, GL.HEIGHT);
    	fb.addTexture(tex, GL30.GL_RGBA16F, GL11.GL_RGBA);
    	GL30.glBindFragDataLocation(sp.getId(), 0, attachmentName);
    	fb.drawBuffers();
	}
	
	private void lowinit(ShaderProgram sp, FrameBuffer fb, String attachmentName, Texture tex) {
		fb.init(true, GL.WIDTH/2, GL.HEIGHT/2);
    	fb.addTexture(tex, GL30.GL_RGBA16F, GL11.GL_RGBA);
    	GL30.glBindFragDataLocation(sp.getId(), 0, attachmentName);
    	fb.drawBuffers();
	}
	
/*	private void init(ShaderProgram sp, FrameBuffer fb, String[] attachmentNames, Texture[] textures) {
    	if(attachmentNames.length != textures.length) throw new RuntimeException("Anzahl attachmentNames und Texturen stimmt nicht ueberein!");
		fb.init(false, GL.WIDTH, GL.HEIGHT);
    	for(int i = 0; i < textures.length; i++) { 
			fb.addTexture(textures[i], GL30.GL_RGBA16F, GL11.GL_RGBA);
			GL30.glBindFragDataLocation(sp.getId(), i, attachmentNames[i]);
		}
    	fb.drawBuffers();
	}*/
	
/*	private void lowinit(ShaderProgram sp, FrameBuffer fb, String[] attachmentNames, Texture[] textures) {
		if(attachmentNames.length != textures.length) throw new RuntimeException("Anzahl attachmentNames und Texturen stimmt nicht ueberein!");
		fb.init(false, GL.WIDTH/2, GL.HEIGHT/2);
		for(int i = 0; i < textures.length; i++) { 
			fb.addTexture(textures[i], GL30.GL_RGBA16F, GL11.GL_RGBA);
			GL30.glBindFragDataLocation(sp.getId(), i, attachmentNames[i]);
		}
		fb.drawBuffers();
	}*/
	
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
		depthSP.setUniform("viewDistance",cam.getViewDistance());
        depthSP.setUniform("camPos", cam.getCamPos());
   	    depthFrameBuffer.bind();
   	    depthFrameBuffer.clearColor();
    
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
      
        testWaterParticles.draw();
        depthFrameBuffer.unbind();

        blur(depthTexture, 0);
        lowBlur(depthTexture,0,4);
   
	}
	
	private void fluidNormals() {
		fluidNormals(0);
	}
	
	private void fluidNormals(int blurCount) {
		fluidNormals(blurCount, 1.0f);
	}
	
	private void fluidNormals(int blurCount, float offsetValue) {
		startPath(normalSP, normalFrameBuffer);
		normalSP.setUniform("depthTex", vBlurTexture);
		normalSP.setUniform("texSize", (float)GL.WIDTH);
		normalSP.setUniform("camPos", cam.getCamPos());
		
	    glDisable(GL_BLEND);
	    glDisable(GL_DEPTH_TEST);
		screenQuadGeo.draw();
		endPath(normalFrameBuffer);
		
		for(int i = 0; i <= blurCount-1; i++){
			startPath(normalHBlurSP, normalHBlurFrameBuffer);
			normalHBlurSP.setUniform("normalTex", i==0?normalTexture:normalBlurTexture);
			normalHBlurSP.setUniform("texSize", (float)GL.WIDTH);
			normalHBlurSP.setUniform("offsetValue", offsetValue);
			screenQuadGeo.draw();
			endPath(normalHBlurFrameBuffer);
			
			startPath(normalVBlurSP, normalVBlurFrameBuffer);
			normalVBlurSP.setUniform("normalTex", normalHBlurTexture);
			normalVBlurSP.setUniform("texSize", (float)GL.WIDTH);
			normalVBlurSP.setUniform("offsetValue", offsetValue);
			screenQuadGeo.draw();
			endPath(normalVBlurFrameBuffer);
		}
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
	    thicknessBlurSP.setUniform("depth", depthTexture);
        screenQuadGeo.draw();
        thicknessBlurFrameBuffer.unbind();
	        
	    startPath(thicknessBlurSP2, thicknessBlurFrameBuffer2);
	    thicknessBlurSP2.setUniform("thickness", thicknessBlurTexture);
	    thicknessBlurSP.setUniform("depth", depthTexture);
        screenQuadGeo.draw();
        thicknessBlurFrameBuffer2.unbind();
        
		/*for(int i = 0; i < 3; i++) {
			startPath(thicknessBlurSP, thicknessBlurFrameBuffer);	    
	    	thicknessBlurSP.setUniform("thickness", thicknessBlurTexture2);	
	    	screenQuadGeo.draw();	
        	thicknessBlurFrameBuffer.unbind();	        
	        
        	startPath(thicknessBlurSP2, thicknessBlurFrameBuffer2);
	    	thicknessBlurSP2.setUniform("thickness", thicknessBlurTexture);
        	screenQuadGeo.draw();
        	thicknessBlurFrameBuffer2.unbind();
		}*/

    }

	
	private void fluidLighting() {  //TODO
		
		startPath(colorSP, colorFrameBuffer);
        colorSP.setUniform("thicknessTex", thicknessTexture);
        screenQuadGeo.draw();
        colorFrameBuffer.unbind();
        
        
        startPath(lightingSP, lightingFrameBuffer);
		
	    lightingSP.setUniform("depthTex", depthTexture);
	    lightingSP.setUniform("normalTex", normalBlurTexture);
	    lightingSP.setUniform("camPos", cam.getCamPos());
	    lightingSP.setUniform("view", cam.getView());
	    lightingSP.setUniform("thicknessTex", thicknessTexture);
	    lightingSP.setUniform("colorTex", colorTexture);

	    
        screenQuadGeo.draw();
        lightingFrameBuffer.unbind();
        	        
	}
	
	private void fluidCubeMap() {  //TODO
		
		startPath(cubeMapSP, cubeMapFrameBuffer);
        cubeMapSP.setUniform("normalTex", normalBlurTexture);
        cubeMapSP.setUniform("depthTex", depthTexture);
        cubeMapSP.setUniform("lightingTex", lightingTexture);
        cubeMapSP.setUniform("plane", testPlaneTexture);
        cubeMapSP.setUniform("thicknessTex", thicknessTexture);
        cubeMapSP.setUniform("cubeMap", cubemap);
		cubeMapSP.setUniform("view", cam.getView());
        screenQuadGeo.draw();
        cubeMapFrameBuffer.unbind();
        	        
	}
		
	private void fluidTestPlane() {  //TODO
		
		startPath(testPlaneSP, testPlaneFrameBuffer);
	    testPlaneSP.setUniform("proj", cam.getProjection());
	    testPlaneSP.setUniform("view", cam.getView());
	    testPlaneSP.setUniform("colorTex", planeTex);
        plane.draw();
        testPlaneFrameBuffer.unbind();
        	        
	}

	
	private void createCubeMap() {

		String[] cubeMapFileName = {"Cubemap/sky_right.jpg","Cubemap/sky_left.jpg","Cubemap/sky_top.jpg",
                "Cubemap/sky_bottom.jpg","Cubemap/sky_front.jpg","Cubemap/sky_back.jpg"};

		int[] cubeMapTargets = {GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X, GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
				GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z};
       
        cubemap = new Texture(GL13.GL_TEXTURE_CUBE_MAP, 15);
        cubemap.bind();
        
		glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		
		for(int i = 0; i < 6; i++) {
		
        	Util.ImageContents contents = Util.loadImage(cubeMapFileName[i]);
        	int format = 0;
        	int internalFormat = 0;
        	switch(contents.colorComponents) {
            	case 1: internalFormat = GL_R8; format = GL_RED; break;
            	case 2: internalFormat = GL_RG8; format = GL_RG; break;
            	case 3: internalFormat = GL_RGB8; format = GL_RGB; break;
            	case 4: internalFormat = GL_RGBA8; format = GL_RGBA; break;
        	}
            
        	glTexImage2D(cubeMapTargets[i], 0, internalFormat, contents.width, contents.height, 0, format, GL_FLOAT, contents.data);
        	
		}
		glGenerateMipmap(GL13.GL_TEXTURE_CUBE_MAP);
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
	
	private void blur(Texture scene, int counter){
		
		hBlurSP.use();
		
		hBlurSP.setUniform("viewProj",Util.mul(null, cam.getProjection(), cam.getView()));
		hBlurSP.setUniform("scene",  scene);
		hBlurSP.setUniform("depthTex", depthTexture);

//   		GL30.glBindFragDataLocation(depthSP.getId(), 0, "color");
		hBlurFrameBuffer.bind();
		hBlurFrameBuffer.clearColor();
    
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        
        screenQuadGeo.draw();
        //testWaterParticles.draw();
        hBlurFrameBuffer.unbind();
// Vertical Blur
		vBlurSP.use();
		
		vBlurSP.setUniform("viewProj",Util.mul(null, cam.getProjection(), cam.getView()));
		vBlurSP.setUniform("scene", hBlurTexture);
		vBlurSP.setUniform("depthTex", depthTexture);

		vBlurFrameBuffer.bind();
		vBlurFrameBuffer.clearColor();
    
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        
        screenQuadGeo.draw();
 
        vBlurFrameBuffer.unbind();
		for(int i = 0; i < counter; i++) {
			startPath(hBlurSP, hBlurFrameBuffer);	    
			hBlurSP.setUniform("scene", vBlurTexture);	
			vBlurSP.setUniform("depthTex",  depthTexture);
	    	screenQuadGeo.draw();	
	    	hBlurFrameBuffer.unbind();	        
	        
        	startPath(vBlurSP, vBlurFrameBuffer);
        	vBlurSP.setUniform("scene", hBlurTexture);
        	vBlurSP.setUniform("depthTex",  depthTexture);
        	screenQuadGeo.draw();
        	vBlurFrameBuffer.unbind();
		}
	}
	private void lowBlur(Texture scene, int counter, int low){
		
        low_h_BlurSP.use();
		
        low_h_BlurSP.setUniform("viewProj",Util.mul(null, cam.getProjection(), cam.getView()));
        low_h_BlurSP.setUniform("scene",  scene);
        low_h_BlurSP.setUniform("depthTex", depthTexture);

//   		GL30.glBindFragDataLocation(depthSP.getId(), 0, "color");
        low_h_BlurFrameBuffer.bind();
        low_h_BlurFrameBuffer.clearColor();
    
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        
        screenQuadGeo.draw();
        //testWaterParticles.draw();
        low_h_BlurFrameBuffer.unbind();
// Vertical Blur
		low_v_BlurSP.use();
		
		low_v_BlurSP.setUniform("viewProj",Util.mul(null, cam.getProjection(), cam.getView()));
		low_v_BlurSP.setUniform("scene", low_h_BlurTexture);
		low_v_BlurSP.setUniform("depthTex", depthTexture);

		low_v_BlurFrameBuffer.bind();
		low_v_BlurFrameBuffer.clearColor();
    
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        
        screenQuadGeo.draw();
 
        low_v_BlurFrameBuffer.unbind();
		for(int i = 0; i < counter; i++) {
			startPath(low_h_BlurSP,low_h_BlurFrameBuffer);	    
			low_h_BlurSP.setUniform("scene", low_v_BlurTexture);	
			low_v_BlurSP.setUniform("depthTex",  depthTexture);
	    	screenQuadGeo.draw();	
	    	low_h_BlurFrameBuffer.unbind();	        
	        
        	startPath(vBlurSP, vBlurFrameBuffer);
        	low_v_BlurSP.setUniform("scene", low_h_BlurTexture);
        	low_v_BlurSP.setUniform("depthTex",  depthTexture);
        	screenQuadGeo.draw();
        	low_v_BlurFrameBuffer.unbind();
		}
	}
	
}
