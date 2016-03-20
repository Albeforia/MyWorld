package helpers;

import maths.Vector3f;

public class Sphere {
	
	public Vector3f center;
	public float radius;
	
	public Sphere() {
		center = new Vector3f();
		radius = 0;
	}
	
	public Sphere setFromPoints(float[] points, Vector3f center) {
		this.center = center;
		Vector3f point = new Vector3f();
		for (int i = 0; i < points.length; i += 3) {
			float x = points[i];
			float y = points[i + 1];
			float z = points[i + 2];
			point.set(x, y, z);
			radius = Math.max(radius, center.distanceToSquared(point));
		}
		radius = (float)Math.sqrt(radius);
		return this;
	}
	
}
