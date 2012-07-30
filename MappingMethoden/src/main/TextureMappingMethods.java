/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import static opengl.GL.*;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class TextureMappingMethods {
    // shader programs
    private static ShaderProgram bumpMappingSP = new ShaderProgram();
    
    // geometries
    private static Geometry quadGeo = GeometryFactory.createQuad();
    
    // textures
    private static Texture quadDiffuseTex = new Texture();
    private static Texture quadBumpTex = new Texture();
    private static Texture quadNormalTex = new Texture();
    
    // current configurations
    private static boolean bContinue = true;
    private static boolean culling = true;
    private static boolean wireframe = true;
    
    // control
    private static final Vector3f moveDir = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final Camera cam = new Camera(); 
    
    // matrices
    private static final Matrix4f viewProjMatrix = new Matrix4f();
    
    // lights
    private static final Vector3f plPosition1 = new Vector3f(0.0f, 0.0f, 2.0f);
    private static final Vector3f plPosition2 = new Vector3f(0.0f, 0.0f, -2.0f);
    
    private static final Matrix4f quadModelMatrices[] = {
        Util.translationZ(1.0f, null),
        Util.mul(null, Util.rotationY(1.0f * Util.PI_DIV2, null), Util.translationZ(1.0f, null)),
        Util.mul(null, Util.rotationY(2.0f * Util.PI_DIV2, null), Util.translationZ(1.0f, null)),
        Util.mul(null, Util.rotationY(3.0f * Util.PI_DIV2, null), Util.translationZ(1.0f, null)),
        Util.mul(null, Util.rotationX(1.0f * Util.PI_DIV2, null), Util.translationZ(1.0f, null)),
        Util.mul(null, Util.rotationX(3.0f * Util.PI_DIV2, null), Util.translationZ(1.0f, null)),
    };
    
    public static void main(String[] argv) {
        try {
            init();
            OpenCL.init();
            
            glEnable(GL_CULL_FACE);
            glFrontFace(GL_CCW);
            glCullFace(GL_BACK);
            glEnable(GL_DEPTH_TEST);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_COLOR);
            
            bumpMappingSP.compile("./shader/BumpMapping_VS.glsl", "./shader/BumpMapping_FS.glsl");
            
            quadDiffuseTex.setData(Util.loadImage("./texture/bricks/diffuse.jpg"));
            quadDiffuseTex.bindToSlot(1);
            quadBumpTex.setData(Util.loadImage("./texture/bricks/bump.jpg"));
            quadBumpTex.bindToSlot(2);
            quadNormalTex.setData(Util.loadImage("./texture/bricks/normal.jpg"));
            quadNormalTex.bindToSlot(3);
                        
            render();
            destroy();
        } catch (LWJGLException ex) {
            Logger.getLogger(TextureMappingMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void render() throws LWJGLException {
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // background color: dark red
        
        long last = System.currentTimeMillis();
        long now, millis;
        long frameTimeDelta = 0;
        int frames = 0;
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
            
            // TODO: Add rendering code here
            setActiveProgram(bumpMappingSP);
            bumpMappingSP.setUniform("diffuseTex", quadDiffuseTex);
            bumpMappingSP.setUniform("bumpTex", quadBumpTex);
            bumpMappingSP.setUniform("normalTex", quadNormalTex);
            
            for(Matrix4f model : quadModelMatrices) {
                bumpMappingSP.setUniform("model", model);
                quadGeo.draw();
            }
            
            
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
                    case Keyboard.KEY_S: moveDir.z -= 1.0f; break;
                    case Keyboard.KEY_A: moveDir.x += 1.0f; break;
                    case Keyboard.KEY_D: moveDir.x -= 1.0f; break;
                    case Keyboard.KEY_SPACE: moveDir.y += 1.0f; break;
                    case Keyboard.KEY_C: moveDir.y -= 1.0f; break;
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
        
        if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) bContinue = false;
        
        Matrix4f.mul(cam.getProjection(), cam.getView(), viewProjMatrix);        
    }
    
    /**
     * Aktualisiert Model Matrizen der Szene.
     * @param millis Millisekunden, die seit dem letzten Aufruf vergangen sind.
     */
    private static void animate(long millis) {
        Util.transformCoord(Util.rotationY(6e-4f * (float)millis, null), plPosition1, plPosition1);
        Util.transformCoord(Util.rotationX(7e-4f * (float)millis, null), plPosition2, plPosition2);
    }
    
    /**
     * Aendert das aktuelle Programm und bindet die viewProjMatrix an die
     * entsprechende Uniform Variable.
     * @param program das neue aktuelle Programm
     */
    private static void setActiveProgram(ShaderProgram program) {
        program.use();
        program.setUniform("viewProj", viewProjMatrix);
        program.setUniform("plPosition1", plPosition1);
        program.setUniform("plPosition2", plPosition2);
        program.setUniform("eyePosition", cam.getCamPos());
    }
}
