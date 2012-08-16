package main;

import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static opengl.GL.*;
import opengl.OpenCL;
import opengl.OpenCL.Device_Type;
import opengl.GL;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import util.*;
import util.Util;

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
        
        //SIMULATION
        private static Particle particles = null;

	// controls
	private static final Camera cam = new Camera();
	private static final Vector3f moveDir = new Vector3f();
	private static final Matrix4f viewProjMatrix = new Matrix4f();

	public static void main(String argv[])
	{
		try
		{
			init();
			glEnable(GL_CULL_FACE);
			glCullFace(GL_BACK);
			glFrontFace(GL_CCW);
			glEnable(GL_DEPTH_TEST);
			glEnable(GL_PRIMITIVE_RESTART);
			glPrimitiveRestartIndex(-1);
			glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_COLOR);
			
                        //SIMMERGE
                        OpenCL.init();
                        glEnable(GL20.GL_VERTEX_PROGRAM_POINT_SIZE);
                        //glEnable(GL20.GL_POINT_SPRITE);
                        glEnable(GL32.GL_PROGRAM_POINT_SIZE);
            
			program = new ShaderProgram("shader/Terrain_VS.glsl", "shader/Terrain_FS.glsl");
			program.use();

			terra = new util.Terrain(0f, 2048, 2048, 4);
			terra.genTerrain(8);

			clip = new ClipMap(254, 8, program, cam);
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
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
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
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			program.setUniform("materials", materialTex);
			
                        //SIMMERGE
                        particles = new Particle(1024*4, Device_Type.GPU, Display.getDrawable());
                        particles.createData(tex.getId());
			
			render();
                        
                        //SIMMERGE
                        OpenCL.destroy();
		} catch (LWJGLException ex)
		{
			Logger.getLogger(MainTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static void render()
	{
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		long now, millis;
		long last = System.currentTimeMillis();

		while (bContinue && !Display.isCloseRequested())
		{
			now = System.currentTimeMillis();
			millis = now - last;
			last = now;
			handleInput(millis);
			param += 5e-3f * (float) millis * 0.25f;
                        program.use();
			program.setUniform("viewProj", Util.mul(null, cam.getProjection(), cam.getView()));
			program.setUniform("model", terrainModelMatrix);
			program.setUniform("modelIT", terrainModelITMatrix);

			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			clip.generateMaps();
			// terrainGeometry.draw();

                        //SIMMERGE
                        particles.getShaderProgram().use();
                        particles.draw(cam, millis);
                        
                        
			Display.update();
			Display.sync(60);
		}
	}

	/** Behandelt Input und setzt die Kamera entsprechend.
	 * @param millis Millisekunden seit dem letzten Aufruf */
	public static void handleInput(long millis)
	{
		float moveSpeed = 2e-3f * (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 2.0f : 1.0f) * (float) millis;
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
}
