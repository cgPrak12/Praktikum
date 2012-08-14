//-Djava.library.path=C:\Users\Floh1111\Desktop\Computergrafik\lwjgl-2.8.3\native\windows
//-Djava.library.path=/home/floh1111/Uni/Computergrafik/Übungsblätter/blatt2/2.2/lwjgl-2.8.3/native/linux/

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
    /**
     * Terrain variables
     */
    // terrain
    private static util.Terrain terra;
    
    // geometries
    private static Geometry terrainGeometry;
    
    // model matrices
    private static final Matrix4f terrainModelMatrix = new Matrix4f();    
    private static final Matrix4f terrainModelITMatrix = new Matrix4f();    

    /**
     * Blender Importer variables
     */
    //Map to simulate a terrain with different types of ground
    private static int[][] map = new int[10][10];
    
    /**
     * Standard variables
     */
    // current configurations
    private static boolean bContinue = true;
    private static boolean culling = true;
    private static boolean wireframe = true;
    private static float param = 0.0f;
    
    // control
    private static final Vector3f moveDir = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final Camera cam = new Camera(); 
    
    // animation params
    private static float ingameTime = 0;
    private static float ingameTimePerSecond = 1.0f;

    private static int sum(int[] values) {
        int result = 0;
        
        for(int value : values){
            result += value;
        }
        
        return result;
    }
    
/*    //einfache geometrie für ein quadrat
    private static Geometry quad = null;
    //shader erzeugen
    private static ShaderProgram shader = null;*/
    
    public static void main(String[] argv) {
        //Generate terrain
        terra = new util.Terrain(0f, 1024, 1024, 4);
        terra.genTerrain(10);
        terrainGeometry = GeometryFactory.genTerrain(terra.getTerra());
        
        //Generate random numbers with wights
        int[] values = {0,1,2,3};
        int[] weights = {20,60,20,0};
        
        int weightSum = sum(weights);        
        int[] histogram = new int[values.length];
        
        Random random = new Random();
        for(int i=0; i<map.length; i++) {
            for(int j=0; j<map.length; j++){
                int currentLimit = random.nextInt(weightSum+1);
            
                int currentSum = 0;
                for(int k = 0; k< values.length;k++){
                    currentSum += weights[k];
                    if(currentSum >= currentLimit){
                        
                        map[i][j] = values[k];
                        
                        histogram[k]++;
                        
                        break;
                    }
                }
            }
        }
        
        try {
            init();
            OpenCL.init();
//            glEnable(GL_CULL_FACE);
            glFrontFace(GL_CCW);
            glCullFace(GL_BACK);
            glEnable(GL_DEPTH_TEST);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_COLOR);
            glPointSize(2.0f);
            
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
        
        ShaderProgram shaderProgramModels = new ShaderProgram("./shader/Models_VS.glsl", "./shader/Models_FS.glsl");
        ShaderProgram shaderProgramTerrain = new ShaderProgram("shader/Terrain_VS.glsl", "shader/Terrain_FS.glsl");
        
        //Current time in millis
    	long timeInMillis = System.currentTimeMillis();
        
//        List modelPartList3 = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\Desktop\\OtherModels\\Palma 001.obj", "C:\\Users\\Floh1111\\Desktop\\OtherModels\\Palma 001.mtl", "");
//        List modelPartList = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\uh60\\uh60.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\uh60\\uh60.mtl",  "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\uh60\\");
//        List modelPartList3 = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\Bandit Heavy\\Bandit Heavy.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\Bandit Heavy\\Bandit Heavy.mtl",  "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\Bandit Heavy\\");
        List modelTallCactus = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\tall-cactus.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\tall-cactus.mtl", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\textures\\");
        List modelPalmTree = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\low-poly-palm-tree.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\low-poly-palm-tree.mtl", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\textures\\");
        List modelBirchTree = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\pseudo-birch2.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\pseudo-birch2.mtl", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\textures\\");
        List modelElmTree = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\low-poly-leaf-tree.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\low-poly-leaf-tree.mtl", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\textures\\");
        List modelPineTree = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\pine-tree.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\pine-tree.mtl", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\textures\\");
        List modelDeadShrub = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\dead-shrub.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\dead-shrub.mtl", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\textures\\");
        List modelFern = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\fern.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\fern.mtl", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\textures\\");
        List modelFlower1 = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\flower1.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\flower1.mtl", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\textures\\");
        List modelFlower2 = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\flower2.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\flower2.mtl", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\textures\\");
        List modelFlower3 = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\flower3.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\flower3.mtl", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\textures\\");
        List modelFlower4 = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\flower4.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\flower4.mtl", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\textures\\");
        List modelFlower5 = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\flower5.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\flower5.mtl", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\textures\\");
        List modelRock1 = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\rock1.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\rock1.mtl", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\textures\\");
        List modelRock2 = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\rock2.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\rock2.mtl", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\textures\\");
        List modelRock3 = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\rock3.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\rock3.mtl", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\textures\\");
        List modelRock4 = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\rock4.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\rock4.mtl", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\textures\\");
        List modelShroom = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\shroom.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\shroom.mtl", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\textures\\");
        List modelShroom2 = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\shroom2.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\shroom2.mtl", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\textures\\");
        List modelShrub2 = GeometryFactory.importFromBlender("C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\shrub2.obj", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\shrub2.mtl", "C:\\Users\\Floh1111\\.ssh\\Praktikum\\blender\\textures\\");
        
        System.out.println("Importing took "+(System.currentTimeMillis()-timeInMillis)+" milliseconds.");
        
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
            
            Matrix4f scale = new Matrix4f().scale(new Vector3f(0.01f, 0.01f, 0.01f));
            Matrix4f model = new Matrix4f();
            Matrix4f viewProj = Util.mul(null, cam.getProjection(), cam.getView());

            shaderProgramModels.use();

            float terrainGrid[][][] = terra.getTerrainGrid().getBlock();
            for(int x=0; x<terrainGrid.length; x+=50) {
                for(int z=0; z<terrainGrid.length; z+=50) {
/*                    System.out.println(terrainGrid[y][x][0]);
                    System.out.println(terrainGrid[y][x][1]);
                    System.out.println(terrainGrid[y][x][2]);
                    System.out.println(terrainGrid[y][x][3]);*/
//                    System.out.println(terrainGrid[y][x][4]);
                    Matrix4f translate = new Matrix4f();
                    translate.m00 = 1;
                    translate.m11 = 1;
                    translate.m22 = 1;
                    translate.m33 = 1;
                    translate.m30 = x/100.0f;
                    translate.m31 = terrainGrid[x][z][0];
                    translate.m32 = z/100.0f;
/*
                    0 default
                    1 meer
                    2 see und fluss
                    3 sand
                    4 erde
                    5 helles flachlandgras
                    6 höheres gras
                    7 stein
                    8 fels
                    9 leichter schnee
                    10 schwerer schnee*/
                    
                    Iterator<ModelPart> modelPartListIterator = null;
                    if(terrainGrid[x][z][4]==3) {
/*                        //Generate random numbers with wights
                        int[] values = {0,1};
                        int[] weights = {70,30};
                        int weightSum = sum(weights);        
                        Random random = new Random();
                        int currentLimit = random.nextInt(weightSum+1);
                        int currentSum = 0;
                        int result=0;
                        for(int k = 0; k< values.length;k++){
                            currentSum += weights[k];
                            if(currentSum >= currentLimit){
                               result = values[k]; break;
                            }
                        }
                        if(result==0) {*/
                            modelPartListIterator = modelPalmTree.listIterator();
                            scale = new Matrix4f().scale(new Vector3f(0.007f, 0.007f, 0.007f));
/*                        } else if (result==1) {
                            modelPartListIterator = modelTallCactus.listIterator();
                            scale = new Matrix4f().scale(new Vector3f(0.007f, 0.007f, 0.007f));
                        }*/
                    } else if(terrainGrid[x][z][4]==5) {
                        modelPartListIterator = modelBirchTree.listIterator();
                        scale = new Matrix4f().scale(new Vector3f(0.004f, 0.004f, 0.004f));
                    } else if(terrainGrid[x][z][4]==6) {
                        modelPartListIterator = modelElmTree.listIterator();
                        scale = new Matrix4f().scale(new Vector3f(0.03f, 0.03f, 0.03f));
                    } else if(terrainGrid[x][z][4]==7) {
                        modelPartListIterator = modelPineTree.listIterator();
                        scale = new Matrix4f().scale(new Vector3f(0.005f, 0.005f, 0.005f));
                    } else if(terrainGrid[x][z][4]==8) {
                        modelPartListIterator = modelDeadShrub.listIterator();
                        scale = new Matrix4f().scale(new Vector3f(0.015f, 0.015f, 0.015f));
                    } else
                        modelPartListIterator = null;
                    if(modelPartListIterator!=null) {
                        while(modelPartListIterator.hasNext()) {
                            ModelPart modelPart = modelPartListIterator.next();
                            shaderProgramModels.setUniform("scale", scale);
                            shaderProgramModels.setUniform("translate", translate);
                            
                            shaderProgramModels.setUniform("model", model);
//                            shaderProgram.setUniform("modelIT", new Matrix4f());
                            shaderProgramModels.setUniform("viewProj", viewProj);   
                            
                            shaderProgramModels.setUniform("k_a", modelPart.material.ambientRef);
                            shaderProgramModels.setUniform("k_dif", modelPart.material.diffuseRef);
                            shaderProgramModels.setUniform("k_spec", modelPart.material.specularRef);
                            shaderProgramModels.setUniform("k_diss", modelPart.material.dissolveFact);
                        
                            if(modelPart.material.textureDiffuseRefColorMap!=null)
                                shaderProgramModels.setUniform("diffuseTex", modelPart.material.textureDiffuseRefColorMap);
                            if(modelPart.material.textureDissolveFactColorMap!=null)
                                shaderProgramModels.setUniform("dissolveTex", modelPart.material.textureDissolveFactColorMap);
                            if(modelPart.material.textureSpecularRefColorMap!=null)
                                shaderProgramModels.setUniform("specularTex", modelPart.material.textureSpecularRefColorMap);
                        
                            modelPart.geometry.draw();
                        }
                    }
                }
            }
            
            shaderProgramTerrain.use();
            shaderProgramTerrain.setUniform("viewProj", viewProj);
            shaderProgramTerrain.setUniform("model", terrainModelMatrix);
            shaderProgramTerrain.setUniform("modelIT", terrainModelITMatrix);
//            shaderProgram.setUniform("param", terrainParamLoc);
            terrainGeometry.draw();
            // present screen
            Display.update();
            Display.sync(60);
        }
        shaderProgramModels.delete();
        shaderProgramTerrain.delete();
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
