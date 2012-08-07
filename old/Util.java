package util;

import static opengl.GL.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

/**
 *
 * @author Sascha Kolodzey, Nico Marniok
 */
public class Util {
	/**
	 * FloatBuffer, der gross genug fuer eine 4x4 Matrix ist.
	 */
	public static final FloatBuffer MAT_BUFFER = BufferUtils.createFloatBuffer(16);

	/**
	 * PI
	 */
	public static final float PI = (float)Math.PI;

	/**
	 * 1/2 * PI
	 */
	public static final float PI_DIV2 = 0.5f * (float)Math.PI;

	/**
	 * 1/4 * PI
	 */
	public static final float PI_DIV4 = 0.25f * (float)Math.PI;

	/**
	 * 2 * PI
	 */
	public static final float PI_MUL2 = 2.0f * (float)Math.PI;

	/**
	 * Temporaere Matrix fuer einige Methoden.
	 */
	private static final Matrix4f TEMP = new Matrix4f();


	private static float[][] GaussMat3 = new float[3][3];
	private static float[][] GaussMat5 = new float[5][5];
	private static float[][] GaussMat7 = new float[7][7];

	/**
	 * Erzeugt eine Viewmatrix aus Augenposition und Fokuspunkt.
	 * @param eye Die Position des Auges
	 * @param at Anvisierter Punkt
	 * @param up Up Vektor des Auges
	 * @param dst Matrix, in die das Ergebnis gespeichert wird. Wenn null wird
	 * eine neue erstellt.
	 * @return Ergebnismatrix
	 */
	public static Matrix4f lookAtRH(Vector3f eye, Vector3f at, Vector3f up, Matrix4f dst) {
		if(dst == null) dst = new Matrix4f();

		Vector3f viewDir = Vector3f.sub(at, eye, null);
		viewDir.normalise();

		Vector3f side = Vector3f.cross(viewDir, up, null);
		side.normalise();

		Vector3f newUp = Vector3f.cross(side, viewDir, null);
		newUp.normalise();

		dst.m00 = side.x;     dst.m10 = side.y;     dst.m20 = side.z;     dst.m30 = -Vector3f.dot(eye, side);
		dst.m01 = newUp.x;    dst.m11 = newUp.y;    dst.m21 = newUp.z;    dst.m31 = -Vector3f.dot(eye, newUp);
		dst.m02 = -viewDir.x; dst.m12 = -viewDir.y; dst.m22 = -viewDir.z; dst.m32 = Vector3f.dot(eye, viewDir);
		dst.m03 = 0.0f;       dst.m13 = 0.0f;       dst.m23 = 0.0f;       dst.m33 = 1.0f;

		return dst;
	}

	/**
	 * Erzeugt eine perspektivische Projektionsmatrix, die dem zweiten Ansatz
	 * der Vorlesung entspricht. (Vorl. vom 29.05.2012, Folie 16)
	 * @param l -x Wert der Viewpane
	 * @param r +x Wert der Viewpane
	 * @param b -y Wert der Viewpane
	 * @param t +y Wert der Viewpane
	 * @param n -z Wert der Viewpane
	 * @param f +z Wert der Viewpane
	 * @param dst Matrix, in die das Ergebnis gespeichert wird. Wenn null wird
	 * eine neue erstellt.
	 * @return Ergebnismatrix
	 */
	public static Matrix4f frustum(float l, float r, float b, float t, float n, float f, Matrix4f dst) {
		if(dst == null) dst = new Matrix4f();

		dst.m00 = 2.0f*n/(r-l); dst.m10 = 0.0f;         dst.m20 = (r+l)/(r-l);  dst.m30 = 0.0f;
		dst.m01 = 0.0f;         dst.m11 = 2.0f*n/(t-b); dst.m21 = (t+b)/(t-b);  dst.m31 = 0.0f;
		dst.m02 = 0.0f;         dst.m12 = 0.0f;         dst.m22 = -(f+n)/(f-n); dst.m32 = -2.0f*n*f/(f-n);
		dst.m03 = 0.0f;         dst.m13 = 0.0f;         dst.m23 = -1.0f;        dst.m33 = 0.0f;

		return dst;
	}

	/**
	 * Erzeugt die orthogonale Projektionsmatrix, die dem klassichen Ansatz
	 * entspricht.
	 * @param dst Matrix, in die das Ergebnis gespeichert wird. Wenn null wird
	 * eine neue erstellt.
	 * @return Ergebnismatrix
	 */
	public static Matrix4f ortho(Matrix4f dst) {
		if(dst == null) dst = new Matrix4f();
		dst.setIdentity();
		dst.m22 = 0.0f;
		return dst;
	}

	/**
	 * Erzeugt eine orthogonal Projektionsmatrix, die dem zweiten Ansatz der
	 * Vorlesung entspricht. (Vorl. vom 29.05.2012, Folie 10)
	 * @param l minimaler Wert in x-Richtung
	 * @param r maximaler Wert in x-Richtung
	 * @param b minimaler Wert in y-Richtung
	 * @param t maximaler Wert in y-Richtung
	 * @param n minimaler Wert in z-Richtung
	 * @param f maximaler Wert in z-Richtung
	 * @param dst Matrix, in die das Ergebnis gespeichert wird. Wenn null wird
	 * eine neue erstellt.
	 * @return Ergebnismatrix
	 */
	public static Matrix4f ortho(float l, float r, float b, float t, float n, float f, Matrix4f dst) {
		return Util.mul(dst, Util.scale(new Vector3f(2.0f / (r - l), 2.0f / (t - b), -2.0f / (f - n)), null),
				Util.translation(new Vector3f(-0.5f * (r + l), -0.5f * (t + b), 0.5f * (f + n)), null));
	}

	/**
	 * Erzeugt eine Rotationsmatrix um die x-Achse.
	 * @param angle Winkel in Bogenass
	 * @param dst Matrix, in die das Ergebnis gespeichert wird. Wenn null wird
	 * eine neue erstellt.
	 * @return Ergebnismatrix
	 */
	public static Matrix4f rotationX(float angle, Matrix4f dst) {
		if(dst == null) dst = new Matrix4f();
		dst.setIdentity();
		dst.m11 = dst.m22 = (float)Math.cos(angle);
		dst.m21 = -(dst.m12 = (float)Math.sin(angle));
		return dst;
	}

	/**
	 * Erzeugt eine Rotationsmatrix um die y-Achse.
	 * @param angle Winkel in Bogenass
	 * @param dst Matrix, in die das Ergebnis gespeichert wird. Wenn null wird
	 * eine neue erstellt.
	 * @return Ergebnismatrix
	 */
	public static Matrix4f rotationY(float angle, Matrix4f dst) {
		if(dst == null) dst = new Matrix4f();
		dst.setIdentity();

		dst.m00 = dst.m22 = (float)Math.cos(angle);
		dst.m02 = -(dst.m20 = (float)Math.sin(angle));

		return dst;
	}

	/**
	 * Erzeugt eine Rotationsmatrix um die z-Achse.
	 * @param angle Winkel in Bogenass
	 * @param dst Matrix, in die das Ergebnis gespeichert wird. Wenn null wird
	 * eine neue erstellt.
	 * @return Ergebnismatrix
	 */    
	public static Matrix4f rotationZ(float angle, Matrix4f dst) {
		if(dst == null) dst = new Matrix4f();
		dst.setIdentity();

		dst.m00 = dst.m11 = (float)Math.cos(angle);
		dst.m10 = -(dst.m01 = (float)Math.sin(angle));

		return dst;
	}

	/**
	 * Erzeugt eine Translationsmatrix.
	 * @param translation Der Translationsvektor
	 * @param dst Matrix, in die das Ergebnis gespeichert wird. Wenn null wird
	 * eine neue erstellt.
	 * @return Ergebnismatrix
	 */
	public static Matrix4f translation(Vector3f translation, Matrix4f dst) {
		if(dst == null) dst = new Matrix4f();
		dst.setIdentity();

		dst.m30 = translation.x;
		dst.m31 = translation.y;
		dst.m32 = translation.z;

		return dst;
	}

	/**
	 * Erzeugt eine Translationsmatrix in x-Richtung.
	 * @param x Der Translationslaenge
	 * @param dst Matrix, in die das Ergebnis gespeichert wird. Wenn null wird
	 * eine neue erstellt.
	 * @return Ergebnismatrix
	 */
	public static Matrix4f translationX(float x, Matrix4f dst) {
		if(dst == null) dst = new Matrix4f();
		dst.setIdentity();
		dst.m30 = x;
		return dst;
	}

	/**
	 * Erzeugt eine Translationsmatrix in y-Richtung.
	 * @param y Der Translationslaenge
	 * @param dst Matrix, in die das Ergebnis gespeichert wird. Wenn null wird
	 * eine neue erstellt.
	 * @return Ergebnismatrix
	 */
	public static Matrix4f translationY(float y, Matrix4f dst) {
		if(dst == null) dst = new Matrix4f();
		dst.setIdentity();
		dst.m31 = y;
		return dst;
	}

	/**
	 * Erzeugt eine Translationsmatrix in z-Richtung.
	 * @param z Der Translationslaenge
	 * @param dst Matrix, in die das Ergebnis gespeichert wird. Wenn null wird
	 * eine neue erstellt.
	 * @return Ergebnismatrix
	 */
	public static Matrix4f translationZ(float z, Matrix4f dst) {
		if(dst == null) dst = new Matrix4f();
		dst.setIdentity();
		dst.m32 = z;
		return dst;
	}

	/**
	 * Erzeugt eine Skalierungsmatrix.
	 * @param scale Skalierungskomponente
	 * @param dst Matrix, in die das Ergebnis gespeichert wird. Wenn null wird
	 * eine neue erstellt.
	 * @return Ergebnismatrix
	 */
	public static Matrix4f scale(Vector3f scale, Matrix4f dst) {
		if(dst == null) dst = new Matrix4f();
		dst.setIdentity();

		dst.m00 = scale.x;
		dst.m11 = scale.y;
		dst.m22 = scale.z;

		return dst;
	}

	/**
	 * Erzeugt eine gleichmaessige Skalierungsmatrix.
	 * @param scale Skalierungskomponente
	 * @param dst Matrix, in die das Ergebnis gespeichert wird. Wenn null wird
	 * eine neue erstellt.
	 * @return Ergebnismatrix
	 */
	public static Matrix4f scale(float scale, Matrix4f dst) {
		return Util.scale(new Vector3f(scale, scale, scale), dst);
	}

	/**
	 * Transformiert einen Vector3f mittels einer Matrix4f. Der Vektor wird um
	 * die homogene Koordinate 1 erweitert und anschliessend homogenisiert.
	 * @param left Trabsformationsmatrix
	 * @param right Zu transformierender Vektor
	 * @param dst Vektor, in den das Ergebnis gespeichert wird. Wenn null wird
	 * ein neuer erstellt.
	 * @return Ergebnisvektor
	 */
	public static Vector3f transformCoord(Matrix4f left, Vector3f right, Vector3f dst) {
		if(dst == null) dst = new Vector3f();
		Vector4f vec = Matrix4f.transform(left, new Vector4f(right.x, right.y, right.z, 1.0f), null);
		vec.scale(1.0f / vec.w);
		dst.set(vec.x, vec.y, vec.z);
		return dst;
	}

	/**
	 * Transformiert einen Vector3f mittels einer Matrix4f. Der Vektor wird um
	 * die homogene Koordinate 0 erweitert.
	 * @param left Trabsformationsmatrix
	 * @param right Zu transformierender Vektor
	 * @param dst Vektor, in den das Ergebnis gespeichert wird. Wenn null wird
	 * ein neuer erstellt.
	 * @return Ergebnisvektor
	 */
	public static Vector3f transformDir(Matrix4f left, Vector3f right, Vector3f dst) {
		if(dst == null) dst = new Vector3f();
		Vector4f vec = Matrix4f.transform(left, new Vector4f(right.x, right.y, right.z, 0.0f), null);
		dst.set(vec.x, vec.y, vec.z);
		return dst;
	}

	/**
	 * Multipliziert beliebig viele Matrizen miteinander.
	 * @param dst Matrix, in die das Ergebnis gespeichert wird. Wenn null wird
	 * eine neue erstellt.
	 * @param factors Matrizen, die multipliziert werden sollen
	 * @return Ergebnismatrix
	 */
	public static Matrix4f mul(Matrix4f dst, Matrix4f ...factors) {
		TEMP.setIdentity();
		for(Matrix4f mat : factors) {
			Matrix4f.mul(TEMP, mat, TEMP);
		}
		if(dst == null) dst = new Matrix4f();
		return dst.load(TEMP);
	}

	/**
	 * Schneidet einen Wert zurecht.
	 * @param val Wert
	 * @param min minimaler Wert
	 * @param max maximaler Wert
	 * @return Falls val &lt; min, dann min. Falls val &gt; max, dann max. sonst
	 * val.
	 */
	public static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(val, max));
	}

	/**
	 * Schneidet einen Vektor komponentenweise zurecht.
	 * @param val Wert
	 * @param min minimaler Wert
	 * @param max maximaler Wert
	 * @param dst Vektor, in den das Ergebnis gespeichert wird. Wenn null wird
	 * ein neuer erstellt.
	 * @return Ergebnisvektor
	 */
	public static Vector2f clamp(Vector2f val, Vector2f min, Vector2f max, Vector2f dst) {
		if(dst == null) dst = new Vector2f();
		dst.x = clamp(val.x, min.x, max.x);
		dst.y = clamp(val.y, min.y, max.y);
		return dst;
	}

	/**
	 * Schneidet einen Vektor zurecht.
	 * @param val Wert
	 * @param min minimaler Wert
	 * @param max maximaler Wert
	 * @param dst Vektor, in den das Ergebnis gespeichert wird. Wenn null wird
	 * ein neuer erstellt.
	 * @return Ergebnisvektor
	 */    
	public static Vector2f clamp(Vector2f val, float min, float max, Vector2f dst) {
		return clamp(val, new Vector2f(min, min), new Vector2f(max, max), dst);
	}

	/**
	 * Schneidet einen Vektor komponentenweise zurecht.
	 * @param val Wert
	 * @param min minimaler Wert
	 * @param max maximaler Wert
	 * @param dst Vektor, in den das Ergebnis gespeichert wird. Wenn null wird
	 * ein neuer erstellt.
	 * @return Ergebnisvektor
	 */    
	public static Vector3f clamp(Vector3f val, Vector3f min, Vector3f max, Vector3f dst) {
		if(dst == null) dst = new Vector3f();
		dst.x = clamp(val.x, min.x, max.x);
		dst.y = clamp(val.y, min.y, max.y);
		dst.z = clamp(val.z, min.z, max.z);
		return dst;
	}

	/**
	 * Schneidet einen Vektor zurecht.
	 * @param val Wert
	 * @param min minimaler Wert
	 * @param max maximaler Wert
	 * @param dst Vektor, in den das Ergebnis gespeichert wird. Wenn null wird
	 * ein neuer erstellt.
	 * @return Ergebnisvektor
	 */    
	public static Vector3f clamp(Vector3f val, float min, float max, Vector3f dst) {
		return clamp(val, new Vector3f(min, min, min), new Vector3f(max, max, max), dst);
	}

	/**
	 * Schneidet einen Vektor komponentenweise zurecht.
	 * @param val Wert
	 * @param min minimaler Wert
	 * @param max maximaler Wert
	 * @param dst Vektor, in den das Ergebnis gespeichert wird. Wenn null wird
	 * ein neuer erstellt.
	 * @return Ergebnisvektor
	 */    
	public static Vector4f clamp(Vector4f val, Vector4f min, Vector4f max, Vector4f dst) {
		if(dst == null) dst = new Vector4f();
		dst.x = clamp(val.x, min.x, max.x);
		dst.y = clamp(val.y, min.y, max.y);
		dst.z = clamp(val.z, min.z, max.z);
		dst.w = clamp(val.w, min.w, max.w);
		return dst;
	}

	/**
	 * Schneidet einen Vektor zurecht.
	 * @param val Wert
	 * @param min minimaler Wert
	 * @param max maximaler Wert
	 * @param dst Vektor, in den das Ergebnis gespeichert wird. Wenn null wird
	 * ein neuer erstellt.
	 * @return Ergebnisvektor
	 */    
	public static Vector4f clamp(Vector4f val, float min, float max, Vector4f dst) {
		return clamp(val, new Vector4f(min, min, min, min), new Vector4f(max, max, max, max), dst);
	}

	/**
	 * Interpoliert zwischen zwei Werten linear.
	 * @param v0 Erster Wert
	 * @param v1 Zweiter Wert
	 * @param t Mischparameter
	 * @return (1 - t) * v0 + t * v1
	 */
	public static float mix(float v0, float v1, float t) {
		return (1.0f - t) * v0 + t * v1;
	}

	/**
	 * Interpoliert zwischen zwei Vektoren linear.
	 * @param v0 Erster Vektor
	 * @param v1 Zweiter Vektor
	 * @param t Mischparameter
	 * @param dst Vektor, in den das Ergebnis gespeichert wird. Wenn null wird
	 * ein neuer erstellt.
	 * @return Ergebnisvektor
	 */
	public static Vector2f mix(Vector2f v0, Vector2f v1, float t, Vector2f dst) {
		if(dst == null) dst = new Vector2f();
		dst.x = mix(v0.x, v1.x, t);
		dst.y = mix(v0.y, v1.y, t);
		return dst;
	}

	/**
	 * Interpoliert zwischen zwei Vektoren linear.
	 * @param v0 Erster Vektor
	 * @param v1 Zweiter Vektor
	 * @param t Mischparameter
	 * @param dst Vektor, in den das Ergebnis gespeichert wird. Wenn null wird
	 * ein neuer erstellt.
	 * @return Ergebnisvektor
	 */
	public static Vector3f mix(Vector3f v0, Vector3f v1, float t, Vector3f dst) {
		if(dst == null) dst = new Vector3f();
		dst.x = mix(v0.x, v1.x, t);
		dst.y = mix(v0.y, v1.y, t);
		dst.z = mix(v0.z, v1.z, t);
		return dst;
	}

	/**
	 * Interpoliert zwischen zwei Vektoren linear.
	 * @param v0 Erster Vektor
	 * @param v1 Zweiter Vektor
	 * @param t Mischparameter
	 * @param dst Vektor, in den das Ergebnis gespeichert wird. Wenn null wird
	 * ein neuer erstellt.
	 * @return Ergebnisvektor
	 */
	public static Vector4f mix(Vector4f v0, Vector4f v1, float t, Vector4f dst) {
		if(dst == null) dst = new Vector4f();
		dst.x = mix(v0.x, v1.x, t);
		dst.y = mix(v0.y, v1.y, t);
		dst.z = mix(v0.z, v1.z, t);
		dst.w = mix(v0.w, v1.w, t);
		return dst;
	}

	/**
	 * Transponiert und invertiert eine Matrix.
	 * @param src Matrix, die invertiert und transpnoert werden soll
	 * @param dst Matrix, in die das Ergebnis gespeichert wird. Wenn null wird
	 * eine neue erstellt.
	 * @return Ergebnismatrix
	 */
	public static Matrix4f transposeInverse(Matrix4f src, Matrix4f dst) {
		if(dst == null) dst = new Matrix4f();
		src.store(MAT_BUFFER);
		MAT_BUFFER.position(0);
		TEMP.loadTranspose(MAT_BUFFER);
		MAT_BUFFER.position(0);
		TEMP.invert();
		dst.load(TEMP);
		return dst;
	}

	/**
	 * Liest den Inhalt einer Datei und liefert ihn als String zurueck.
	 * @param filename Pfad der Datei
	 * @return Inhalt der Datei
	 */

	static String getFileContents(String filename) {

		BufferedReader reader = null;
		String source = null;
		try {
			reader = new BufferedReader(new FileReader(filename));
			source = "";
			String line;
			while((line = reader.readLine()) != null) {
				source += line + "\n";
			}
		} catch (IOException ex) {
			Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			try {
				reader.close();
			} catch (IOException ex) {
				Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return source;
	}

	/**

	 * @author ARECKNAG, FMAESCHIG
	 * @param terra The terrain which is to be modified
	 * @param noise Some noisemap (pref 32²)
	 * @param freq The frequency by which the noisemap is taken
	 * @param amp The amplitude with which the noise is applied
	 */
	public static void biLinIpol(float[][][] terra, float[][]noise, float freq, float amp){

		int terraX = terra.length;
		int terraY = terra[0].length;

		int noiseX = noise.length;
		int noiseY = noise[0].length;
		int pX, pY;
		float a, b;
		float dX, dY;


		for(int i = 0; i < (terraX-1); i++){

			a = (float)noiseX * freq * (float)i / (float)terraX;
			pX = (int)(a);
			dX = a - pX;
			//System.out.println("pX:"+pX);

			for(int j = 0; j < (terraY-1); j++){

				b = (float)noiseY * freq * (float)j / (float)terraY;
				pY = (int)(b);
				dY = b - pY;
				//System.out.println("pY:"+pY);

				terra[i][j][0] += amp * (iPol(iPol(noise[pX % noiseX][pY % noiseY], 
						noise[pX % noiseX][(pY+1)%noiseY], dY),
						iPol(noise[(pX+1)%noiseX][pY % noiseY], 
								noise[(pX+1)%noiseX][(pY+1)%noiseY], dY), dX));

			}

		}

	}
	
	/**
	 * smoothes terra depending on material
	 * @author ARECKNAG, FMAESCHIG
	 * @param terra
	 * @param x
	 * @param z
	 */
	public static void smooth(float[][][] terra, int x, int z){

		switch(Math.round(terra[x][z][4])){

		case 0:break;

		case 1:smoothGauss3(terra, x, z, 3);break; //Sea

		case 2:smoothGauss3(terra, x, z, 7);break; //River

		case 3:smoothGauss7(terra, x, z, 50);break; //Sand

		case 4:smoothGauss7(terra, x, z, 8);break; //Earth

		case 5:smoothGauss7(terra, x, z, 15);break; //LightGrass

		case 6:smoothGauss7(terra, x, z, 15);break; //DarkGrass

		case 7:smoothGauss3(terra, x, z, 1);break; //Stone

		case 8:break;	//Rock

		case 9:smoothGauss7(terra, x, z, 50);break;	//Snow
		}



	}

	/**
	 * @author ARECKNAG, FMAESCHIG
	 * smoothing with 3x3 Gausskernel 
	 * 
	 * @param heightmap to smooth
	 */
	private static void smoothGauss3(float [][][] terra, int x, int z, int count){

		int width = terra.length, height = terra[0].length;
		float sum = 0;



		// Fill GaussPattern
		GaussMat3[0][0] =((((x-1)>=0) & ((z-1)>=0))     ? terra[x-1][z-1][0] :
										(z-1)>=0       ? terra[x]  [z-1][0] :
						(x-1)>=0			       	   ? terra[x-1][z]  [0] : terra[x][z][0]) *1;
		GaussMat3[0][1] =  ((x-1)>=0  			       ? terra[x-1][z]  [0] : terra[x][z][0]) *2;
		GaussMat3[0][2] =((((x-1)>=0) & ((z+1)<height)) ? terra[x-1][z+1][0] :
										(z+1)<height   ? terra[x]  [z+1][0] :
						(x-1)>=0			            ? terra[x-1][z]  [0] : terra[x][z][0]) *1;


		GaussMat3[1][0] = 				((z-1)>=0 		? terra[x][z-1][0] : terra[x][z][0] )*2;
		GaussMat3[1][2] = 				 ((z+1)<height  ? terra[x][z+1][0]    : terra[x][z][0] )*2;


		GaussMat3[2][0] =((((x+1)<width) & ((z-1)>=0))    ? terra[x+1][z-1][0] :
											(z-1)>=0       ? terra[x]  [z-1][0] :
							(x+1)<width			       	  ? terra[x+1][z]  [0] : terra[x][z][0]) *1;
		GaussMat3[2][1] =  ((x+1)<width  			      ? terra[x+1][z]  [0] : terra[x][z][0]) *2;
		GaussMat3[2][2] =((((x+1)<width) & ((z+1)<height))? terra[x+1][z+1][0] :
											(z+1)<height   ? terra[x]  [z+1][0] :
							(x+1)<width			          ? terra[x+1][z]  [0] : terra[x][z][0]) *1;


		for(int i=0; i<count; i++){
			GaussMat3[1][1] = terra[x][z][0]*4;
			for(int k=0; k<3; k++){
				for(int l=0; l<3; l++){
					sum += GaussMat3[k][l];
				}
			}
			terra[x][z][0] = sum/16f;
			sum = 0;
		}		
	}


	/**
	 * @author ARECKNAG, FMAESCHIG
	 * smoothing with 5x5 Gausskernel (old)
	 * 
	 * @param terra
	 */
//	private static void smoothGauss5(float [][][] terra){
//
//		int width = terra.length, height = terra[0].length;
//		float[][] helpMap = new float[width+4][height+4];
//		float sum = 0;
//
//		// Fill helpMat
//		for(int i=2; i<width+2; i++){
//			for(int j=2; j<height+2; j++){
//				helpMap[i][j] = terra[i-2][j-2][0];
//			}
//		}
//
//
//		for(int i=2; i<width+2; i++){
//			for(int j=2; j<height+2; j++){
//
//				// Fill GaussPattern
//				GaussMat5[0][0] = helpMap[i-2][j-2];
//				GaussMat5[0][1] = helpMap[i-2][j-1] * 4;
//				GaussMat5[0][2] = helpMap[i-2][j] * 6;
//				GaussMat5[0][3] = helpMap[i-2][j+1] * 4;
//				GaussMat5[0][4] = helpMap[i-2][j+2];
//
//				GaussMat5[1][0] = helpMap[i-1][j-2] * 4;
//				GaussMat5[1][1] = helpMap[i-1][j-1] * 16;
//				GaussMat5[1][2] = helpMap[i-1][j] * 24;
//				GaussMat5[1][3] = helpMap[i-1][j+1] * 16;
//				GaussMat5[1][4] = helpMap[i-1][j+2] * 4;
//
//				GaussMat5[2][0] = helpMap[i][j-2] * 6;
//				GaussMat5[2][1] = helpMap[i][j-1] * 24;
//				GaussMat5[2][2] = helpMap[i][j] * 36;
//				GaussMat5[2][3] = helpMap[i][j+1] * 24;
//				GaussMat5[2][4] = helpMap[i][j+2] * 6;
//
//				GaussMat5[3][0] = helpMap[i+1][j-2] * 4;
//				GaussMat5[3][1] = helpMap[i+1][j-1] * 16;
//				GaussMat5[3][2] = helpMap[i+1][j] * 24;
//				GaussMat5[3][3] = helpMap[i+1][j+1] * 16;
//				GaussMat5[3][4] = helpMap[i+1][j+2] * 4;
//
//				GaussMat5[4][0] = helpMap[i+2][j-2];
//				GaussMat5[4][1] = helpMap[i+2][j-1] * 4;
//				GaussMat5[4][2] = helpMap[i+2][j] * 6;
//				GaussMat5[4][3] = helpMap[i+2][j+1] * 4;
//				GaussMat5[4][4] = helpMap[i+2][j+2];
//
//				for(int k=0; k<5; k++){
//					for(int l=0; l<5; l++){
//						sum += GaussMat5[k][l];
//					}
//				}
//				terra[i-2][j-2][0] = sum/256f;
//				sum = 0;
//			}
//
//		}
//	}

	/**
	 * @author ARECKNAG, FMAESCHIG
	 * smoothing with 7x7 Gausskernel
	 * @param terra
	 */
	private static void smoothGauss7(float [][][] terra, int x, int z, int count){

		int width = terra.length, height = terra[0].length;
		float sum = 0;

				// Fill GaussPattern
				GaussMat7[0][0] = ((((x-3)>=0) & ((z-3)>=0))     ? terra[x-3][z-3][0] :
					(z-3)>=0       ? terra[x]  [z-3][0] :
						(x-3)>=0			       	   ? terra[x-3][z]  [0] : terra[x][z][0]) *1;
						
	
				GaussMat7[0][1] =  ((((x-3)>=0) & ((z-2)>=0))     ? terra[x-3][z-2][0] :
					(z-2)>=0       ? terra[x]  [z-2][0] :
						(x-3)>=0			       	   ? terra[x-3][z]  [0] : terra[x][z][0]) *6;
				GaussMat7[0][2]= (((x-3)>=0) & ((z-1>=0))     ? terra[x-3][z-1][0] :
					(z-1)>=0       ? terra[x]  [z-1][0] :
						(x-3)>=0			       	   ? terra[x-3][z]  [0] : terra[x][z][0]) *15;
				
				GaussMat7[0][3] = ((x-3)>=0  			       ? terra[x-3][z]  [0] : terra[x][z][0]) *20;
				
				GaussMat7[0][4] = ((((x-3)>=0) & ((z+1)<height)) ? terra[x-3][z+1][0] :
					(z+1)<height   ? terra[x]  [z+1][0] :
						(x-3)>=0			            ? terra[x-3][z]  [0] : terra[x][z][0]) *15;
						
			
				
				GaussMat7[0][5] =((((x-3)>=0) & ((z+2)<height)) ? terra[x-3][z+2][0] :
					(z+2)<height   ? terra[x]  [z+2][0] :
						(x-3)>=0			            ? terra[x-3][z]  [0] : terra[x][z][0]) *6;
						
						
				
				GaussMat7[0][6] =((((x-3)>=0) & ((z+3)<height)) ? terra[x-3][z+3][0] :
					(z+3)<height   ? terra[x]  [z+3][0] :
						(x-3)>=0			            ? terra[x-3][z]  [0] : terra[x][z][0]) *1;
			

				
				
				GaussMat7[1][0] = ((((x-2)>=0) & ((z-3)>=0))     ? terra[x-2][z-3][0]:
					(z-3)>=0       ? terra[x]  [z-3][0] :
						(x-2)>=0			       	   ? terra[x-2][z]  [0] : terra[x][z][0]) *6;
						
	
				GaussMat7[1][1] =  ((((x-2)>=0) & ((z-2)>=0))     ? terra[x-2][z-2][0] :
					(z-2)>=0       ? terra[x]  [z-2][0] :
						(x-2)>=0			       	   ? terra[x-2][z]  [0] : terra[x][z][0]) *36;
				
				GaussMat7[1][2]= (((x-2)>=0) & ((z-1>=0))     ? terra[x-2][z-1][0] :
					(z-1)>=0       ? terra[x]  [z-1][0] :
						(x-2)>=0			       	   ? terra[x-2][z]  [0] : terra[x][z][0]) *90;
				
				GaussMat7[1][3] = ((x-2)>=0  			       ? terra[x-2][z]  [0] : terra[x][z][0]) *120;
				
				GaussMat7[1][4] = ((((x-2)>=0) & ((z+1)<height)) ? terra[x-2][z+1][0] :
					(z+1)<height   ? terra[x]  [z+1][0] :
						(x-2)>=0			            ? terra[x-2][z]  [0] : terra[x][z][0]) *90;
						
			
				
				GaussMat7[1][5] =((((x-2)>=0) & ((z+2)<height)) ? terra[x-2][z+2][0] :
					(z+2)<height   ? terra[x]  [z+2][0] :
						(x-2)>=0			            ? terra[x-2][z]  [0] : terra[x][z][0]) *36;
						
						
				
				GaussMat7[1][6] =((((x-2)>=0) & ((z+3)<height)) ? terra[x-2][z+3][0] :
					(z+3)<height   ? terra[x]  [z+3][0] :
						(x-2)>=0			            ? terra[x-2][z]  [0] : terra[x][z][0]) *6;
							
				
				
				GaussMat7[2][0] = ((((x-1)>=0) & ((z-3)>=0))     ? terra[x-1][z-3][0]:
					(z-3)>=0       ? terra[x]  [z-3][0] :
						(x-1)>=0			       	   ? terra[x-1][z]  [0] : terra[x][z][0]) *15;
						
	
				GaussMat7[2][1] =  ((((x-1)>=0) & ((z-2)>=0))     ? terra[x-1][z-2][0] :
					(z-2)>=0       ? terra[x]  [z-2][0] :
						(x-1)>=0			       	   ? terra[x-1][z]  [0] : terra[x][z][0]) *90;
				
				GaussMat7[2][2]= (((x-1)>=0) & ((z-1>=0))     ? terra[x-1][z-1][0] :
					(z-1)>=0       ? terra[x]  [z-1][0] :
						(x-1)>=0			       	   ? terra[x-1][z]  [0] : terra[x][z][0]) *225;
				
				GaussMat7[2][3] = ((x-1)>=0  			       ? terra[x-1][z]  [0] : terra[x][z][0]) *300;
				
				GaussMat7[2][4] = ((((x-1)>=0) & ((z+1)<height)) ? terra[x-1][z+1][0] :
					(z+1)<height   ? terra[x]  [z+1][0] :
						(x-1)>=0			            ? terra[x-1][z]  [0] : terra[x][z][0]) *225;
						
			
				
				GaussMat7[2][5] =((((x-1)>=0) & ((z+2)<height)) ? terra[x-1][z+2][0] :
					(z+2)<height   ? terra[x]  [z+2][0] :
						(x-1)>=0			            ? terra[x-1][z]  [0] : terra[x][z][0]) *90;
						
						
				
				GaussMat7[2][6] =((((x-1)>=0) & ((z+3)<height)) ? terra[x-1][z+3][0] :
					(z+3)<height   ? terra[x]  [z+3][0] :
						(x-1)>=0			            ? terra[x-1][z]  [0] : terra[x][z][0]) *15;
				
		
				GaussMat7[3][0] = ((z-3)>=0 		? terra[x][z-3][0] : terra[x][z][0] ) * 20;
				
				GaussMat7[3][1] = ((z-2)>=0 		? terra[x][z-2][0] : terra[x][z][0] ) * 120;
				
				GaussMat7[3][2] = ((z-1)>=0 		? terra[x][z-1][0] : terra[x][z][0] ) * 300;
				

				
				GaussMat7[3][4] = ((z+1)<height		? terra[x][z+1][0] : terra[x][z][0] ) * 300;
				
				GaussMat7[3][5] = ((z+2)<height			? terra[x][z+2][0] : terra[x][z][0] ) * 120;
				
				GaussMat7[3][6] = ((z+3)<height	 		? terra[x][z+3][0] : terra[x][z][0] ) * 20;	
				
				
				GaussMat7[4][0] = ((((x+1)<width) & ((z-3)>=0))    ? terra[x+1][z-3][0] :
					(z-3)>=0       ? terra[x]  [z-3][0] :
						(x+1)<width			       	  ? terra[x+1][z]  [0] : terra[x][z][0]) *15;
						
					
				
				GaussMat7[4][1] = ((((x+1)<width) & ((z-2)>=0))    ? terra[x+1][z-2][0] :
					(z-2)>=0       ? terra[x]  [z-2][0] :
						(x+1)<width			       	  ? terra[x+1][z]  [0] : terra[x][z][0]) *90;
						 
						
						
				
				GaussMat7[4][2] = ((((x+1)<width) & ((z-1)>=0))    ? terra[x+1][z-1][0] :
					(z-1)>=0       ? terra[x]  [z-1][0] :
						(x+1)<width			       	  ? terra[x+1][z]  [0] : terra[x][z][0]) *225; 
						
						
				GaussMat7[4][3] = ((x+1)<width    ? terra[x+1][z][0] : terra[x][z][0]) *300;
				
				

				
				GaussMat7[4][4] = ((((x+1)<width) & ((z+1)<height))    ? terra[x+1][z+1][0] :
					(z+1)<height       ? terra[x]  [z+1][0] :
						(x+1)<width			       	  ? terra[x+1][z]  [0] : terra[x][z][0]) *225; 		
	
				
				GaussMat7[4][5] = ((((x+1)<width) & ((z+2)<height))    ? terra[x+1][z+2][0] :
					(z+2)<height       ? terra[x]  [z+2][0] :
						(x+1)<width			       	  ? terra[x+1][z]  [0] : terra[x][z][0]) * 90;
				
				GaussMat7[4][6] = ((((x+1)<width) & ((z+3)<height))    ? terra[x+1][z+3][0] :
					(z+3)<height       ? terra[x]  [z+3][0] :
						(x+1)<width			       	  ? terra[x+1][z]  [0] : terra[x][z][0]) * 15;
			
				
				GaussMat7[5][0] = ((((x+2)<width) & ((z-3)>=0))    ? terra[x+2][z-3][0] :
					(z-3)>=0       ? terra[x]  [z-3][0] :
						(x+2)<width			       	  ? terra[x+2][z]  [0] : terra[x][z][0]) *6;
						
					
				
				GaussMat7[5][1] = ((((x+2)<width) & ((z-2)>=0))    ? terra[x+2][z-2][0] :
					(z-2)>=0       ? terra[x]  [z-2][0] :
						(x+2)<width			       	  ? terra[x+2][z]  [0] : terra[x][z][0]) *36;
						 
						
						
				
				GaussMat7[5][2] = ((((x+2)<width) & ((z-1)>=0))    ? terra[x+2][z-1][0] :
					(z-1)>=0       ? terra[x]  [z-1][0] :
						(x+2)<width			       	  ? terra[x+2][z]  [0] : terra[x][z][0]) *90;
						
						
				GaussMat7[5][3] = ((x+2)<width    ? terra[x+2][z][0] : terra[x][z][0]) *120;
				
				

				
				GaussMat7[5][4] = ((((x+2)<width) & ((z+1)<height))    ? terra[x+2][z+1][0] :
					(z+1)<height       ? terra[x]  [z+1][0] :
						(x+2)<width			       	  ? terra[x+2][z]  [0] : terra[x][z][0]) *90; 
				
				
	
				
				GaussMat7[5][5] = ((((x+2)<width) & ((z+2)<height))    ? terra[x+2][z+2][0] :
					(z+2)<height       ? terra[x]  [z+2][0] :
						(x+2)<width			       	  ? terra[x+2][z]  [0] : terra[x][z][0]) * 36;
				
				GaussMat7[5][6] = ((((x+2)<width) & ((z+3)<height))    ? terra[x+2][z+3][0] :
					(z+3)<height       ? terra[x]  [z+3][0] :
						(x+2)<width			       	  ? terra[x+2][z]  [0] : terra[x][z][0]) * 6;

				
				


				
				
				
				GaussMat7[6][0] = ((((x+3)<width) & ((z-3)>=0))    ? terra[x+3][z-3][0] :
					(z-3)>=0       ? terra[x]  [z-3][0] :
						(x+3)<width			       	  ? terra[x+3][z]  [0] : terra[x][z][0]) *1;
						
					
				
				GaussMat7[6][1] = ((((x+3)<width) & ((z-2)>=0))    ? terra[x+3][z-2][0] :
					(z-2)>=0       ? terra[x]  [z-2][0] :
						(x+3)<width			       	  ? terra[x+3][z]  [0] : terra[x][z][0]) *6;
						 
						
						
				
				GaussMat7[6][2] = ((((x+3)<width) & ((z-1)>=0))    ? terra[x+3][z-1][0] :
					(z-1)>=0       ? terra[x]  [z-1][0] :
						(x+3)<width			       	  ? terra[x+3][z]  [0] : terra[x][z][0]) *15;
						
						
				GaussMat7[6][3] = ((x+3)<width    ? terra[x+3][z][0] : terra[x][z][0]) *20;
				
				

				
				GaussMat7[6][4] = ((((x+3)<width) & ((z+1)<height))    ? terra[x+3][z+1][0] :
					(z+1)<height       ? terra[x]  [z+1][0] :
						(x+3)<width			       	  ? terra[x+3][z]  [0] : terra[x][z][0]) *15; 
				
				
	
				
				GaussMat7[6][5] = ((((x+3)<width) & ((z+2)<height))    ? terra[x+3][z+2][0] :
					(z+2)<height       ? terra[x]  [z+2][0] :
						(x+3)<width			       	  ? terra[x+3][z]  [0] : terra[x][z][0]) * 6;
				
				GaussMat7[6][6] = ((((x+3)<width) & ((z+3)<height))    ? terra[x+3][z+3][0] :
					(z+3)<height       ? terra[x]  [z+3][0] :
						(x+3)<width			       	  ? terra[x+3][z]  [0] : terra[x][z][0]) * 1;
				

				
				for(int k=0; k<7; k++){
					GaussMat7[3][3] = 	terra[x][z][0]  * 400;
					for(int l=0; l<7; l++){
						sum += GaussMat7[k][l];
					}
				}
				terra[x][z][0] = sum/4096f;
				sum = 0;

	}
	

	/**
	 * @author ARECKNAG, FMAESCHIG
	 * @param valA First value which is to be interpolated
	 * @param valB Second value which is to be interpolated
	 * @param rel The relation between the position of the values
	 * @return the interpolated value
	 */
	private static float iPol(float valA, float valB, float rel) {

		return valA +((valB - valA) * rel);

	}

	/**
	 * Attribut Index von positionMC
	 */
	public static final int ATTR_POS = 0;

	/**
	 * Attribut Index von normalMC
	 */
	public static final int ATTR_NORMAL = 1;

	/**
	 * Attribut Index von vertexColor
	 */
	public static final int ATTR_COLOR = 2;

	/**
	 * Attribut Index von vertexMaterial
	 */
	public static final int ATTR_MATERIAL = 3;

	/**
	 * Erzeugt ein ShaderProgram aus einem Vertex- und Fragmentshader.
	 * @param vs Pfad zum Vertexshader
	 * @param fs Pfad zum Fragmentshader
	 * @return ShaderProgram ID
	 */
	public static int createShaderProgram(String vs, String fs) {
		int programID = glCreateProgram();

		int vsID = glCreateShader(GL_VERTEX_SHADER);
		int fsID = glCreateShader(GL_FRAGMENT_SHADER);

		glAttachShader(programID, vsID);
		glAttachShader(programID, fsID);

		String vertexShaderContents = Util.getFileContents(vs);
		String fragmentShaderContents = Util.getFileContents(fs);

		glShaderSource(vsID, vertexShaderContents);
		glShaderSource(fsID, fragmentShaderContents);

		glCompileShader(vsID);
		glCompileShader(fsID);

		String log;
		log = glGetShaderInfoLog(vsID, 1024);
		System.out.print(log);
		log = glGetShaderInfoLog(fsID, 1024);
		System.out.print(log);

		glBindAttribLocation(programID, ATTR_POS, "positionMC");
		glBindAttribLocation(programID, ATTR_NORMAL, "normalMC");        
		glBindAttribLocation(programID, ATTR_COLOR, "vertexColor");

		glLinkProgram(programID);        

		log = glGetProgramInfoLog(programID, 1024);
		System.out.print(log);

		return programID;
	}  

	/**
	 * Laedt ein Bild und speichert die einzelnen Bildpunke in einem
	 * 2-dimensionalen float-Array. Die erste Koordinate ist die y-Position und
	 * liegt zwischen 0 und der Hoehe des Bildes - 1. Die zweite Koordinate ist
	 * die x-Position und liegt zwischen 0 und der Breite des Bildes. Die dritte
	 * Koordinate ist die Farbkomponente des Bildpunktes und ist 0 (rot), 1
	 * (gruen) oder 2 (blau).
	 * @param imageFile Pfad zur Bilddatei
	 * @return Bild enthaltendes float-Array
	 */
	public static float[][][] getImageContents(String imageFile) {
		File file = new File(imageFile);
		if(!file.exists()) {
			throw new IllegalArgumentException(imageFile + " does not exist");
		}
		try {
			BufferedImage image = ImageIO.read(file);
			float[][][] result = new float[image.getHeight()][image.getWidth()][3];
			for(int y=0; y < image.getHeight(); ++y) {
				for(int x=0; x < image.getWidth(); ++x) {
					Color c = new Color(image.getRGB(image.getWidth() - 1 - x, y));
					result[y][x][0] = (float)c.getRed() / 255.0f;
					result[y][x][1] = (float)c.getGreen() / 255.0f;
					result[y][x][2] = (float)c.getBlue() / 255.0f;
				}
			}
			return result;
		} catch (IOException ex) {
			Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}
}
