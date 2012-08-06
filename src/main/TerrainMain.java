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

import util.*;

/**
 *
 * @author nico3000
 */
public class TerrainMain {
	//Test2 Commit
    // current configurations
    private static boolean bContinue = true;
    private static boolean culling = true;
    private static boolean wireframe = true;

    //Lights
    private static Vector3f lightPosition1 = new Vector3f(0.0f, 0.0f, 20.0f);
    
    private static boolean holdLight =false;
    
    // textures
    private static Texture normalQuaderTexture;
    private static Texture quaderTexture;
    private static Texture diffuseQuaderTexture;
    private static Texture specularQuaderTexture;
    private static Texture bumpQuaderTexture;
    private static Texture skydomeTexture;
    private static Texture sunTexture;
    private static Texture FBTexture2;
    private static Texture FBTexture1;
    private static Texture skyCloudTexture;
    private static Texture noiseTexture;
    
    // uniform locations
    private static int modelLoc;
    
    //Buffer
    private static FrameBuffer fbuffer1;
    private static FrameBuffer fbuffer2;
    
    //matrix
    private static final Matrix4f sunModelMatrix = new Matrix4f();
    private static final Matrix4f cloudModelMatrix = new Matrix4f();
    private static final Matrix4f cubeModelMatrix = new Matrix4f();
    
    // temp data
    private static final Matrix4f sunRotation = new Matrix4f();
    private static final Matrix4f sunTilt = new Matrix4f();
    private static final Matrix4f sunTranslation = new Matrix4f();
    
    // geometries
    
    // shader programs
    private static ShaderProgram normalMappingSP;
    private static ShaderProgram BlurSP ;
    private static ShaderProgram GodRaysSP;
    private static ShaderProgram LightningSP;
    private static ShaderProgram sunSP;
    private static ShaderProgram sunRaysSP;
    private static ShaderProgram AOSP;
    
    //private static float specCoeff = 0.5f;
    
    // control
    private static final Vector3f moveDir = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final Camera cam = new Camera(); 
    
    // animation params
    private static float ingameTime = 0;
    private static float ingameTimePerSecond = 1.0f;
    
    private static ShaderProgram fboSP; 
    
    //private static Matrix4f sunModel = new Matrix4f();
    
    public static void main(String[] argv) {
        try {
            init();
            OpenCL.init();
            glEnable(GL_CULL_FACE);
            glFrontFace(GL_CCW);
            glCullFace(GL_BACK);
            glEnable(GL_DEPTH_TEST);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_COLOR);
            
            //glDisable(GL_CULL_FACE);
            
            
            
            
            //textures
            normalQuaderTexture   = Texture.generateTexture("./stone_wall_normal_map.jpg",0 );
            quaderTexture         = Texture.generateTexture("./stone_wall.jpg",1 );
            diffuseQuaderTexture  = Texture.generateTexture("./stone_wall.jpg",2 );
            specularQuaderTexture = Texture.generateTexture("./stone_wall_specular.jpg",3 );
            bumpQuaderTexture     = Texture.generateTexture("./stone_wall_bump.jpg",4 );
           
            skydomeTexture         = Texture.generateTexture("./sky_2.jpg",5 );
            sunTexture			   = Texture.generateTexture("./sun.jpg",6);
            skyCloudTexture        = Texture.generateTexture("./sky_sw.jpg",9 );
            noiseTexture 		   = Texture.generateTexture("./noise.png",10);
            //blurPosteffect
            BlurSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl","./shader/Blur_FS.glsl");
            
            //godRaysPosteffect
			GodRaysSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/GodRayFS.glsl");            
            
            //Lightning
            LightningSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/Normal_FS.glsl");  
            
            //SunEffect
            sunSP = new ShaderProgram("./shader/sun_VS.glsl", "./shader/sun_FS.glsl");
            sunRaysSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/sunRays_FS.glsl");
            
          //SunEffect
            AOSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/AmbientOcclusion_FS.glsl");
            
            //Framebuffer1
            fbuffer1 = new FrameBuffer();
            fbuffer1.init(false, GL.WIDTH, GL.HEIGHT) ;
            FBTexture1 = new Texture(GL_TEXTURE_2D, 7);
            fbuffer1.addTexture(FBTexture1, GL_RGBA8, GL_RGBA);
            //Framebuffer2
            fbuffer2 = new FrameBuffer();
            fbuffer2.init(false, GL.WIDTH, GL.HEIGHT) ;
            FBTexture2 = new Texture(GL_TEXTURE_2D, 8);
            fbuffer2.addTexture(FBTexture2, GL_RGBA8, GL_RGBA);
            
            //bind shade to FBuffer
            fbuffer1.BindFragDataLocations(LightningSP, "finalColor");
            fbuffer2.BindFragDataLocations(BlurSP, "finalColor");
            
            
            Util.translationX(5.0f, sunTranslation);
            Util.rotationX((float)Math.toRadians(-45.0), sunTilt);
            
            
            render();
            OpenCL.destroy();
            destroy();
        } catch (LWJGLException ex) {
            Logger.getLogger(TerrainMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void render() throws LWJGLException {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // background color: dark red
        
        long last = System.currentTimeMillis();
        long now, millis;
        long frameTimeDelta = 0;
        int frames = 0;
        

        fboSP = new ShaderProgram("./shader/Main_VS.glsl", "./shader/Main_FS.glsl");
        DeferredShader shader = new DeferredShader();
        shader.init();
        shader.registerShaderProgram(fboSP);
        shader.registerShaderProgram(sunSP);
        Texture tex = Texture.generateTexture("asteroid.jpg", 0);

        
        //Geometry testCube = GeometryFactory.createCube();
        //Skydome
        Geometry skyDome  = GeometryFactory.createSkyDome(50, 50, 50);
        Geometry skyCloud  = GeometryFactory.createSkyDome(47, 50, 50);
        //Sonne
        Geometry sun = GeometryFactory.createQuad();
        Geometry kugel1 =GeometryFactory.createSphere(1,20,20);
        Geometry kugel2 =GeometryFactory.createSphere(1,20,20);
        Geometry kugel3 =GeometryFactory.createSphere(1,20,20);
        Geometry kugel4 =GeometryFactory.createSphere(1,20,20);
        
        Geometry geo = GeometryFactory.createScreenQuad();
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
            modelLoc = glGetUniformLocation(fboSP.getId(), "model");
            
            // input and animation
            handleInput(millis);
            animate(millis);
            
            // clear screen
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            
            fboSP.use();
        	Matrix4f modelMatrix = new Matrix4f();
        	Matrix4f modelIT = Util.transposeInverse(modelMatrix, null);
        	fboSP.setUniform("model", 	 modelMatrix);
        	fboSP.setUniform("modelIT",  modelIT);
        	fboSP.setUniform("viewProj", Util.mul(null, cam.getProjection(), cam.getView()));
            fboSP.setUniform("camPos",   cam.getCamPos());
            fboSP.setUniform("normalTexture", normalQuaderTexture);
            fboSP.setUniform("specularTexture", specularQuaderTexture);
            fboSP.setUniform("view", cam.getView());
            fboSP.setUniform("camFar", cam.getFar());
            
            
            
            shader.bind();
            shader.clear();
            
            
            //Quader zeichen
            fboSP.setUniform("textureImage", quaderTexture);
           
            Util.translationX(0.9f,cubeModelMatrix);
            matrix2uniform(cubeModelMatrix, modelLoc);
            kugel1.draw();
            
            Util.translationX(-0.9f,cubeModelMatrix);
            matrix2uniform(cubeModelMatrix, modelLoc);
            kugel2.draw();
            
            Util.translationY(1.3f,cubeModelMatrix);
            matrix2uniform(cubeModelMatrix, modelLoc);
            kugel3.draw();
            
            Util.translation(new Vector3f(2.1f,1.1f,1.5f),cubeModelMatrix);
            matrix2uniform(cubeModelMatrix, modelLoc);
            
            kugel4.draw();
            glEnable(GL_BLEND);
            
            //Himmel
            fboSP.setUniform("textureImage", skydomeTexture);
            skyDome.draw();
            
            
            
            //Sonne
//            fboSP.setUniform("textureImage", sunTexture);
//            matrix2uniform(sunModelMatrix, modelLoc);
//            sun.draw();
            
            //Wolken
            fboSP.setUniform("textureImage", skyCloudTexture);
            matrix2uniform(cloudModelMatrix, modelLoc);
            skyCloud.draw();
            glDisable(GL_BLEND);
            
            
//            sunSP.use();
//            Matrix4f sun_modelMatrix = new Matrix4f();
//        	Matrix4f sun_modelIT = Util.transposeInverse(sun_modelMatrix, null);
//            sunSP.setUniform("sunPosition", lightPosition1);
//      		sunSP.setUniform("sun_model", 	 sun_modelMatrix);
//      		sunSP.setUniform("sun_modelIT",  sun_modelIT);
//      		sunSP.setUniform("sun_viewProj", Util.mul(null, cam.getProjection(), cam.getView()));
//      		sunSP.setUniform("sunTexture", sunTexture);
//      		           
      		
        	shader.finish();

            //shader.DrawTexture(shader.getNormalTexture());
            

            
            // TODO: postfx
            
            
            
        	//Lightning
            fbuffer1.bind();
            fbuffer1.clearColor();
            
			LightningSP.use();
            LightningSP.setUniform("normalTexture",  shader.getNormalTexture());
            LightningSP.setUniform("diffuseTexture",  shader.getDiffuseTexture());
            LightningSP.setUniform("specularTexture", shader.getSpecTexture());
            LightningSP.setUniform("eyePosition", cam.getCamPos());
            LightningSP.setUniform("bumpTexture", bumpQuaderTexture);
            LightningSP.setUniform("skyTexture", shader.getSkyTexture());
            LightningSP.setUniform("lightPosition1", lightPosition1);
            geo.draw();
            fbuffer1.unbind();
            
            
            fbuffer2.bind();
            fbuffer2.clearColor();
            AOSP.use();
            AOSP.setUniform("normalTexture",  shader.getNormalTexture());
            AOSP.setUniform("noiseTexture", noiseTexture);
            AOSP.setUniform("diffuseTexture", FBTexture1);
            geo.draw();
            fbuffer2.unbind();
            
//            fbuffer2.bind();
//            fbuffer2.clearColor();
//			GodRaysSP.use();
//			GodRaysSP.setUniform("diffuseTexture", FBTexture1);
//			GodRaysSP.setUniform("model", 	 modelMatrix);
//        	GodRaysSP.setUniform("viewProj", Util.mul(null, cam.getProjection(), cam.getView()));
//			GodRaysSP.setUniform("lightPosition", lightPosition1);
//			GodRaysSP.setUniform("skyTexture", shader.getSkyTexture());
//			geo.draw();
//			fbuffer2.unbind();		
            
            
//            fbuffer2.bind();
//            fbuffer2.clearColor();
//			BlurSP.use();
//            BlurSP.setUniform("worldTexture",  shader.getWorldTexture());
//            BlurSP.setUniform("diffuseTexture", FBTexture1);
//            BlurSP.setUniform("deltaBlur", 0.001f);
//            geo.draw();
//            fbuffer2.unbind();
//            
            
            // Ausgabe auf dem Bildschirm
    		shader.DrawTexture(FBTexture2);
            
            // present screen
            Display.update();
            Display.sync(60);
        }
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
                    case Keyboard.KEY_E: moveDir.z += 5.0f; break;
                    case Keyboard.KEY_S: moveDir.z -= 1.0f; break;
                    case Keyboard.KEY_A: moveDir.x += 1.0f; break;
                    case Keyboard.KEY_D: moveDir.x -= 1.0f; break;
                    case Keyboard.KEY_SPACE: moveDir.y += 1.0f; break;
                    case Keyboard.KEY_C: moveDir.y -= 1.0f; break;
                    case Keyboard.KEY_ESCAPE: bContinue = false; break;
                    case Keyboard.KEY_P: 
                    	if (holdLight==false)
                    		{holdLight = true;}
                    	else
                    		{holdLight = false;}
                    	break;
                }
            } else {
                switch(Keyboard.getEventKey()) {
                    case Keyboard.KEY_W: moveDir.z -= 1.0f; break;
                    case Keyboard.KEY_E: moveDir.z -= 5.0f; break;
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
    	if (!holdLight)
    	{
    		//Drehen der Sonne
    		ingameTime += ingameTimePerSecond * 1e-3f * (float)millis;
    		Util.rotationY((0.05f)*Util.PI_MUL2 * ingameTime, sunRotation);
            Util.mul(sunModelMatrix,sunTilt, sunRotation, sunTranslation, Util.scale(5.0f, null));
            
            //drehen der Wolken
            Util.rotationY((0.005f)*Util.PI_MUL2 * ingameTime,cloudModelMatrix);
            
            //drehen des Lichtes 
            //lightPosition1  = Util.transformCoord(sunModelMatrix, new Vector3f(1.0f,1.0f,1.0f), null);
            Util.transformCoord(Util.rotationY(10e-4f * (float)millis, null), lightPosition1, lightPosition1);
    	
    	}
    }
    
    /**
     * Hilfsmethode, um eine Matrix in eine Uniform zu schreiben. Das
     * zugehoerige Programmobjekt muss aktiv sein.
     * @param matrix Quellmatrix
     * @param uniform Ziellocation
     */
    private static void matrix2uniform(Matrix4f matrix, int uniform) {
        matrix.store(Util.MAT_BUFFER);
        Util.MAT_BUFFER.position(0);
        glUniformMatrix4(uniform, false, Util.MAT_BUFFER);
    }
}
