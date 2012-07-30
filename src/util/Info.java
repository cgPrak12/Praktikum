package util;

import java.util.LinkedList;
import java.util.List;

/**
 * represents an element in a VertexInfo-object
 * @author LZELLER
 *
 */
public class Info {
	
	private List<Float> list;
	
	/**
	 * Ctor, fills the list with all given floats
	 * @param floats
	 */
	public Info(Float ...floats)
	{
		list = new LinkedList<Float>();
		for(Float f : floats)
			list.add(f);
	}
	
	

}
