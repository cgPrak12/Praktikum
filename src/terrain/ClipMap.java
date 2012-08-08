package terrain;

import org.lwjgl.util.vector.Matrix4f;

import util.Camera;
import util.Geometry;
import util.GeometryFactory;
import util.ShaderProgram;
import util.Util;

/**
 * Clip Map
 * 
 * @author Christoph, Michael
 */

// @SuppressWarnings("unused")
public class ClipMap
{

	// ClipMap Size
	private int stage; // Anzahl der Aufl�sungsebenen
	private int gridsize; // Kantenl�nge der 12 Quadrate
	private int middlesize; // Kantenl�nge der F�llst�cke
	private int lsize; // L�nge der Ls
	private int size; // Kantenl�nge des ClipMapRings

	// Shader Updates
	private ShaderProgram program;
	private Matrix4f translation;

	// Animation Params
	private int[][] movement; // Array das Bewegungstranslation speichert
	private boolean[][] alignment; // Array das Lage der Clipmap angibt
	private float tempX;
	private float tempZ;
//	private float tempY;
	private int scaleFaktor;
	private int countScales;

	private Camera cam;

	// Cached Geometries
	private Geometry mxm;
	private Geometry mxn;
	private Geometry nxm;
	private Geometry topRight;
	private Geometry topLeft;
	private Geometry bottomLeft;
	private Geometry bottomRight;
	private Geometry center;
	private Geometry outer;

	private float generalScale = 0.1f; // Skaliert die gesamte ClipMap um Faktor

	/**
	 * Erstellt eine ClipMap aus den gegebenen Parametern
	 * 
	 * @param size
	 *            Gr��e der ClipMap (Anzahl der K�stchen)
	 * @param stage
	 *            Anzahl der Aufl�sungslevel
	 * @param program
	 *            Dazugeh�riges Shaderprogram
	 * @param cam
	 *            Kamera des Programms
	 */
	public ClipMap(int size, int stage, ShaderProgram program, Camera cam)
	{
		if ((size + 2) % 2 != 0)
			throw new IllegalArgumentException("(size+2) muss Zweierpotenz sein!");

		/*
		 * float camX = cam.getCamPos().x; float camZ = cam.getCamPos().z; float
		 * vertexHeight = terrain[(int) camX][(int) camZ][0];
		 * 
		 * float camHeight = cam.getCamPos().z; float minHeight = vertexHeight;
		 * float maxHeight = cam.getMaxHeight();
		 * 
		 * size = Util.scale(camHeight, minHeight, maxHeight, minSize, maxSize);
		 */

		// Gr��en der ClipMap
		this.size = size;
		this.stage = stage;
		updateGeometries();

		this.program = program;
		this.cam = cam;
		translation = new Matrix4f();

		movement = new int[stage][2];
		scaleFaktor = 1;
		countScales = 0;
		alignment = new boolean[stage][4];

		for (int i = 0; i < alignment.length; i++)
		{
			alignment[i][0] = false;
			alignment[i][1] = true;
			alignment[i][2] = true;
			alignment[i][3] = false;
		}

		// Initialisierung der vorgeladenen Geometrien
		mxm = GeometryFactory.createGrid(gridsize + 1, gridsize + 1);
		mxn = GeometryFactory.createMxNGrid(middlesize + 1, gridsize + 1);
		nxm = GeometryFactory.createMxNGrid(gridsize + 1, middlesize + 1);
		topLeft = GeometryFactory.createTopLeft(lsize);
		topRight = GeometryFactory.createTopRight(lsize);
		bottomLeft = GeometryFactory.createBottomLeft(lsize);
		bottomRight = GeometryFactory.createBottomRight(lsize);
		center = GeometryFactory.createGrid(4 * gridsize + middlesize + middlesize / 2, 4 * gridsize + middlesize + middlesize
				/ 2);
		outer = GeometryFactory.outerTriangle(size + 1);

		updateSize();
		updateHeightScale();

	}

	private void updateGeometries()
	{
		this.gridsize = size / 4;
		this.middlesize = size % 4;
		this.lsize = 2 * gridsize + middlesize + 1;
	}

	/**
	 * Updatet des Shaderprogramm mit der neuen Translationsmatrix
	 */
	private void setProgram()
	{
		this.program.setUniform("translation", translation);
	}

	/**
	 * Legt den Scalefaktor des aktuellen Aufl�sungslevels fest
	 * 
	 * @param scale
	 *            Skalierungsfaktor 2er Potenz
	 */

	private void setScale(float scale)
	{
		this.program.setUniform("scale", Util.scale(scale * generalScale, null));
	}

	/**
	 * Zeichnet die Geometrie eines ClipMap "Rings" aus den vorgeladenen
	 * Geometrien. Nach Vorlage von
	 * http://research.microsoft.com/en-us/um/people/hoppe/gpugcm.pdf (S.33f.)
	 * 
	 * @param i
	 *            Level des gezeichneten Rings
	 */
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
		Util.mul(translation, Util.translationX(size / 2 - gridsize - gridsize - middlesize / 2 + movement[i][0], null),
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

	/**
	 * Generiert die Clip Map
	 */
	public void generateMaps()
	{

		tempX += cam.getAlt().x / generalScale / scaleFaktor;
		tempZ += cam.getAlt().z / generalScale / scaleFaktor;

		// Positiv Z --- Nach Vorn
		if (tempZ > 2)
		{
			moveClip(0, 1);
			tempZ %= 2;
		}
		// Positiv X --- Nach Links
		if (tempX > 2)
		{
			moveClip(0, 0);
			tempX %= 2;
		}
		// Negativ Z --- Nach Hinten
		if (tempZ < -2)
		{
			moveClip(0, 3);
			tempZ %= 2;
		}
		// Negativ X --- Nach Rechts
		if (tempX < -2)
		{
			moveClip(0, 2);
			tempX %= 2;
		}

		// updateHeight();
		System.out.println("Cam Pos " + cam.getCamPos().x);
		System.out.println("Movement0 y " + movement[0][1]);
		System.out.println("Movement1 y " + movement[1][1]);
		System.out.println("Movement2 y " + movement[2][1]);
		System.out.println("Movement3 y " + movement[3][1]);
		System.out.println("Movement4 y " + movement[4][1]);
		System.out.println("Movement5 y " + movement[5][1]);

		for (int i = 0; i < stage; i++)
		{
			if (i == 0)
			{
				Util.mul(translation, Util.translationX(2 * (-gridsize - middlesize) + middlesize + movement[0][0], null),
						Util.translationZ(-2 * gridsize + movement[0][1], null));
				setScale(scaleFaktor);
				setProgram();
				center.draw();
				outer.draw();
			} else
			{
				setScale((float) Math.pow(2, i + countScales));
				createClip(i);
				setLGrid(i);
			}
		}
	}

//	private void updateHeight()
//	{
//		tempY += cam.getAlt().y;
//		System.out.println(cam.getCamPos().y);
//		if (tempY > 20 && stage > 1)
//		{
//			stage--;
//			tempY %= 10;
//			scaleFaktor *= 2;
//			countScales++;
//			for (int i = 0; i < movement.length - 1; i++)
//			{
//				movement[i][0] /= 2;
//				// movement[i][0] += (movement[i][0]%2);
//				movement[i][1] /= 2;
//				// movement[i][1] += (movement[i][1]%2);
//			}
//		}
//		if (tempY <= -20 && cam.getCamPos().y > 5)
//		{
//			stage++;
//			tempY %= 10;
//			scaleFaktor /= 2;
//			countScales--;
//			for (int i = 0; i < movement.length; i++)
//			{
//				movement[i][0] *= 2;
//				movement[i][1] *= 2;
//			}
//			movement[0][0] -= (movement[1][0] % 2);
//			movement[0][1] -= (movement[1][1] % 2);
//		}
//	}

	/**
	 * Verschiebt die ClipMap abh�ngig vom Kamerastandpunkt
	 * 
	 * @param i
	 *            Ebene der aktuellen ClipMap
	 * @param dir
	 *            Richtung in die geschoben werden soll
	 */
	private void moveClip(int i, int dir)
	{
		if (dir == 0 || dir == 1)
		{
			if (i == stage - 1)
			{
				movement[i][dir] += 2;
			} else
			{
				if (alignment[i][dir])
				{
					movement[i][dir] += 2;
					alignment[i][dir] ^= true;
					alignment[i][dir + 2] ^= true;
				} else
				{
					alignment[i][dir] ^= true;
					alignment[i][dir + 2] ^= true;
					movement[i][dir] += 2;
					moveClip(i + 1, dir);
				}
			}
		} else
		{
			if (dir == 2 || dir == 3)
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

	/**
	 * Erzeug die f�r eine ClipMap ben�tigten L-Geometrien und zeichnet diese
	 * lageabh�ngig
	 * 
	 * @param i
	 *            ClipMap Ebene
	 */
	private void setLGrid(int i)
	{

		int side = 0;
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

	private void updateSize()
	{
		program.setFloat("worldSize", (float) (Math.pow(2, stage - 1) * (size)) * generalScale);
	}

	private void updateHeightScale()
	{
		program.setFloat("heightScale", (float) ((stage * (size + 2) / 16) + 12f));

	}
}
