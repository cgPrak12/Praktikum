package util;

import static opengl.GL.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import java.io.*;
import java.util.*;
import opengl.GL;

/**
 * Stellt Methoden zur Erzeugung von Geometrie bereit.
 * @author Sascha Kolodzey, Nico Marniok
 */
public class GeometryFactory {    
    /**
     * Erzeugt eine Geometrie aus einer OBJ-Datei von Blender
     * @param objFile Pfad zur OBJ Datei
     * @return Erzeugte geometrie
     */
    public static Geometry createFromOBJ(String objPath) {
        //Erzeuge eine Liste in der die Vertices zwischengespeichert werden (die Anzahl der Vertices ist ja nicht bekannt)        
        Obj model = new Obj();
        
        //öffne Datei
        try (BufferedReader objBufferedReader = new BufferedReader(new FileReader(objPath))) {
            //Durchlaufe OBJ-File zeilenweise
            String line = "";
            while((line = objBufferedReader.readLine()) != null) {
                //Spalt Zeile in Elemente anhand von Leerzeichen auf
                String[] lineElements = line.split(" {1,}");
                //Parse verticex coordinates (v)
                if(lineElements.length>0 && lineElements[0].equals("v")) {
                    if(!lineElements[1].isEmpty() && !lineElements[2].isEmpty() && !lineElements[3].isEmpty()) {
                        Vector3f vertex = new Vector3f(Float.parseFloat(lineElements[1]), Float.parseFloat(lineElements[2]), Float.parseFloat(lineElements[3]));
                        model.vertexList.add(vertex);
                    }
                }

                //Parse vertex texture coordinates (vt)
                if(lineElements.length>0 && lineElements[0].equals("vt")) {
                    if(!lineElements[1].isEmpty() && !lineElements[2].isEmpty() && !lineElements[3].isEmpty()) {
                        Vector3f vertex = new Vector3f(Float.parseFloat(lineElements[1]), Float.parseFloat(lineElements[2]), Float.parseFloat(lineElements[3]));
                        model.vertexTextureList.add(vertex);
                    }
                }
                
                //Parse vertex normals (vn)
                if(lineElements.length>0 && lineElements[0].equals("vn")) {
                    if(!lineElements[1].isEmpty() && !lineElements[2].isEmpty() && !lineElements[3].isEmpty()) {
                        Vector3f vertex = new Vector3f(Float.parseFloat(lineElements[1]), Float.parseFloat(lineElements[2]), Float.parseFloat(lineElements[3]));
                        model.vertexNormalList.add(vertex);
                    }
                }
                
                //Parse Faces
                if(lineElements[0].equals("f") && lineElements.length==5) {
                    String[][] faceGroup = new String[4][];
                    if(lineElements[1].contains("/")) {
                        faceGroup[0] = lineElements[1].split("/");
                        faceGroup[1] = lineElements[2].split("/");
                        faceGroup[2] = lineElements[3].split("/");
                        faceGroup[3] = lineElements[4].split("/");

                        Face faceOne = new Face(Integer.parseInt(faceGroup[0][0])-1, Integer.parseInt(faceGroup[1][0])-1, Integer.parseInt(faceGroup[2][0])-1,
                                                Integer.parseInt(faceGroup[0][2])-1, Integer.parseInt(faceGroup[1][2])-1, Integer.parseInt(faceGroup[2][2])-1);
                        model.faceListe.add(faceOne);
                        Face faceTwo = new Face(Integer.parseInt(faceGroup[0][0])-1, Integer.parseInt(faceGroup[2][0])-1, Integer.parseInt(faceGroup[3][0])-1,
                                                Integer.parseInt(faceGroup[0][2])-1, Integer.parseInt(faceGroup[2][2])-1, Integer.parseInt(faceGroup[3][2])-1);
                        model.faceListe.add(faceTwo);
                    } else {
                        Face faceOne = new Face(Integer.parseInt(lineElements[1])-1, Integer.parseInt(lineElements[2])-1, Integer.parseInt(lineElements[3])-1);
                        model.faceListe.add(faceOne);
                        Face faceTwo = new Face(Integer.parseInt(lineElements[1])-1, Integer.parseInt(lineElements[3])-1, Integer.parseInt(lineElements[4])-1);
                        model.faceListe.add(faceTwo);
                    }

                }
            }
        } catch (IOException e) {
            System.out.println("Problem beim Lesen der OBJ-Datei! Das Programm wird beendet.");
            System.exit(0);
        }               
        
        int vaid = glGenVertexArrays();
        glBindVertexArray(vaid);

        //Erzeuge vertex Buffer
        FloatBuffer vertexData = BufferUtils.createFloatBuffer(3*model.vertexList.size());
        //Schreibe Vertexdaten aus der Vertex Liste in den Vertex Buffer
        Iterator<Vector3f> vertexListIterator = model.vertexList.iterator();
        while(vertexListIterator.hasNext())
            vertexListIterator.next().store(vertexData);
        vertexData.position(0);

        
        //Erzeuge index Buffer
        IntBuffer indexData = BufferUtils.createIntBuffer(model.faceListe.size()*3);
        //Schreibe Indexdaten aus der Index Liste in den Index Buffer
        Iterator<Face> faceIterator = model.faceListe.listIterator();
        while(faceIterator.hasNext()) {
            Vector3f tmpVertexIndizies = faceIterator.next().vertexIndizies;
            indexData.put((int)tmpVertexIndizies.x);
            indexData.put((int)tmpVertexIndizies.y);
            indexData.put((int)tmpVertexIndizies.z);
        }
        indexData.position(0);

        Geometry geo = new Geometry();
        geo.setIndices(indexData, GL_TRIANGLES);
        geo.setVertices(vertexData);
        geo.addVertexAttribute(ShaderProgram.ATTR_POS, 3, 0);
/*      TODO:
        geo.addVertexAttribute(ShaderProgram.ATTR_NORMAL, 3, 12);
        geo.addVertexAttribute(ShaderProgram.ATTR_NORMAL, 3, 12);*/
        return geo;
    }
    
    /**
     * Erzeugt ein Vierexk in der xy-Ebene. (4 Indizes)
     * @return VertexArrayObject ID
     */
    public static Geometry createScreenQuad() {
        int vaid = glGenVertexArrays();
        glBindVertexArray(vaid);
        
        // vertexbuffer
        FloatBuffer vertexData = BufferUtils.createFloatBuffer(8);
        vertexData.put(new float[] {
            -1.0f, -1.0f,
            +1.0f, -1.0f,
            -1.0f, +1.0f,
            +1.0f, +1.0f,
        });
        vertexData.position(0);
        
        // indexbuffer
        IntBuffer indexData = BufferUtils.createIntBuffer(4);
        indexData.put(new int[] { 0, 1, 2, 3, });
        indexData.position(0);
        
        Geometry geo = new Geometry();
        geo.setIndices(indexData, GL_TRIANGLE_STRIP);
        geo.setVertices(vertexData);
        geo.addVertexAttribute(ShaderProgram.ATTR_POS, 2, 0);
        return geo;
    }
    
    /**
     * Erzeugt ein Vierexk in der xy-Ebene. (4 Indizes)
     * @return VertexArrayObject ID
     */
    public static Geometry createTriangle() {        
        int vaid = glGenVertexArrays();
        glBindVertexArray(vaid);        
        
        // vertexbuffer
        FloatBuffer vertexData = BufferUtils.createFloatBuffer((3+4)*3); // world coords, color
        vertexData.put(new float[] {
            -1.0f, -1.0f, +1.0f,  1.0f, 1.0f, 0.4f, 1.0f,
            +1.0f, -1.0f, -1.0f,  0.4f, 1.0f, 0.4f, 1.0f,
            +1.0f, +1.0f, +1.0f,  1.0f, 1.0f, 0.4f, 1.0f,
        });
        vertexData.position(0);
        
        // indexbuffer
        IntBuffer indexData = BufferUtils.createIntBuffer(4);
        indexData.put(new int[] { 0, 1, 2, });
        indexData.position(0);
        
        Geometry geo = new Geometry();
        geo.setIndices(indexData, GL_TRIANGLE_STRIP);
        geo.setVertices(vertexData);
        geo.addVertexAttribute(ShaderProgram.ATTR_POS, 3, 0);
        geo.addVertexAttribute(ShaderProgram.ATTR_COLOR, 4, 12);
        return geo;
    }
}