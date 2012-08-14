package util;

import opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;

/**
 * Usage:
 * - Initial einmal:
 *  - ShaderProgram erzeugen mit folgenden FS out Variablen:
 *   - out vec4 position;   // position.xyz enthalten Weltkoordinaten, position.w enthaelt Abstand zur Kamera
 *   - out vec3 normal;     // enthaelt Normale in Weltkoordinaten
 *   - out vec4 color;      // enthaelt diffuse Farbe (Textur oder Vertexfarbe)
 *  - DeferredShader.registerShaderProgram(...); (ShaderProgram fuer Nutzung mit DeferredShader registrieren)
 * - In jedem Frame fuer jedes ShaderProgram:
 *  - ShaderProgram.use();      (ShaderProgram aktivieren)
 *  - setUniform*(...);         (alle Unidforms setzen)
 *  - DeferredShader.bind();    (Deferred Shading aktivieren)
 *  - optional: DeferredShader.clear(); (Bild loeschen)
 *  - Alle Geometrien zeichnen
 * - Jeden Fram ganz zum Schluss:
 *  - DeferredShader.finish();
 *  - DeferredShader.drawTexture(Texture ...);  (Textur direkt zeichnen)
 *  - oder
 *  - DeferredShader.get*Texture();             (Textur weiter benutzen)
 * @author nico3000
 */
public class DeferredShader {
    private ShaderProgram drawTextureSP = new ShaderProgram("./shader/ScreenQuad_VS.glsl", "./shader/CopyTexture_FS.glsl");
    private Geometry screenQuadGeo = GeometryFactory.createScreenQuad();
    
    private FrameBuffer frameBuffer = new FrameBuffer();
    
	private Texture texPosition;
	private Texture texNormal;
	private Texture texVertexColor;
	private Texture texSpec;
	private Texture skyColor;
	private Texture texShadow;
	private Texture texBump;
    
    public DeferredShader() {
    }
   
    public void init(int unitOffset) {
    	this.init(unitOffset, GL.WIDTH, GL.HEIGHT);
    }
    
    public void init(int unitOffset, int width, int height) {
    	frameBuffer.init(true, width, height);
        
    	// generate textures
    	texPosition    = new Texture(GL11.GL_TEXTURE_2D,  unitOffset +0);
    	texPosition.bind();
    	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
    	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
    	
    	texNormal 	   = new Texture(GL11.GL_TEXTURE_2D,  unitOffset +1);
    	texVertexColor = new Texture(GL11.GL_TEXTURE_2D,  unitOffset +2);
    	texSpec        = new Texture(GL11.GL_TEXTURE_2D,  unitOffset +3);
    	skyColor       = new Texture(GL11.GL_TEXTURE_2D,  unitOffset +4);
    	texShadow = 	 new Texture(GL11.GL_TEXTURE_2D,  unitOffset +5);
    	texBump = 	 new Texture(GL11.GL_TEXTURE_2D,  unitOffset +6);
    	
    	frameBuffer.addTexture(texPosition, GL30.GL_RGBA32F, GL11.GL_RGBA);
    	frameBuffer.addTexture(texNormal, GL30.GL_RGBA16F, GL11.GL_RGBA);
    	frameBuffer.addTexture(texVertexColor, GL11.GL_RGBA, GL11.GL_RGBA);
    	frameBuffer.addTexture(texSpec, GL11.GL_RGBA, GL11.GL_RGBA);
    	frameBuffer.addTexture(skyColor, GL11.GL_RGBA, GL11.GL_RGBA);
    	frameBuffer.addTexture(texShadow, GL30.GL_RGBA32F, GL11.GL_RGBA);
    	frameBuffer.addTexture(texBump, GL11.GL_RGBA, GL11.GL_RGBA);
    	
    	frameBuffer.drawBuffers();
    }
    
    public void bind() {
   		frameBuffer.bind();
    }
    
    public void registerShaderProgram(ShaderProgram shaderProgram) {
    	shaderProgram.use();
        frameBuffer.BindFragDataLocations(shaderProgram, "position", "normal", "color", "spec", "skyColor","shadowCoord","bumpColor");        
    }
    
    public void clear() {
    	frameBuffer.clearColor(); 
    }
    
    public void finish() {
    	frameBuffer.unbind();
    }
    
    // Hinweis: In der w-Komponente der World-Koordinaten steht der Abstand des Punktes zur Kamera
    public Texture getWorldTexture() {
        return frameBuffer.getTexture(0);
    }
    
    public Texture getNormalTexture() {
        return frameBuffer.getTexture(1);
    }
    
    public Texture getDiffuseTexture() {
        return frameBuffer.getTexture(2);
    }
    
    public Texture getSpecTexture() {
        return frameBuffer.getTexture(3);
    }
    
    public Texture getSkyTexture() {
        return frameBuffer.getTexture(4);
    }
    
    public Texture getShadowTexture() {
    	return frameBuffer.getTexture(5);
    }
    
    public Texture getBumpTexture() {
    	return frameBuffer.getTexture(6);
    }
    
    public void DrawTexture(Texture tex) {
        drawTextureSP.use();
        drawTextureSP.setUniform("image", tex);
        screenQuadGeo.draw();
    }
    
    public void delete() {
        drawTextureSP.delete();
        screenQuadGeo.delete();
        
        texPosition.delete();
        texNormal.delete();
        texVertexColor.delete();
    }

}
