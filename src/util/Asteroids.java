package util;

import static opengl.GL.glGetUniformLocation;
import static opengl.GL.glUniform1f;
import static opengl.GL.glUniform3f;
import static opengl.OpenCL.CL_MEM_COPY_HOST_PTR;
import static opengl.OpenCL.CL_MEM_READ_WRITE;
import static opengl.OpenCL.clBuildProgram;
import static opengl.OpenCL.clCreateBuffer;
import static opengl.OpenCL.clCreateCommandQueue;
import static opengl.OpenCL.clCreateFromGLBuffer;
import static opengl.OpenCL.clCreateKernel;
import static opengl.OpenCL.clCreateProgramWithSource;
import static opengl.OpenCL.clEnqueueAcquireGLObjects;
import static opengl.OpenCL.clEnqueueNDRangeKernel;
import static opengl.OpenCL.clEnqueueReleaseGLObjects;
import static opengl.OpenCL.clFinish;
import static opengl.OpenCL.clReleaseCommandQueue;
import static opengl.OpenCL.clReleaseContext;
import static opengl.OpenCL.clReleaseKernel;
import static opengl.OpenCL.clReleaseMemObject;
import static opengl.OpenCL.clReleaseProgram;
import static opengl.OpenCL.create;

import java.nio.FloatBuffer;
import java.util.Random;

import opengl.GL;
import opengl.OpenCL.Device_Type;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CLCommandQueue;
import org.lwjgl.opencl.CLContext;
import org.lwjgl.opencl.CLDevice;
import org.lwjgl.opencl.CLKernel;
import org.lwjgl.opencl.CLMem;
import org.lwjgl.opencl.CLPlatform;
import org.lwjgl.opencl.CLProgram;
import org.lwjgl.opengl.Drawable;
import org.lwjgl.util.vector.Matrix4f;

/**
 * 
 * @author Nico und Sascha
 *
 */
public class Asteroids {
    
    //opencl pointer
    private CLContext context;
    private CLProgram program;
    private CLDevice device;
    private CLCommandQueue queue;
    private CLKernel kernel0;
    private CLKernel kernel1;
    
    //data
    private FloatBuffer posBuffer, veloBuffer;
    
    //opencl buffer
    private CLMem old_pos, new_pos, old_velos, new_velos;
    
    //asteroid settings
    private int count = 256;//(int)Math.pow(2, 10);//(int)Math.pow(2, n); //2^n to work properly with local memory
    private float clusterScale = 4;
    
    private float veloScale = 1f;
    
    //kernel settings
    private boolean swap = true;
    private int localWorkSize = 32;
    private final PointerBuffer gwz = BufferUtils.createPointerBuffer(1);
    private final PointerBuffer lwz = BufferUtils.createPointerBuffer(1);
    
    //asteroids geometry
    private Geometry asteroids_old;
    private Geometry asteroids_new;
    
    //shader
    private int diffuseTexture;
    private int specularTexture;
    private int shaderProgram;
    private int viewProjLoc;
    private int eyeLoc;
    private int diffTexLoc;
    private int specTexLoc;
    private int kaLoc;
    private int kdLoc;
    private int ksLoc;
    private int esLoc;
    private int caLoc;
    
    private final Matrix4f viewProj = new Matrix4f();
    
    /**
     * Asteroid-Partikelsystem
     * 
     * Die Implementation benutzt jeweils zwei Buffer fuer die Position und die Velocity.
     * Nach jedem Zeitschritt werden die Buffer getauscht.
     * Somit kommt es bei der parallelen Verarbeitung zu keinen Problemen.
     * Die Vertauschen wird ueber zwei Kernel und zwei Geometry Objekte geregelt.
     * Die Eingangsdaten sind in beiden Kernel jeweils vertauscht.
     * In der updateSimulation Methode wird im Wechsel immer ein Kernel aufgerufen.
     * In der draw Methode wird das zuletzt veraenderte Geometry Objekt gezeichnet.
     * Um die Asterioden zu Zeichnen wird 'OpenGL Instancing' verwendet. Im Vertexshader wird
     * die Welttranslation (Positionsbuffer) fuer jede Instanz auf jeden Vertex addiert.
     * @param device_type GPU /CPU
     * @param drawable OpenGL drawable
     * @throws LWJGLException
     */
    public Asteroids(Device_Type device_type, Drawable drawable) throws LWJGLException {
        
        this.createCLContext(device_type, Util.getFileContents("./shader/AsteroidSim.cl"), drawable);
        this.createData();
        this.createBuffer();
        this.createKernels();
        this.createShaderProgram();
        this.gwz.put(0, this.count);
        this.lwz.put(0, this.localWorkSize);   
    }
    
    /**
     * Das angeforderte Device wird gestezt (GPU /CPU) (wenn vorhanden sonst fehler)
     * Context, Queue und Program werden erstellt.
     * @param type
     * @param source
     * @param drawable
     * @throws LWJGLException
     */
    private void createCLContext(Device_Type type, String source, Drawable drawable) throws LWJGLException {
        
        int device_type;
        
        switch(type) {
        case CPU: device_type = CL10.CL_DEVICE_TYPE_CPU; break;
        case GPU: device_type = CL10.CL_DEVICE_TYPE_GPU; break;
        default: throw new IllegalArgumentException("Wrong device type!");
        }
        
        CLPlatform platform = null;
        
        for(CLPlatform plf : CLPlatform.getPlatforms()) {
            if(plf.getDevices(device_type) != null) {
                this.device = plf.getDevices(device_type).get(0);
                platform = plf;
                if(this.device != null) {
                    break;
                }
            }
        }
        
        this.context = create(platform, platform.getDevices(device_type), null, drawable);
        
        this.queue = clCreateCommandQueue(this.context, this.device, 0);
        
        this.program = clCreateProgramWithSource(this.context, source);

        clBuildProgram(this.program, this.device, "", null);
    }
    
    public int getShaderProgram() {
        return this.shaderProgram;
    }
    
    /**
     * Erzeugt das OpenGL Shaderprogram zum Rendern der Partikel
     */
    private void createShaderProgram() {
        
        shaderProgram = Util.createShaderProgram("./shader/Asteroid_VS.glsl", "./shader/Asteroid_FS.glsl");
        viewProjLoc = glGetUniformLocation(shaderProgram, "viewProj");
        diffTexLoc = glGetUniformLocation(shaderProgram, "diffuseTex");
        specTexLoc = glGetUniformLocation(shaderProgram, "specularTex");
        eyeLoc = glGetUniformLocation(shaderProgram, "eyePosition");
        kaLoc = glGetUniformLocation(shaderProgram, "k_a");
        kdLoc = glGetUniformLocation(shaderProgram, "k_dif");
        ksLoc = glGetUniformLocation(shaderProgram, "k_spec");
        esLoc = glGetUniformLocation(shaderProgram, "es");
        caLoc = glGetUniformLocation(shaderProgram, "c_a");
        GL.glUseProgram(shaderProgram);
        glUniform1f(kaLoc, 0.05f);
        glUniform1f(kdLoc, 0.6f);
        glUniform1f(ksLoc, 0.3f);
        glUniform1f(esLoc, 16.0f);
        glUniform3f(caLoc, 1.0f, 1.0f, 1.0f);
        
        diffuseTexture = Util.generateTexture("asteroid.jpg");
        specularTexture = Util.generateTexture("asteroid_spec.jpg");
    }
    
    /**
     * Erzeugt per "Zufall" initiale Positions und Geschwindigkeits Daten
     * Positions Layout[x, y, z, radius]
     * Velocity Layout[v_x, v_y, v_z, _]
     */
    private void createData() {
        
        this.asteroids_old = GeometryFactory.createSphere(1f, 16, 8);
        this.asteroids_new = GeometryFactory.createSphere(1f, 16, 8);
        
        this.posBuffer = BufferUtils.createFloatBuffer(4*count);
        this.veloBuffer = BufferUtils.createFloatBuffer(4*count);
        
        int i = 0;
        Random r = new Random(4);
        while(i < this.count) {
            
            float x = this.clusterScale * r.nextInt() / (float)Integer.MAX_VALUE;
            float y = this.clusterScale * r.nextInt() / (float)Integer.MAX_VALUE;
            float z = this.clusterScale * r.nextInt() / (float)Integer.MAX_VALUE;
            float radius = 0.05f + 0.2f*r.nextFloat();
            if(x*x + y*y + z*z < this.clusterScale*3/2f || x*x + y*y + z*z > clusterScale*3f) continue;
            this.posBuffer.put(x);posBuffer.put(y);posBuffer.put(z);posBuffer.put(radius);
            float mass = 1*radius*radius*radius*(float)(Math.PI / 4f * 3f);
            float vx = r.nextInt() / (float)Integer.MAX_VALUE * this.veloScale;
            float vy = r.nextInt() / (float)Integer.MAX_VALUE * this.veloScale;
            float vz = r.nextInt() / (float)Integer.MAX_VALUE * this.veloScale;
            this.veloBuffer.put(vx); this.veloBuffer.put(vy); this.veloBuffer.put(vz); this.veloBuffer.put(mass);
            i++;
        }
        
        this.posBuffer.position(0);
        this.veloBuffer.position(0);
    }

    /**
     * Ruft den jeweiligen Kernel auf um das Partikelsystem in den naechsten
     * Zeitschritt zu updaten
     * Wird in jedem Frame einmal aufgerufen.
     * @param deltaTime
     */
    public void updateSimulation(long deltaTime) {
        
        clEnqueueAcquireGLObjects(this.queue, this.new_pos, null, null);
        clEnqueueAcquireGLObjects(this.queue, this.old_pos, null, null);
        
        if(this.swap) {
            
            this.kernel0.setArg(5, 1e-3f*deltaTime);
            clEnqueueNDRangeKernel(this.queue, kernel0, 1, null, gwz, lwz, null, null);
            
        } else {
            
            this.kernel1.setArg(5, 1e-3f*deltaTime);
            clEnqueueNDRangeKernel(this.queue, kernel1, 1, null, gwz, lwz, null, null);
           
        }
        clEnqueueReleaseGLObjects(this.queue, this.new_pos, null, null);
        clEnqueueReleaseGLObjects(this.queue, this.old_pos, null, null);
        clFinish(this.queue);
        this.swap = !this.swap;
    }
    
    /**
     * Rendert das Partikelsystem
     * @param cam
     */
    public void draw(Camera cam) {
        
        GL.glUseProgram(shaderProgram);
        
        glUniform3f(this.eyeLoc, cam.getCamPos().x, cam.getCamPos().y, cam.getCamPos().z);       
        Matrix4f.mul(cam.getProjection(), cam.getView(), viewProj);  
        viewProj.store(Util.MAT_BUFFER);
        Util.MAT_BUFFER.position(0);
        GL.glUniformMatrix4(viewProjLoc, false, Util.MAT_BUFFER);
        
        GL.glActiveTexture(GL.GL_TEXTURE0 + 0);
        GL.glBindTexture(GL.GL_TEXTURE_2D, this.diffuseTexture);
        GL.glUniform1i(this.diffTexLoc, 0);
        
        GL.glActiveTexture(GL.GL_TEXTURE0 + 1);
        GL.glBindTexture(GL.GL_TEXTURE_2D, this.specularTexture);
        GL.glUniform1i(this.specTexLoc, 1);
        
        if(this.swap) {
            this.asteroids_old.draw();
        } else {
            this.asteroids_new.draw();
        }
    }
    
    /**
     * Erstellt alle notwendigen OpenCL Buffer
     */
    private void createBuffer() {
        this.asteroids_old.setInstanceBuffer(this.posBuffer, 4);
        this.asteroids_old.construct();
        this.asteroids_new.setInstanceBuffer(this.posBuffer, 4);
        this.asteroids_new.construct();
        
        this.old_velos = clCreateBuffer(this.context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, this.veloBuffer);
        this.new_velos = clCreateBuffer(this.context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, this.veloBuffer);
        this.new_pos = clCreateFromGLBuffer(this.context, CL_MEM_READ_WRITE, this.asteroids_new.getInstanceBuffer());
        this.old_pos = clCreateFromGLBuffer(this.context, CL_MEM_READ_WRITE, this.asteroids_old.getInstanceBuffer());
        
        this.posBuffer = null;
        this.veloBuffer = null;
    }
    
    /**
     * Erstellt zwei OpenCL Kernel
     */
    private void createKernels() {
        this.kernel0 = clCreateKernel(this.program, "asteroid_sim");
        this.kernel0.setArg(0, this.old_pos);
        this.kernel0.setArg(1, this.new_pos);
        this.kernel0.setArg(2, this.old_velos);
        this.kernel0.setArg(3, this.new_velos);
        this.kernel0.setArg(4, this.count);
        this.kernel0.setArg(5, 0f);
        
        this.kernel1 = clCreateKernel(this.program, "asteroid_sim");
        this.kernel1.setArg(0, this.new_pos);
        this.kernel1.setArg(1, this.old_pos);
        this.kernel1.setArg(2, this.new_velos);
        this.kernel1.setArg(3, this.old_velos);
        this.kernel1.setArg(4, this.count);
        this.kernel1.setArg(5, 0f);
    }
    
    /**
     * free memory
     */
    public void destroy() {
        clReleaseMemObject(this.new_pos);
        clReleaseMemObject(this.old_pos);
        clReleaseMemObject(this.new_velos);
        clReleaseMemObject(this.old_velos);
        clReleaseKernel(this.kernel0);
        clReleaseKernel(this.kernel1);
        clReleaseCommandQueue(this.queue);
        clReleaseProgram(this.program);
        clReleaseContext(this.context);
        this.asteroids_new.delete();
        this.asteroids_old.delete();
    }
}
