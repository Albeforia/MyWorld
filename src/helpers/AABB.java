package helpers;

import maths.Vector3f;

public class AABB {
	
	public Vector3f min;
	public Vector3f max;
	
	public AABB() {
		min = new Vector3f();
		max = new Vector3f();
		makeEmpty();
	}
	
	public AABB makeEmpty() {
		min.x = min.y = min.z = Integer.MAX_VALUE;
		max.x = max.y = max.z = Integer.MIN_VALUE;
		return this;
	}
	
	public AABB setFromPoints(float[] points) {
		for (int i = 0; i < points.length; i += 3) {
			float x = points[i];
			float y = points[i + 1];
			float z = points[i + 2];
			min.x = Math.min(min.x, x);
			min.y = Math.min(min.y, y);
			min.z = Math.min(min.z, z);
			max.x = Math.max(max.x, x);
			max.y = Math.max(max.y, y);
			max.z = Math.max(max.z, z);
		}
		return this;
	}
	
	public Vector3f getCenter() {
		return new Vector3f(.5f * (min.x + max.x), .5f * (min.y + max.y), .5f * (min.z + max.z));
	}
	
}
