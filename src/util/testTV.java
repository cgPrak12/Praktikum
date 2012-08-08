package util;

import org.lwjgl.input.Keyboard;

public class testTV {

	public static void main(String[] argv){
		
System.out.println("hi");
		
		int size = 50;
		Camera cam = new Camera();
		TerrainWrap BF = new TerrainWrap(size*256, size*256, 1);
		TerrainView tv = new TerrainView(cam);
		
		boolean tmp = true;
		while(tmp) {
			Keyboard.next();
			switch(Keyboard.getEventKey()){
            case Keyboard.KEY_W: cam.move(0.8f, 1.0f, 1.0f);tv.updateTerrainView();show(tv);break;
            case Keyboard.KEY_S: cam.move(1.2f, 1.0f, 1.0f);tv.updateTerrainView();show(tv);break;
            case Keyboard.KEY_A: cam.move(1.0f, 0.8f, 1.0f);tv.updateTerrainView();show(tv);break;
            case Keyboard.KEY_D: cam.move(1.0f, 1.2f, 1.0f);tv.updateTerrainView();show(tv);break;
            case Keyboard.KEY_SPACE: tmp = false; break;
            default:break;
            }
		}
	}
	
	public static void show(TerrainView tv){
		
		for(int i=0;i<9;i++){
			for(int j=0;j<9;j++){
				System.out.print(tv.getBlocks()[i][j].getID()[0]+"/"+tv.getBlocks()[i][j].getID()[1]);
			}	
			System.out.println();
		}
	}
}
