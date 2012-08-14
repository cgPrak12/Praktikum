package terrain;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import util.Camera;
import util.Geometry;
import util.GeometryFactory;
import util.ShaderProgram;
import util.Util;

/** Clip Map
 * 
 * @author Christoph, Michael */

public class ClipMap
{

	// ClipMap Size
	private int stage; // Anzahl der Auflösungsebenen
	private int gridsize; // Kantenlänge der 12 Quadrate
	private int middlesize; // Kantenlänge der Füllstücke
	private int lsize; // Länge der Ls
	private int size; // Kantenlänge des ClipMapRings

	// Shader Updates
	private ShaderProgram program; // Aktives Shaderprogramm
	private Matrix4f translation; // Translationsmatrix für Shaderprogramm

	// Animation Params
	private Camera cam; // Kamera des Programms
	private int[][] movement; // Array das Bewegungstranslation speichert
	private boolean[][] alignment; // Array das Lage der Clipmap angibt
	private float tempX; // Variable für Bewegungsschwellenwert
	private float tempY; // Variable für Bewegungsschwellenwert
	private float tempZ; // Variable für Bewegungsschwellenwert

	private int scaleSteps;
	private int scaleFaktor;
	private Vector2f initialCamPos;

	// Cached Geometries
	private Geometry mxm; // Quadratisches Grid
	private Geometry mxn; // Rechteckiges Grid
	private Geometry nxm; // Rechteckiges Grid
	private Geometry topRight; // L
	private Geometry topLeft; // L
	private Geometry bottomLeft; // L
	private Geometry bottomRight; // L
	private Geometry center; // Quadratisches Grid
	private Geometry outer; // Füllgeometrie um Löcher am Rand zu "stopfen"

	private final float generalScale = 1f; // Skaliert die gesamte ClipMap um
												// Faktor

	/** Konstruktor Erstellt eine ClipMap aus den gegebenen Parametern
	 * 
	 * @param size Größe der ClipMap (Anzahl der Kästchen)
	 * @param stage Anzahl der Auflösungslevel
	 * @param program Dazugehöriges Shaderprogram
	 * @param cam Kamera des Programms */
	public ClipMap(int size, int stage, ShaderProgram program, Camera cam)
	{

		if ((size + 2) % 2 != 0)
			throw new IllegalArgumentException("(size+2) muss Zweierpotenz sein!");

		// Größen der ClipMap
		this.size = size;
		this.stage = stage;
		this.gridsize = size / 4;
		this.middlesize = size % 4;
		this.lsize = 2 * gridsize + middlesize + 1;

		// Animationsparameter
		this.program = program;
		this.cam = cam;
		translation = new Matrix4f();
		movement = new int[stage][2];
		alignment = new boolean[stage][4];
		scaleFaktor = 1;
		scaleSteps = 0;
		for (int i = 0; i < alignment.length; i++)
		{
			alignment[i][0] = false;
			alignment[i][1] = true;
			alignment[i][2] = true;
			alignment[i][3] = false;
		}
		initialCamPos = new Vector2f();
		initialCamPos.x = cam.getCamPos().x;
		initialCamPos.y = cam.getCamPos().z;
		// Initialisierung der vorgeladenen Geometrien
		mxm = GeometryFactory.createGrid(gridsize + 1, gridsize + 1);
		mxn = GeometryFactory.createMxNGrid(middlesize + 1, gridsize + 1);
		nxm = GeometryFactory.createMxNGrid(gridsize + 1, middlesize + 1);
		topLeft = GeometryFactory.createTopLeft(lsize);
		topRight = GeometryFactory.createTopRight(lsize);
		bottomLeft = GeometryFactory.createBottomLeft(lsize);
		bottomRight = GeometryFactory.createBottomRight(lsize);
		center = GeometryFactory.createGrid(4 * gridsize + middlesize + middlesize / 2, 4 * gridsize + middlesize
				+ middlesize / 2);
		outer = GeometryFactory.outerTriangle(size + 1);

		// Anpassung der Höhe und Texturkoordinaten
		updateSize();
		updateHeightScale();
		adjustCamera();
	}

	private void adjustCamera()
	{
		int camPosX = Math.abs((int) (cam.getCamPos().x / generalScale / 2));
		int camPosZ = Math.abs((int) (cam.getCamPos().z / generalScale / 2));
		int dirX = (int) cam.getCamPos().x < 0 ? 2 : 0;
		int dirZ = (int) cam.getCamPos().z < 0 ? 3 : 1;
		for (int i = 0; i <= camPosX; i++)
		{
			moveClip(0, dirX);
		}
		for (int i = 0; i <= camPosZ; i++)
		{
			moveClip(0, dirZ);
		}
	}

	/** Updatet des Shaderprogramm mit der neuen Translationsmatrix */
	private void setProgram()
	{
		this.program.setUniform("translation", translation);
	}

	/** Legt den Scalefaktor des aktuellen Auflösungslevels fest
	 * 
	 * @param scale Skalierungsfaktor 2er Potenz */

	private void setScale(float scale)
	{
		this.program.setUniform("scale", Util.scale(scale * generalScale, null));
	}

	/** Zeichnet die Geometrie eines ClipMap "Rings" aus den vorgeladenen
	 * Geometrien. Nach Vorlage von
	 * http://research.microsoft.com/en-us/um/people/hoppe/gpugcm.pdf (S.33f.)
	 * 
	 * @param i Level des gezeichneten Rings */
	private void createClip(int i)
	{
		// 1
		Util.mul(translation, Util.translationX(size / 2 - gridsize - middlesize / 2 + movement[i][0], null),
				Util.translationZ(size / 2 - gridsize + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 2
		Util.mul(translation, Util.translationX(size / 2 - 2 * gridsize - middlesize / 2 + movement[i][0], null),
				Util.translationZ(size / 2 - gridsize + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 3
		Util.mul(translation, Util.translationX(-size / 2 + gridsize - middlesize / 2 + movement[i][0], null),
				Util.translationZ(size / 2 - gridsize + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 4
		Util.mul(translation, Util.translationX(-size / 2 - middlesize / 2 + movement[i][0], null),
				Util.translationZ(size / 2 - gridsize + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 5
		Util.mul(translation, Util.translationX(size / 2 - gridsize - middlesize / 2 + movement[i][0], null),
				Util.translationZ(size / 2 - 2 * gridsize + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 6
		Util.mul(translation, Util.translationX(-size / 2 - middlesize / 2 + movement[i][0], null),
				Util.translationZ(size / 2 - 2 * gridsize + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 7
		Util.mul(translation, Util.translationX(size / 2 - gridsize - middlesize / 2 + movement[i][0], null),
				Util.translationZ(-size / 2 + gridsize + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 8
		Util.mul(translation, Util.translationX(-size / 2 - middlesize / 2 + movement[i][0], null),
				Util.translationZ(-size / 2 + gridsize + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 9
		Util.mul(translation, Util.translationX(size / 2 - gridsize - middlesize / 2 + movement[i][0], null),
				Util.translationZ(-size / 2 + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 10
		Util.mul(translation,
				Util.translationX(size / 2 - gridsize - gridsize - middlesize / 2 + movement[i][0], null),
				Util.translationZ(-size / 2 + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 11
		Util.mul(translation, Util.translationX(-size / 2 + gridsize - middlesize / 2 + movement[i][0], null),
				Util.translationZ(-size / 2 + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 12
		Util.mul(translation, Util.translationX(-size / 2 - middlesize / 2 + movement[i][0], null),
				Util.translationZ(-size / 2 + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// Oben
		Util.mul(translation, Util.translationX(-middlesize + movement[i][0], null),
				Util.translationZ(size / 2 - gridsize + middlesize / 2 + movement[i][1], null));
		setProgram();
		nxm.draw();

		// Unten
		Util.mul(translation, Util.translationX(-middlesize + movement[i][0], null),
				Util.translationZ(-size / 2 + middlesize / 2 + movement[i][1], null));
		setProgram();
		nxm.draw();

		// Links
		Util.mul(translation, Util.translationX(size / 2 - gridsize - middlesize / 2 + movement[i][0], null),
				Util.translationZ(movement[i][1], null));
		setProgram();
		mxn.draw();

		// Rechts
		Util.mul(translation, Util.translationX(-size / 2 - middlesize / 2 + movement[i][0], null),
				Util.translationZ(movement[i][1], null));
		setProgram();
		mxn.draw();

		// Outer Triangles
		Util.mul(translation, Util.translationX(-size / 2 - middlesize / 2 + movement[i][0], null),
				Util.translationZ(movement[i][1] - 2 * gridsize, null));
		setProgram();
		outer.draw();

	}

	/** Generiert die Clip Map **/
	public void generateMaps()
	{

		// Zähle Floats hoch, bis Schwellenwert erreicht ist
		tempX += cam.getAlt().x / generalScale / scaleFaktor;
//		tempY += cam.getAlt().y;
		tempZ += cam.getAlt().z / generalScale / scaleFaktor;

//		if (tempY > 10)
//		{
//			updateHeight(true);
//			tempY %= 10;
//		}
//		if (tempY < -10)
//		{
//			updateHeight(false);
//			tempY %= 10;
//		}

		// Positiv Z --- Nach Vorn
		if (tempZ > 2)
		{
//			TerrainView.updateTerrainView();
			moveClip(0, 1);
			tempZ %= 2;
		}
		// Positiv X --- Nach Links
		if (tempX > 2)
		{
//			TerrainView.updateTerrainView();
			moveClip(0, 0);
			tempX %= 2;
		}
		// Negativ Z --- Nach Hinten
		if (tempZ < -2)
		{
//			TerrainView.updateTerrainView();
			moveClip(0, 3);
			tempZ %= 2;
		}
		// Negativ X --- Nach Rechts
		if (tempX < -2)
		{
//			TerrainView.updateTerrainView();
			moveClip(0, 2);
			tempX %= 2;
		}

		for (int i = 0; i < stage; i++)
		{
			if (i == 0) // Wenn Stage == 0, schreibe innerstes Grid
			{
				Util.mul(translation,
						Util.translationX(2 * (-gridsize - middlesize) + middlesize + movement[0][0], null),
						Util.translationZ(-2 * gridsize + movement[0][1], null));
				setScale(scaleFaktor);
				setProgram();
				center.draw();
				outer.draw();
			} else
			// Zeichne ClipMap Ring
			{
				setScale(scaleFaktor * (float) pow(i));
				createClip(i);
				setLGrid(i);
			}
		}
	}

	private void updateHeight(boolean mode)
	{
		if (mode)
		{
			if (scaleSteps < 3)
			{
				this.stage--;
				scaleFaktor *= 2;
				scaleSteps++;
				moveClipBy(movement[0][0] / -4, movement[0][1] / -4);
				System.out.println("Größe Movement" + movement[0][0] + " bei Auflösungslevel " + scaleSteps);
			}
		} else
		{
			if (scaleSteps > 0)
			{
				this.stage++;
				scaleFaktor /= 2;
				scaleSteps--;
				moveClipBy(movement[0][0] / 2, movement[0][1] / 2);
				System.out.println("Größe Movement" + movement[0][0] + " bei Auflösungslevel " + scaleSteps);
			}
		}

	}

	/** Verschiebt die ClipMap abhängig vom Kamerastandpunkt
	 * 
	 * @param i Ebene der aktuellen ClipMap
	 * @param dir Richtung in die geschoben werden soll */
	public void moveClip(int i, int dir)
	{
		if (dir == 0 || dir == 1) // Unterscheidung der Bewegungsrichtung: pos X
									// & pos Y
		{
			if (i == stage - 1) // Abbruchbedingung wenn der äußerste
								// ClipMapRing erreicht ist
			{
				movement[i][dir] += 2;
			} else
			{
				if (alignment[i][dir]) // Wenn noch "Platz" zum bewegen ist
				{
					movement[i][dir] += 2;
					alignment[i][dir] ^= true;
					alignment[i][dir + 2] ^= true;
				} else
				// Ansonsten schiebe rekursiv alle ClipMapRinge in die
				// Bewegungsrichtung bis Platz ist oder äußerster Ring erreicht
				{
					alignment[i][dir] ^= true;
					alignment[i][dir + 2] ^= true;
					movement[i][dir] += 2;
					moveClip(i + 1, dir);
				}
			}
		} else
		{
			if (dir == 2 || dir == 3) // Analog für neg X & neg Y
			{
				if (i == stage - 1)
				{
					movement[i][dir - 2] -= 2;
				} else
				{
					if (alignment[i][dir])
					{
						movement[i][dir - 2] -= 2;
						alignment[i][dir] ^= true;
						alignment[i][dir - 2] ^= true;
					} else
					{
						alignment[i][dir] ^= true;
						alignment[i][dir - 2] ^= true;
						movement[i][dir - 2] -= 2;
						moveClip(i + 1, dir);
					}
				}
			}
		}
	}

	/** Erzeugt die für eine ClipMap benötigten L-Geometrien und zeichnet diese
	 * lageabhängig
	 * 
	 * @param i ClipMap Ebene */
	private void setLGrid(int i)
	{

		int side = 0; // Lage der L Geometrien, abhängig von der Lage bestimmen
		if (alignment[i - 1][0] && alignment[i - 1][1])
		{
			side = 2;
		} else if (alignment[i - 1][1] && alignment[i - 1][2])
		{
			side = 1;
		} else if (alignment[i - 1][2] && alignment[i - 1][3])
		{
			side = 4;
		} else if (alignment[i - 1][3] && alignment[i - 1][0])
		{
			side = 3;
		} else
			throw new IllegalStateException("L Grid kann nicht gesetzt werden");
		// 1 = TopRight
		// 2 = TopLeft
		// 3 = BottomLeft
		// 4 = BottomRight
		switch (side)
		{
		case 0:
			break;
		case 1:
			Util.mul(translation,
					Util.translationX(size / 2 - (gridsize + 1) - 2 * gridsize - middlesize + movement[i][0], null),
					Util.translationZ(movement[i][1] - gridsize, null));
			setProgram();
			topRight.draw();
			break;
		case 2:
			Util.mul(translation, Util.translationX(size / 2 - (gridsize + 1) - 1 + movement[i][0], null),
					Util.translationZ(movement[i][1] - gridsize, null));
			setProgram();
			topLeft.draw();
			break;
		case 3:
			Util.mul(translation, Util.translationX(size / 2 - (gridsize + 1) - 1 + movement[i][0], null),
					Util.translationZ(movement[i][1] - gridsize, null));
			setProgram();
			bottomLeft.draw();
			break;
		case 4:
			Util.mul(translation,
					Util.translationX(size / 2 - (gridsize + 1) - 2 * gridsize - middlesize + movement[i][0], null),
					Util.translationZ(movement[i][1] - gridsize, null));
			setProgram();
			bottomRight.draw();
			break;
		}
	}

	/** Bewegt die ClipMap um die angegebenen Parameter in X und Z Richtung
	 * @param x Bewegung in X
	 * @param z Bewegung in Z */
	public void moveClipBy(int x, int z)
	{
		int dirX = x < 0 ? 2 : 0;
		int dirZ = z < 0 ? 3 : 1;

		for (int i = 0; i < Math.abs(x); i++)
		{
			moveClip(0, dirX);
		}
		for (int i = 0; i < Math.abs(z); i++)
		{
			moveClip(0, dirZ);
		}
	}

	/** Setzt die Welttexturkoordinaten */
	private void updateSize()
	{
		program.setFloat("worldSize", (float) (pow(stage - 1) * (size)) * generalScale);
	}

	/** Skaliert die Höhe passend der gewählten Gridgröße */
	private void updateHeightScale()
	{
		program.setFloat("heightScale", (float) ((stage * (size + 2) / 16) + 12f));
	}

	/** Unglaublich performante Wundermethode
	 * 
	 * @param expo 2^expo
	 * @return 2^expo */
	private int pow(int expo)
	{
		int result = 1;
		return result << expo;
	}
}
