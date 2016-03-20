package entities;

import lights.Light;
import models.Mesh;

public class Lamp extends Entity {
	
	private Light light;
	
	private float intensity;
	
	private boolean state;
	
	public Lamp(Mesh mesh) {
		super(mesh, 2, 0);
		state = false;
	}
	
	public Lamp setLight(int color, float intensity) {
		this.intensity = intensity;
		light = new Light(color, intensity).setAttenuation(1, 0.01f, 0.002f);
		if (!state) light.setIntensity(0);
		return this;
	}
	
	public void trigger() {
		if (state) {
			light.setIntensity(0);
			state = false;
		}
		else {
			light.setIntensity(intensity);
			state = true;
		}
	}
	
	public Light getLight() {
		return light;
	}
	
}
