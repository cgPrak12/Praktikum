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
public class ClipMap {

	// ClipMap Size
	private int stage;
	private int gridsize;
	private int middlesize;
	private int lsize;
	private float size;

	// Shader Updates
	private ShaderProgram program;
	private Matrix4f translation;

	// Animation Params
	private int[][] movement;
	private float[][] temp;
	float tempX;
	float tempZ;
	private boolean[][] alignment;
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
	private final float generalScale = 1f; // Skaliert die gesamte ClipMap um

	// Faktor

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
	public ClipMap(int size, int stage, ShaderProgram program, Camera cam) {
		if ((size + 2) % 2 != 0)
			throw new IllegalArgumentException(
					"(size+2) muss Zweierpotenz sein!");
		this.gridsize = size / 4;
		this.middlesize = size % 4;
		// this.lsize = size / 2 + 2;
		this.lsize = 2 * gridsize + middlesize + 1;
		this.stage = stage;
		this.program = program;
		this.size = size;
		this.cam = cam;
		movement = new int[stage][2];
		temp = new float[stage][2];
		tempX = 0;
		tempZ = 0;
		alignment = new boolean[stage][4];

		for (int i = 0; i < alignment.length; i++) {
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

		center = GeometryFactory.createGrid(4 * gridsize + middlesize
				+ middlesize / 2, 4 * gridsize + middlesize + middlesize / 2);

	}

	/**
	 * Updatet des Shaderprogramm mit der neuen Translationsmatrix
	 */
	public void setProgram() {
		this.program.setUniform("translation", translation);
	}

	/**
	 * Legt den Scalefaktor des aktuellen Aufl�sungslevels fest
	 * 
	 * @param scale
	 *            Skalierungsfaktor 2er Potenz
	 */

	public void setScale(float scale) {
		this.program
				.setUniform("scale", Util.scale(scale * generalScale, null));
	}

	/**
	 * Zeichnet die Geometrie eines ClipMap "Rings" aus den vorgeladenen
	 * Geometrien. Nach Vorlage von
	 * http://research.microsoft.com/en-us/um/people/hoppe/gpugcm.pdf (S.33f.)
	 * 
	 * @param i
	 *            Level des gezeichneten Rings
	 */
	public void createClip(int i) {

		// 1
		Util.mul(
				translation,
				Util.translationX(size / 2 - gridsize - middlesize / 2
						+ movement[i][0], null),
				Util.translationZ(size / 2 - gridsize + middlesize / 2
						+ movement[i][1], null));
		setProgram();
		mxm.draw();

		// 2
		Util.mul(
				translation,
				Util.translationX(size / 2 - 2 * gridsize - middlesize / 2
						+ movement[i][0], null),
				Util.translationZ(size / 2 - gridsize + middlesize / 2
						+ movement[i][1], null));
		setProgram();
		mxm.draw();

		// 3
		Util.mul(
				translation,
				Util.translationX(-size / 2 + gridsize - middlesize / 2
						+ movement[i][0], null),
				Util.translationZ(size / 2 - gridsize + middlesize / 2
						+ movement[i][1], null));
		setProgram();
		mxm.draw();

		// 4
		Util.mul(translation, Util.translationX(-size / 2 - middlesize / 2
				+ movement[i][0], null), Util.translationZ(size / 2 - gridsize
				+ middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 5
		Util.mul(
				translation,
				Util.translationX(size / 2 - gridsize - middlesize / 2
						+ movement[i][0], null),
				Util.translationZ(size / 2 - 2 * gridsize + middlesize / 2
						+ movement[i][1], null));
		setProgram();
		mxm.draw();

		// 6
		Util.mul(translation, Util.translationX(-size / 2 - middlesize / 2
				+ movement[i][0], null), Util.translationZ(size / 2 - 2
				* gridsize + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 7
		Util.mul(
				translation,
				Util.translationX(size / 2 - gridsize - middlesize / 2
						+ movement[i][0], null),
				Util.translationZ(-size / 2 + gridsize + middlesize / 2
						+ movement[i][1], null));
		setProgram();
		mxm.draw();

		// 8
		Util.mul(translation, Util.translationX(-size / 2 - middlesize / 2
				+ movement[i][0], null), Util.translationZ(-size / 2 + gridsize
				+ middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 9
		Util.mul(translation, Util.translationX(size / 2 - gridsize
				- middlesize / 2 + movement[i][0], null), Util.translationZ(
				-size / 2 + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 10
		Util.mul(translation, Util.translationX(size / 2 - gridsize - gridsize
				- middlesize / 2 + movement[i][0], null), Util.translationZ(
				-size / 2 + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 11
		Util.mul(translation, Util.translationX(-size / 2 + gridsize
				- middlesize / 2 + movement[i][0], null), Util.translationZ(
				-size / 2 + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 12
		Util.mul(translation, Util.translationX(-size / 2 - middlesize / 2
				+ movement[i][0], null), Util.translationZ(-size / 2
				+ middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// Oben
		Util.mul(
				translation,
				Util.translationX(-middlesize + movement[i][0], null),
				Util.translationZ(size / 2 - gridsize + middlesize / 2
						+ movement[i][1], null));
		setProgram();
		nxm.draw();

		// Unten
		Util.mul(translation, Util.translationX(-middlesize + movement[i][0],
				null), Util.translationZ(-size / 2 + middlesize / 2
				+ movement[i][1], null));
		setProgram();
		nxm.draw();

		// Links
		Util.mul(
				translation,
				Util.translationX(size / 2 - gridsize - middlesize / 2
						+ movement[i][0], null),
				Util.translationZ(movement[i][1], null));
		setProgram();
		mxn.draw();

		// Rechts
		Util.mul(translation, Util.translationX(-size / 2 - middlesize / 2
				+ movement[i][0], null), Util
				.translationZ(movement[i][1], null));
		setProgram();
		mxn.draw();

	}

	/**
	 * Generiert die Clip Map
	 */
	public void generateMaps() {
		translation = new Matrix4f();
		setScale(1);
		tempX += cam.getAlt().x;
		tempZ += cam.getAlt().z;

		if (tempZ > 2) {
			moveClip(0, 1);

			tempZ = 0;
		}
		// Positiv X --- Nach Links
		if (tempX > 2) {

			moveClip(0, 0);

			tempX = 0;
		}
		if (tempZ < -2) {
			moveClip(0, 3);
			tempZ = 0;
		}
		if (tempX < -2) {

			moveClip(0, 2);

			tempX = 0;
		}

		Util.mul(
				translation,
				Util.translationX(2 * (-gridsize - middlesize) + middlesize
						+ movement[0][0], null),
				Util.translationZ(-2 * gridsize + movement[0][1], null));
		setProgram();
		center.draw();

		for (int i = 1; i < stage; i++) {
			setScale((float) Math.pow(2, i));
			createClip(i);
		}
	}

	public void moveClip(int i, int dir) {
		if (dir == 0 || dir == 1) {
			if (i == stage - 1) {
				movement[i][dir] += 2;
			} else {
				if (alignment[i][dir]) {
					movement[i][dir] += 2;
					alignment[i][dir] ^= true;
					alignment[i][dir + 2] ^= true;
				} else {
					alignment[i][dir] ^= true;
					alignment[i][dir + 2] ^= true;
					movement[i][dir] += 2;
					moveClip(i + 1, dir);
				}
			}
		} else {
			if (dir == 2 || dir == 3) {
				if (i == stage - 1) {
					movement[i][dir - 2] -= 2;
				} else {
					if (alignment[i][dir]) {
						movement[i][dir - 2] -= 2;
						alignment[i][dir] ^= true;
						alignment[i][dir - 2] ^= true;
					} else {
						alignment[i][dir] ^= true;
						alignment[i][dir - 2] ^= true;
						movement[i][dir - 2] -= 2;
						moveClip(i + 1, dir);
					}
				}
			}
		}
	}

	public void setLGrid(int i) {

		int side = 0;
		if (alignment[i][0] && alignment[i][1]) {
			side = 2;
			System.out.println("0 und 1 ist true");
		} else if (alignment[i][1] && alignment[i][2]) {
			side = 1;
			System.out.println("1 und 2 ist true");
		} else if (alignment[i][2] && alignment[i][3]) {
			side = 4;
			System.out.println("2 und 3 ist true");
		} else if (alignment[i][3] && alignment[i][4]) {
			side = 3;
			System.out.println("3 und 4 ist true");
		} else
			throw new IllegalStateException("L Grid kann nicht gesetzt werden");

		switch (side) {
		// 1 = TopRight
		// 2 = TopLeft
		// 3 = BottomLeft
		// 4 = BottomRight
		case 1:
			// TopRight
			Util.mul(
					translation,
					Util.translationX(size / 2 - (gridsize + 1) - 2 * gridsize
							- middlesize + movement[i][0], null),
					Util.translationZ(movement[i][1] - gridsize, null));
			setProgram();
			topRight.draw();
			// TopLeft
		case 2:
			Util.mul(
					translation,
					Util.translationX(size / 2 - (gridsize + 1)
							+ movement[i][0], null),
					Util.translationZ(movement[i][1] - gridsize, null));
			setProgram();
			topLeft.draw();
			// BottomLeft
		case 3:
			Util.mul(
					translation,
					Util.translationX(size / 2 - (gridsize + 1)
							+ movement[i][0], null),
					Util.translationZ(movement[i][1] - gridsize - 1, null));
			setProgram();
			bottomLeft.draw();
			// BottomRight
		case 4:
			Util.mul(
					translation,
					Util.translationX(size / 2 - (gridsize + 1) - 2 * gridsize
							- middlesize + movement[i][0], null),
					Util.translationZ(movement[i][1] - gridsize - 1, null));
			setProgram();
			bottomRight.draw();

		}

	}

}
