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
    public static List<Geometry> importFromBlender(String objFile, String mtlFile) {
        Model model = new Model();
        model.objectList = getObjectListFromOBJ(objFile);
        model.materialList = getMaterialListFromMTL(mtlFile);
        
        List<Geometry> geometryList = new LinkedList();
        
        Iterator<Object> objectListIterator = model.objectList.listIterator();
        while(objectListIterator.hasNext()) {
            Object currentObject = objectListIterator.next();

            System.out.println("Erzeuge geometrie Objekt...");
            Geometry geo = new Geometry();
    
            int vaid = glGenVertexArrays();
            glBindVertexArray(vaid);

            geo.setIndices(currentObject.indexBuffer, GL_TRIANGLES);
            geo.setVertices(currentObject.vertexBuffer);
            geo.addVertexAttribute(ShaderProgram.ATTR_POS, 3, 0);
            geo.addVertexAttribute(ShaderProgram.ATTR_TEX, 3, 12);
            geo.addVertexAttribute(ShaderProgram.ATTR_NORMAL, 3, 24);
            geometryList.add(geo);
        }
        
/*        List materialList = GeometryFactory.getMaterialListFromMTL();
        Iterator<Material> materialListIterator = materialList.listIterator();

        while(materialListIterator.hasNext()) {
            System.out.println("bla");
            System.out.println(materialListIterator.next());
            
        }*/
        
        return geometryList;
    }
    
    public static List<Material> getMaterialListFromMTL(String mtlPath) {
        List<Material> materialList = new LinkedList<Material>();
        
       //Open File
        try (BufferedReader objBufferedReader = new BufferedReader(new FileReader(mtlPath))) {
            //Loop through file line by line
            String line = "";
            int i=1;
            
            String materialLibrary = "default";
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
                    String[] filePath = line.split("\\\\");
                    material.diffuseRefColorMap = filePath[filePath.length-1];
                } else if (line.startsWith("map_d ")) {
                    String[] filePath = line.split("\\\\");
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
    
    private static void addBuffersToObject(Object object, List<Face>faceList,
                                           List<Vector3f>vertexList,
                                           List<Vector3f>vertexTextureList,
                                           List<Vector3f>vertexNormalList) {
        System.out.println("Erzeuge Buffer...");


        //Erzeuge vertex Buffer
        FloatBuffer vertexData = BufferUtils.createFloatBuffer(faceList.size()*3*3*3);
        //Erzeuge index Buffer
        IntBuffer indexData = BufferUtils.createIntBuffer(faceList.size()*3);
        //Schreibe Indexdaten aus der Index Liste in den Index Buffer
        Iterator<Face> faceIterator = faceList.listIterator();
        //Durchlaufe alle Faces

        System.out.println("Durchlaufe Faces und erzeuge Vertex Buffer...");
        int counter= 0;
        while(faceIterator.hasNext()) {
            //Speichere aktuelles Face zwischen
            Face currentFace = faceIterator.next();

            //Hole vertexCoordinaten zum jeweiligen vertexIndizies
            vertexList.get((int)currentFace.vertexIndizies.x).store(vertexData);            
            //check if the model has texture coordinates
            if(currentFace.vertexTextureIndizies.length()!=0)
                vertexTextureList.get((int)currentFace.vertexTextureIndizies.x).store(vertexData);
            else
                new Vector3f().store(vertexData);
            //check if the model has normals
            if(currentFace.vertexNormalIndizies.length()!=0)
                vertexNormalList.get((int)currentFace.vertexNormalIndizies.x).store(vertexData);
            else
                new Vector3f().store(vertexData);
            
            vertexList.get((int)currentFace.vertexIndizies.y).store(vertexData);
            //check if the model has texture coordinates
            if(currentFace.vertexTextureIndizies.length()!=0)
                vertexTextureList.get((int)currentFace.vertexTextureIndizies.y).store(vertexData);
            else
                new Vector3f().store(vertexData);
            //check if the model has normals
            if(currentFace.vertexNormalIndizies.length()!=0)
                vertexNormalList.get((int)currentFace.vertexNormalIndizies.y).store(vertexData);
            else
                new Vector3f().store(vertexData);

            vertexList.get((int)currentFace.vertexIndizies.z).store(vertexData);
            //check if the model has texture coordinates
            if(currentFace.vertexTextureIndizies.length()!=0)
                vertexTextureList.get((int)currentFace.vertexTextureIndizies.z).store(vertexData);
            else
                new Vector3f().store(vertexData);
            //check if the model has normals
            if(currentFace.vertexNormalIndizies.length()!=0)
                vertexNormalList.get((int)currentFace.vertexNormalIndizies.z).store(vertexData);
            else
                new Vector3f().store(vertexData);
            
/*            indexData.put((int)currentFace.vertexIndizies.x);
            indexData.put((int)currentFace.vertexIndizies.y);
            indexData.put((int)currentFace.vertexIndizies.z);*/
        }
        
        System.out.println("Erzeuge Indexbuffer...");
        for(int i=0; i<faceList.size()*3; i++) {
            indexData.put(i);
        }
        
        System.out.println("Setze Bufferposition auf 0...");
        vertexData.position(0);
        indexData.position(0);

        object.vertexBuffer = vertexData;
        object.indexBuffer = indexData;
    }
    
    /**
     * Creates an object lists. Each object in the list contains vertex and
     * material information
     * @param objFile ath to OBJ file
     * @return creates object list
     */
    public static List<Object> getObjectListFromOBJ(String objPath) {
        //Create new Object
        List<Object> objectList = new LinkedList<Object>();

        //Create lists to temporary save all vertices, texture coordinates and
        //normals from the obj file. Once filled, they sould never be changed!
        List<Vector3f> vertexList = new LinkedList<Vector3f>();
        List<Vector3f> vertexTextureList = new LinkedList<Vector3f>();
        List<Vector3f> vertexNormalList = new LinkedList<Vector3f>();
        
        //Read the obj file and parse all vertices, texture coordinates
        //and normals from the obj file. Save them in the lists created above.
        try (BufferedReader objBufferedReader = new BufferedReader(new FileReader(objPath))) {
            //Read obj file line by line
            String line = "";
            int i=1;
            System.out.println("Read OBJ-File line by line and parse all"
                    + "vertices, texture coordinates and normals...");

            while((line = objBufferedReader.readLine()) != null) {
                //Parse verticex coordinates (v)
                if(line.startsWith("v ")) {
                    //Spalt Zeile in Elemente anhand von Leerzeichen auf
                    String[] lineElements = line.split(" {1,}");
                    try{
                        if(!lineElements[1].isEmpty() && !lineElements[2].isEmpty() && !lineElements[3].isEmpty()) {
                            Vector3f vertexCoordinates = new Vector3f(Float.parseFloat(lineElements[1]), Float.parseFloat(lineElements[2]), Float.parseFloat(lineElements[3]));
                            vertexList.add(vertexCoordinates);
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
                            vertexTextureList.add(vertexTextureCoordinates);
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
                        vertexNormalList.add(vertexNormals);
                    }
                }
                i++;
            } //end of while loop
        } catch (IOException e) {
            System.out.println("Problem beim Lesen der OBJ-Datei! Das Programm wird beendet.");
            System.exit(0);
        }
        
        //List to temorary save the faces of an object.
        //The list is deleted and newly created for each object
        List<Face> faceList = null;
        
        //Oject to add to the object lists. The object is added to the object
        //list after parsing of an object ist done. Afer parsing of an object
        //is done, this reference is overwritten by a new object
        Object object = null;
        
        //Read the obj file and parse all faces
        try (BufferedReader objBufferedReader = new BufferedReader(new FileReader(objPath))) {
            //Read obj file line by line
            String line = "";
            int i=1;
            System.out.println("Read OBJ-File line by line and parse all"
                    + "faces to objects...");

            while((line = objBufferedReader.readLine()) != null) {
                if(line.startsWith("usemtl ")) { //New object detected. Create a new facelist and a new Object
                    if(object!=null) {
                        addBuffersToObject(object, faceList, vertexList, vertexTextureList, vertexNormalList);
                        objectList.add(object);
                    }
                    object = new Object();
                    faceList = new LinkedList<Face>();
                } else if(line.startsWith("s ")) { //Parse smoothing group
                    if(line.split(" {1,}")[1].equals("off"))
                        object.smothingGroup = 0;
                    else
                        object.smothingGroup = Integer.parseInt(line.split(" {1,}")[1]);
                }
                
                //Parse faces into facelist
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
                        faceList.add(new Face(faceVertexIndizies, faceTextureIndizies, faceNormalIndizies));
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
                            faceList.add(new Face(faceVertexIndizies, faceTextureIndizies, faceNormalIndizies));
                        } catch(ArrayIndexOutOfBoundsException e) {
                            System.out.println("Read error while creating Face two at line: "+i);
                            System.out.println(e);
                        }
                    }
                }
                i++;
            } //end of while loop
            //Add last object to object list
            if(object!=null) {
                addBuffersToObject(object, faceList, vertexList, vertexTextureList, vertexNormalList);
                objectList.add(object);
            }
        } catch (IOException e) {
            System.out.println("Problem beim Lesen der OBJ-Datei! Das Programm wird beendet.");
            System.exit(0);
        }
        
        return objectList;
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