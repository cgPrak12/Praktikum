package util;

import opengl.GL;
import org.lwjgl.opengl.GL11;
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
    private ShaderProgram drawTextureSP;
    private Geometry geo = null;

    public DeferredShader(ShaderProgram shader, Geometry geo) {
        this.drawTextureSP = shader;
        this.geo = geo;
    }
    
    private FrameBuffer frameBuffer = new FrameBuffer();
    
	private Texture texPosition;
	private Texture texNormal;
	private Texture texVertexColor;
    
    public DeferredShader() {
    }
   
    public void init() {
    	frameBuffer.init(true, GL.WIDTH, GL.HEIGHT);
        
    	// generate textures
    	texPosition = 	 new Texture(GL11.GL_TEXTURE_2D, 0);
    	texVertexColor = new Texture(GL11.GL_TEXTURE_2D, 1);
    	texNormal = 	 new Texture(GL11.GL_TEXTURE_2D, 2);
    	    	
    	frameBuffer.addTexture(texPosition, GL30.GL_RGBA16F, GL11.GL_RGBA);
    	frameBuffer.addTexture(texVertexColor, GL11.GL_RGBA8, GL11.GL_RGBA);
    	frameBuffer.addTexture(texNormal, GL30.GL_RGBA16F, GL11.GL_RGBA);
    	
    	frameBuffer.drawBuffers();
    }
    
    public void bind() {
   		frameBuffer.bind();
    }
    
    public void registerShaderProgram(ShaderProgram shaderProgram) {
    	shaderProgram.use();
        frameBuffer.BindFragDataLocations(shaderProgram, "position", "normal", "color");        
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
    
    public void draw(Geometry geometry, Texture texture) {
        drawTextureSP.use();
        drawTextureSP.setUniform("image", texture);
        geometry.draw();
    }
    
/*    public void DrawTexture(Geometry geometry, Texture texture) {
        drawTextureSP.use();
        drawTextureSP.setUniform("image", texture);
        geometry.draw();
    }*/
    
    public void delete() {
        drawTextureSP.delete();
        geo.delete();
        
        texPosition.delete();
        texNormal.delete();
        texVertexColor.delete();
    }

}
