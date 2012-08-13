package main;

import java.util.logging.Level;
import java.util.logging.Logger;
import opengl.GL;
import static opengl.GL.*;
import opengl.OpenCL;
import opengl.OpenCL.Device_Type;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import util.*;

/**
 *
 * @author nico3000
 */
public class TerrainMain {
    // current configurations
    private static boolean bContinue = true;
    private static boolean culling = true;
    private static boolean wireframe = true;
    private static ShaderProgram drawTextureSP;
    
    // control
    private static final Vector3f moveDir = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final Camera cam = new Camera();
    
    // animation params
    private static float ingameTimePerSecond = 1.0f;

    // particles
    private static Particle particles;
    
    // simulation 
    private static ShaderProgram simShader;

    // fluid rendering + simulation
    private static Geometry screenQuad;
    
    
    public static void main(String[] argv) {
        try {
            init();
            OpenCL.init();
            glDisable(GL_CULL_FACE);
            glFrontFace(GL_CCW);
            glCullFace(GL_BACK);
            glEnable(GL_DEPTH_TEST);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_COLOR);

            glEnable(GL_VERTEX_PROGRAM_POINT_SIZE);
            //glEnable(GL_POINT_SPRITE);
            glEnable(GL_PROGRAM_POINT_SIZE);

            render();
            OpenCL.destroy();
            destroy();
        } catch (LWJGLException ex) {
            Logger.getLogger(TerrainMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void render() throws LWJGLException {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // background color: black
        
        long last = System.currentTimeMillis();
        long now, millis;
        long frameTimeDelta = 0;
        int frames = 0;
        
        // create screen quad
        screenQuad = GeometryFactory.createScreenQuad();
        
        // create new shader programs
        simShader = new ShaderProgram("shader/simulation_vs.glsl", "shader/simulation_fs.glsl");
        drawTextureSP = new ShaderProgram("shader/ScreenQuad_VS.glsl", "shader/CopyTexture_FS.glsl");
        
        // create Fluid Rendererer
        FluidRenderer fluidRenderer = new FluidRenderer(cam);
        Vector3f lightPos = new Vector3f(0.0f, 5.0f, 0.0f);
        
        // simulation test terrain
        Geometry terrain = GeometryFactory.createTerrainFromMap("maps/04.jpg",0.3f);
        Texture normalTex = terrain.getNormalTex();
        Texture heightTex = terrain.getHeightTex();
        
        // particle creation
        particles = new Particle(4096*4, Device_Type.GPU, Display.getDrawable());
        particles.createData(heightTex.getId(), normalTex.getId());
        
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
            
            // clear screen
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            // prepare simulation: draw terrain to world
            simShader.use();
            simShader.setUniform("proj", cam.getProjection());
            simShader.setUniform("view", cam.getView());
            simShader.setUniform("normalTex", normalTex);
            simShader.setUniform("heightTex", heightTex);
//            terrain.draw();
            
            // simulate particles
            particles.getShaderProgram().use();
            particles.draw(cam, millis);
            
            // render fluid
    		drawTextureSP.use();        
    		drawTextureSP.setUniform("image", fluidRenderer.render(lightPos, particles.getVertexArray(), particles.getNumParticles(), terrain));
    		screenQuad.draw();  
            
            // present screen
            Display.update();
            Display.sync(60);
        }
        simShader.delete();
        OpenCL.destroy();
    }
    
    /**
     * Behandelt Input und setzt die Kamera entsprechend.
     * @param millis Millisekunden seit dem letzten Aufruf
     */
    public static void handleInput(long millis) {
        float moveSpeed = 2e-3f*(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 2.0f : 1.0f)*(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) ? 0.1f : 1.0f)*(float)millis;
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
                }
            }
        }
        
        moveSpeed = moveSpeed * 0.25f;
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
     * Aktualisiert Model Matrizen
     * @param millis Millisekunden, die seit dem letzten Aufruf vergangen sind.
     */
    private static void animate(long millis) {

    }
}
