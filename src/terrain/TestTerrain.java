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

		long time12 = System.currentTimeMillis();
		System.out.println("Time passed: " + (time12 - time01));
		
		terra.set(1000, 0, 0, 5.0f);

		long time13 = System.currentTimeMillis();
		System.out.println("Time passed: " + (time13 - time12));
		
		terra.set(0, 1000, 0, 2.0f);

		long time14 = System.currentTimeMillis();
		System.out.println("Time passed: " + (time14 - time13));
		
		terra.set(1000, 1000, 0, 3.0f);
		
		long time02 = System.currentTimeMillis();
		System.out.println("Time passed: " + (time02 - time01));
		
		System.out.println(terra.get(0, 0, 0));
		
		long time03 = System.currentTimeMillis();
		System.out.println("Time passed: " + (time03 - time02));
		
		System.out.println(terra.get(1000, 0, 0));

		long time04 = System.currentTimeMillis();
		System.out.println("Time passed: " + (time04 - time03));
		
		System.out.println(terra.get(0, 1000, 0));

		long time05 = System.currentTimeMillis();
		System.out.println("Time passed: " + (time05 - time04));
		System.out.println(terra.get(1000, 1000, 0));
		
		long time06 = System.currentTimeMillis();
		System.out.println("Time passed: " + (time06 - time05));
	}

}
