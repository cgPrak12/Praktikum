/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
//import window.MenuDialog;
import window.MenuDialog;

/**
 *
 * @author nico3000
 * test
 */
public class TerrainMain {
    // current configurations
    private static boolean bContinue = true;
    public static boolean isCulling() {
		return culling;
	}

	private static boolean culling   = true;
    private static boolean wireframe = true;
    
    private static boolean tonemapping =  true;
    private static boolean rotatelight = false;
    private static boolean bloomBlend = false;
    private static boolean bloom = true;
    
    // control
    private static final Vector3f moveDir = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final Camera cam = new Camera(); 
    
    // animation params
    private static float ingameTime = 0.0f;
    private static float ingameTimePerSecond = 1.0f;
    
    //tone mapping
    private static float exposure    = 0.4f;
    private static float bloomFactor = 0.3f;
    private static Vector4f brightnessFactor  = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    
    private static Vector4f sunDirection = new Vector4f(1.0f, 1.0f, 1.0f, 0f);
    
    private static MenuDialog dialog;
     
    private static final ScreenManipulation screenMan = new ScreenManipulation();
    
    private static ShaderProgram fboSP; 
    
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
        
        fboSP = new ShaderProgram("./shader/Main_VS.glsl", "./shader/Main_FS.glsl");
        
        DeferredShader shader = new DeferredShader();
        shader.init();
        shader.registerShaderProgram(fboSP);
        Texture tex = Texture.generateTexture("asteroid.jpg", 0);
        
        Geometry testCube = GeometryFactory.createCube();
        Geometry screenQuad = GeometryFactory.createScreenQuad();
        
        //enlighted fbo
        FrameBuffer enlightenedFBO = new FrameBuffer();
          
        while(bContinue && !Display.isCloseRequested()) {
            // time handling
            now = System.currentTimeMillis();
            millis = now - last;
            last = now;     
            frameTimeDelta += millis;
            ++frames;
            if(frameTimeDelta > 1000) {
                System.out.println(1e3f * (float)frames / (float)frameTimeDelta + " FPS");
                frameTimeDelta -= 1000;
                frames = 0;
            }
            
            // input and animation
            handleInput(millis);
            animate(millis);
            if(rotatelight) {
            	Matrix4f.transform(Util.rotationY(0.005f, null), sunDirection, sunDirection);
            }
            if(bloomBlend && bloomFactor >0.5f) {
            	bloomFactor -= 0.08;
            }
            else {
            	bloomBlend = false;
            }
            	
            // clear screen
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            fboSP.use();
        	Matrix4f modelMatrix = Util.mul(null, Util.rotationX(1.0f, null), Util.rotationZ(1.0f, null));
        	Matrix4f modelIT = Util.transposeInverse(modelMatrix, null);
        	fboSP.setUniform("model", 	 modelMatrix);
        	fboSP.setUniform("modelIT",  modelIT);
        	fboSP.setUniform("viewProj", Util.mul(null, cam.getProjection(), cam.getView()));
            fboSP.setUniform("camPos",   cam.getCamPos());
            
            shader.bind();
            shader.clear();
        	
            testCube.draw();

        	shader.finish();
        	
        	enlightenedFBO = screenMan.getLighting(shader, cam.getCamPos(), sunDirection, screenQuad);
        	
        	//shader.DrawTexture(enlightenedFBO.getTexture(0));

        	FrameBuffer fbo = enlightenedFBO;
        	if (tonemapping) {
            	if (bloom) {
        		fbo = screenMan.getToneMappedBloomed(enlightenedFBO, bloomFactor, brightnessFactor, exposure, screenQuad);
            	}
            	else {
            		fbo = screenMan.getToneMapped(enlightenedFBO, exposure, screenQuad);
            	}
        	}
        	else {
            	if (bloom) {
            		fbo = screenMan.getBloom(enlightenedFBO, bloomFactor, brightnessFactor, screenQuad);
            	}
        	}

        	shader.DrawTexture(fbo.getTexture(0));
        	
            // TODO: postfx
            
            // present screen
            Display.update();
            Display.sync(60);
        }
        dialog.close();
        screenMan.delete();
        shader.delete();
        tex.delete();
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
                    case Keyboard.KEY_M:  dialog = new MenuDialog(); break;
                    
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
		System.out.println(TerrainMain.brightnessFactor);
	}
}
