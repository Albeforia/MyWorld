package renderers;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import entities.Entity;
import entities.Lamp;

public class EntityRenderer extends Renderer {
	
	public EntityRenderer(ShaderProgram program) {
		super(program);
	}
	
	public void render(List<Entity> entities) {
		entities.forEach(entity -> {
			entity.updateMatrix();
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.getTexture());
			program.uploadUniformData("textureSampler", 0);
			program.uploadUniformData("modelMatrix", entity.getMatrix());
			if (entity instanceof Lamp) {
				program.uploadUniformData("useFakeLight", 1f);
			}
				else {
					program.uploadUniformData("useFakeLight", 0f);
				}
				if (entity.isMouseHovering) {
					program.uploadUniformData("isMouseHovering", 1f);
				}
				else {
					program.uploadUniformData("isMouseHovering", 0f);
				}
				GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getMesh().getNumVertices(), GL11.GL_UNSIGNED_INT, 0);
			});
	}
	
}
