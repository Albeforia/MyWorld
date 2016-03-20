package renderers;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import terrains.Terrain;

public class TerrainRenderer extends Renderer {
	
	public TerrainRenderer(ShaderProgram program) {
		super(program);
	}
	
	public void render(Terrain terrain) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBackgroundTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getrTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getgTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getbTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap());
		program.uploadUniformData("backgroundTexture", 0);
		program.uploadUniformData("rTexture", 1);
		program.uploadUniformData("gTexture", 2);
		program.uploadUniformData("bTexture", 3);
		program.uploadUniformData("blendMap", 4);
		program.uploadUniformData("modelMatrix", terrain.getMatrix());
		GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getMesh().getNumVertices(), GL11.GL_UNSIGNED_INT, 0);
	}
}
