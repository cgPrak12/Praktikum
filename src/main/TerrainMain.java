/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import static opengl.GL.*;

import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import opengl.OpenCL;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import terrain.ClipMap;
import terrain.Terrain;
import terrain.TerrainView;
import util.*;

/** @author nico3000 */
@SuppressWarnings("unused")
public class TerrainMain
{
	// current configurations
	private static boolean bContinue = true;
	private static boolean culling = true;
	private static boolean wireframe = true;
	private static boolean movement = false;
	
	 // terrain
    private static terrain.Terrain terra;
    
    
    // geometries
    private static Geometry terrainGeometry;

	// control
	private static final Vector3f moveDir = new Vector3f(0.0f, 0.0f, 0.0f);
	private static final Camera cam = new Camera();

	// animation params
	private static float ingameTimePerSecond = 1.0f;
	private static float moveSpeed;

	// Shader Programs
	private static ShaderProgram program;

	// Geometries
	private static ClipMap clip;

	// Textures
	private static Texture tex;
	private static Texture high;

	public static void main(String[] argv)
	{
		try
		{
			init();
			OpenCL.init();
			glEnable(GL_CULL_FACE);
			glCullFace(GL_BACK);
			glFrontFace(GL_CCW);
			glEnable(GL_DEPTH_TEST);
			glEnable(GL_PRIMITIVE_RESTART);
			glPrimitiveRestartIndex(-1);
			glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_COLOR);
			program = new ShaderProgram("./shader/Test_Vs.glsl", "./shader/Test_Fs.glsl");
			program.use();

            terra = new Terrain(1024, 0f);
            TerrainFactory.init();
            TerrainFactory.genTerrain(terra, 1);

            terrainGeometry = GeometryFactory.genTerrain(terra);
            
			
			terrain.Terrain terrain = new terrain.Terrain(1024);
			TerrainView.init(terra, cam);
			
			clip = new ClipMap(30, 8, program, cam);

			float[][] heightMap = TerrainView.getHeightMap();
			
//			int zero =0;
//			
//			for(int x=0; x<heightMap.length;x++){
//				for (int y=0; y<heightMap.length; y++){
//					if(heightMap[x][y]==0)System.out.println("x:"+x+ " y:"+y+"  "+zero++);
//				}
//			}
//			for(float[] i : heightMap){
//				for(float j :i){
//					if (j==0)System.out.println("+++++" +zero++);
//				}
//			}
			
			
			
			FloatBuffer fbuffer = BufferUtils.createFloatBuffer(heightMap.length*heightMap.length);
			for(int i = 0; i < heightMap.length; i++) {
				fbuffer.put(heightMap[i]);
			
			}
			fbuffer.flip();
			tex = new Texture(GL_TEXTURE_2D, 1);
			tex.bind();
			glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_R32F, heightMap.length, heightMap[0].length, 0, GL11.GL_RED, GL_FLOAT, fbuffer);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

			program.setUniform("elevation", tex);
			tex = Texture.generateTexture("./earth.jpg", 2);
			tex.bind();
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

			program.setUniform("coloration", tex);

			render();
			OpenCL.destroy();
			destroy();
		} catch (LWJGLException ex)
		{
			Logger.getLogger(TerrainMain.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static void render() throws LWJGLException
	{
		glClearColor(0.1f, 0.0f, 0.0f, 1.0f); // background color: dark red

		long last = System.currentTimeMillis();
		long now, millis;
		long frameTimeDelta = 0;
		int frames = 0;

		while (bContinue && !Display.isCloseRequested())
		{
			now = System.currentTimeMillis();
			millis = now - last;
			last = now;
			frameTimeDelta += millis;
			++frames;
			if (frameTimeDelta > 1000)
			{
//				System.out.println(1e3f * (float) frames / (float) frameTimeDelta + " FPS");
				frameTimeDelta -= 1000;
				frames = 0;
			}

			// input and animation
			updateUniforms();
			handleInput(millis);
			animate(millis);

			program.use();

			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			clip.generateMaps();

			Display.update();
			Display.sync(60);
		}
	}

	private static void updateUniforms()
	{
		program.use();
		program.setUniform("viewProj", Util.mul(null, cam.getProjection(), cam.getView()));
		program.setUniform("model", new Matrix4f());
	}

	/** Behandelt Input und setzt die Kamera entsprechend.
	 * @param millis Millisekunden seit dem letzten Aufruf */
	public static void handleInput(long millis)
	{
		moveSpeed = 2e-3f * (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 2.0f : 1.0f) * (float) millis;
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
				case Keyboard.KEY_ESCAPE:
					bContinue = false;
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
				case Keyboard.KEY_LEFT:
					if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
					{
						ingameTimePerSecond = 0.0f;
					} else
					{
						ingameTimePerSecond = Math.max(1.0f / 64.0f, 0.5f * ingameTimePerSecond);
					}
					break;
				case Keyboard.KEY_RIGHT:
					if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
					{
						ingameTimePerSecond = 1.0f;
					} else
					{
						ingameTimePerSecond = Math.min(64.0f, 2.0f * ingameTimePerSecond);
					}
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

		// if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) bContinue = false;
	}

	/** Aktualisiert Model Matrizen der Erde und des Mondes.
	 * @param millis Millisekunden, die seit dem letzten Aufruf vergangen sind. */
	private static void animate(long millis)
	{
	}

}
