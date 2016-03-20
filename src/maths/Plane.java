package maths;

public class Plane {
	
	public Vector3f normal;
	public float constant;
	
	public Plane() {
		normal = new Vector3f();
		constant = 0;
	}
	
	public Plane set(float x, float y, float z, float w) {
		normal.x = x;
		normal.y = y;
		normal.z = z;
		constant = w;
		return this;
	}
	
	public Plane normalize() {
		// Note: will lead to a divide by zero if the plane is invalid
		float inverseNormalLength = (float)(1.0 / normal.length());
		normal.scale(inverseNormalLength);
		constant *= inverseNormalLength;
		return this;
	}
	
	public float distanceToPoint(Vector3f point) {
		return normal.dot(point) + constant;
	}
	
}
