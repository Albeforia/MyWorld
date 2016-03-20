package cameras;

import maths.Matrix4f;
import maths.Vector3f;

import org.lwjgl.input.Mouse;

import entities.Entity;

public class PerspectiveCamera {
	
	public static final float MOUSE_SENSITIVITY = 0.004f;
	
	private Matrix4f projectionMatrix;
	
	private Vector3f position;
	private float pitch;
	private float yaw;
	private Matrix4f viewMatrix;
	private Matrix4f projScreenMatrix;
	
	private Entity target;
	private float distanceToPlayer;
	private float angleAroundPlayer;
	
	public PerspectiveCamera(float fov, float aspect, float near, float far) {
		projectionMatrix = new Matrix4f().makePerspective(fov, aspect, near, far);
		position = new Vector3f();
		viewMatrix = new Matrix4f();
		projScreenMatrix = new Matrix4f();
	}
	
	public void bind(Entity target) {
		this.target = target;
		distanceToPlayer = 50;
		pitch = 1;
	}
	
	public void update() {
		if (target != null) {
			distanceToPlayer -= Mouse.getDWheel() * 0.1f;
			if (distanceToPlayer > 80) {
				distanceToPlayer = 80;
			}
			if (distanceToPlayer < 30) {
				distanceToPlayer = 30;
			}
			if (Mouse.isButtonDown(1)) {
				pitch -= Mouse.getDY() * MOUSE_SENSITIVITY;
				if (pitch > Math.PI / 2) {
					pitch = (float)(Math.PI / 2);
				}
				if (pitch < -Math.PI / 2) {
					pitch = -(float)(Math.PI / 2);
				}
			}
			if (Mouse.isButtonDown(0)) {
				angleAroundPlayer -= Mouse.getDX() * MOUSE_SENSITIVITY;
				if (angleAroundPlayer > Math.PI) {
					angleAroundPlayer -= Math.PI * 2;
				}
				if (angleAroundPlayer < -Math.PI) {
					angleAroundPlayer += Math.PI * 2;
				}
			}
			float hd = (float)(distanceToPlayer * Math.cos(pitch));
			float vd = (float)(distanceToPlayer * Math.sin(pitch));
			float theta = target.getRotation().y + angleAroundPlayer;
			float ox = (float)(hd * Math.sin(theta));
			float oz = (float)(hd * Math.cos(theta));
			position.x = target.getPosition().x - ox;
			position.z = target.getPosition().z - oz;
			position.y = target.getPosition().y + vd;
			//
			yaw = (float)(Math.PI - theta);
		}
		updateViewMatrix();
		projScreenMatrix.multiplyMatrices(projectionMatrix, viewMatrix);
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
	
	public Matrix4f getProjScreenMatrix() {
		return projScreenMatrix;
	}
	
	private void updateViewMatrix() {
		Matrix4f m = new Matrix4f().multiplyMatrices(
				Matrix4f.rotate(pitch, 1, 0, 0), Matrix4f.rotate(yaw, 0, 1, 0));
		viewMatrix.setIdentity().multiplyMatrices(m, Matrix4f.translate(-position.x, -position.y, -position.z));
	}
	
	public void invertPitch() {
		pitch = -pitch;
	}
	
}
