package lights;

import maths.Vector3f;
import helpers.Color;

public class Light {
	
	private Vector3f position;
	private Color color;
	private float intensity;
	private Vector3f attenuation;
	
	public Light(int color, float intensity) {
		position = new Vector3f();
		this.color = new Color(color);
		this.intensity = intensity;
		attenuation = new Vector3f(1, 0, 0);
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public Light setPosition(float x, float y, float z) {
		position.set(x, y, z);
		return this;
	}
	
	public Vector3f getColor() {
		return new Vector3f(color.r * intensity, color.g * intensity, color.b * intensity);
	}
	
	public Vector3f getAttenuation() {
		return attenuation;
	}
	
	public Light setAttenuation(float x, float y, float z) {
		attenuation.set(x, y, z);
		return this;
	}
	
	public float getIntensity() {
		return intensity;
	}
	
	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}
	
}
