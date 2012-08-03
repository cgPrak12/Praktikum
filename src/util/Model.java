/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;
import java.util.*;

/**
 *
 * @author Floh1111
 */
public class Model {
    public List<Object> objectList = new LinkedList<Object>();
    public List<Material> materialList = new LinkedList<Material>();

    public String toString() {
        String returnString="";
        
        Iterator<Object> objectListIterator = objectList.listIterator();
        while(objectListIterator.hasNext())
            returnString = new String(returnString+objectListIterator.next());

        Iterator<Material> materialListIterator = materialList.listIterator();
        while(materialListIterator.hasNext())
            returnString = new String(returnString+materialListIterator.next());
        
        return returnString;
    }
}