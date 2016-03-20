package helpers;

import maths.Matrix4f;
import maths.Vector2f;
import maths.Vector3f;
import maths.Vector4f;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import cameras.PerspectiveCamera;

public class MousePicker {
	
	private Vector3f currentRay;
	
	private PerspectiveCamera camera;
	private Matrix4f invertedProjection;
	private Matrix4f invertedView;
	
	public MousePicker(PerspectiveCamera camera) {
		this.camera = camera;
		currentRay = new Vector3f();
		invertedProjection = new Matrix4f();
		camera.getProjectionMatrix().getInverseTo(invertedProjection);
		invertedView = new Matrix4f();
		camera.getViewMatrix().getInverseTo(invertedView);
	}
	
	public Vector3f getCurrentRay() {
		return currentRay;
	}
	
	public void update() {
		camera.getViewMatrix().getInverseTo(invertedView);
		currentRay = calculateMouseRay();
	}
	
	private Vector3f calculateMouseRay() {
		float mouseX = Mouse.getX();
		float mouseY = Mouse.getY();
		Vector2f normalizedCoords = getnormalizedDeviceCoordinates(mouseX, mouseY);
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		Vector3f worldRay = toWorldCoords(eyeCoords);
		return worldRay;
	}
	
	private Vector3f toWorldCoords(Vector4f eyeCoords) {
		Vector4f rayWorld = eyeCoords.applyMatrix4(invertedView);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z).normalize();
		return mouseRay;
	}
	
	private Vector4f toEyeCoords(Vector4f clipCoords) {
		Vector4f eyeCoords = clipCoords.applyMatrix4(invertedProjection);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
	}
	
	private Vector2f getnormalizedDeviceCoordinates(float mouseX, float mouseY) {
		float x = (2.0f * mouseX) / Display.getWidth() - 1f;
		float y = (2.0f * mouseY) / Display.getHeight() - 1f;
		return new Vector2f(x, y);
	}
	
}
