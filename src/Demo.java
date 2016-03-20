import helpers.DisplayManager;

import java.util.Random;

import org.lwjgl.opengl.Display;

import worlds.World;

public class Demo {
	
	private World world;
	
	public Demo() {
		DisplayManager.create("Demo");
		world = new World();
		genWorld();
	}
	
	public static void main(String[] args) {
		Demo demo = new Demo();
		demo.run();
	}
	
	private void run() {
		while (!Display.isCloseRequested()) {
			long delta = DisplayManager.getDelta();
			world.update(delta);
			world.render();
			DisplayManager.update();
		}
		world.cleanUp();
		DisplayManager.close();
	}
	
	private void genWorld() {
		world = new World();
		Random random = new Random();
		for (int i = 0; i < 400; ++i) {
			if (i % 5 == 0) {
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * 800;
				float y = world.getTerrainHeight(x, z);
				world.addEntity("grass", x, y, z, 0.8f, -1, "cylinder");
			}
			if (i % 10 == 0) {
				float x = random.nextFloat() * 800;
				float z = random.nextFloat() * 800;
				float y = world.getTerrainHeight(x, z);
				float scale = random.nextFloat() * 5 + 12;
				world.addEntity("tree", x, y, z, scale, 0, "cylinder");
			}
		}
		//
		world.addLamp(0xff00ff, 4, 100, world.getTerrainHeight(100, 100), 100);
		world.addLamp(0x00ff00, 4, 300, world.getTerrainHeight(300, 300), 300);
		world.addLamp(0xffff00, 4, 500, world.getTerrainHeight(500, 500), 500);
	}
	
}
