package main;

import static opengl.GL.*;

import java.awt.peer.LightweightPeer;
import java.util.logging.Level;
import java.util.logging.Logger;

import opengl.GL;
import opengl.OpenCL;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import util.*;
import window.MenuDialog;

/**
 * @author nico3000
 */
public class TerrainMain {
	// uniform locations
    private static int modelLoc;
    
    
	// textures
    private static Texture normalQuaderTexture;
    private static Texture quaderTexture;
    private static Texture specularQuaderTexture;
    private static Texture bumpQuaderTexture;
    private static Texture skydomeTexture;
    private static Texture sunTexture;
   private static Texture skyCloudTexture;
    private static Texture noiseTexture;
    private static Texture blackTexture;
    
    // current configurations
    private static boolean bContinue = true;
    private static boolean splitScreen = false;
    private static int[] splitScreenVal = { 1, 2, 3, 4 };

	private static boolean culling   = true;
    private static boolean wireframe = true;
    
    private static boolean tonemapping =  true;
    private static boolean rotatelight = true;
    private static boolean bloomBlend = false;
    private static boolean bloom = true;
    private static boolean shadows = true;
    private static boolean godRays = true;
    private static boolean ambientocclusion = true;
    private static boolean normalmapping = true;
    
    // control
    private static final Vector3f moveDir = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final Camera cam = new Camera(); 
    private static final Camera shadowCam = new Camera();
    
    // animation params
    private static float ingameTime = 0.0f;
    private static float ingameTimePerSecond = 1.0f;
    
    //post processing
    private static float exposure    = 0.8f;
    private static float bloomFactor = 0.6f;
    private static Vector4f brightnessFactor  = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    
    private static Vector3f sunDirection = new Vector3f(47.0f, 20f, 0f);
    private static Vector3f sunDirectionOld = new Vector3f();
    private static Vector3f sunDirectionStart = new Vector3f(0f, 0f, 0f);
         
    private static final ScreenManipulation screenMan = new ScreenManipulation();
    
    private static float orthoScaleValue = 15f;
    
    private static final ScreenManipulation screenMan = new ScreenManipulation();
    
    private static ShaderProgram fboSP;
    private static ShaderProgram shadowSP;
    
    //matrix
    private static Matrix4f cloudModelMatrix = new Matrix4f();
    private static Matrix4f sunMatrix = new Matrix4f();
    private static Matrix4f sunTilt = new Matrix4f();
    private static Matrix4f sunTranslation = new Matrix4f();
    private static Matrix4f sunRotation = new Matrix4f();
    
    
    public static void main(String[] argv) {
        try {
            init();
            OpenCL.init();
            glEnable(GL_CULL_FACE);
            glFrontFace(GL_CCW);
            glCullFace(GL_BACK);
            glEnable(GL_DEPTH_TEST);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_COLOR);

            //texturen
            normalQuaderTexture   = Texture.generateTexture("./res/stone_wall_normal_map.jpg",0 );
            quaderTexture         = Texture.generateTexture("./res/stone_wall.jpg",1 );
            specularQuaderTexture = Texture.generateTexture("./res/stone_wall_specular.jpg",3 );
            bumpQuaderTexture     = Texture.generateTexture("./res/stone_wall_bump.jpg",4 );
           
            skydomeTexture         = Texture.generateTexture("./res/sky_2.jpg",5 );
            sunTexture			   = Texture.generateTexture("./res/sun.jpg",6);
            skyCloudTexture        = Texture.generateTexture("./res/sky_sw.jpg",9 );
            noiseTexture 		   = Texture.generateTexture("./res/noise.png",10);
            blackTexture		   = Texture.generateTexture("./res/blackTex.jpg",12);
            
            
            //initialize ScreenManipulation with all the used Shaders
            screenMan.init("./shader/ScreenQuad_VS.glsl", "./shader/Blur_FS.glsl",
            "./shader/Brightness_FS.glsl", "./shader/Bloom_FS.glsl", "./shader/ToneMapping_FS.glsl",
            "./shader/PhongLighting_FS.glsl", 15, GL.WIDTH, GL.HEIGHT);
            
            //movement 
            Util.rotationX((float)Math.toRadians(-45.0), sunTilt);
            Util.translationX(48.0f, sunTranslation);
            
            render();
            OpenCL.destroy();
            destroy();
        } catch (LWJGLException ex) {
            Logger.getLogger(TerrainMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

	public static void render() throws LWJGLException {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // background color: dark red
        
        long last = System.currentTimeMillis();
        long now, millis;
        long frameTimeDelta = 0;
        int frames = 0;
        
        shadowSP = new ShaderProgram("./shader/Main_VS.glsl", "./shader/Main_FS.glsl");
        fboSP = new ShaderProgram("./shader/Main_VS.glsl", "./shader/Main_FS.glsl");
        //fboSP = new ShaderProgram("./shader/MainShadow_VS.glsl", "./shader/MainShadow_FS.glsl");
        
        Matrix4f floorQuadMatrix = new Matrix4f();
        Matrix4f skyDomeMatrix = new Matrix4f();
        //Matrix4f floorQuadITMatrix = new Matrix4f();
        
        shadowCam.changeProjection();
        Util.mul(floorQuadMatrix, Util.translationY(0, null), Util.scale(50, null), Util.rotationX(-Util.PI_DIV2, null)); 

        
        ShadowShader shadowShader = new ShadowShader();
        shadowShader.init(14);
        shadowShader.registerShaderProgram(shadowSP);
        
        DeferredShader shader = new DeferredShader();
        shader.init(40);
        shader.registerShaderProgram(fboSP);
        modelLoc = glGetUniformLocation(fboSP.getId(), "model");
        
        
        Geometry testCube = GeometryFactory.createCube();
        
        Geometry testCube2 = GeometryFactory.createSphere(0.5f, 20, 20);
        
        Geometry testCube1 = GeometryFactory.createCube();
        Geometry floorQuad = GeometryFactory.createWhiteScreenQuad();
        Geometry sunCube = GeometryFactory.createQuad();
        
        //Skydome
        Geometry skyDome  = GeometryFactory.createSkyDome(50, 50, 50);
        Geometry skyCloud  = GeometryFactory.createSkyDome(45, 50, 50);
        //Sonne
        
        
        //enlighted fbo
        FrameBuffer enlightenedFBO = new FrameBuffer();
        enlightenedFBO.init(false, GL.WIDTH, GL.HEIGHT);
        
        FrameBuffer fbo = new FrameBuffer();
        fbo.init(false, GL.WIDTH, GL.HEIGHT);
        
        FrameBuffer AOfbo = new FrameBuffer();
        AOfbo.init(false, GL.WIDTH, GL.HEIGHT);
        
        FrameBuffer shadowFBO = new FrameBuffer();
        shadowFBO.init(false, GL.WIDTH, GL.HEIGHT);
        
        //static Matrix calculation
    	Matrix4f modelMatrix = Util.mul(null, Util.rotationX(1.0f, null), Util.rotationZ(1.0f, null));
    	Matrix4f modelIT = Util.transposeInverse(modelMatrix, null);
    	
    	Matrix4f modelMatrix1 = Util.mul(null, Util.translationX(10f, null), Util.translationZ(10f, null), Util.translationY(5f, null));
    	Matrix4f modelIT1 = Util.transposeInverse(modelMatrix1, null);
    	
    	//dynamic matrices
    	Matrix4f shadowMatrix = new Matrix4f();
        
        while(bContinue && !Display.isCloseRequested()) {
            // time handling
            now = System.currentTimeMillis();
            millis = now - last;
            last = now;     
            frameTimeDelta += millis;
            ++frames;
            
            
            shadowCam.setCamDir(sunDirection.negate(null));
            shadowCam.setCamPos(sunDirection);
            
            //animate(millis);
           
            
            if(frameTimeDelta > 1000) {
                System.out.println(1e3f * (float)frames / (float)frameTimeDelta + " FPS");
                frameTimeDelta -= 1000;
                frames = 0;
            }
            
            // input and animation
            handleInput(millis);
            animate(millis);
            if(rotatelight) {
            	Util.transformDir(Util.rotationY(0.005f, null), sunDirection, sunDirection);
            }
            if(bloomBlend && bloomFactor >0.5f) {
            	bloomFactor -= 0.08;
            }
            else {
            	bloomBlend = false;
            }
            	
            // clear screen
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            
            Util.mul(shadowMatrix, shadowCam.getProjection(), shadowCam.getView());

            //test cube
            fboSP.use();

        	//Main_VS
        	fboSP.setUniform("model", 	 	 modelMatrix);
        	fboSP.setUniform("modelIT",  	 modelIT);
        	fboSP.setUniform("viewProj", 	 Util.mul(null, cam.getProjection(), cam.getView()));
            fboSP.setUniform("shadowMatrix", shadowMatrix);
        	fboSP.setUniform("camPos",   	 cam.getCamPos());
            fboSP.setUniform("view", 		 cam.getView());
            fboSP.setUniform("bumpTexture", bumpQuaderTexture);
            //Main_FS
            fboSP.setUniform("normalTexture", normalQuaderTexture);
            fboSP.setUniform("specularTexture", specularQuaderTexture);
            fboSP.setUniform("textureImage", quaderTexture);
            fboSP.setUniform("camFar", cam.getFar());
            
            shader.bind();            
            shader.clear();
        	
            testCube.draw();
            
            //2nd test cube
            fboSP.setUniform("model", modelMatrix1);
            fboSP.setUniform("modelIT", modelIT1);

            testCube1.draw();
            
            fboSP.setUniform("model", modelMatrix2);
            fboSP.setUniform("modelIT", modelIT2);
            testCube2.draw();
            
            
            //skydome 
			fboSP.setUniform("model", skyDomeMatrix);
            fboSP.setUniform("modelIT", skyDomeMatrix);
            fboSP.setUniform("normalTexture",blackTexture );
            fboSP.setUniform("specularTexture", blackTexture);
            
            glEnable(GL_BLEND);
            fboSP.setUniform("textureImage", skydomeTexture);
            skyDome.draw();
            
            //Wolken
            fboSP.setUniform("textureImage", skyCloudTexture);
            matrix2uniform(cloudModelMatrix, modelLoc);
            skyCloud.draw();
            
            
            
			//sun cube
            fboSP.setUniform("model", sunMatrix);
            fboSP.setUniform("modelIT", Util.transposeInverse(sunMatrix, null));
            fboSP.setUniform("textureImage", sunTexture);
            sunCube.draw();
            glDisable(GL_BLEND);
            
            fboSP.setUniform("normalTexture", normalQuaderTexture);
            //floor quad
            fboSP.setUniform("model", floorQuadMatrix);
            fboSP.setUniform("modelIT", floorQuadMatrix);
            fboSP.setUniform("textureImage", quaderTexture);
            floorQuad.draw();
            
        	shader.finish();
        	
        	//shader.DrawTexture(shader.getNormalTexture());

        	//test cube (shadow map)
        	if(rotatelight) {
	        	glCullFace(GL_FRONT);
	        	shadowSP.use();
	        	shadowSP.setUniform("model", 	modelMatrix);
	        	shadowSP.setUniform("modelIT",  modelIT);
	        	shadowSP.setUniform("viewProj", shadowMatrix);
	        	shadowSP.setUniform("camPos",   shadowCam.getCamPos());
	    	
	        	shadowShader.bind();
	        	shadowShader.clear();
	   
	        	testCube.draw();
	
	            shadowSP.setUniform("model", modelMatrix1);
	            shadowSP.setUniform("modelIT", modelIT1);
	            testCube1.draw();
				glCullFace(GL_BACK);
	            
	            
	        	shadowSP.setUniform("model",    floorQuadMatrix);
	        	shadowSP.setUniform("modelIT",  floorQuadMatrix);
	        	shadowSP.setUniform("viewProj", shadowMatrix);
	        	//shadowSP.setUniform("camPos",   shadowCam.getCamPos());
	        	
	        	floorQuad.draw();
	        	
	        	shadowShader.finish();
        	}
        	
        	
        	if (shadows) {
            	enlightenedFBO = screenMan.getShadowLighting(shader, shadowShader, cam.getCamPos(), sunDirection, shadowCam);
        	}
        	else {
        		enlightenedFBO = screenMan.getMaglight(shader, cam.getCamPos(), sunDirection);
        	}
        	
        	if (ambientocclusion){
            	//Ambient Occlusion
            	AOfbo = screenMan.getAmbientOcclusion(noiseTexture, shader.getNormalTexture(), enlightenedFBO.getTexture(0));
        	}
        	else{
        		AOfbo = enlightenedFBO;
        	}

        	if (godRays) {
            	enlightenedFBO = screenMan.getGodRay(shader.getSkyTexture(), AOfbo.getTexture(0), Util.mul(null, cam.getProjection(), cam.getView()), sunDirection );
        	}
        	
        	if (splitScreen) {
        		fbo = getQuadScreen(splitScreenVal, shader, shadowShader);
        	}
        	else {
	        	if (tonemapping) {
	            	if (bloom) {
	            		fbo = screenMan.getToneMappedBloomed(enlightenedFBO, bloomFactor, brightnessFactor, exposure);
	            	}
	            	else {
	            		fbo = screenMan.getToneMapped(enlightenedFBO, exposure);
	            	}
	        	}
	        	else {
	            	if (bloom) {
	            		fbo = screenMan.getBloom(enlightenedFBO, bloomFactor, brightnessFactor);
	            	}
	            	else {
	            		fbo = enlightenedFBO;
	            	}
	        	}
        	} 
        	
        	shader.DrawTexture(fbo.getTexture(0));

        	sunDirectionOld.set(sunDirection);
            // present screen
            Display.update();
            Display.sync(60);
        }
        MenuDialog.destroyInstance();
        screenMan.delete();
        shader.delete();
        shadowShader.delete();
    }
    
	/**
	 * creates a screen with 4 frame buffers
	 * @param splitScreenValue what shall be shown on the screen parts
	 * @param shader deferred shader
	 * @return frame buffer containing the screen 
	 */
	private static FrameBuffer getQuadScreen(int[] splitScreenValue, DeferredShader shader, ShadowShader shadowShader) {
	
		FrameBuffer fbo0 = new FrameBuffer();
		FrameBuffer fbo1 = new FrameBuffer();
		FrameBuffer fbo2 = new FrameBuffer();
		FrameBuffer fbo3 = new FrameBuffer();
		
		fbo0 = getScreen(splitScreenValue[0], shader, shadowShader);
		fbo1 = getScreen(splitScreenValue[1], shader, shadowShader);
		fbo2 = getScreen(splitScreenValue[2], shader, shadowShader);
		fbo3 = getScreen(splitScreenValue[3], shader, shadowShader);
		return screenMan.getQuadScreenView(fbo0, fbo1, fbo2, fbo3);	
	}
	
	/**
	 * Returns a specific frame buffer according to a input variable.
	 * @param splitScreenValue defining what to render
	 * @param shader deferred shader
	 * @return frame buffer with the screen
	 */
	private static FrameBuffer getScreen(int splitScreenValue, DeferredShader shader, ShadowShader shadowShader) {
		
		FrameBuffer fbo = new FrameBuffer();
		FrameBuffer enlightenedFBO = screenMan.getLighting(shader, cam.getCamPos(), sunDirection);
		
		switch (splitScreenValue) {   		
			case 0: 
				fbo = enlightenedFBO; break;
			case 1:
				fbo = screenMan.getToneMappedBloomed(enlightenedFBO, bloomFactor, brightnessFactor, exposure); break;
			case 2:
				fbo = screenMan.getToneMapped(enlightenedFBO, exposure); break;
			case 3:
				fbo = screenMan.getBlur54(enlightenedFBO); break;
			case 4:
				fbo = screenMan.getBloom(enlightenedFBO, bloomFactor, brightnessFactor); break;
			case 5:
				fbo = screenMan.getBrightness(enlightenedFBO, brightnessFactor); break;
			case 6:
				//fbo = screenMan.getShadowMix(shadowShader.getWorldTexture()); break;

		}		
		return fbo;
	}
	
    /**
     * Behandelt Input und setzt die Kamera entsprechend.
     * @param millis Millisekunden seit dem letzten Aufruf
     */
    public static void handleInput(long millis) {
        float moveSpeed = 2e-3f*(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 2.0f : 1.0f)*(float)millis;
        float camSpeed = 5e-3f;
        
        while(Keyboard.next()) {
            if(Keyboard.getEventKeyState()) {
                switch(Keyboard.getEventKey()) {
                    case Keyboard.KEY_W: moveDir.z += 1.0f; break;
                    case Keyboard.KEY_S: moveDir.z -= 1.0f; break;
                    case Keyboard.KEY_A: moveDir.x += 1.0f; break;
                    case Keyboard.KEY_D: moveDir.x -= 1.0f; break;
                    case Keyboard.KEY_SPACE: moveDir.y += 1.0f; break;
                    case Keyboard.KEY_C: moveDir.y -= 1.0f; break;
                    case Keyboard.KEY_ESCAPE: bContinue = false; break;
                }
            } else {
                switch(Keyboard.getEventKey()) {
                    case Keyboard.KEY_W: moveDir.z -= 1.0f; break;
                    case Keyboard.KEY_S: moveDir.z += 1.0f; break;
                    case Keyboard.KEY_A: moveDir.x -= 1.0f; break;
                    case Keyboard.KEY_D: moveDir.x += 1.0f; break;
                    case Keyboard.KEY_SPACE: moveDir.y -= 1.0f; break;
                    case Keyboard.KEY_C: moveDir.y += 1.0f; break;
                    case Keyboard.KEY_F1: cam.changeProjection(); break;
                    case Keyboard.KEY_P: shadowCam.changeProjection(); break;
                    case Keyboard.KEY_LEFT:
                        if(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                            ingameTimePerSecond = 0.0f;
                        } else {
                            ingameTimePerSecond = Math.max(1.0f / 64.0f, 0.5f * ingameTimePerSecond);
                        }
                        break;
                    case Keyboard.KEY_RIGHT:
                        if(Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                            ingameTimePerSecond = 1.0f;
                        } else {
                            ingameTimePerSecond = Math.min(64.0f, 2.0f * ingameTimePerSecond);
                        }
                        break;
                    case Keyboard.KEY_F2: glPolygonMode(GL_FRONT_AND_BACK, (wireframe ^= true) ? GL_FILL : GL_LINE); break;
                    case Keyboard.KEY_F3: if(culling ^= true) glEnable(GL_CULL_FACE); else glDisable(GL_CULL_FACE); break;
                    case Keyboard.KEY_M:  MenuDialog.getInstance(); break;
                    
                    // exposure adjustment
                    case Keyboard.KEY_NUMPAD8:
                    	if (exposure <  19.0f && exposure >= 1.0f)
                    		exposure += 1.0f;
                    	else if (exposure < 1.0f)
                    		exposure += 0.1f;
                    	break;
                    case Keyboard.KEY_NUMPAD2:
                    	if (exposure > 1.0f)
                    		exposure -= 1.0f;
                    	else if (exposure <= 1.0f && exposure > 0f)
                    		exposure -= 0.1f ;
                    	break;
                    // bloom adjustment
                    case Keyboard.KEY_NUMPAD7:
                    	if (bloomFactor <  19.0f && bloomFactor >= 1.0f)
                    		bloomFactor += 1.0f;
                    	else if (bloomFactor < 1.0f)
                    		bloomFactor += 0.1f;
                    	break;
                    case Keyboard.KEY_NUMPAD1:
                    	if (bloomFactor > 1.0f)
                    		bloomFactor -= 1.0f;
                    	else if (bloomFactor > 0f && bloomFactor <= 1.0f)
                    		bloomFactor -= 0.1f;
                    	break;
                    	
                    // brightness adjustment
                    case Keyboard.KEY_NUMPAD9:
                    	brightnessFactor.x += 0.1f;
                    	brightnessFactor.y += 0.1f;
                    	brightnessFactor.z += 0.1f;
                       	break; 
                    case Keyboard.KEY_NUMPAD3:
                    	brightnessFactor.x -= 0.1f;
                    	brightnessFactor.y -= 0.1f;
                    	brightnessFactor.z -= 0.1f;
                       	break; 
                    		
                    case Keyboard.KEY_F5:
                    	tonemapping = !tonemapping; break;
                    case Keyboard.KEY_F6:
                    	rotatelight = !rotatelight; break;
                    case Keyboard.KEY_F7:
                    	bloomBlend = !bloomBlend; break;
                    case Keyboard.KEY_F8:
                    	bloom = !bloom; break;
                    case Keyboard.KEY_ADD:
                    	shadowCam.setOrthoScaleValue(orthoScaleValue += 0.5f); break;
                    case Keyboard.KEY_SUBTRACT:
                    	if(orthoScaleValue >= 0.5f) {
                    		shadowCam.setOrthoScaleValue(orthoScaleValue -= 0.5f);
                    	}
                    	else {
                    		orthoScaleValue = 0f; 
                    	}
                    	break;
                }
            }
        }
        
        cam.move(moveSpeed * moveDir.z, moveSpeed * moveDir.x, moveSpeed * moveDir.y);
        
        while(Mouse.next()) {
            if(Mouse.getEventButton() == 0) {
                Mouse.setGrabbed(Mouse.getEventButtonState());
            }
            if(Mouse.isGrabbed()) {
                cam.rotate(-camSpeed*Mouse.getEventDX(), -camSpeed*Mouse.getEventDY());
            }
        }
        
        //if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) bContinue = false;
    }
    
    public static boolean isGodRays() {
		return godRays;
	}

	public static void setGodRays(boolean godRays) {
		TerrainMain.godRays = godRays;
	}

	/**
     * Aktualisiert Model Matrizen der Erde und des Mondes.
     * @param millis Millisekunden, die seit dem letzten Aufruf vergangen sind.
     */
    private static void animate(long millis) {
    	if(rotatelight) {
    		//sonne drehen
       	 	ingameTime += ingameTimePerSecond * 1e-3f * (float)millis;
       	 	Util.rotationY((0.05f)*Util.PI_MUL2 * ingameTime, sunRotation);
            Util.mul(sunMatrix,sunTilt, sunRotation, sunTranslation, Util.scale(5.0f, null));
            //licht drehen
            Util.transformCoord(Util.mul(null,sunTilt, sunRotation, sunTranslation), sunDirectionStart, sunDirection);
        }
    	
    	 //drehen der Wolken
         Util.rotationY((0.005f)*Util.PI_MUL2 * ingameTime,cloudModelMatrix);

    }  

    
    //Getter and setter 

	public static void setCulling(boolean culling) {
		TerrainMain.culling = culling;
	}

	public static boolean isWireframe() {
		return wireframe;
	}

	public static void setWireframe(boolean wireframe) {
		TerrainMain.wireframe = wireframe;
	}

	public static boolean isBloom() {
		return bloom;
	}

	public static void setBloom(boolean bloom) {
		TerrainMain.bloom = bloom;
	}

	public static boolean isTonemapping() {
		return tonemapping;
	}

	public static void setTonemapping(boolean tonemapping) {
		TerrainMain.tonemapping = tonemapping;
	}

	public static boolean isRotatelight() {
		return rotatelight;
	}

	public static void setRotatelight(boolean rotatelight) {
		TerrainMain.rotatelight = rotatelight;
	}

	public static float getExposure() {
		return exposure;
	}

	public static void setExposure(float exposure) {
		TerrainMain.exposure = exposure;
	}

	public static float getBloomFactor() {
		return bloomFactor;
	}

	public static void setBloomFactor(float bloomFactor) {
		TerrainMain.bloomFactor = bloomFactor;
	}

	public static Vector4f getBrightnessFactor() {
		return brightnessFactor;
	}

	public static void setBrightnessFactor(Vector4f brightnessFactor) {
		TerrainMain.brightnessFactor.set(brightnessFactor);
	}
	
	public static boolean isSplitScreen() {
		return splitScreen;
	}

	public static void setSplitScreen(boolean splitScreen) {
		TerrainMain.splitScreen = splitScreen;
	}

	public static int getSplitScreenVal(int loc) {
		return splitScreenVal[loc];
	}

	public static void setSplitScreenVal(int value, int loc) {
		TerrainMain.splitScreenVal[loc] = value;
	}
	
    public static boolean isCulling() {
		return culling;
	}

	public static boolean isShadows() {
		return shadows;
	}

	public static void setShadows(boolean shadows) {
		TerrainMain.shadows = shadows;
	}

    public static boolean isAmbientocclusion() {
		return ambientocclusion;
	}

	public static void setAmbientocclusion(boolean ambientocclusion) {
		TerrainMain.ambientocclusion = ambientocclusion;
	}

	public static boolean isNormalmapping() {
		return normalmapping;
	}

	public static void setNormalmapping(boolean normalmapping) {
		TerrainMain.normalmapping = normalmapping;
	}

	private static void matrix2uniform(Matrix4f matrix, int uniform) {
        matrix.store(Util.MAT_BUFFER);
        Util.MAT_BUFFER.position(0);
        glUniformMatrix4(uniform, false, Util.MAT_BUFFER);
    }
}
