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
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import util.*;

/**
 *
 * @author nico3000
 * test
 */
public class TerrainMain {
    // current configurations
    private static boolean bContinue = true;
    private static boolean culling = true;
    private static boolean wireframe = true;
    
    private static boolean bloom = true;
    private static boolean tonemapping = true;
    private static boolean rotatelight = false;
    
    // control
    private static final Vector3f moveDir = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final Camera cam = new Camera(); 
    
    // animation params
    private static float ingameTime = 0.0f;
    private static float ingameTimePerSecond = 1.0f;
    
    //tone mapping
    private static float exposure = 1.0f;
    private static float bloomFactor = 1.0f;
    
    private static Vector4f sunDirection = new Vector4f(1.0f, 1.0f, 1.0f, 0f);
    
    //offset
    private static Vector2f[] tc_offset_5;
    
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
            tc_offset_5 = generateTCOffset(5);            
            render();
            OpenCL.destroy();
            destroy();
        } catch (LWJGLException ex) {
            Logger.getLogger(TerrainMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     *  Generate texturecoordinate_offset for blur and tone mapping
     *  @param size Generate size * size offset
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
        
        ShaderProgram phongSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/PhongLighting_FS.glsl");
        ShaderProgram toneSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/ToneMapping_FS.glsl");
        ShaderProgram brightSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/Brightness_FS.glsl");
    	ShaderProgram blurSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/Blur_FS.glsl");
    	ShaderProgram bloomSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/Bloom_FS.glsl");

        
        //enlighted fbo
        FrameBuffer enlightenedFBO = new FrameBuffer();
        enlightenedFBO.init(false, GL.WIDTH, GL.HEIGHT);
        enlightenedFBO.addTexture(new Texture(GL_TEXTURE_2D, 0), GL30.GL_RGBA16F, GL_RGBA);
        
        //brightness fbo
        FrameBuffer brightnessFBO = new FrameBuffer();
        brightnessFBO.init(false, GL.WIDTH, GL.HEIGHT);
        brightnessFBO.addTexture(new Texture(GL_TEXTURE_2D, 0), GL30.GL_RGBA16F, GL_RGBA);
        
        //blur
    	FrameBuffer blurFBO = new FrameBuffer();
        blurFBO.init(false, GL.WIDTH, GL.HEIGHT);
        blurFBO.addTexture(new Texture(GL_TEXTURE_2D, 0), GL30.GL_RGBA16F, GL_RGBA);
        
        //bloom
    	FrameBuffer bloomFBO = new FrameBuffer();
        bloomFBO.init(false, GL.WIDTH, GL.HEIGHT);
        bloomFBO.addTexture(new Texture(GL_TEXTURE_2D, 0), GL30.GL_RGBA16F, GL_RGBA);
        
        
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
        	
        	// blinn-phong lighting
        	enlightenedFBO.bind();
        	phongSP.use();
        	phongSP.setUniform("normalTex",  shader.getNormalTexture());
        	phongSP.setUniform("worldTex",   shader.getWorldTexture());
        	phongSP.setUniform("diffuseTex", shader.getDiffuseTexture());
        	phongSP.setUniform("camPos",     cam.getCamPos());
        	phongSP.setUniform("sunDir",	 new Vector3f(sunDirection.x, sunDirection.y, sunDirection.z));
        	
        	screenQuad.draw();        	
        	enlightenedFBO.unbind();
        	
        	if(bloom) {       	
	        	//brightness
	        	brightnessFBO.bind();
	        	brightSP.use();
	        	brightSP.setUniform("colorTex", enlightenedFBO.getTexture(0));
	        	brightSP.setUniform("colorFactor", new Vector4f(1f, 1f, 1f, 1f));
	        	
	        	screenQuad.draw();
	        	
	        	brightnessFBO.unbind();
	        	
	        	//blur
	        	FrameBuffer blured1 = genBlur(enlightenedFBO, screenQuad);
	        	FrameBuffer blured2 = genBlur(blured1, screenQuad);
	        	FrameBuffer blured3 = genBlur(blured2, screenQuad);
	        	FrameBuffer blured4 = genBlur(blured3, screenQuad);
	        	
	        	//bloom
	        	bloomFBO.bind();
	        	bloomSP.use();
	        	bloomSP.setUniform("origImage", enlightenedFBO.getTexture(0));
	        	bloomSP.setUniform("brightImage", brightnessFBO.getTexture(0));
	        	bloomSP.setUniform("blur1", blured1.getTexture(0));
	        	bloomSP.setUniform("blur2", blured2.getTexture(0));
	        	bloomSP.setUniform("blur3", blured3.getTexture(0));
	        	bloomSP.setUniform("blur4", blured4.getTexture(0));
	        	bloomSP.setUniform("bloomLevel", bloomFactor);
	        	
	        	screenQuad.draw();	        	
	        	bloomFBO.unbind();       	
        	}
        	
        	// tone mapping
        	if(tonemapping) {
	        	toneSP.use();
	        	
	        	if(bloom) { 
	        		toneSP.setUniform("colorTex", bloomFBO.getTexture(0));
	        	}
	        	else {
	        		toneSP.setUniform("colorTex", enlightenedFBO.getTexture(0));
	        	}
	        		
	        	toneSP.setUniform("exposure", exposure);
	        	toneSP.setUniform("tc_offset", tc_offset_5);
        	}
        	screenQuad.draw();

        	        	
//        	shader.DrawTexture(enlightened.getTexture(0));
        	
        	
            // TODO: postfx
            
            // present screen
            Display.update();
            Display.sync(60);
        }
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
                    
                    case Keyboard.KEY_NUMPAD8: if (exposure <  19) exposure += 1.0f; ; break;
                    case Keyboard.KEY_NUMPAD2:
                    	if (exposure > 1.0)
                    		exposure -= 1.0f;
                    	else if (exposure <= 1.0 && exposure > 0)
                    		exposure -= 0.1f ; break;
                    		
                    case Keyboard.KEY_NUMPAD7: if (bloomFactor <  19) exposure += 1.0f; ; break;
                    case Keyboard.KEY_NUMPAD1:
                    	if (bloomFactor > 1.0)
                    		bloomFactor -= 1.0f;
                    	else if (bloomFactor <= 1.0 && bloomFactor > 0)
                    		bloomFactor -= 0.1f ; 
                    		
                    case Keyboard.KEY_K: 
                    	Matrix4f.transform(Util.rotationY(0.1f, null), sunDirection, sunDirection);
                    	break;
                    	//sunDirection.x = 1.0f; sunDirection.y = 0.0f; sunDirection.z = 0.0f; break;
                    case Keyboard.KEY_L: 
                    	Matrix4f.transform(Util.rotationY(-0.1f, null), sunDirection, sunDirection);
                    	break;
                    case Keyboard.KEY_COMMA:
                    	Matrix4f.transform(Util.rotationZ(-0.1f, null), sunDirection, sunDirection);
                    	break;
                    case Keyboard.KEY_O:
                    	Matrix4f.transform(Util.rotationZ(0.1f, null), sunDirection, sunDirection);
                    	break;
                    case Keyboard.KEY_F5:
                    	tonemapping = !tonemapping; break;
                    case Keyboard.KEY_F6:
                    	bloom = !bloom; break;
                    case Keyboard.KEY_F9:
                    	rotatelight = !rotatelight; break;
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
    
    /**
     * 	Generate blured Framebuffer
     * @param count Blur Iterations
     * @param toBlur FrameBuffer to blur
     * @param screenQuad screenQuad
     */
    
    private static FrameBuffer genBlur(FrameBuffer toBlur, Geometry screenQuad) {
        ShaderProgram blurSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/Blur_FS.glsl");
    	
    	FrameBuffer blur = new FrameBuffer();
        blur = toBlur;
        blur.bind();
	    blurSP.use();
	    blurSP.setUniform("colorTex", blur.getTexture(0));
	    blurSP.setUniform("tc_offset", tc_offset_5);
	    	
	    screenQuad.draw();
	    blurSP.delete();
	    	
	    blur.unbind();
    	
        return blur;
    }
}
