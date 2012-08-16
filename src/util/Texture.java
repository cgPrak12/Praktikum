package util;

import static opengl.GL.*;
import org.lwjgl.opengl.GL11;
import java.io.Serializable;

/**
 *
 * @author nico3000
 */
public class Texture implements Serializable {
    private int id;
    private int unit;
    private int target;

    public Texture(int target, int unit) {
        this.id = glGenTextures();
        this.unit = unit;
        this.target = target;
    }

    public int getId() {
        return id;
    }

    public int getUnit() {
        return unit;
    }
    
    public void bind() {
        glActiveTexture(GL_TEXTURE0 + this.unit);
        glBindTexture(this.target, this.id);
    }
    
    /**
     * Laedt eine Bilddatei und erzeugt daraus eine OpenGL Textur
     * @param filename Pfad zu einer Bilddatei
     * @return ID der erstellten Textur
     */
    public static Texture generateTexture(String filename, int unit) {
        Util.ImageContents contents = Util.loadImage(filename);
        int format = 0;
        int internalFormat = 0;
        switch(contents.colorComponents) {
            case 1: internalFormat = GL_R8; format = GL_RED; break;
            case 2: internalFormat = GL_RG8; format = GL_RG; break;
            case 3: internalFormat = GL_RGB8; format = GL_RGB; break;
            case 4: internalFormat = GL_RGBA8; format = GL_RGBA; break;
        }
        Texture tex = new Texture(GL_TEXTURE_2D, unit);
        tex.bind();
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, contents.width, contents.height, 0, format, GL_FLOAT, contents.data);
        glGenerateMipmap(GL_TEXTURE_2D);
        return tex;
    }
    
    public void delete() {
        GL11.glDeleteTextures(this.id);
    }
}
