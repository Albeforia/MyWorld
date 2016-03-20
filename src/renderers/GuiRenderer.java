package renderers;

import guis.BasicGui;

import java.util.List;

import models.Mesh;
import models.MeshLoader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

public class GuiRenderer extends Renderer {
	
	private Mesh quad;
	
	private static final float[] positions = {-1, 1, -1, -1, 1, 1, 1, -1};
	
	public GuiRenderer(ShaderProgram program, MeshLoader loader) {
		super(program);
		quad = loader.load(positions, 2, program);
	}
	
	public void render(List<BasicGui> guis) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL30.glBindVertexArray(quad.getVAO());
		program.enableAttributes();
		guis.forEach(gui -> {
			if (gui.isActive()) {
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
				program.uploadUniformData("guiTexture", 0);
				program.uploadUniformData("transformationMatrix", gui.getMatrix());
				GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getNumVertices());
			}
		});
		program.disableAttributes();
		GL30.glBindVertexArray(0);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
}
