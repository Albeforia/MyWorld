package helpers;

import maths.Vector3f;

public class Color {
	
	public float r;
	public float g;
	public float b;
	
	public Color(int hex) {
		this.r = (hex >> 16 & 255) / 255f;
		this.g = (hex >> 8 & 255) / 255f;
		this.b = (hex & 255) / 255f;
	}
	
	public Color setHSL(float h, float s, float l) {
		// h,s,l ranges are in [0.0, 1.0]
		if (s == 0) {
			this.r = this.g = this.b = l;
		}
		else {
			float p = l <= 0.5 ? l * (1 + s) : l + s - (l * s);
			float q = (2 * l) - p;
			this.r = hue2rgb(q, p, h + 1 / 3);
			this.g = hue2rgb(q, p, h);
			this.b = hue2rgb(q, p, h - 1 / 3);
		}
		return this;
	}
	
	private float hue2rgb(float p, float q, float t) {
		if (t < 0) t += 1;
		if (t > 1) t -= 1;
		if (t < 1 / 6) return p + (q - p) * 6 * t;
		if (t < 1 / 2) return q;
		if (t < 2 / 3) return p + (q - p) * 6 * (2 / 3 - t);
		return p;
	}
	
	public Color set(int hex) {
		this.r = (hex >> 16 & 255) / 255f;
		this.g = (hex >> 8 & 255) / 255f;
		this.b = (hex & 255) / 255f;
		return this;
	}
	
	public Color set(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
		return this;
	}
	
	public Vector3f toVector() {
		return new Vector3f(r, g, b);
	}
	
}
