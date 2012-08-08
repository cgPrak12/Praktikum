package main;

import static opengl.GL.*;
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
    // current configurations
    private static boolean bContinue = true;
    private static boolean splitScreen = false;
    private static int[] splitScreenVal = { 1, 2, 3, 4 };

	private static boolean culling   = true;
    private static boolean wireframe = true;
    
    private static boolean tonemapping =  true;
    private static boolean rotatelight = false;
    private static boolean bloomBlend = false;
    private static boolean bloom = true;
    private static boolean shadows = true;
    
    // control
    private static final Vector3f moveDir = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final Camera cam = new Camera(); 
    private static final Camera shadowCam = new Camera();
    
    // animation params
    private static float ingameTime = 0.0f;
    private static float ingameTimePerSecond = 1.0f;
    
    //tone mapping
    private static float exposure    = 0.4f;
    private static float bloomFactor = 0.3f;
    private static Vector4f brightnessFactor  = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    
    private static Vector3f sunDirection = new Vector3f(1.0f, 1.0f, 1.0f);
         
    private static final ScreenManipulation screenMan = new ScreenManipulation();
    
    private static float orthoScaleValue = 1f;
    
    private static Matrix4f bias;
    
    private static ShaderProgram fboSP;
    private static ShaderProgram shadowSP;
    
    public static void main(String[] argv) {
        try {
            init();
            OpenCL.init();
            glEnable(GL_CULL_FACE);
            glFrontFace(GL_CCW);
            glCullFace(GL_BACK);
            glEnable(GL_DEPTH_TEST);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_COLOR);
            
            //initialize ScreenManipulation with all the used Shaders
            screenMan.init("./shader/ScreenQuad_VS.glsl", "./shader/Blur_FS.glsl",
            "./shader/Brightness_FS.glsl", "./shader/Bloom_FS.glsl", "./shader/ToneMapping_FS.glsl",
            "./shader/PhongLighting_FS.glsl",GL.WIDTH, GL.HEIGHT);
            
            render();
            OpenCL.destroy();
            destroy();
        } catch (LWJGLException ex) {
            Logger.getLogger(TerrainMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

	public static void render() throws LWJGLException {
        glClearColor(0.1f, 0.0f, 0.0f, 1.0f); // background color: dark red
        
        long last = System.currentTimeMillis();
        long now, millis;
        long frameTimeDelta = 0;
        int frames = 0;
        
		//bias matrix for shadow mapping
		bias = new Matrix4f();
		bias.m00 = 0.5f;
		bias.m11 = 0.5f;
		bias.m22 = 0.5f;
		bias.m33 = 1.0f;
		bias.m30 = 0.5f;
		bias.m31 = 0.5f;
		bias.m32 = 0.5f;
        
        shadowSP = new ShaderProgram("./shader/Main_VS.glsl", "./shader/Main_FS.glsl");
        fboSP = new ShaderProgram("./shader/MainShadow_VS.glsl", "./shader/MainShadow_FS.glsl");
        
        Matrix4f floorQuadMatrix = new Matrix4f();
        //Matrix4f floorQuadITMatrix = new Matrix4f();
        
//        shadowCam.changeProjection();
        Util.mul(floorQuadMatrix, Util.rotationX(-Util.PI_DIV2, null), Util.translationZ(-1.0f, null), Util.scale(10, null)); 

        
        DeferredShader shadowShader = new DeferredShader();
        shadowShader.init(0);
        shadowShader.registerShaderProgram(shadowSP);
        
        DeferredShader shader = new DeferredShader();
        shader.init(4);
        shader.registerShaderProgram(fboSP);
        
        Geometry testCube = GeometryFactory.createCube();
        Geometry floorQuad = GeometryFactory.createWhiteScreenQuad();
        Geometry sunCube = GeometryFactory.createCube();
        
        Matrix4f sunMatrix = new Matrix4f();
        
        //enlighted fbo
        FrameBuffer enlightenedFBO = new FrameBuffer();
        enlightenedFBO.init(false, GL.WIDTH, GL.HEIGHT);
        FrameBuffer fbo = new FrameBuffer();
        fbo.init(false, GL.WIDTH, GL.HEIGHT);
        FrameBuffer shadowFBO = new FrameBuffer();
        shadowFBO.init(false, GL.WIDTH, GL.HEIGHT);

        while(bContinue && !Display.isCloseRequested()) {
            // time handling
            now = System.currentTimeMillis();
            millis = now - last;
            last = now;     
            frameTimeDelta += millis;
            ++frames;
            
            shadowCam.setCamDir(sunDirection.negate(null));
            shadowCam.setCamPos(new Vector3f(sunDirection.x * 10f, sunDirection.y * 10f, sunDirection.z * 10f));
            
            Util.mul(sunMatrix, Util.translation(new Vector3f(sunDirection.x*10, sunDirection.y*10, sunDirection.z*10), null));
            
            if(frameTimeDelta > 1000) {
                System.out.println(1e3f * (float)frames / (float)frameTimeDelta + " FPS");
                frameTimeDelta -= 1000;
                frames = 0;
            }
            
            // input and animation
            handleInput(millis);
            animate(millis);
            if(rotatelight) {
            	Vector4f sunDirection4f = new Vector4f(sunDirection.x, sunDirection.y, sunDirection.z, 0f);
            	Matrix4f.transform(Util.rotationY(0.005f, null), sunDirection4f, sunDirection4f);
            	sunDirection.set(sunDirection4f.x, sunDirection4f.y, sunDirection4f.z);
            }
            if(bloomBlend && bloomFactor >0.5f) {
            	bloomFactor -= 0.08;
            }
            else {
            	bloomBlend = false;
            }
            	
            // clear screen
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            //test cube
            fboSP.use();
            
        	Matrix4f modelMatrix = Util.mul(null, Util.rotationX(1.0f, null), Util.rotationZ(1.0f, null));
        	Matrix4f modelIT = Util.transposeInverse(modelMatrix, null);
        	Matrix4f shadowMatrix = Util.mul(null, bias, shadowCam.getProjection(), shadowCam.getView(), modelMatrix);
        	
        	fboSP.setUniform("model", 	 	 modelMatrix);
        	fboSP.setUniform("modelIT",  	 modelIT);
        	fboSP.setUniform("viewProj", 	 Util.mul(null, cam.getProjection(), cam.getView()));
            fboSP.setUniform("shadowMatrix", shadowMatrix);
        	fboSP.setUniform("camPos",   	 cam.getCamPos());
            
            shader.bind();
            shader.clear();
        	
            testCube.draw();
            
            //floor quad
            shadowMatrix = Util.mul(null, bias, shadowCam.getProjection(), shadowCam.getView(), floorQuadMatrix);

            fboSP.setUniform("model", floorQuadMatrix);
        	fboSP.setUniform("modelIT", floorQuadMatrix); //Util.transposeInverse(floorQuadMatrix, null));
        	fboSP.setUniform("shadowMatrix", shadowMatrix);
            
            floorQuad.draw();
            
            //sun cube
            shadowMatrix = Util.mul(null, bias, shadowCam.getProjection(), shadowCam.getView(), sunMatrix);
            
            fboSP.setUniform("model", sunMatrix);
            fboSP.setUniform("modelIT", Util.transposeInverse(sunMatrix, null));
            fboSP.setUniform("shadowMatrix", shadowMatrix);
            
            sunCube.draw();
            
        	shader.finish();
        	
        	//test cube (shadow map)
        	glCullFace(GL_FRONT);
        	shadowSP.use();
        	shadowSP.setUniform("model", 	modelMatrix);
        	shadowSP.setUniform("modelIT",  modelIT);
        	shadowSP.setUniform("viewProj", Util.mul(null, shadowCam.getProjection(), shadowCam.getView()));
        	shadowSP.setUniform("camPos",   shadowCam.getCamPos());
       	
        	shadowShader.bind();
        	shadowShader.clear();
   	
        	testCube.draw();
        	glCullFace(GL_BACK);
        	
        	//floor quad (shadow map)
        	shadowSP.setUniform("model",    floorQuadMatrix);
        	shadowSP.setUniform("modelIT",  floorQuadMatrix);
        	shadowSP.setUniform("viewProj", Util.mul(null, shadowCam.getProjection(), shadowCam.getView()));
        	shadowSP.setUniform("camPos",   shadowCam.getCamPos());
        	
        	floorQuad.draw();
        	
        	shadowShader.finish();
        	
        	if (shadows) {
            	enlightenedFBO = screenMan.getShadowLighting(shader, shadowShader, cam.getCamPos(), sunDirection);
        	}
        	else {
        		enlightenedFBO = screenMan.getLighting(shader, cam.getCamPos(), sunDirection);
        	}
        	        	        	
//        	shader.DrawTexture(shader.getShadowTexture());

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
        	            
            // present screen
            Display.update();
            Display.sync(60);
        }
        MenuDialog.destroyInstance();
        screenMan.delete();
        shader.delete();
    }
    
	/**
	 * creates a screen with 4 frame buffers
	 * @param splitScreenValue what shall be shown on the screen parts
	 * @param shader deferred shader
	 * @return frame buffer containing the screen 
	 */
	private static FrameBuffer getQuadScreen(int[] splitScreenValue, DeferredShader shader, DeferredShader shadowShader) {
	
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
	private static FrameBuffer getScreen(int splitScreenValue, DeferredShader shader, DeferredShader shadowShader) {
		
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
				fbo = screenMan.getShadowMap(shadowShader.getWorldTexture()); break;
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
    
    /**
     * Aktualisiert Model Matrizen der Erde und des Mondes.
     * @param millis Millisekunden, die seit dem letzten Aufruf vergangen sind.
     */
    private static void animate(long millis) {

    }  

    
    //Getter and setter 

	public static void setCulling(boolean culling) {
		TerrainMain.culling = culling;
	}

	public static boolean isWireframe() {
		return !wireframe;
	}

	public static void setWireframe(boolean wireframe) {
		TerrainMain.wireframe = !wireframe;
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
}
