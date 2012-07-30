package util;

import java.util.LinkedList;
import java.util.List;

/**
 * represents a set of vertex information.
 * vertex information is typically a float, can also be a set of floats. 
 * @author LZELLER
 *
 */

public class VertexInfo {
	
	private List<Info> list;
	
	/**
	 * Ctor, fills the list with all given information
	 * @param floats
	 */
	public VertexInfo(Info ...floats)
	{
		list = new LinkedList<Info>();
		
		for(Info f : floats)
			list.add(f);
	}
	
	/**
	 * Getter
	 * @param i index
	 * @return value at given index
	 */
	Info getElem(int i)
	{ 
		return list.get(i);
	}
	
	/**
	 * Setter
	 * @param i index
	 * @param f new value
	 */
	void setElem(int i, Info f)
	{
		list.set(i, f);
	}
	

}
