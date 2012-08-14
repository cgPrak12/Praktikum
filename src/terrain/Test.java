package terrain;

public class Test
{
	
	public static void main(String[] argv)
	{
		Terrain terra = new Terrain();
		terra.set(0, 0, 0, 10.0f);
		terra.set(0, 1000, 0, 15.0f);
		terra.set(300,0,0,20.0f);
		
		System.out.println(terra.get(0,0,0));
		System.out.println(terra.get(0,1000,0));
		System.out.println(terra.get(300,0,0));
	}

}
