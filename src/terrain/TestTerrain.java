package terrain;

public class TestTerrain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		long time00 = System.currentTimeMillis();

		Terrain terra = new Terrain();
		
		long time01 = System.currentTimeMillis();
		System.out.println("Time passed: " + (time01 - time00));
		
		terra.set(0, 0, 0, 10.0f);
		terra.set(1000, 0, 0, 5.0f);
		terra.set(0, 1000, 0, 2.0f);
		terra.set(1000, 1000, 0, 3.0f);
		
		long time02 = System.currentTimeMillis();
		System.out.println("Time passed: " + (time02 - time01));
		
		System.out.println(terra.get(0, 0, 0));
		System.out.println(terra.get(1000, 0, 0));
		System.out.println(terra.get(0, 1000, 0));
		System.out.println(terra.get(1000, 1000, 0));
		
		long time03 = System.currentTimeMillis();
		System.out.println("Time passed: " + (time03 - time02));
	}

}
