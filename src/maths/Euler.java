package maths;



public class Euler {
	
	public float x;
	public float y;
	public float z;
	
	public Euler() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public Euler(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Euler setFromRotationMatrix(Matrix4f m) {
		// ASSERT: m is an unscaled pure rotation matrix
		y = (float)Math.asin(m.m02);
		if (Math.abs(m.m02) < 0.99999) {
			x = (float)Math.atan2(-m.m12, m.m22);
			z = (float)Math.atan2(-m.m01, m.m00);
		}
		else {
			x = (float)Math.atan2(m.m21, m.m11);
			z = 0;
		}
		return this;
	}
	
	public Euler setFromQuaternion(Quaternion q) {
		Matrix4f m = new Matrix4f();
		q.setRotationMatrixTo(m);
		setFromRotationMatrix(m);
		return this;
	}
	
}
