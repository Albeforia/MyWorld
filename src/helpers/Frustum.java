package helpers;

import maths.Matrix4f;
import maths.Plane;
import maths.Vector3f;
import entities.Entity;

public class Frustum {
	
	public Plane[] planes;
	
	public Frustum() {
		planes = new Plane[6];
		planes[0] = new Plane();
		planes[1] = new Plane();
		planes[2] = new Plane();
		planes[3] = new Plane();
		planes[4] = new Plane();
		planes[5] = new Plane();
	}
	
	public void setFromMatrix(Matrix4f m) {
		planes[0].set(m.m30 - m.m00, m.m31 - m.m01, m.m32 - m.m02, m.m33 - m.m03).normalize();
		planes[1].set(m.m30 + m.m00, m.m31 + m.m01, m.m32 + m.m02, m.m33 + m.m03).normalize();
		planes[2].set(m.m30 + m.m10, m.m31 + m.m11, m.m32 + m.m12, m.m33 + m.m13).normalize();
		planes[3].set(m.m30 - m.m10, m.m31 - m.m11, m.m32 - m.m12, m.m33 - m.m13).normalize();
		planes[4].set(m.m30 - m.m20, m.m31 - m.m21, m.m32 - m.m22, m.m33 - m.m23).normalize();
		planes[5].set(m.m30 + m.m20, m.m31 + m.m21, m.m32 + m.m22, m.m33 + m.m23).normalize();
	}
	
	public boolean intersectsEntity(Entity entity) {
		return intersectsSphere(entity.getBoundingSphere());
	}
	
	private boolean intersectsSphere(Sphere sphere) {
		Vector3f center = sphere.center;
		float negRadius = -sphere.radius;
		for (int i = 0; i < 6; i++) {
			float distance = planes[i].distanceToPoint(center);
			if (distance < negRadius) {
				return false;
			}
		}
		return true;
	}
}
