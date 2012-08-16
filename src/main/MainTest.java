package main;

import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static opengl.GL.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import util.*;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

/** @author NMARNIOK */
public class MainTest
{
	// shader programs
	private static int terrainProgram;

	// terrain
	private static util.Terrain terra;

	// geometries
	private static Geometry terrainGeometry;

	// model matrices
	private static final Matrix4f terrainModelMatrix = new Matrix4f();
	private static final Matrix4f terrainModelITMatrix = new Matrix4f();

	// configs
	private static boolean bContinue = true;
	private static boolean wireframe = false;
	private static boolean culling = true;
	private static float param = 0.0f;

	private static ShaderProgram program;

	private static ClipMap clip;

	private static Texture tex;

	private static Texture materialTex;

	// controls
	private static final Camera cam = new Camera();
	private static final Vector3f moveDir = new Vector3f();
	private static final Matrix4f viewProjMatrix = new Matrix4f();

    //Map to simulate a terrain with different types of ground
    private static ModelMapEntry[][] modelMap;
    
	public static void main(String argv[])
	{
		try
		{
                init();
    //            glEnable(GL_CULL_FACE);
                glFrontFace(GL_CCW);
                glCullFace(GL_BACK);
                glEnable(GL_DEPTH_TEST);
                glEnable(GL_PRIMITIVE_RESTART);
/*                glPrimitiveRestartIndex(-1);
                glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_COLOR);*/
                glPointSize(2.0f);
                        
			terra = new util.Terrain(0f, 2048, 2048, 4);
			terra.genTerrain(8);
                        modelMap = generateModelMap(terra);
                        
/*			program = new ShaderProgram("shader/Terrain_VS.glsl", "shader/Terrain_FS.glsl");
			program.use();

			clip = new ClipMap(254, 14, program, cam);
			cam.setClipMap(clip);

			float heightMap[][][] = new float[terra.getTerra().length][terra.getTerra()[0].length][4];
			float materials[][] = new float[terra.getTerra().length][terra.getTerra()[0].length];

			for (int i = 0; i < terra.getTerra().length; i++)
			{
				for (int j = 0; j < terra.getTerra()[0].length; j++)
				{
					for (int k = 0; k < 4; k++)
					{
						heightMap[i][j][k] = terra.getTerra()[i][j][k];
					}
					materials[i][j] = terra.getTerra()[i][j][4];
				}
			}

			tex = new Texture(GL_TEXTURE_2D, 1);
			materialTex = new Texture(GL_TEXTURE_2D, 2);
			FloatBuffer fbuffer = BufferUtils.createFloatBuffer(4* heightMap.length * heightMap.length);
			for (int i = 0; i < heightMap.length; i++)
			{
				for (int j = 0; j < heightMap.length; j++)
				{
					fbuffer.put(heightMap[i][j]);
				}
			}
			fbuffer.flip();
			tex.bind();
			glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_RGBA32F, heightMap.length, heightMap[0].length, 0, GL11.GL_RGBA,
					GL_FLOAT, fbuffer);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_MIRRORED_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_MIRRORED_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			program.setUniform("elevation", tex);
		
			for(int i = 0; i < materials.length; i++) {
				fbuffer.put(materials[i]);
			}
			fbuffer.flip();
			materialTex.bind();
			glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_R32F, heightMap.length, heightMap[0].length, 0, GL11.GL_RED,
					GL_FLOAT, fbuffer);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_MIRRORED_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_MIRRORED_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			program.setUniform("materials", materialTex);
			*/
			
			render();
		} catch (LWJGLException ex)
		{
			Logger.getLogger(MainTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static void render()
	{
        glClearColor(0.1f, 0.0f, 0.0f, 1.0f); // background color: dark red
        
        long last = System.currentTimeMillis();
        long now, millis;
        long frameTimeDelta = 0;
        int frames = 0;
        
        ShaderProgram shaderProgramModels = new ShaderProgram("./shader/Models_VS.glsl", "./shader/Models_FS.glsl");
 //       ShaderProgram shaderProgramTerrain = new ShaderProgram("shader/Terrain_VS.glsl", "shader/Terrain_FS.glsl");
        
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
            
            Matrix4f model = new Matrix4f();
            Matrix4f viewProj = Util.mul(null, cam.getProjection(), cam.getView());

            shaderProgramModels.use();
//            System.out.println(cam.getCamPos().x+", "+cam.getCamPos().z);
            for(int x=0; x<modelMap.length; x++) {
                for(int z=0; z<modelMap.length; z++) {
                    if(modelMap[x][z]!=null && modelMap[x][z].getModelList()!=null) {
                        ListIterator modelListIterator = modelMap[x][z].getModelList().listIterator();
                        
                        while(modelListIterator.hasNext()) {
                            ModelPart modelPart = (ModelPart)modelListIterator.next();
                            shaderProgramModels.setUniform("scale", modelMap[x][z].getScale());
                            shaderProgramModels.setUniform("translate", modelMap[x][z].getPosition());
                            
                            shaderProgramModels.setUniform("model", model);
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

                            
/*                            if(modelMap[x][z].getPosition().m30-cam.getCamPos().x<2 &&
                               modelMap[x][z].getPosition().m30-cam.getCamPos().x>-2 &&
                               modelMap[x][z].getPosition().m32-cam.getCamPos().z<2 &&
                               modelMap[x][z].getPosition().m32-cam.getCamPos().z>-2)
                            {*/
//                                System.out.println("test");
//                               modelPart.geometry.createBuffers();
                               modelPart.geometry.draw();
                            //}
                        }
                    }
                }
            }
            
/*            shaderProgramTerrain.use();
            shaderProgramTerrain.setUniform("viewProj", viewProj);
            shaderProgramTerrain.setUniform("model", terrainModelMatrix);
            shaderProgramTerrain.setUniform("modelIT", terrainModelITMatrix);
            terrainGeometry.draw();*/

            // present screen
            Display.update();
            Display.sync(60);
        }
        shaderProgramModels.delete();
//        shaderProgramTerrain.delete();
	}

	/** Behandelt Input und setzt die Kamera entsprechend.
	 * @param millis Millisekunden seit dem letzten Aufruf */
	public static void handleInput(long millis)
	{
            float moveSpeed = 2e-4f*(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 2.0f : 1.0f)*(float)millis;
            float camSpeed = 5e-3f;

		while (Keyboard.next())
		{
			if (Keyboard.getEventKeyState())
			{
				switch (Keyboard.getEventKey())
				{
				case Keyboard.KEY_W:
					moveDir.z += 1.0f;
					break;
				case Keyboard.KEY_S:
					moveDir.z -= 1.0f;
					break;
				case Keyboard.KEY_A:
					moveDir.x += 1.0f;
					break;
				case Keyboard.KEY_D:
					moveDir.x -= 1.0f;
					break;
				case Keyboard.KEY_SPACE:
					moveDir.y += 1.0f;
					break;
				case Keyboard.KEY_C:
					moveDir.y -= 1.0f;
					break;
				}
			} else
			{
				switch (Keyboard.getEventKey())
				{
				case Keyboard.KEY_W:
					moveDir.z -= 1.0f;
					break;
				case Keyboard.KEY_S:
					moveDir.z += 1.0f;
					break;
				case Keyboard.KEY_A:
					moveDir.x -= 1.0f;
					break;
				case Keyboard.KEY_D:
					moveDir.x += 1.0f;
					break;
				case Keyboard.KEY_SPACE:
					moveDir.y -= 1.0f;
					break;
				case Keyboard.KEY_C:
					moveDir.y += 1.0f;
					break;
				case Keyboard.KEY_F1:
					cam.changeProjection();
					break;
				case Keyboard.KEY_F2:
					glPolygonMode(GL_FRONT_AND_BACK, (wireframe ^= true) ? GL_FILL : GL_LINE);
					break;
				case Keyboard.KEY_F3:
					if (culling ^= true)
						glEnable(GL_CULL_FACE);
					else
						glDisable(GL_CULL_FACE);
					break;
				case Keyboard.KEY_F4: clip.moveClipBy(1, 0); break;
				case Keyboard.KEY_F5: clip.moveClipBy(0, 1); break;
				case Keyboard.KEY_F6: clip.moveClipBy(-1, 0); break;
				case Keyboard.KEY_F7: clip.moveClipBy(0, -1); break;
				case Keyboard.KEY_F8: cam.beam(); break;
				}
			}
		}

		cam.move(moveSpeed * moveDir.z, moveSpeed * moveDir.x, moveSpeed * moveDir.y);

		while (Mouse.next())
		{
			if (Mouse.getEventButton() == 0)
			{
				Mouse.setGrabbed(Mouse.getEventButtonState());
			}
			if (Mouse.isGrabbed())
			{
				cam.rotate(-camSpeed * Mouse.getEventDX(), -camSpeed * Mouse.getEventDY());
			}
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			bContinue = false;

		Matrix4f.mul(cam.getProjection(), cam.getView(), viewProjMatrix);
	}


    public static ModelMapEntry[][] generateModelMap(util.Terrain terra) {
        //load models
        //Current time in millis
    	long timeInMillis = System.currentTimeMillis();
        List modelTallCactus = GeometryFactory.importFromBlender("blender\\tall-cactus.obj", "blender\\tall-cactus.mtl", "blender\\textures\\");
        List modelPalmTree = GeometryFactory.importFromBlender("blender\\low-poly-palm-tree.obj", "blender\\low-poly-palm-tree.mtl", "blender\\textures\\");
        List modelBirchTree = GeometryFactory.importFromBlender("blender\\pseudo-birch2.obj", "blender\\pseudo-birch2.mtl", "blender\\textures\\");
        List modelElmTree = GeometryFactory.importFromBlender("blender\\low-poly-leaf-tree.obj", "blender\\low-poly-leaf-tree.mtl", "blender\\textures\\");
        List modelPineTree = GeometryFactory.importFromBlender("blender\\pine-tree.obj", "blender\\pine-tree.mtl", "blender\\textures\\");
        List modelDeadShrub = GeometryFactory.importFromBlender("blender\\dead-shrub.obj", "blender\\dead-shrub.mtl", "blender\\textures\\");
        List modelFern = GeometryFactory.importFromBlender("blender\\fern.obj", "blender\\fern.mtl", "blender\\textures\\");
        List modelFlower1 = GeometryFactory.importFromBlender("blender\\flower1.obj", "blender\\flower1.mtl", "blender\\textures\\");
        List modelFlower2 = GeometryFactory.importFromBlender("blender\\flower2.obj", "blender\\flower2.mtl", "blender\\textures\\");
        List modelFlower3 = GeometryFactory.importFromBlender("blender\\flower3.obj", "blender\\flower3.mtl", "blender\\textures\\");
        List modelFlower4 = GeometryFactory.importFromBlender("blender\\flower4.obj", "blender\\flower4.mtl", "blender\\textures\\");
        List modelFlower5 = GeometryFactory.importFromBlender("blender\\flower5.obj", "blender\\flower5.mtl", "blender\\textures\\");
        List modelRock1 = GeometryFactory.importFromBlender("blender\\rock1.obj", "blender\\rock1.mtl", "blender\\textures\\");
        List modelRock2 = GeometryFactory.importFromBlender("blender\\rock2.obj", "blender\\rock2.mtl", "blender\\textures\\");
        List modelRock3 = GeometryFactory.importFromBlender("blender\\rock3.obj", "blender\\rock3.mtl", "blender\\textures\\");
        List modelRock4 = GeometryFactory.importFromBlender("blender\\rock4.obj", "blender\\rock4.mtl", "blender\\textures\\");
        List modelShroom = GeometryFactory.importFromBlender("blender\\shroom.obj", "blender\\shroom.mtl", "blender\\textures\\");
        List modelShroom2 = GeometryFactory.importFromBlender("blender\\shroom2.obj", "blender\\shroom2.mtl", "blender\\textures\\");
        List modelShrub2 = GeometryFactory.importFromBlender("blender\\shrub2.obj", "blender\\shrub2.mtl", "blender\\textures\\");        
        System.out.println("Importing took "+(System.currentTimeMillis()-timeInMillis)+" milliseconds.");

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

        Matrix4f scale = new Matrix4f().scale(new Vector3f(0.01f, 0.01f, 0.01f));
        float terrainGrid[][][] = terra.getTerrainGrid().getBlock();
        ModelMapEntry[][] modelMap = new ModelMapEntry[terrainGrid.length][terrainGrid.length];
 
        for(int x=0; x<terrainGrid.length; x+=10) {
            for(int z=0; z<terrainGrid.length; z+=10) {
                modelMap[x][z] = new ModelMapEntry();
                
                Matrix4f translate = new Matrix4f();
                translate.m00 = 1;
                translate.m11 = 1;
                translate.m22 = 1;
                translate.m33 = 1;
                translate.m30 = x/100.0f;
                translate.m31 = terrainGrid[x][z][0];
                translate.m32 = z*100.0f;
                modelMap[x][z].setPosition(translate);
                
                if(terrainGrid[x][z][4]==3) {
                    //Generate random numbers with wights
                    int[] values = {0,1,2};
                    int[] weights = {70,20,10};
                    int result=randomNumber(values, weights);
                    if(result==0) {
                        modelMap[x][z].setScale(null);
                        modelMap[x][z].setModelList(null);
                    } else if(result==1) {
                        modelMap[x][z].setScale(new Matrix4f().scale(new Vector3f(0.007f, 0.007f, 0.007f)));
                        modelMap[x][z].setModelList(modelPalmTree);
                    } else if (result==2) {
                        modelMap[x][z].setScale(new Matrix4f().scale(new Vector3f(0.007f, 0.007f, 0.007f)));
                        modelMap[x][z].setModelList(modelTallCactus);
                    }
                } else if(terrainGrid[x][z][4]==5) {
                    //Generate random numbers with wights
                    int[] values = {0,1,2,3,4};
                    int[] weights = {30,5,20,15,30};
                    int result=randomNumber(values, weights);
                    if(result==0) {
                        modelMap[x][z].setScale(null);
                        modelMap[x][z].setModelList(null);
                    } else if(result==1) {
                        modelMap[x][z].setScale(new Matrix4f().scale(new Vector3f(0.004f, 0.004f, 0.004f)));
                        modelMap[x][z].setModelList(modelBirchTree);
                    } else if (result==2) {
                        modelMap[x][z].setScale(new Matrix4f().scale(new Vector3f(0.005f, 0.005f, 0.005f)));
                        modelMap[x][z].setModelList(modelFlower1);
                    } else if (result==3) {
                        modelMap[x][z].setScale(new Matrix4f().scale(new Vector3f(0.005f, 0.005f, 0.005f)));
                        modelMap[x][z].setModelList(modelFlower2);
                    } else if (result==4) {
                        modelMap[x][z].setScale(new Matrix4f().scale(new Vector3f(0.005f, 0.005f, 0.005f)));
                        modelMap[x][z].setModelList(modelFlower3);
                    }
                } else if(terrainGrid[x][z][4]==6) {
                    //Generate random numbers with wights
                    int[] values = {0,1};
                    int[] weights = {85,15};
                    int result=randomNumber(values, weights);
                    if(result==0) {
                        modelMap[x][z].setScale(null);
                        modelMap[x][z].setModelList(null);
                    } else if(result==1) {
                        modelMap[x][z].setScale(new Matrix4f().scale(new Vector3f(0.03f, 0.03f, 0.03f)));
                        modelMap[x][z].setModelList(modelElmTree);
                    }
                } else if(terrainGrid[x][z][4]==7) {
                    //Generate random numbers with wights
                    int[] values = {0,1};
                    int[] weights = {95,5};
                    int result=randomNumber(values, weights);
                    if(result==0) {
                        modelMap[x][z].setScale(null);
                        modelMap[x][z].setModelList(null);
                    } else if(result==1) {
                        modelMap[x][z].setScale(new Matrix4f().scale(new Vector3f(0.005f, 0.005f, 0.005f)));
                        modelMap[x][z].setModelList(modelPineTree);
                    }
                } else if(terrainGrid[x][z][4]==8) {
                    //Generate random numbers with wights
                    int[] values = {0,1};
                    int[] weights = {98,2};
                    int result=randomNumber(values, weights);
                    if(result==0) {
                        modelMap[x][z].setScale(null);
                        modelMap[x][z].setModelList(null);
                    } else if(result==1) {
                    modelMap[x][z].setScale(new Matrix4f().scale(new Vector3f(0.015f, 0.015f, 0.015f)));
                    modelMap[x][z].setModelList(modelDeadShrub);
                    }
                } else if(terrainGrid[x][z][4]==9) {
                    //Generate random numbers with wights
                    int[] values = {0,1};
                    int[] weights = {90,10};
                    int result=randomNumber(values, weights);
                    if(result==0) {
                        modelMap[x][z].setScale(null);
                        modelMap[x][z].setModelList(null);
                    } else if(result==1) {
                    modelMap[x][z].setScale(new Matrix4f().scale(new Vector3f(0.015f, 0.015f, 0.015f)));
                    modelMap[x][z].setModelList(modelRock1);
                    }
                } else {
                    modelMap[x][z].setScale(null);
                    modelMap[x][z].setPosition(null);
                    modelMap[x][z].setModelList(null);
                }
                
/*                if(modelMap[x][z].getModelList()!=null)
                    System.out.println(modelMap[x][z].getPosition().m30+", "+modelMap[x][z].getPosition().m32);*/
            }
        }
        return modelMap;
    }

    /**
     * Creates the sum of a one dimensional int array
     * @param values
     * @return sum
     */
    private static int sum(int[] values) {
        int result = 0;
        
        for(int value : values){
            result += value;
        }
        
        return result;
    }
    
    /**
     * Pics a number from values[] randomly using weights
     * @param values
     * @param weights
     * @return Integer random number
     */
    public static int randomNumber(int[] values, int[] weights) {
        int weightSum = sum(weights);        
        Random random = new Random();
        int currentLimit = random.nextInt(weightSum+1);
        int currentSum = 0;
        for(int k = 0; k< values.length;k++){
            currentSum += weights[k];
            if(currentSum >= currentLimit){
                return values[k];
            }
        }
        return 0;
    }
    
    /**
     * Aktualisiert Model Matrizen der Erde und des Mondes.
     * @param millis Millisekunden, die seit dem letzten Aufruf vergangen sind.
     */
    private static void animate(long millis) {

    }
}
