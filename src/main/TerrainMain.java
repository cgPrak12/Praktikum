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
import java.util.*;

/**
 *
 * @author nico3000
 */
public class TerrainMain {
    // current configurations
    private static boolean bContinue = true;
    private static boolean culling = true;
    private static boolean wireframe = true;
    
    // control
    private static final Vector3f moveDir = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final Camera cam = new Camera(); 
    
    // animation params
    private static float ingameTime = 0;
    private static float ingameTimePerSecond = 1.0f;
    
/*    //einfache geometrie für ein quadrat
    private static Geometry quad = null;
    //shader erzeugen
    private static ShaderProgram shader = null;*/
    
    
    public static void main(String[] argv) {
        try {
            init();
            OpenCL.init();
//            glEnable(GL_CULL_FACE);
            glFrontFace(GL_CCW);
            glCullFace(GL_BACK);
            glEnable(GL_DEPTH_TEST);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_COLOR);
                                    
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
        
        ShaderProgram shaderProgram = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/FragmentLighting_FS.glsl");
        
//        List modelPartList = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\Desktop\\OtherModels\\Palma 001.obj", "C:\\Users\\Floh1111\\Desktop\\OtherModels\\Palma 001.mtl", "");
//        List modelPartList = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\uh60\\uh60.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\uh60\\uh60.mtl",  "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\uh60\\");
//        List modelPartList = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\Bandit Heavy\\Bandit Heavy.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\Bandit Heavy\\Bandit Heavy.mtl",  "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\Bandit Heavy\\");
//        List modelPartList = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\low-poly-palm-tree.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\low-poly-palm-tree.mtl", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\");
        List modelPartList = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\elm-tree\\elm-tree.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\elm-tree\\elm-tree.mtl", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\elm-tree\\");


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
            
            //clear screen
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            Matrix4f scale = new Matrix4f().scale(new Vector3f(1.0f, 1.0f, 1.0f));
            Matrix4f model = new Matrix4f();
            Matrix4f viewProj = Util.mul(null, cam.getProjection(), cam.getView());
            
            shaderProgram.use();
                        
            for(int i=0; i<=6; i++) {
                for(int j=0; j<=6; j++) {
                    Matrix4f translate = new Matrix4f();
                    translate.m00 = 1;
                    translate.m11 = 1;
                    translate.m22 = 1;
                    translate.m33 = 1;
                    translate.m30 = 15*i;
                    translate.m32 = 15*j;
                
                    Iterator<ModelPart> modelPartListIterator = modelPartList.listIterator();
                    while(modelPartListIterator.hasNext()) {
                        ModelPart modelPart = modelPartListIterator.next();
                        
                        shaderProgram.setUniform("scale", scale);
                        shaderProgram.setUniform("translate", translate);
                        
                        shaderProgram.setUniform("model", model);
//                      shaderProgram.setUniform("modelIT", new Matrix4f());
                        shaderProgram.setUniform("viewProj", viewProj);   
 
                        shaderProgram.setUniform("k_a", modelPart.material.ambientRef);
                        shaderProgram.setUniform("k_dif", modelPart.material.diffuseRef);
                        shaderProgram.setUniform("k_spec", modelPart.material.specularRef);
                        shaderProgram.setUniform("k_diss", modelPart.material.dissolveFact);
                        
                        if(modelPart.material.textureDiffuseRefColorMap!=null)
                            shaderProgram.setUniform("diffuseTex", modelPart.material.textureDiffuseRefColorMap);
                        if(modelPart.material.textureDissolveFactColorMap!=null)
                            shaderProgram.setUniform("dissolveTex", modelPart.material.textureDissolveFactColorMap);
                        if(modelPart.material.textureSpecularRefColorMap!=null)
                            shaderProgram.setUniform("specularTex", modelPart.material.textureSpecularRefColorMap);                
                        
                        modelPart.geometry.draw();
                    }
                }
            }
            // present screen
            Display.update();
            Display.sync(60);
        }
        shaderProgram.delete();
    }
    
    /**
     * Behandelt Input und setzt die Kamera entsprechend.
     * @param millis Millisekunden seit dem letzten Aufruf
     */
    public static void handleInput(long millis) {
        float moveSpeed = 2e-2f*(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 2.0f : 1.0f)*(float)millis;
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
}
