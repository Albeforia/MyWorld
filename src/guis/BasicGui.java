package guis;

import maths.Matrix4f;
import models.MeshLoader;

public class BasicGui {
	
	private int texture;
	private Matrix4f matrix;
	private boolean active;
	
	public BasicGui(float x, float y, float sx, float sy) {
		matrix = Matrix4f.translate(x, y, 0).multiply(Matrix4f.scale(sx, sy, 1));
		texture = -1;
		active = true;
	}
	
	public void setTexture(String file, String format, MeshLoader loader) {
		this.texture = loader.loadTexture("gui/" + file, format);
	}
	
	public void setTexture(int texture) {
		this.texture = texture;
	}
	
	public Matrix4f getMatrix() {
		return matrix;
	}
	
	public int getTexture() {
		return texture;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
}
