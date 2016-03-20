package renderers;

import java.util.List;

import maths.Matrix4f;
import models.Mesh;
import models.MeshLoader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import waters.WaterFrameBuffers;
import waters.WaterTile;

public class WaterRenderer extends Renderer {
	
	private Mesh quad;
	
	public WaterRenderer(ShaderProgram program, MeshLoader loader) {
		super(program);
		setUpVAO(loader);
	}
	
	public void render(List<WaterTile> waterTiles, WaterFrameBuffers fbos) {
		GL30.glBindVertexArray(quad.getVAO());
		program.enableAttributes();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getReflectionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionTexture());
		program.uploadUniformData("reflectionTexture", 0);
		program.uploadUniformData("refractionTexture", 1);
		waterTiles.forEach(tile -> {
			program.uploadUniformData("modelMatrix",
					Matrix4f.translate(tile.getX(), tile.getHeight(), tile.getZ())
							.multiply(Matrix4f.scale(WaterTile.TILE_SIZE, WaterTile.TILE_SIZE, WaterTile.TILE_SIZE)));
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quad.getNumVertices());
		});
		program.disableAttributes();
		GL30.glBindVertexArray(0);
	}
	
	private void setUpVAO(MeshLoader loader) {
		// Just x and z vectex positions here, y is set to 0 in v.program
		float[] vertices = {-1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1};
		quad = loader.load(vertices, 2, program);
	}
	
}
