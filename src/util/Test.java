package util;

import java.util.Date;

public class Test extends Terrain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		long time = new Date().getTime();
		
		Terrain terrain = new Terrain();
		
		System.out.println("Schreibdauer: " + (new Date().getTime() - time) + " Millisekunden");
		
		time = new Date().getTime();
		terrain.set(2, 2, 0, 100.0f);
		terrain.set(2, 2, 0, 101.0f);
		terrain.set(2, 2, 0, 101.0f);
		terrain.set(2, 2, 0, 101.0f);
		terrain.set(2, 2, 0, 101.0f);
		terrain.set(2, 2, 0, 101.0f);
		terrain.set(2, 2, 0, 101.0f);
		terrain.set(2, 2, 0, 101.0f);
		terrain.set(2, 2, 0, 101.0f);
		terrain.set(2, 2, 0, 101.0f);
		
		System.out.println("Schreibdauer: " + (new Date().getTime() - time) + " Millisekunden");
		
		time = new Date().getTime();
		terrain.set(2, 2, 0, 100.0f);
		
		System.out.println("Schreibdauer: " + (new Date().getTime() - time) + " Millisekunden");
		
		time = new Date().getTime();
		System.out.println(terrain.get(2,2,0));
		
		System.out.println("Schreibdauer: " + (new Date().getTime() - time) + " Millisekunden");
		

	}

}
