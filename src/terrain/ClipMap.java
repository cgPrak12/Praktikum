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

@SuppressWarnings("unused")
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
	private int[] pq;
	private Camera cam;
	private float correctionX;
	private float correctionZ;

	// Cached Geometries
	private Geometry mxm;
	private Geometry mxn;
	private Geometry nxm;
	private Geometry topLeft;
	private Geometry topRight;
	private Geometry bottomLeft;
	private Geometry bottomRight;
	private Geometry center;
	private final float generalScale = 1f; // Skaliert die gesamte ClipMap um
	private boolean update;

	// Faktor

	/**
	 * Erstellt eine ClipMap aus den gegebenen Parametern
	 * 
	 * @param size
	 *            Größe der ClipMap (Anzahl der Kästchen)
	 * @param stage
	 *            Anzahl der Auflösungslevel
	 * @param program
	 *            Dazugehöriges Shaderprogram
	 * @param cam
	 *            Kamera des Programms
	 */
	public ClipMap(int size, int stage, ShaderProgram program, Camera cam) {
		if ((size + 2) % 2 != 0)
			throw new IllegalArgumentException(
					"(size+2) muss Zweierpotenz sein!");
		this.gridsize = size / 4;
		this.middlesize = size % 4;
		this.lsize = size / 2 + 2;
		this.stage = stage;
		this.program = program;
		this.size = size;
		this.cam = cam;
		movement = new int[stage][2];
		temp = new float[stage][2];
		pq = new int[stage];
		for (int i=0; i<pq.length;i++) pq[i] = 2;
		correctionX = 0;
		correctionZ = 0;

		// Initialisierung der vorgeladenen Geometrien
		mxm = GeometryFactory.createGridTex(gridsize + 1, gridsize + 1);

		mxn = GeometryFactory.createMxNGrid(middlesize + 1, gridsize + 1);
		nxm = GeometryFactory.createMxNGrid(gridsize + 1, middlesize + 1);
		bottomLeft = GeometryFactory.createTopLeft(lsize);
		topLeft = GeometryFactory.createTopRight(lsize);
		topRight = GeometryFactory.createBottomLeft(lsize);
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
	 * Legt den Scalefaktor des aktuellen Auflösungslevels fest
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
						+ movement[i][0] + correctionX, null),
				Util.translationZ(size / 2 - gridsize + middlesize / 2
						+ movement[i][1] + correctionZ, null));
		setProgram();
		mxm.draw();

		// 2
		Util.mul(
				translation,
				Util.translationX(size / 2 - 2 * gridsize - middlesize / 2
						+ movement[i][0] + correctionX, null),
				Util.translationZ(size / 2 - gridsize + middlesize / 2
						+ movement[i][1] + correctionZ, null));
		setProgram();
		mxm.draw();

		// 3
		Util.mul(
				translation,
				Util.translationX(-size / 2 + gridsize - middlesize / 2
						+ movement[i][0] + correctionX, null),
				Util.translationZ(size / 2 - gridsize + middlesize / 2
						+ movement[i][1] + correctionZ, null));
		setProgram();
		mxm.draw();

		// 4
		Util.mul(
				translation,
				Util.translationX(-size / 2 - middlesize / 2 + movement[i][0]
						+ correctionX, null),
				Util.translationZ(size / 2 - gridsize + middlesize / 2
						+ movement[i][1] + correctionZ, null));
		setProgram();
		mxm.draw();

		// 5
		Util.mul(
				translation,
				Util.translationX(size / 2 - gridsize - middlesize / 2
						+ movement[i][0] + correctionX, null),
				Util.translationZ(size / 2 - 2 * gridsize + middlesize / 2
						+ movement[i][1] + correctionZ, null));
		setProgram();
		mxm.draw();

		// 6
		Util.mul(
				translation,
				Util.translationX(-size / 2 - middlesize / 2 + movement[i][0]
						+ correctionX, null),
				Util.translationZ(size / 2 - 2 * gridsize + middlesize / 2
						+ movement[i][1] + correctionZ, null));
		setProgram();
		mxm.draw();

		// 7
		Util.mul(
				translation,
				Util.translationX(size / 2 - gridsize - middlesize / 2
						+ movement[i][0] + correctionX, null),
				Util.translationZ(-size / 2 + gridsize + middlesize / 2
						+ movement[i][1] + correctionZ, null));
		setProgram();
		mxm.draw();

		// 8
		Util.mul(
				translation,
				Util.translationX(-size / 2 - middlesize / 2 + movement[i][0]
						+ correctionX, null),
				Util.translationZ(-size / 2 + gridsize + middlesize / 2
						+ movement[i][1] + correctionZ, null));
		setProgram();
		mxm.draw();

		// 9
		Util.mul(
				translation,
				Util.translationX(size / 2 - gridsize - middlesize / 2
						+ movement[i][0] + correctionX, null),
				Util.translationZ(-size / 2 + middlesize / 2 + movement[i][1]
						+ correctionZ, null));
		setProgram();
		mxm.draw();

		// 10
		Util.mul(
				translation,
				Util.translationX(size / 2 - gridsize - gridsize - middlesize
						/ 2 + movement[i][0] + correctionX, null),
				Util.translationZ(-size / 2 + middlesize / 2 + movement[i][1]
						+ correctionZ, null));
		setProgram();
		mxm.draw();

		// 11
		Util.mul(
				translation,
				Util.translationX(-size / 2 + gridsize - middlesize / 2
						+ movement[i][0] + correctionX, null),
				Util.translationZ(-size / 2 + middlesize / 2 + movement[i][1]
						+ correctionZ, null));
		setProgram();
		mxm.draw();

		// 12
		Util.mul(
				translation,
				Util.translationX(-size / 2 - middlesize / 2 + movement[i][0]
						+ correctionX, null),
				Util.translationZ(-size / 2 + middlesize / 2 + movement[i][1]
						+ correctionZ, null));
		setProgram();
		mxm.draw();

		// Oben
		Util.mul(translation, Util.translationX(-middlesize + movement[i][0]
				+ correctionX, null), Util.translationZ(size / 2 - gridsize
				+ middlesize / 2 + movement[i][1] + correctionZ, null));
		setProgram();
		nxm.draw();

		// Unten
		Util.mul(translation, Util.translationX(-middlesize + movement[i][0]
				+ correctionX, null), Util.translationZ(-size / 2 + middlesize
				/ 2 + movement[i][1] + correctionZ, null));
		setProgram();
		nxm.draw();

		// Links
		Util.mul(
				translation,
				Util.translationX(size / 2 - gridsize - middlesize / 2
						+ movement[i][0] + correctionX, null),
				Util.translationZ(movement[i][1] + correctionZ, null));
		setProgram();
		mxn.draw();

		// Rechts
		Util.mul(
				translation,
				Util.translationX(-size / 2 - middlesize / 2 + movement[i][0]
						+ correctionX, null),
				Util.translationZ(movement[i][1] + correctionZ, null));
		setProgram();
		mxn.draw();
	}

	/**
	 * Generiert die Clip Map
	 */
	public void generateMaps() {
		translation = new Matrix4f();
		setScale(1);
		System.out.println(temp[0][0]);
		if (temp[0][1]  > 2) {
			movement[0][1] += 2;
			switch(pq[0]){
			case 1: pq[0] = 2; update = true; break;
			case 2: pq[0] = 1; break;
			case 3: pq[0] = 4; break;
			case 4: pq[0] = 3; update = true; break;
			default: update=true; break;
			}
			temp[0][1] = 0;
		}
		if (temp[0][0] > 2) {
			movement[0][0] += 2;
			switch(pq[0]){
			case 1: pq[0] = 4; update = true;
			case 2: pq[0] = 3; update = true;
			case 3: pq[0] = 2; break;
			case 4: pq[0] = 1; break;
			default: update = true; pq[0] = 3; break;
			}
			temp[0][0] = 0;
		}
		if (temp[0][1] < -2) {
			movement[0][1] -= 2;
			switch(pq[0]){
			case 1: pq[0] = 2; break;
			case 2: pq[0] = 1; update = true; break;
			case 3: pq[0] = 4; update = true; break;
			case 4: pq[0] = 3; break;
			default: update = true; break;
			}
			temp[0][1] = 0;
		}
		if (temp[0][0] < -2) {
			movement[0][0] -= 2;
			switch(pq[0]){
			case 1: pq[0] = 2; break;
			case 2: pq[0] = 3; break;
			case 3: pq[0] = 4; update = true; break;
			case 4: pq[0] = 1; update = true; break;
			default: update = true; break;
			}
			temp[0][0] = 0;
		}
		temp[0][1] += cam.getAlt().z;
		temp[0][0] += cam.getAlt().x;

		Util.mul(translation, Util.translationX(2 * (-gridsize - middlesize)
				+ middlesize + movement[0][0] + correctionX, null), Util
				.translationZ(-2 * gridsize + movement[0][1] + correctionZ,
						null));
		setProgram();
		center.draw();

		for (int i = 1; i < stage; i++) {
			setScale((float) Math.pow(2, i));
			getMovement(i);
			createClip(i);
		}
	}

	/**
	 * Errechnet die Geschwindigkeit des aktuellen ClipMapRings anhand der Ebene
	 * 
	 * @param i
	 *            aktuelle Auflösungsebene
	 */
	public void getMovement(int i) {
		// Positiv Z
		if (temp[i][1] > 2) {
			movement[i][1] += 2;
			switch(pq[i]){
			case 1: pq[i] = 2; update = true; break;
			case 2: pq[i] = 1; break;
			case 3: pq[i] = 4; break;
			case 4: pq[i] = 3; update = true; break;
			default: update=true; break;
			}
			temp[i][1] = 0;
		}
		// Positiv X
		if (temp[i][0] > 2) {
			movement[i][0] += 2;
			switch(pq[i]){
			case 1: pq[i] = 4; update = true; break;
			case 2: pq[i] = 3; update = true; break;
			case 3: pq[i] = 2; break;
			case 4: pq[i] = 1; break;
			default: update = true; break;
			}
			temp[i][0] = 0;
		}
		// Negativ Z
		if (temp[i][1] < -2) {
			movement[i][1] -= 2;
			switch(pq[i]){
			case 1: pq[i] = 2; break;
			case 2: pq[i] = 1; update = true; break;
			case 3: pq[i] = 4; update = true; break;
			case 4: pq[i] = 3; break;
			default: update = true; break;
			}
			temp[i][1] = 0;
		}
		// Negativ X
		if (temp[i][0] < -2) {
			movement[i][0] -= 2;
			switch(pq[i]){
			case 1: pq[i] = 4; break;
			case 2: pq[i] = 3; break;
			case 3: pq[i] = 2; update = true; break;
			case 4: pq[i] = 1; update = true; break;
			default: update = true; break;
			}
			temp[i][0] = 0;
		}
		temp[i][1] += cam.getAlt().z * getUpdate(i);
		temp[i][0] += cam.getAlt().x * getUpdate(i);
	}
	
	private float getUpdate(int stage){
		if(update){
			update = false;
			return 1f;
		}
		else{
			update = false;
			return (float)Math.pow(2, -stage);
		}
	}
}
