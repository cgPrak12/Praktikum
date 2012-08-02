package terrain;

import org.lwjgl.util.vector.Matrix4f;

import util.Camera;
import util.Geometry;
import util.GeometryFactory;
import util.ShaderProgram;
import util.Util;


/** Clip Map
 * @author Christoph, Michael
 */

@SuppressWarnings("unused")
public class ClipMap {
	
	
	//ClipMap Size
	private int stage;
	private int gridsize;
	private int middlesize;
	private int lsize;
	private float size;
	
	//Shader Updates
	private ShaderProgram program;
	private Matrix4f translation;

	//Animation Params
	private int[][] movement;
	private float[][] temp;
	private Camera cam;
	//Cached Geometries
	private Geometry mxm;
	private Geometry mxn;
	private Geometry nxm;
	private Geometry topLeft;
	private Geometry topRight;
	private Geometry bottomLeft;
	private Geometry botomRight;
	private Geometry center;
	
	
	/** Erstellt eine ClipMap aus den gegebenen Parametern
	 * 
	 * @param size		Größe der ClipMap (Anzahl der Kästchen)
	 * @param stage 	Anzahl der Auflösungslevel
	 * @param program	Dazugehöriges Shaderprogram
	 * @param cam		Kamera des Programms
	 */
	public ClipMap(int size, int stage, ShaderProgram program, Camera cam) {
		this.gridsize = size / 4;
		this.middlesize = size % 4;
		this.lsize = size / 2 + 2;
		this.stage = stage;
		this.program = program;
		this.size = size;
		this.cam = cam;
		movement = new int[stage][2];
		temp = new float[stage][2];
		
		// Initialisierung der vorgeladenen Geometrien
		mxm = GeometryFactory.createGridTex(gridsize + 1, gridsize + 1);
		nxm = GeometryFactory.createMxNGrid(middlesize + 1, gridsize + 1);
		mxn = GeometryFactory.createMxNGrid(gridsize + 1, middlesize + 1);
		topLeft = GeometryFactory.createL(lsize, 2);
		topRight = GeometryFactory.createL(lsize, 3);
		bottomLeft = GeometryFactory.createL(lsize, 1);
		botomRight = GeometryFactory.createL(lsize, 0);
		center = GeometryFactory.createGrid(2 * (2 * gridsize + middlesize),
				2 * (2 * gridsize + middlesize));
	}
	
	
	/**
	 * Updatet des Shaderprogramm mit der neuen Translationsmatrix
	 */
	public void setProgram() {
		this.program.setUniform("translation", translation);
	}
	
	/** 
	 * Legt den Scalefaktor des aktuellen Auflösungslevels fest
	 * @param scale Skalierungsfaktor 2er Potenz
	 */
	
	public void setScale(float scale) {
		this.program.setUniform("scale", Util.scale(scale, null));
	}
	
	/**  
	 * Zeichnet die Geometrie eines ClipMap "Rings" aus den vorgeladenen Geometrien.
	 * Nach Vorlage von http://research.microsoft.com/en-us/um/people/hoppe/gpugcm.pdf (S.33f.)
	 * @param i Level des gezeichneten Rings
	 */
	public void createClip(int i) {

		// 1
		Util.mul(translation,
				Util.translationX(size / 2 - gridsize - middlesize / 2 + movement[i][0], null),
				Util.translationZ(size / 2 - gridsize + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 2
		Util.mul(translation, Util.translationX(size / 2 - 2 * gridsize
				- middlesize / 2 + movement[i][0], null), Util.translationZ(size / 2 - gridsize
				+ middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 3
		Util.mul(translation,
				Util.translationX(-size / 2 + gridsize - middlesize / 2 + movement[i][0], null),
				Util.translationZ(size / 2 - gridsize + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 4
		Util.mul(translation,
				Util.translationX(-size / 2 - middlesize / 2 + movement[i][0], null),
				Util.translationZ(size / 2 - gridsize + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 5
		Util.mul(translation, Util.translationX(size / 2 - gridsize
				- middlesize / 2 + movement[i][0], null), Util.translationZ(size / 2 - 2
				* gridsize + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 6
		Util.mul(translation, Util.translationX(-size / 2 - middlesize / 2 + movement[i][0],
				null), Util.translationZ(size / 2 - 2 * gridsize + middlesize
				/ 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 7
		Util.mul(translation,
				Util.translationX(size / 2 - gridsize - middlesize / 2 + movement[i][0], null),
				Util.translationZ(-size / 2 + gridsize + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 8
		Util.mul(translation,
				Util.translationX(-size / 2 - middlesize / 2 + movement[i][0], null),
				Util.translationZ(-size / 2 + gridsize + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 9
		Util.mul(translation,
				Util.translationX(size / 2 - gridsize - middlesize / 2 + movement[i][0], null),
				Util.translationZ(-size / 2 + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 10
		Util.mul(
				translation,
				Util.translationX(size / 2 - gridsize - gridsize - middlesize
						/ 2 + movement[i][0], null),
				Util.translationZ(-size / 2 + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 11
		Util.mul(translation,
				Util.translationX(-size / 2 + gridsize - middlesize / 2 + movement[i][0], null),
				Util.translationZ(-size / 2 + middlesize / 2 + movement[i][1], null));
		setProgram();
		mxm.draw();

		// 12
		Util.mul(translation,
				Util.translationX(-size / 2 - middlesize / 2 + movement[i][0], null),
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
		Util.mul(translation ,Util.translationX(-size / 2 - middlesize / 2 + movement[i][0], null),
				Util.translationZ(movement[i][1], null));
		setProgram();
		mxn.draw();
	}

	/**
	 * Generiert die Clip Map
	 */
	public void generateMaps() {
		translation = new Matrix4f();
		setScale(1);
		temp[0][1] += cam.getAlt().z;
		temp[0][0] += cam.getAlt().x;
		if(temp[0][1] > 1){ movement[0][1] += 1; temp[0][1] = 0;}
		if(temp[0][0] > 1){ movement[0][0] += 1; temp[0][0] = 0;}
		if(temp[0][1] < -1){ movement[0][1] -= 1; temp[0][1] = 0;}
		if(temp[0][0] < -1){ movement[0][0] -= 1; temp[0][0] = 0;}
		Util.mul(
				translation,
				Util.translationX(2 * (-gridsize - middlesize) + middlesize / 2
						+ movement[0][0], null),
				Util.translationZ(-2 * gridsize + movement[0][1], null));
		setProgram();
		center.draw();
		
		for (int i = 1; i < stage; i++) {
			setScale((float) Math.pow(2, i));
			getMovement(i);
			createClip(i);
		}

	}
	
	/**Errechnet die Geschwindigkeit des aktuellen ClipMapRings anhand der Ebene
	 * 
	 * @param i aktuelle Auflösungsebene
	 */
	public void getMovement(int i) {
		temp[i][1] += cam.getAlt().z * Math.pow(2, -i);
		temp[i][0] += cam.getAlt().x * Math.pow(2, -i);
		if(temp[i][1] > 1){ movement[i][1] += 1; temp[i][1] = 0;}
		if(temp[i][0] > 1){ movement[i][0] += 1; temp[i][0] = 0;}
		if(temp[i][1] < -1){ movement[i][1] -= 1; temp[i][1] = 0;}
		if(temp[i][0] < -1){ movement[i][0] -= 1; temp[i][0] = 0;}
	}

         

}
