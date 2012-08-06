package util;

import static opengl.GL.*;

import java.nio.FloatBuffer;

import opengl.GL;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.GL42;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Matrix4f;


public class FluidRenderer {
	
	private Geometry testWaterParticles = GeometryFactory.createTestParticles(1024);
	private Geometry testCube = GeometryFactory.createCube();
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
    
    // Normal-Path
    private FrameBuffer normalFrameBuffer = new FrameBuffer();
    private ShaderProgram normalSP = new ShaderProgram("./shader/fluid/Normal_VS.glsl", "./shader/fluid/Normal_FS.glsl");
    private Texture normalTexture = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);
    private FrameBuffer normalWCFrameBuffer = new FrameBuffer();
    private ShaderProgram normalWCSP = new ShaderProgram("./shader/fluid/NormalWC_VS.glsl", "./shader/fluid/NormalWC_FS.glsl");
    private Texture normalWCTexture = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);
    
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

    private FrameBuffer normalBlurFrameBuffer = new FrameBuffer();
    private ShaderProgram normalBlurSP = new ShaderProgram("./shader/fluid/FluidLighting_VS.glsl", "./shader/fluid/NormalBlur_FS.glsl");
    private Texture normalBlurTexture = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);
    private FrameBuffer normalBlurFrameBuffer2 = new FrameBuffer();
    private ShaderProgram normalBlurSP2 = new ShaderProgram("./shader/fluid/FluidLighting_VS.glsl", "./shader/fluid/NormalBlur2_FS.glsl");
    private Texture normalBlurTexture2 = new Texture(GL11.GL_TEXTURE_2D, textureUnit++);

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

    	init(depthSP, depthFrameBuffer, "depth", depthTexture);
		init(hBlurSP, hBlurFrameBuffer, "color", hBlurTexture);
		init(vBlurSP, vBlurFrameBuffer, "color", vBlurTexture);
    	init(normalSP, normalFrameBuffer, "color", normalTexture);
    	init(normalWCSP, normalWCFrameBuffer, "color", normalWCTexture);
    	init(thicknessSP, thicknessFrameBuffer, "color", thicknessTexture);
    	init(thicknessBlurSP, thicknessBlurFrameBuffer, "color", thicknessBlurTexture);
    	init(thicknessBlurSP2, thicknessBlurFrameBuffer2, "color", thicknessBlurTexture2);
    	init(lightingSP, lightingFrameBuffer, "color", lightingTexture);
    	init(colorSP, colorFrameBuffer, "color", colorTexture);
    	init(cubeMapSP, cubeMapFrameBuffer, "color", cubeMapTexture);
    	init(testPlaneSP, testPlaneFrameBuffer, "color", testPlaneTexture);
    	init(normalBlurSP, normalBlurFrameBuffer, "color", normalBlurTexture);
    	init(normalBlurSP2, normalBlurFrameBuffer2, "color", normalBlurTexture2);
    	init(finalImageSP, finalImageFB, "color", finalImage);
    	createCubeMap();
	} 
	
	public void render() {
		// fluid depth
		depthTexture();
		// fluid normals
		fluidNormals();
		// fluid normals in WC
		fluidNormalsWC();
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
		fluidNormalBlur();
		
		// combine images to final image
		createFinalImage();
		
		// Draws image (will be removed later)
        glDisable(GL_BLEND);
		drawTextureSP.use();

//        drawTextureSP.setUniform("image", depthTexture);
//        drawTextureSP.setUniform("image", hBlurTexture);
//        drawTextureSP.setUniform("image", vBlurTexture);
//        drawTextureSP.setUniform("image", normalTexture);
//        drawTextureSP.setUniform("image", normalWCTexture);
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
        
        // resets buffers
        depthFrameBuffer.reset();
		normalFrameBuffer.reset();
        thicknessFrameBuffer.reset();
        thicknessBlurFrameBuffer.reset();
        thicknessBlurFrameBuffer2.reset();
        lightingFrameBuffer.reset();
        colorFrameBuffer.reset();
        cubeMapFrameBuffer.reset();
        testPlaneFrameBuffer.reset();
        normalBlurFrameBuffer.reset();
        normalBlurFrameBuffer2.reset();
        
        finalImageFB.reset();
	}
	
	public Texture getDepthTexture() {
		return this.depthTexture;
	}
	
	private void init(ShaderProgram sp, FrameBuffer fb, String attachmentName, Texture tex) {
    	fb.addTexture(tex, GL30.GL_RGBA16F, GL11.GL_RGBA);
    	GL30.glBindFragDataLocation(sp.getId(), 0, attachmentName);
    	fb.drawBuffers();
	}
	
/*	private void init(ShaderProgram sp, FrameBuffer fb, String[] attachmentNames, Texture[] textures) {
    	if(attachmentNames.length != textures.length) throw new RuntimeException("Anzahl attachmentNames und Texturen stimmt nicht ueberein!");
		
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
        blur(depthTexture, 2);
   
	}
	
	private void fluidNormals() {
		startPath(normalSP, normalFrameBuffer);
		normalSP.setUniform("depthTex", depthTexture);
		normalSP.setUniform("texSize", (float)GL.WIDTH);
		normalSP.setUniform("camPos", cam.getCamPos());
		
	    glDisable(GL_BLEND);
	    glDisable(GL_DEPTH_TEST);
		screenQuadGeo.draw();
		endPath(normalFrameBuffer);
	}

	private void fluidNormalsWC() {
		startPath(normalWCSP, normalWCFrameBuffer);
		normalWCSP.setUniform("depthTex", depthTexture);
		normalWCSP.setUniform("camPos", cam.getCamPos());
		glDisable(GL_BLEND);
		glDisable(GL_DEPTH_TEST);
		screenQuadGeo.draw();
		endPath(normalWCFrameBuffer);
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
	    thicknessBlurSP2.setUniform("depth", depthTexture);
        screenQuadGeo.draw();
        thicknessBlurFrameBuffer2.unbind();
        
		for(int i = 0; i < 1; i++) {
			startPath(thicknessBlurSP, thicknessBlurFrameBuffer);	    
	    	thicknessBlurSP.setUniform("thickness", thicknessBlurTexture2);	
	    	screenQuadGeo.draw();	
        	thicknessBlurFrameBuffer.unbind();	        
	        
        	startPath(thicknessBlurSP2, thicknessBlurFrameBuffer2);
	    	thicknessBlurSP2.setUniform("thickness", thicknessBlurTexture);
        	screenQuadGeo.draw();
        	thicknessBlurFrameBuffer2.unbind();
		}

    }

	private void fluidNormalBlur() {  //TODO

		startPath(normalBlurSP, normalBlurFrameBuffer);
	    normalBlurSP.setUniform("normalTex", normalTexture);
        screenQuadGeo.draw();
        normalBlurFrameBuffer.unbind();
        
    	startPath(normalBlurSP2, normalBlurFrameBuffer2);
    	normalBlurSP2.setUniform("normalTex", normalBlurTexture);
    	screenQuadGeo.draw();
    	normalBlurFrameBuffer2.unbind();

        for(int i = 0; i < 0; i++) {
        	startPath(normalBlurSP, normalBlurFrameBuffer);
        	normalBlurSP.setUniform("normalTex", normalBlurTexture2);
        	screenQuadGeo.draw();
        	normalBlurFrameBuffer.unbind();

        	startPath(normalBlurSP2, normalBlurFrameBuffer2);
	    	normalBlurSP2.setUniform("normalTex", normalBlurTexture);
        	screenQuadGeo.draw();
        	normalBlurFrameBuffer2.unbind();
        	
        }
        
	}
	
	private void fluidLighting() {  //TODO
		
		startPath(colorSP, colorFrameBuffer);
        colorSP.setUniform("thicknessTex", thicknessTexture);
        screenQuadGeo.draw();
        colorFrameBuffer.unbind();
        
        
        startPath(lightingSP, lightingFrameBuffer);
		
	    lightingSP.setUniform("depthTex", depthTexture);
	    lightingSP.setUniform("normalTex", normalBlurTexture2);
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
		vBlurSP.setUniform("depthTex", depthTexture);

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
		scene = vBlurTexture;
	}
}
