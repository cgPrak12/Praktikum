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
    public static List<ModelPart> importFromBlender(String objFile, String mtlFile, String texturePath) {
        List<Material> materialList = getMaterialListFromMTL(mtlFile, texturePath);
        List<ModelPart> modelPartList = getModelPartListFromOBJ(objFile, materialList);

/*        Iterator<ModelPart> modelPartListIterator = modelPartList.listIterator();
        while(modelPartListIterator.hasNext()) {
            System.out.println(modelPartListIterator.next());
        }*/
        
        return modelPartList;
    }
    
    public static List<Material> getMaterialListFromMTL(String mtlPath, String texturePath) {
        System.out.println("Reading MTL-file and creating material objects...");
        List<Material> materialList = new LinkedList<Material>();
        
       //Open File
        try (BufferedReader objBufferedReader = new BufferedReader(new FileReader(mtlPath))) {
            //Loop through file line by line
            String line = "";
            
            String materialLibrary = "default";
            Material material = null;
            while((line = objBufferedReader.readLine()) != null) {
                //add previos used material object to list if one exists
                //generate a new material object
                //parse material name
                if(line.startsWith("newmtl ")) {
                    if(material!=null) {
                        material.loadTextures(texturePath);
                        materialList.add(material);
                    }
                    material = new Material();
                    material.materialName = line.split(" {1,}")[1];
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
                    String[] filePath = line.split(" {1,}");
                    if(filePath.length>=2 && filePath[1].contains("\\")) {
                        filePath = line.split("\\\\");
                        material.diffuseRefColorMap = filePath[filePath.length-1];
                    } else if(filePath.length>=2) {
                        material.diffuseRefColorMap = filePath[1];
                    }
                } else if (line.startsWith("map_d ")) {
                    String[] filePath = line.split(" {1,}");
                    if(filePath.length>=2 && filePath[1].contains("\\")) {
                        filePath = line.split("\\\\");
                        material.dissolveFactColorMap = filePath[filePath.length-1];
                    } else if(filePath.length>=2) {
                        material.dissolveFactColorMap = filePath[1];
                    }
                } else if (line.startsWith("map_Ks ")) {
                    String[] filePath = line.split(" {1,}");
                    if(filePath.length>=2 && filePath[1].contains("\\")) {
                        filePath = line.split("\\\\");
                        material.specularRefColorMap = filePath[filePath.length-1];
                    } else if(filePath.length>=2) {
                        material.specularRefColorMap = filePath[1];
                    }
                }
            } //end of while loop
            //add the last material object to the list (its not being added in the while loop!)
            if(material!=null) {
                material.loadTextures(texturePath);
                materialList.add(material);
            }
        } catch (IOException e) {
            System.out.println("Problem beim Lesen der OBJ-Datei! Das Programm wird beendet.");
            System.exit(0);
        }     
        
        return materialList;
    }
    
    /**
     * Creates an modelPart lists. Each modelPart in the list contains vertex and
     * material information
     * @param objFile ath to OBJ file
     * @return creates modelPart list
     */
    public static List<ModelPart> getModelPartListFromOBJ(String objPath, List<Material> materialList) {
        //Create new ModelPart
        List<ModelPart> modelPartList = new LinkedList<ModelPart>();

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
            System.out.println("Read OBJ-File line by line and parse all "
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
        
        //List to temorary save the faces of an modelPart.
        //The list is deleted and newly created for each modelPart
        List<Face> faceList = null;
        
        //Oject to add to the modelPart lists. The modelPart is added to the modelPart
        //list after parsing of an modelPart ist done. Afer parsing of an modelPart
        //is done, this reference is overwritten by a new modelPart
        PreStageModelPart preStageModelPart = null;
        
        //Read the obj file and parse all faces
        try (BufferedReader objBufferedReader = new BufferedReader(new FileReader(objPath))) {
            //Read obj file line by line
            String line = "";
            int i=1;
            System.out.println("Read OBJ-File line by line and parse all "
                    + "faces to modelParts...");

            while((line = objBufferedReader.readLine()) != null) {
                if(line.startsWith("usemtl ")) { //New modelPart detected. Create a new facelist and a new ModelPart
                    if(preStageModelPart!=null) {
                        preStageModelPart.createBuffers(faceList, vertexList, vertexTextureList, vertexNormalList);
                        modelPartList.add(preStageModelPart.createModelPart(materialList));
                    }
                    System.out.println("Create preStageModelPart...");
                    preStageModelPart = new PreStageModelPart();
                    faceList = new LinkedList<Face>();
                    
                    preStageModelPart.materialName = line.split(" {1,}")[1];
                } else if(line.startsWith("s ")) { //Parse smoothing group
                    if(line.split(" {1,}")[1].equals("off"))
                        preStageModelPart.smoothingGroup = 0;
                    else
                        preStageModelPart.smoothingGroup = Integer.parseInt(line.split(" {1,}")[1]);
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
            //Add last modelPart to modelPart list
            if(preStageModelPart!=null) {
                preStageModelPart.createBuffers(faceList, vertexList, vertexTextureList, vertexNormalList);
                modelPartList.add(preStageModelPart.createModelPart(materialList));
            }
        } catch (IOException e) {
            System.out.println("Problem beim Lesen der OBJ-Datei! Das Programm wird beendet.");
            System.exit(0);
        }
        
        return modelPartList;
    }
    

    public static Geometry getGrid(String heightmap) {
        float heightfield[][][] = Util.getImageContents(heightmap);
        int maxX = heightfield[0].length;
        int maxZ = heightfield.length;
        
        int vertexSize = 6;
        FloatBuffer vertices = BufferUtils.createFloatBuffer(vertexSize * maxX * maxZ);
        for(int z=0; z < maxZ; ++z) {
            for(int x=0; x < maxX; ++x) {
                vertices.put(1e-2f * (float)x); // 1e-2 = 1 * 10 ^ -2 = 0.01
                vertices.put(0.75f*heightfield[z][x][0]);
                vertices.put(1e-2f * (float)z);
                
                vertices.put(0);
                vertices.put(0);
                vertices.put(0);
            }
        }
        
        IntBuffer indices = BufferUtils.createIntBuffer(3 * 2 * (maxX - 1) * (maxZ - 1));
        for(int z=0; z < maxZ - 1; ++z) {
            for(int x=0; x < maxX - 1; ++x) {
                indices.put(z * maxX + x);
                indices.put((z + 1) * maxX + x + 1);
                indices.put(z * maxX + x + 1);
                
                indices.put(z * maxX + x);
                indices.put((z + 1) * maxX + x);
                indices.put((z + 1) * maxX + x + 1);
            }
        }
        
        indices.position(0);
        for(int i=0; i < indices.capacity();) {
            int index0 = indices.get(i++);
            int index1 = indices.get(i++);
            int index2 = indices.get(i++);
            
            vertices.position(vertexSize * index0);
            Vector3f p0 = new Vector3f();
            p0.load(vertices);
            vertices.position(vertexSize * index1);
            Vector3f p1 = new Vector3f();
            p1.load(vertices);
            vertices.position(vertexSize * index2);
            Vector3f p2 = new Vector3f();
            p2.load(vertices);
            
            //System.out.println(p0 + " " + p1 + " " + p2);
            
            Vector3f a = Vector3f.sub(p1, p0, null);
            Vector3f b = Vector3f.sub(p2, p0, null);
            Vector3f normal = Vector3f.cross(a, b, null);
            normal.normalise();
            
            vertices.position(vertexSize * index0 + 3);
            normal.store(vertices);
        }
        
        vertices.position(0);
        indices.position(0);
        Geometry geo = new Geometry();
        geo.setIndices(indices, GL_TRIANGLES);
        geo.setVertices(vertices);
        return geo;
    }
    
    
    
    
        public static Geometry genTerrain(float[][][] terra) {
        	
        	int vertexSize = 7;
        	int maxX = terra.length;
        	int maxZ = terra[0].length; 	
        	
           	FloatBuffer vertices = BufferUtils.createFloatBuffer(vertexSize*maxX*maxZ);

        	
        	
           	// Gen Vbuffer
           	for(int z=0; z < maxZ; ++z) {
                for(int x=0; x < maxX; ++x) {
                	vertices.put(1e-2f * (float)x);
                	vertices.put(terra[x][z][0]);
                	vertices.put(1e-2f * (float)z);
                	
                	vertices.put(terra[x][z][1]);	// norm.x
                	vertices.put(terra[x][z][2]);	// norm.y
                	vertices.put(terra[x][z][3]);	// norm.z
                	vertices.put(terra[x][z][4]);

                }                	    
           	}
           	
           	
           	// Gen IndexBuffer
            IntBuffer indices = BufferUtils.createIntBuffer(3 * 2 * (maxX - 1) * (maxZ - 1));
            for(int z=0; z < maxZ - 1; ++z) {
                for(int x=0; x < maxX - 1; ++x) {
                    indices.put(z * maxX + x);
                    indices.put((z + 1) * maxX + x + 1);
                    
                    indices.put(z * maxX + x + 1);
                    
                    indices.put(z * maxX + x);
                    indices.put((z + 1) * maxX + x);
                    indices.put((z + 1) * maxX + x + 1);
                }
            }
            
            // Gen norms
            indices.position(0);
//            for(int i=0; i < indices.capacity();) {
//                int index0 = indices.get(i++);
//                int index1 = indices.get(i++);
//                int index2 = indices.get(i++);
//                
//                vertices.position(vertexSize * index0);
//                Vector3f p0 = new Vector3f();
//                p0.load(vertices);
//                vertices.position(vertexSize * index1);
//                Vector3f p1 = new Vector3f();
//                p1.load(vertices);
//                vertices.position(vertexSize * index2);
//                Vector3f p2 = new Vector3f();
//                p2.load(vertices);
//                
//                Vector3f a = Vector3f.sub(p1, p0, null);
//                Vector3f b = Vector3f.sub(p2, p0, null);
//                Vector3f normal = Vector3f.cross(a, b, null);
//                normal.normalise();
//                
//                vertices.position(vertexSize * index0 + 3);
//                normal.store(vertices);
//            }
           	
           	
           	
           	
            vertices.position(0);
            indices.position(0);
            Geometry geo = new Geometry();
            geo.setIndices(indices, GL_TRIANGLES);
            geo.setVertices(vertices);
        	geo.addVertexAttribute(ShaderProgram.ATTR_POS, 3, 0);
        	geo.addVertexAttribute(ShaderProgram.ATTR_NORMAL, 4, 12);

            return geo;	
        }
 
        /** Erzeugt ein MxNGrid in der XZ-Ebene
    	 * 
    	 * @param m breite
    	 * @param n länge
    	 * @return Grid : Geometry */
    	public static Geometry createMxNGrid(int m, int n)
    	{
    		int vaid = glGenVertexArrays();
    		glBindVertexArray(vaid);

    		// VertexBufferArray erstellen
    		float[] vertices = new float[2 * m * n];
    		int count = 0;

    		for (int y = 0; y < n; y++)
    		{
    			for (int x = 0; x < m; x++)
    			{
    				vertices[count++] =  y;
    				vertices[count++] =  x;
    			}
    		}

    		// IndexBufferArray erstellen
    		int[] indices = new int[((m - 1) * 2 + 3) * (n - 1)];

    		count = 0;

    		for (int i = 0; i < n - 1; i++)
    		{
    			for (int j = 0; j < m; j++)
    			{

    				indices[count++] = i * (m) + j + m;
    				indices[count++] = i * (m) + j;

    			}
    			indices[count++] = -1;

    		}

    		// Buffer erzeugen
    		FloatBuffer fbu = BufferUtils.createFloatBuffer(vertices.length);
    		IntBuffer ibu = BufferUtils.createIntBuffer(indices.length);

    		fbu.put(vertices);
    		fbu.flip();
    		ibu.put(indices);
    		ibu.flip();

    		// Geometry erzeugen und setzen
    		Geometry geo = new Geometry();
    		geo.setVertices(fbu);
    		geo.setIndices(ibu, GL_TRIANGLE_STRIP);
    		geo.addVertexAttribute(ShaderProgram.ATTR_POS, 2, 0);

    		return geo;

    	}

    	/** Erzeugt ein Grid in der XZ-Ebene
    	 * 
    	 * @param m breite
    	 * @param n länge
    	 * @return Grid : Geometry */
    	public static Geometry createGrid(int x, int y)
    	{
    		int vaid = glGenVertexArrays();
    		glBindVertexArray(vaid);

    		float[] vertices = new float[2 * x * y];
    		int count = 0;

    		for (int j = 0; j < y; j++)
    		{
    			for (int i = 0; i < x; i++)
    			{
    				vertices[count++] = i;
    				vertices[count++] = j;

    			}
    		}

    		int[] indices = new int[6 * x * y];
    		count = 0;
    		for (int i = 0; i < (y - 1); i++)
    		{
    			for (int j = 0; j < (x - 1); j++)
    			{
    				indices[count++] = i + j * (x); // 0 + 0*10 = 0
    				indices[count++] = i + (j + 1) * (x); // 0+1*10 = 10
    				indices[count++] = i + 1 + (j + 1) * (x); // 1+1*10 = 11

    				indices[count++] = i + j * (x); // 0 + 0*10 = 0
    				indices[count++] = i + 1 + (j + 1) * (x); // 1+1*10 = 11
    				indices[count++] = i + j * (x) + 1; // 0 + 0*10+1 = 1
    			}
    		}

    		FloatBuffer fbu = BufferUtils.createFloatBuffer(vertices.length);
    		IntBuffer ibu = BufferUtils.createIntBuffer(indices.length);

    		fbu.put(vertices);
    		fbu.flip();
    		ibu.put(indices);
    		ibu.flip();

    		Geometry geo = new Geometry();
    		geo.setVertices(fbu);
    		geo.setIndices(ibu, GL_TRIANGLES);
    		geo.addVertexAttribute(ShaderProgram.ATTR_POS, 2, 0);


    		return geo;
    	}

    	/** Erzeugt ein Grid in der XZ-Ebene
    	 * 
    	 * @param m breite
    	 * @param n länge
    	 * @return Grid : Geometry */
    	public static Geometry createGridTex(int x, int y)
    	{
    		int vaid = glGenVertexArrays();
    		glBindVertexArray(vaid);

    		float[] vertices = new float[6 * x * y];
    		int count = 0;

    		for (int i = 0; i < x; i++)
    		{
    			for (int j = 0; j < y; j++)
    			{
    				vertices[count++] = i;
    				vertices[count++] = j;
    				vertices[count++] = ((float) 1 / (float) x) * (float) i;
    				vertices[count++] = ((float) 1 / (float) y) * (float) j;
    			}
    		}

    		int[] indices = new int[6 * x * y];
    		count = 0;
    		for (int i = 0; i < (y - 1); i++)
    		{
    			for (int j = 0; j < (x - 1); j++)
    			{
    				indices[count++] = i + j * (x); // 0 + 0*10 = 0
    				indices[count++] = i + (j + 1) * (x); // 0+1*10 = 10
    				indices[count++] = i + 1 + (j + 1) * (x); // 1+1*10 = 11

    				indices[count++] = i + j * (x); // 0 + 0*10 = 0
    				indices[count++] = i + 1 + (j + 1) * (x); // 1+1*10 = 11
    				indices[count++] = i + j * (x) + 1; // 0 + 0*10 = 0
    			}
    		}

    		FloatBuffer fbu = BufferUtils.createFloatBuffer(vertices.length);
    		IntBuffer ibu = BufferUtils.createIntBuffer(indices.length);

    		fbu.put(vertices);
    		fbu.flip();
    		ibu.put(indices);
    		ibu.flip();

    		Geometry geo = new Geometry();
    		geo.setVertices(fbu);
    		geo.setIndices(ibu, GL_TRIANGLES);
    		geo.addVertexAttribute(ShaderProgram.ATTR_POS, 2, 0);
    		geo.addVertexAttribute(ShaderProgram.ATTR_TEX, 2, 2 * 4);

    		return geo;
    	}

    	/** Erzeugt ein L-Grid in der XZ-Ebene Kante des Ls liegt "oben rechts"
    	 * 
    	 * @param length Länge einer Kante
    	 * @return TopRightL Geometrie */

    	public static Geometry createTopRight(int length)
    	{

    		int vaid = glGenVertexArrays();
    		glBindVertexArray(vaid);

    		// Vertex und Index Arrays mit passender Größe erzeugen
    		float[] vertices = new float[7 * length + 7 * (length - 1)];
    		int[] indices = new int[2 * length + (length - 2) * 4 + (length - 2) * 10];
    		int count = 0;

    		// VertexBufferArray beschreiben
    		for (int x = 0; x < length; x++)
    		{
    			for (int y = 0; y < 2; y++)
    			{
    				vertices[count++] =  y;
    				vertices[count++] = x;
    			}
    		}

    		for (int x = length - 2; x < length; x++)
    		{
    			for (int y = 2; y < length; y++)
    			{
    				vertices[count++] =  y;
    				vertices[count++] =  x;
    			}
    		}

    		// IndexBufferArray beschreiben als TRIANGLE_STRIP

    		int icount = 0;
    		for (int i = 0; i < 2 * length; i += 2)
    		{
    			indices[icount++] = i + 1;
    			indices[icount++] = i;
    		}
    		indices[icount++] = -1;
    		indices[icount++] = 2 * length;
    		indices[icount++] = 2 * length - 3;
    		indices[icount++] = 3 * length - 2;
    		indices[icount++] = 2 * length - 1;

    		indices[icount++] = -1;
    		for (int j = 0; j < length - 3; j++)
    		{
    			for (int i = 1; i >= 0; i--)
    			{
    				indices[icount++] = 2 * length + i + j;
    			}
    			for (int i = 1; i >= 0; i--)
    			{
    				indices[icount++] = 3 * length + i + j - 2;
    			}
    			indices[icount++] = -1;
    		}

    		// Buffer erstellen
    		FloatBuffer fbu = BufferUtils.createFloatBuffer(vertices.length);
    		IntBuffer ibu = BufferUtils.createIntBuffer(indices.length);
    		fbu.put(vertices);
    		fbu.flip();
    		ibu.put(indices);
    		ibu.flip();

    		// Geometry erzeugen
    		Geometry geo = new Geometry();

    		geo.setVertices(fbu);
    		geo.setIndices(ibu, GL_TRIANGLE_STRIP);
    		geo.addVertexAttribute(ShaderProgram.ATTR_POS, 2, 0);

    		return geo;
    	}

    	/** Erzeugt ein L-Grid in der XZ-Ebene Kante des Ls liegt "unten rechts"
    	 * 
    	 * @param length Länge "einer" Kante
    	 * @return BottomRechtsL Geometrie */

    	public static Geometry createBottomRight(int length)
    	{

    		int vaid = glGenVertexArrays();
    		glBindVertexArray(vaid);

    		float[] vertices = new float[7 * length + 7 * (length - 1)];
    		int[] indices = new int[2 * length + (length - 2) * 4 + (length - 2) * 10];
    		int count = 0;

    		for (int x = 0; x < length; x++)
    		{
    			for (int y = 0; y < 2; y++)
    			{
    				vertices[count++] =  y;
    				vertices[count++] =  x;
    			}
    		}

    		for (int x = 0; x < 2; x++)
    		{
    			for (int y = 2; y < length; y++)
    			{
    				vertices[count++] =  y;
    				vertices[count++] =  x;

    			}
    		}

    		int icount = 0;
    		for (int i = 0; i < 2 * length; i += 2)
    		{
    			indices[icount++] = i + 1;
    			indices[icount++] = i;
    		}
    		indices[icount++] = -1;
    		indices[icount++] = 2 * length;
    		indices[icount++] = 1;
    		indices[icount++] = 3 * length - 2;
    		indices[icount++] = 3;

    		indices[icount++] = -1;
    		for (int j = 0; j < length - 3; j++)
    		{
    			for (int i = 1; i >= 0; i--)
    			{
    				indices[icount++] = 2 * length + i + j;
    			}
    			for (int i = 1; i >= 0; i--)
    			{
    				indices[icount++] = 3 * length + i + j - 2;
    			}
    			indices[icount++] = -1;
    		}

    		FloatBuffer fbu = BufferUtils.createFloatBuffer(vertices.length);
    		IntBuffer ibu = BufferUtils.createIntBuffer(indices.length);
    		fbu.put(vertices);
    		fbu.flip();
    		ibu.put(indices);
    		ibu.flip();

    		Geometry geo = new Geometry();

    		geo.setVertices(fbu);
    		geo.setIndices(ibu, GL_TRIANGLE_STRIP);
    		geo.addVertexAttribute(ShaderProgram.ATTR_POS, 2, 0);

    		return geo;
    	}

    	/** Erzeugt ein L-Grid in der XZ-Ebene Kante des Ls liegt "oben links"
    	 * 
    	 * @param length Länge einer Kante
    	 * @return TopLeftL Geometrie */
    	public static Geometry createTopLeft(int length)
    	{

    		int vaid = glGenVertexArrays();
    		glBindVertexArray(vaid);

    		float[] vertices = new float[7 * length + 7 * (length - 1)];
    		int[] indices = new int[(length - 2) * 5 + 2 * length + 1];
    		int count = 0;

    		for (int x = 0; x < length; x++)
    		{
    			for (int y = 0; y < 2; y++)
    			{
    				vertices[count++] =  y;
    				vertices[count++] =  x;

    			}
    		}

    		for (int x = length - 2; x < length; x++)
    		{
    			for (int y = -1; y > -length + 1; y--)
    			{
    				vertices[count++] = y;
    				vertices[count++] = x;

    			}
    		}

    		int icount = 0;
    		for (int i = 0; i < 2 * length; i += 2)
    		{
    			indices[icount++] = i + 1;
    			indices[icount++] = i;
    		}
    		indices[icount++] = -1;

    		indices[icount++] = 2 * length - 4;
    		indices[icount++] = 2 * length;
    		indices[icount++] = 2 * length - 2;
    		indices[icount++] = 3 * length - 2;

    		indices[icount++] = -1;
    		for (int j = 0; j < length - 3; j++)
    		{
    			for (int i = 0; i < 2; i++)
    			{
    				indices[icount++] = 2 * length + i + j;
    			}
    			for (int i = 0; i < 2; i++)
    			{
    				indices[icount++] = 3 * length + i + j - 2;
    			}
    			indices[icount++] = -1;
    		}

    		FloatBuffer fbu = BufferUtils.createFloatBuffer(vertices.length);
    		IntBuffer ibu = BufferUtils.createIntBuffer(indices.length);
    		fbu.put(vertices);
    		fbu.flip();
    		ibu.put(indices);
    		ibu.flip();

    		Geometry geo = new Geometry();

    		geo.setVertices(fbu);
    		geo.setIndices(ibu, GL_TRIANGLE_STRIP);
    		geo.addVertexAttribute(ShaderProgram.ATTR_POS, 2, 0);

    		return geo;
    	}

    	/** Erzeugt ein L-Grid in der XZ-Ebene Kante des Ls liegt "unten links"
    	 * 
    	 * @param length Länge einer Kante
    	 * @return BottomLeftL Geometrie */

    	public static Geometry createBottomLeft(int length)
    	{

    		int vaid = glGenVertexArrays();
    		glBindVertexArray(vaid);

    		float[] vertices = new float[7 * length + 7 * (length - 1)];
    		int[] indices = new int[(length - 2) * 5 + 2 * length + 1];
    		int count = 0;

    		for (int x = 0; x < length; x++)
    		{
    			for (int y = 0; y < 2; y++)
    			{
    				vertices[count++] =  y;
    				vertices[count++] =  x;
    				
    			}
    		}

    		for (int x = 0; x < 2; x++)
    		{
    			for (int y = -1; y > -length + 1; y--)
    			{
    				vertices[count++] =  y;
    				vertices[count++] =  x;

    			}
    		}

    		int icount = 0;
    		for (int i = 0; i < 2 * length; i += 2)
    		{
    			indices[icount++] = i + 1;
    			indices[icount++] = i;
    		}
    		indices[icount++] = -1;

    		indices[icount++] = 0;
    		indices[icount++] = 2 * length;
    		indices[icount++] = 2;
    		indices[icount++] = 3 * length - 2;

    		indices[icount++] = -1;
    		for (int j = 0; j < length - 3; j++)
    		{
    			for (int i = 0; i < 2; i++)
    			{
    				indices[icount++] = 2 * length + i + j;
    			}
    			for (int i = 0; i < 2; i++)
    			{
    				indices[icount++] = 3 * length + i + j - 2;
    			}
    			indices[icount++] = -1;
    		}

    		FloatBuffer fbu = BufferUtils.createFloatBuffer(vertices.length);
    		IntBuffer ibu = BufferUtils.createIntBuffer(indices.length);
    		fbu.put(vertices);
    		fbu.flip();
    		ibu.put(indices);
    		ibu.flip();

    		Geometry geo = new Geometry();

    		geo.setVertices(fbu);
    		geo.setIndices(ibu, GL_TRIANGLE_STRIP);
    		geo.addVertexAttribute(ShaderProgram.ATTR_POS, 2, 0);


    		return geo;
    	}

    	/**
    	 * Erzeugt die outer Triangles die am Rand jedes Rings liegen um 
    	 * Geometrielücken zu vermeiden
    	 * 
    	 * @param length Länge eined Rings
    	 * @return outerTriangle Geometry
    	 */
    	public static Geometry outerTriangle(int length)
    	{

    		int vaid = glGenVertexArrays();
    		glBindVertexArray(vaid);

    		float[] vertices = new float[4 * length *2];
    		int[] indices = new int[4 * length + 4];
    		int count = 0;

    		for (int y = 0; y < length; y += length - 1)
    		{
    			for (int i = 0; i < length; i++)
    			{
    				vertices[count++] =  i;
    				vertices[count++] =  y;
    			}
    		}

    		for (int y = 0; y < length; y += length - 1)
    		{
    			for (int i = 0; i < length; i++)
    			{
    				vertices[count++] =  y;
    				vertices[count++] =  i;
    			}
    		}

    		// Indices
    		int icount = 0;

    		// 1
    		for (int i = 0; i < length; i++)
    		{
    			indices[icount++] = i;
    		}
    		indices[icount++] = -1;
    		// 2
    		for (int i = 3 * length; i < 4 * length; i++)
    		{
    			indices[icount++] = i;
    		}
    		indices[icount++] = -1;
    		// 3
    		for (int i = 2 * length - 1; i >= length; i--)
    		{
    			indices[icount++] = i;
    		}
    		indices[icount++] = -1;
    		// 4
    		for (int i = 3 * length - 1; i >= 2 * length; i--)
    		{
    			indices[icount++] = i;
    		}

    		FloatBuffer fbu = BufferUtils.createFloatBuffer(vertices.length);
    		IntBuffer ibu = BufferUtils.createIntBuffer(indices.length);
    		fbu.put(vertices);
    		fbu.flip();
    		ibu.put(indices);
    		ibu.flip();

    		Geometry geo = new Geometry();

    		geo.setVertices(fbu);
    		geo.setIndices(ibu, GL_TRIANGLE_STRIP);
    		geo.addVertexAttribute(ShaderProgram.ATTR_POS, 2, 0);

    		return geo;
    	}

    	 public static Geometry genTerrain(TerrainGrid terra) {
         	
         	int vertexSize = 7;
         	int maxX = terra.getBlock().length;
         	int maxZ = terra.getBlock().length; 	
         	
            	FloatBuffer vertices = BufferUtils.createFloatBuffer(vertexSize*maxX*maxZ);

         	
         	
            	// Gen Vbuffer
            	for(int z=0; z < maxZ; ++z) {
                 for(int x=0; x < maxX; ++x) {
                 	vertices.put( (float)x);
                 	vertices.put(terra.get(x, z, 0));
                 	vertices.put( (float)z);
                 									
                 	vertices.put(terra.get(x, z, 1));	// norm.x
                 	vertices.put(terra.get(x, z, 2));	// norm.y
                 	vertices.put(terra.get(x, z, 3));	// norm.z
                 	vertices.put(terra.get(x, z, 4));	// material

                 }                	    
            	}
            	
            	
            	// Gen IndexBuffer
             IntBuffer indices = BufferUtils.createIntBuffer(3 * 2 * (maxX - 1) * (maxZ - 1));
             for(int z=0; z < maxZ - 1; ++z) {
                 for(int x=0; x < maxX - 1; ++x) {
                     indices.put(z * maxX + x);
                     indices.put((z + 1) * maxX + x + 1);
                     
                     indices.put(z * maxX + x + 1);
                     
                     indices.put(z * maxX + x);
                     indices.put((z + 1) * maxX + x);
                     indices.put((z + 1) * maxX + x + 1);
                 }
             }
             
             vertices.position(0);
             indices.position(0);
             Geometry geo = new Geometry();
             geo.setIndices(indices, GL_TRIANGLES);
             geo.setVertices(vertices);
             return geo;	
         }
}