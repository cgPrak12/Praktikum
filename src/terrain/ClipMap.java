package terrain;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import util.Camera;
import util.Geometry;
import util.GeometryFactory;
import util.ShaderProgram;
import util.Util;

public class ClipMap {

	private int stage;
	private int gridsize;
	private int middlesize;
	private int lsize;
	private ShaderProgram program;
	private Matrix4f translation;
	private float size;
	private Geometry geo;
	private int[][] movement;
	private Camera cam;
	private float[][] temp;
 
	public ClipMap(int n, int stage, ShaderProgram program, Camera cam) {
		this.gridsize = n / 4;
		this.middlesize = n % 4;
		this.lsize = n / 2 + 2;
		this.stage = stage;
		this.program = program;
		this.size = n;
		this.cam = cam;
		movement = new int[stage][2];
		temp = new float[stage][2];
		
		for(int i: movement[0]) i = 0;
		for(int i: movement[1]) i = 0;
		for(float i: temp[0]) i = 0;
		for(float i: temp[0]) i = 1;
		
	}

	public Geometry createMxMgrid() {
		geo = GeometryFactory.createGridTex(gridsize + 1, gridsize + 1);
		return geo;
	}

	public Geometry createNxMgrid() {
		geo = GeometryFactory.createMxNGrid(middlesize + 1, gridsize + 1);
		return geo;
	}

	public Geometry createMxNgrid() {
		geo = GeometryFactory.createMxNGrid(gridsize + 1, middlesize + 1);
		return geo;
	}

	public Geometry createTopLeft() {
		return GeometryFactory.createL(lsize, 2);
	}

	public Geometry createTopRight() {
		return GeometryFactory.createL(lsize, 3);
	}

	public Geometry createBottomLeft() {
		return GeometryFactory.createL(lsize, 1);
	}

	public Geometry createBottomRight() {
		return GeometryFactory.createL(lsize, 0);
	}

	public void setProgram() {
		this.program.setUniform("translation", translation);
	}

	public void setScale(float scale) {
		this.program.setUniform("scale", Util.scale(scale, null));
	}

	public void createClip(int i) {

		// 1
		geo = createMxMgrid();
		Util.mul(translation,
				Util.translationX(size / 2 - gridsize - middlesize / 2 + movement[i][0], null),
				Util.translationZ(size / 2 - gridsize + middlesize / 2 + movement[i][1], null));
		setProgram();
		geo.draw();

		// 2
		Util.mul(translation, Util.translationX(size / 2 - 2 * gridsize
				- middlesize / 2 + movement[i][0], null), Util.translationZ(size / 2 - gridsize
				+ middlesize / 2 + movement[i][1], null));
		setProgram();
		geo.draw();

		// 3
		Util.mul(translation,
				Util.translationX(-size / 2 + gridsize - middlesize / 2 + movement[i][0], null),
				Util.translationZ(size / 2 - gridsize + middlesize / 2 + movement[i][1], null));
		setProgram();
		geo.draw();

		// 4
		Util.mul(translation,
				Util.translationX(-size / 2 - middlesize / 2 + movement[i][0], null),
				Util.translationZ(size / 2 - gridsize + middlesize / 2 + movement[i][1], null));
		setProgram();
		geo.draw();

		// 5
		Util.mul(translation, Util.translationX(size / 2 - gridsize
				- middlesize / 2 + movement[i][0], null), Util.translationZ(size / 2 - 2
				* gridsize + middlesize / 2 + movement[i][1], null));
		setProgram();
		geo.draw();

		// 6
		Util.mul(translation, Util.translationX(-size / 2 - middlesize / 2 + movement[i][0],
				null), Util.translationZ(size / 2 - 2 * gridsize + middlesize
				/ 2 + movement[i][1], null));
		setProgram();
		geo.draw();

		// 7
		Util.mul(translation,
				Util.translationX(size / 2 - gridsize - middlesize / 2 + movement[i][0], null),
				Util.translationZ(-size / 2 + gridsize + middlesize / 2 + movement[i][1], null));
		setProgram();
		geo.draw();

		// 8
		Util.mul(translation,
				Util.translationX(-size / 2 - middlesize / 2 + movement[i][0], null),
				Util.translationZ(-size / 2 + gridsize + middlesize / 2 + movement[i][1], null));
		setProgram();
		geo.draw();

		// 9
		Util.mul(translation,
				Util.translationX(size / 2 - gridsize - middlesize / 2 + movement[i][0], null),
				Util.translationZ(-size / 2 + middlesize / 2 + movement[i][1], null));
		setProgram();
		geo.draw();

		// 10
		Util.mul(
				translation,
				Util.translationX(size / 2 - gridsize - gridsize - middlesize
						/ 2 + movement[i][0], null),
				Util.translationZ(-size / 2 + middlesize / 2 + movement[i][1], null));
		setProgram();
		geo.draw();

		// 11
		Util.mul(translation,
				Util.translationX(-size / 2 + gridsize - middlesize / 2 + movement[i][0], null),
				Util.translationZ(-size / 2 + middlesize / 2 + movement[i][1], null));
		setProgram();
		geo.draw();

		// 12
		Util.mul(translation,
				Util.translationX(-size / 2 - middlesize / 2 + movement[i][0], null),
				Util.translationZ(-size / 2 + middlesize / 2 + movement[i][1], null));
		setProgram();
		geo.draw();

		geo = createNxMgrid();

		// Oben
		Util.mul(translation, Util.translationX(-middlesize + movement[i][0], null),
				Util.translationZ(size / 2 - gridsize + middlesize / 2 + movement[i][1], null));
		setProgram();
		geo.draw();

		// Unten
		Util.mul(translation, Util.translationX(-middlesize + movement[i][0], null),
				Util.translationZ(-size / 2 + middlesize / 2 + movement[i][1], null));
		setProgram();
		geo.draw();

		geo = createMxNgrid();

		// Links
		Util.translationX(size / 2 - gridsize - middlesize / 2 + movement[i][0], translation);
		setProgram();
		geo.draw();

		// Rechts
		Util.translationX(-size / 2 - middlesize / 2 + movement[i][0], translation);
		setProgram();
		geo.draw();
	}

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
		geo = GeometryFactory.createGrid(2 * (2 * gridsize + middlesize),
				2 * (2 * gridsize + middlesize));
		geo.draw();
		
		for (int i = 1; i < stage; i++) {
			setScale((float) Math.pow(2, i));
			getMovement(i);
			createClip(i);
		}

	}

	public void getMovement(int i) {
		temp[i][1] += cam.getAlt().z * Math.pow(2, -i);
		temp[i][0] += cam.getAlt().x * Math.pow(2, -i);
		if(temp[i][1] > 1){ movement[i][1] += 1; temp[i][1] = 0;}
		if(temp[i][0] > 1){ movement[i][0] += 1; temp[i][0] = 0;}
		if(temp[i][1] < -1){ movement[i][1] -= 1; temp[i][1] = 0;}
		if(temp[i][0] < -1){ movement[i][0] -= 1; temp[i][0] = 0;}
	}

         

}
