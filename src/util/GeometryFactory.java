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
    public static List<Material> getMaterialListFromMTL(String mtlPath) {
        List<Material> materialList = new LinkedList<Material>();
        
       //Open File
        try (BufferedReader objBufferedReader = new BufferedReader(new FileReader(mtlPath))) {
            //Loop through file line by line
            String line = "";
            int i=1;
            
            Material material = null;
            while((line = objBufferedReader.readLine()) != null) {
                //add previos used material object to list if one exists
                //generate a new material object
                //parse material name
                if(line.startsWith("newmtl ")) {
                    if(material!=null)
                        materialList.add(material);
                    material = new Material();
                    material.name = line.split(" {1,}")[1];
                } else if (line.startsWith("Ns ")) {
                    material.specularEx = Float.valueOf(line.split(" {1,}")[1]);
                } else if (line.startsWith("Ka ")) {
                    material.ambientRef = new Vector3f(Float.valueOf(line.split(" {1,}")[1]),
                                                   Float.valueOf(line.split(" {1,}")[2]),
                                                   Float.valueOf(line.split(" {1,}")[3]));
                } else if (line.startsWith("Kd ")) {
                    material.diffuseRef = new Vector3f(Float.valueOf(line.split(" {1,}")[1]),
                                                   Float.valueOf(line.split(" {1,}")[2]),
                                                   Float.valueOf(line.split(" {1,}")[3]));                    
                } else if (line.startsWith("Ks ")) {
                    material.specularRef = new Vector3f(Float.valueOf(line.split(" {1,}")[1]),
                                                   Float.valueOf(line.split(" {1,}")[2]),
                                                   Float.valueOf(line.split(" {1,}")[3])); 
                } else if (line.startsWith("Ni ")) {
                    material.opticalDens = Float.valueOf(line.split(" {1,}")[1]);
                } else if (line.startsWith("d ")) {
                    material.dissolveFact = Float.valueOf(line.split(" {1,}")[1]);
                } else if (line.startsWith("illum ")) {
                    material.illuminationModel = Integer.parseInt(line.split(" {1,}")[1]);
                } else if (line.startsWith("map_Kd ")) {
                    String[] filePath = line.split("\\\\"); //POSSIBLE ERROR BECAUSE INVALID REGEXPR???
                    material.diffuseRefColorMap = filePath[filePath.length-1];
                } else if (line.startsWith("map_d ")) {
                    String[] filePath = line.split("\\\\"); //POSSIBLE ERROR BECAUSE INVALID REGEXPR???
                    material.dissolveFactColorMap = filePath[filePath.length-1];
                }
            } //end of while loop
            //add the last material object to the list (its not being added in the while loop!)
            if(material!=null)
                        materialList.add(material);
        } catch (IOException e) {
            System.out.println("Problem beim Lesen der OBJ-Datei! Das Programm wird beendet.");
            System.exit(0);
        }     
        
        return materialList;
    }
    
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
            int i=1;
            System.out.println("Lese OBJ zeilenweise ein...");
            while((line = objBufferedReader.readLine()) != null) {
                //Parse verticex coordinates (v)
                if(line.startsWith("v ")) {
                    //Spalt Zeile in Elemente anhand von Leerzeichen auf
                    String[] lineElements = line.split(" {1,}");
                    try{ 
                        if(!lineElements[1].isEmpty() && !lineElements[2].isEmpty() && !lineElements[3].isEmpty()) {
                            Vector3f vertexCoordinates = new Vector3f(Float.parseFloat(lineElements[1]), Float.parseFloat(lineElements[2]), Float.parseFloat(lineElements[3]));
                            model.vertexList.add(vertexCoordinates);
                        }
                    } catch(ArrayIndexOutOfBoundsException e) {
                        System.out.println("Read error while reading v at line: "+i);
                        System.out.println(e);
                    }
                }
                
                //Parse vertex texture coordinates (vt)
                if(line.startsWith("vt ")) {
                    //Spalt Zeile in Elemente anhand von Leerzeichen auf
                    String[] lineElements = line.split(" {1,}");
                    try{
                        if(lineElements.length>=3) {
                            //Switch for vt lines with 2 or 3 elements (if 2 elements, third element is 0 by default)
                            float thirdElement;
                            if(lineElements.length==3)
                                thirdElement = 0;
                            else
                                thirdElement = Float.parseFloat(lineElements[3]);
                            
                            Vector3f vertexTextureCoordinates = new Vector3f(Float.parseFloat(lineElements[1]), Float.parseFloat(lineElements[2]), thirdElement);
                            model.vertexTextureList.add(vertexTextureCoordinates);
                        }
                    } catch(ArrayIndexOutOfBoundsException e) {
                        System.out.println("Read error while reading vt at line: "+i);
                        System.out.println(e);
                    }
                }

                //Parse vertex normals (vn)
                if(line.startsWith("vn ")) {
                    //Spalt Zeile in Elemente anhand von Leerzeichen auf
                    String[] lineElements = line.split(" {1,}");
                    if(!lineElements[1].isEmpty() && !lineElements[2].isEmpty() && !lineElements[3].isEmpty()) {
                        Vector3f vertexNormals = new Vector3f(Float.parseFloat(lineElements[1]), Float.parseFloat(lineElements[2]), Float.parseFloat(lineElements[3]));
                        model.vertexNormalList.add(vertexNormals);
                    }
                }
                
                //Parse Faces
                if(line.startsWith("f ")) {
                    //Spalt Zeile in Elemente anhand von Leerzeichen auf
                    String[] lineElements = line.split(" {1,}");
                    String[][] faceGroup = new String[4][];
                    //split lineElements into their index components 

                    try {
                        faceGroup[0] = lineElements[1].split("/");
                        faceGroup[1] = lineElements[2].split("/");
                        //switch for facelines with only 2 face groups
                        if(lineElements.length>3)
                            faceGroup[2] = lineElements[3].split("/");
                        else
                            faceGroup[2] = faceGroup[0];
                    } catch(ArrayIndexOutOfBoundsException e) {
                        System.out.println("Read error while splitting line elements (f) at line: "+i);
                        System.out.println(e);
                    }
                        
                    Vector3f faceVertexIndizies = new Vector3f();
                    Vector3f faceTextureIndizies = new Vector3f();
                    Vector3f faceNormalIndizies = new Vector3f();

                    //Switch for index groups with 1 (vertex only), 2 (vertex and texture) or 3 (vertex, texture and normal) elements
                    if(faceGroup[0].length>=1)
                        faceVertexIndizies = new Vector3f(Float.valueOf(faceGroup[0][0])-1, Float.valueOf(faceGroup[1][0])-1, Float.valueOf(faceGroup[2][0])-1);
                    if(faceGroup[0].length>=2 && !faceGroup[0][1].isEmpty())
                        faceTextureIndizies = new Vector3f(Float.valueOf(faceGroup[0][1])-1, Float.valueOf(faceGroup[1][1])-1, Float.valueOf(faceGroup[2][1])-1);
                    if(faceGroup[0].length>=3 && !faceGroup[0][2].isEmpty())
                        faceNormalIndizies = new Vector3f(Float.valueOf(faceGroup[0][2])-1, Float.valueOf(faceGroup[1][2])-1, Float.valueOf(faceGroup[2][2])-1);

                    //Add the created faces to the face list
                    try {
                        model.faceListe.add(new Face(faceVertexIndizies, faceTextureIndizies, faceNormalIndizies));
                    } catch(ArrayIndexOutOfBoundsException e) {
                        System.out.println("Read error while creating Face one at line: "+i);
                        System.out.println(e);
                    }
                        
                    //if face consists of 2 triangles (4 index groups), then create a second triangle
                    if(lineElements.length>4) {
                        faceGroup[3] = lineElements[4].split("/");

                        faceVertexIndizies = new Vector3f();
                        faceTextureIndizies = new Vector3f();
                        faceNormalIndizies = new Vector3f();

                        //Switch for index groups with 1 (vertex only), 2 (vertex and texture) or 3 (vertex, texture and normal) elements
                        if(faceGroup[0].length>=1)
                            faceVertexIndizies = new Vector3f(Float.valueOf(faceGroup[0][0])-1, Float.valueOf(faceGroup[2][0])-1, Float.valueOf(faceGroup[3][0])-1);
                        if(faceGroup[0].length>=2 && !faceGroup[0][1].isEmpty())
                            faceTextureIndizies = new Vector3f(Float.valueOf(faceGroup[0][1])-1, Float.valueOf(faceGroup[2][1])-1, Float.valueOf(faceGroup[3][1])-1);
                        if(faceGroup[0].length>=3 && !faceGroup[0][2].isEmpty())
                            faceNormalIndizies = new Vector3f(Float.valueOf(faceGroup[0][2])-1, Float.valueOf(faceGroup[2][2])-1, Float.valueOf(faceGroup[3][2])-1);

                        try {
                            model.faceListe.add(new Face(faceVertexIndizies, faceTextureIndizies, faceNormalIndizies));
                        } catch(ArrayIndexOutOfBoundsException e) {
                            System.out.println("Read error while creating Face two at line: "+i);
                            System.out.println(e);
                        }                        
                    }   
                }
                i++;
            } //end of while loop
        } catch (IOException e) {
            System.out.println("Problem beim Lesen der OBJ-Datei! Das Programm wird beendet.");
            System.exit(0);
        }               
     
        System.out.println("Erzeuge Buffer...");
        int vaid = glGenVertexArrays();
        glBindVertexArray(vaid);

        //Erzeuge vertex Buffer
        FloatBuffer vertexData = BufferUtils.createFloatBuffer(model.faceListe.size()*3*3*3);
        //Erzeuge index Buffer
        IntBuffer indexData = BufferUtils.createIntBuffer(model.faceListe.size()*3);
        //Schreibe Indexdaten aus der Index Liste in den Index Buffer
        Iterator<Face> faceIterator = model.faceListe.listIterator();
        //Durchlaufe alle Faces

        System.out.println("Durchlaufe Faces und erzeuge Vertex Buffer...");
        int counter= 0;
        while(faceIterator.hasNext()) {
            //Speichere aktuelles Face zwischen
            Face currentFace = faceIterator.next();

            //Hole vertexCoordinaten zum jeweiligen vertexIndizies
            model.vertexList.get((int)currentFace.vertexIndizies.x).store(vertexData);            
            //check if the model has texture coordinates
            if(currentFace.vertexTextureIndizies.length()!=0)
                model.vertexTextureList.get((int)currentFace.vertexTextureIndizies.x).store(vertexData);
            else
                new Vector3f().store(vertexData);
            //check if the model has normals
            if(currentFace.vertexNormalIndizies.length()!=0)
                model.vertexNormalList.get((int)currentFace.vertexNormalIndizies.x).store(vertexData);
            else
                new Vector3f().store(vertexData);
            
            model.vertexList.get((int)currentFace.vertexIndizies.y).store(vertexData);
            //check if the model has texture coordinates
            if(currentFace.vertexTextureIndizies.length()!=0)
                model.vertexTextureList.get((int)currentFace.vertexTextureIndizies.y).store(vertexData);
            else
                new Vector3f().store(vertexData);
            //check if the model has normals
            if(currentFace.vertexNormalIndizies.length()!=0)
                model.vertexNormalList.get((int)currentFace.vertexNormalIndizies.y).store(vertexData);
            else
                new Vector3f().store(vertexData);

            model.vertexList.get((int)currentFace.vertexIndizies.z).store(vertexData);
            //check if the model has texture coordinates
            if(currentFace.vertexTextureIndizies.length()!=0)
                model.vertexTextureList.get((int)currentFace.vertexTextureIndizies.z).store(vertexData);
            else
                new Vector3f().store(vertexData);
            //check if the model has normals
            if(currentFace.vertexNormalIndizies.length()!=0)
                model.vertexNormalList.get((int)currentFace.vertexNormalIndizies.z).store(vertexData);
            else
                new Vector3f().store(vertexData);
            
/*            indexData.put((int)currentFace.vertexIndizies.x);
            indexData.put((int)currentFace.vertexIndizies.y);
            indexData.put((int)currentFace.vertexIndizies.z);*/
        }
        
        System.out.println("Erzeuge Indexbuffer...");
        for(int i=0; i<model.faceListe.size()*3; i++) {
            indexData.put(i);
        }
        
        System.out.println("Setze Bufferposition auf 0...");
        vertexData.position(0);
        indexData.position(0);
        
        System.out.println("Erzeuge geometrie Objekt...");
        Geometry geo = new Geometry();
        geo.setIndices(indexData, GL_TRIANGLES);
        geo.setVertices(vertexData);
        geo.addVertexAttribute(ShaderProgram.ATTR_POS, 3, 0);
        geo.addVertexAttribute(ShaderProgram.ATTR_TEX, 3, 12);
        geo.addVertexAttribute(ShaderProgram.ATTR_NORMAL, 3, 24);
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