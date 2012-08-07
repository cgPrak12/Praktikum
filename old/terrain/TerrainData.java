/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package terrain;

/**
 *
 * @author nico3000
 */
public interface TerrainData {
    public int getDimensionX();
    public int getDimensionY();
    public float getHeight(int x, int y);
    public void setHeight(int x, int y, float height);
    public int getMaterial(int x, int y);
    public void setMaterial(int x, int y, int material);
}
