package renderers;

import maths.Vector3f;
import models.Mesh;
import models.MeshLoader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

public class SkyboxRenderer extends Renderer {
	
	private static final float SIZE = 500f;
	
	private static final float[] VERTICES = {
			-SIZE, SIZE, -SIZE,
			-SIZE, -SIZE, -SIZE,
			SIZE, -SIZE, -SIZE,
			SIZE, -SIZE, -SIZE,
			SIZE, SIZE, -SIZE,
			-SIZE, SIZE, -SIZE,
			
			-SIZE, -SIZE, SIZE,
			-SIZE, -SIZE, -SIZE,
			-SIZE, SIZE, -SIZE,
			-SIZE, SIZE, -SIZE,
			-SIZE, SIZE, SIZE,
			-SIZE, -SIZE, SIZE,
			
			SIZE, -SIZE, -SIZE,
			SIZE, -SIZE, SIZE,
			SIZE, SIZE, SIZE,
			SIZE, SIZE, SIZE,
			SIZE, SIZE, -SIZE,
			SIZE, -SIZE, -SIZE,
			
			-SIZE, -SIZE, SIZE,
			-SIZE, SIZE, SIZE,
			SIZE, SIZE, SIZE,
			SIZE, SIZE, SIZE,
			SIZE, -SIZE, SIZE,
			-SIZE, -SIZE, SIZE,
			
			-SIZE, SIZE, -SIZE,
			SIZE, SIZE, -SIZE,
			SIZE, SIZE, SIZE,
			SIZE, SIZE, SIZE,
			-SIZE, SIZE, SIZE,
			-SIZE, SIZE, -SIZE,
			
			-SIZE, -SIZE, -SIZE,
			-SIZE, -SIZE, SIZE,
			SIZE, -SIZE, -SIZE,
			SIZE, -SIZE, -SIZE,
			-SIZE, -SIZE, SIZE,
			SIZE, -SIZE, SIZE
	};
	
	private static final String[] TEXTURE_DAY = {"right", "left", "top", "bottom", "back", "front"};
	private static final String[] TEXTURE_NIGHT = {"nightRight", "nightLeft", "nightTop", "nightBottom", "nightBack", "nightFront"};
	
	private Mesh box;
	private int cubeMap1;
	private int cubeMap2;
	private int time;
	private float lightFactor;
	
	public SkyboxRenderer(ShaderProgram program, MeshLoader loader) {
		super(program);
		box = loader.load(VERTICES, 3, program);
		cubeMap1 = loader.loadCubeMap(TEXTURE_DAY);
		cubeMap2 = loader.loadCubeMap(TEXTURE_NIGHT);
		time = 14000;
		lightFactor = 1;
	}
	
	public void render(Vector3f fogColor) {
		time += 4;
		time %= 24000;
		int texture1;
		int texture2;
		float blendFactor;
		if (time >= 0 && time < 10000) {
			texture1 = cubeMap2;
			texture2 = cubeMap2;
			blendFactor = 1f * (time - 0) / (10000 - 0);
			lightFactor = 0;
		}
		else if (time >= 10000 && time < 12000) {
			texture1 = cubeMap2;
			texture2 = cubeMap1;
			blendFactor = 1f * (time - 10000) / (12000 - 10000);
			lightFactor = blendFactor;
		}
		else if (time >= 12000 && time < 22000) {
			texture1 = cubeMap1;
			texture2 = cubeMap1;
			blendFactor = 1f * (time - 12000) / (22000 - 12000);
			lightFactor = 1;
		}
		else {
			texture1 = cubeMap1;
			texture2 = cubeMap2;
			blendFactor = 1f * (time - 22000) / (24000 - 22000);
			lightFactor = 1 - blendFactor;
		}
		GL30.glBindVertexArray(box.getVAO());
		program.enableAttributes();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);
		program.uploadUniformData("cubeMap1", 0);
		program.uploadUniformData("cubeMap2", 1);
		program.uploadUniformData("blendFactor", blendFactor);
		program.uploadUniformData("fogColor", fogColor);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, box.getNumVertices());
		program.disableAttributes();
		GL30.glBindVertexArray(0);
	}
	
	public float getLightFactor() {
		return lightFactor;
	}
	
}
