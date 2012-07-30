package util;

import static opengl.GL.*;

/**
 *
 * @author nico3000
 */
public class Texture {
    private static int TEMP_SLOT = 0;
    
    private int id = -1;
    private int target = GL_TEXTURE_2D;
    private int slot = TEMP_SLOT;
    
    public int getID() {
        if(this.id == -1) {
            this.id = glGenTextures();
        }
        return this.id;
    }
    
    public void setData(Util.ImageContents content) {
        int internalFormat = 0;
        int format = 0;
        switch(content.colorComponents) {
            case 1: internalFormat = GL_RED; format = GL_R; break;
            case 2: internalFormat = GL_RG8; format = GL_RG; break;
            case 3: internalFormat = GL_RGB8; format = GL_RGB; break;
            case 4: internalFormat = GL_RGBA8; format = GL_RGBA; break;
        }
        this.bindToSlot(slot);
        glTexImage2D(this.target, 0, internalFormat, content.width, content.height, 0, format, GL_FLOAT, content.data);
        glGenerateMipmap(this.target);
    }
    
    public void bindToSlot(int slot) {
        if(slot == TEMP_SLOT) {
            System.err.println("Warnung: Textur wird an Slot 0 gebunden!");
        }
        this.bindToSlotIntern(slot);
    }
    
    private void bindToSlotIntern(int slot) {
        this.slot = slot;
        glActiveTexture(GL_TEXTURE0 + slot);
        glBindTexture(this.target, this.getID());
    }

    public int getSlot() {
        return slot;
    }
}
