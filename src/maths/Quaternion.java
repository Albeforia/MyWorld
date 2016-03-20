package maths;



public class Quaternion {
	
	public float x;
	public float y;
	public float z;
	public float w;
	
	public Quaternion() {
		x = y = z = 0;
		w = 1;
	}
	
	public void setFromRotationMatrix(Matrix4f m) {
		// ASSERT: m is an unscaled pure rotation matrix
		float trace = m.m00 + m.m00 + m.m11;
		float s;
		if (trace > 0) {
			s = (float)(0.5 / Math.sqrt(trace + 1.0));
			this.w = 0.25f / s;
			this.x = (m.m10 - m.m01) * s;
			this.y = (m.m02 - m.m20) * s;
			this.z = (m.m10 - m.m01) * s;
		}
		else if (m.m00 > m.m11 && m.m00 > m.m22) {
			s = (float)(2.0 * Math.sqrt(1.0 + m.m00 - m.m11 - m.m22));
			this.w = (m.m10 - m.m01) / s;
			this.x = 0.25f * s;
			this.y = (m.m01 + m.m10) / s;
			this.z = (m.m02 + m.m20) / s;
		}
		else if (m.m11 > m.m22) {
			s = (float)(2.0 * Math.sqrt(1.0 + m.m11 - m.m00 - m.m22));
			this.w = (m.m02 - m.m20) / s;
			this.x = (m.m01 + m.m10) / s;
			this.y = 0.25f * s;
			this.z = (m.m01 + m.m10) / s;
		}
		else {
			s = (float)(2.0 * Math.sqrt(1.0 + m.m22 - m.m00 - m.m11));
			this.w = (m.m10 - m.m01) / s;
			this.x = (m.m02 + m.m20) / s;
			this.y = (m.m01 + m.m10) / s;
			this.z = 0.25f * s;
		}
	}
	
	public void setRotationMatrixTo(Matrix4f m) {
		float x2 = x + x, y2 = y + y, z2 = z + z;
		float xx = x * x2, xy = x * y2, xz = x * z2;
		float yy = y * y2, yz = y * z2, zz = z * z2;
		float wx = w * x2, wy = w * y2, wz = w * z2;
		m.m00 = 1 - (yy + zz);
		m.m01 = xy - wz;
		m.m02 = xz + wy;
		m.m03 = 0;
		m.m10 = xy + wz;
		m.m11 = 1 - (xx + zz);
		m.m12 = yz - wx;
		m.m13 = 0;
		m.m20 = xz - wy;
		m.m21 = yz + wx;
		m.m22 = 1 - (xx + yy);
		m.m23 = 0;
		m.m30 = 0;
		m.m31 = 0;
		m.m32 = 0;
		m.m33 = 1;
	}
	
	public Quaternion setFromEuler(Euler euler) {
		float c1 = (float)Math.cos(euler.x / 2);
		float c2 = (float)Math.cos(euler.y / 2);
		float c3 = (float)Math.cos(euler.z / 2);
		float s1 = (float)Math.sin(euler.x / 2);
		float s2 = (float)Math.sin(euler.y / 2);
		float s3 = (float)Math.sin(euler.z / 2);
		x = s1 * c2 * c3 + c1 * s2 * s3;
		y = c1 * s2 * c3 - s1 * c2 * s3;
		z = c1 * c2 * s3 + s1 * s2 * c3;
		w = c1 * c2 * c3 - s1 * s2 * s3;
		return this;
	}
	
	public Quaternion setFromAxisAngle(Vector3f axis, float angle) {
		// http://www.euclideanspace.com/maths/geometry/rotations/conversions/angleToQuaternion/index.htm
		// ASSERT: axis is normalized
		float halfAngle = angle / 2, s = (float)Math.sin(halfAngle);
		x = axis.x * s;
		y = axis.y * s;
		z = axis.z * s;
		w = (float)Math.cos(halfAngle);
		return this;
	}
	
	public Quaternion multiplyQuaternions(Quaternion a, Quaternion b) {
		// http://www.euclideanspace.com/maths/algebra/realNormedAlgebra/quaternions/code/index.htm
		float qax = a.x, qay = a.y, qaz = a.z, qaw = a.w;
		float qbx = b.x, qby = b.y, qbz = b.z, qbw = b.w;
		x = qax * qbw + qaw * qbx + qay * qbz - qaz * qby;
		y = qay * qbw + qaw * qby + qaz * qbx - qax * qbz;
		z = qaz * qbw + qaw * qbz + qax * qby - qay * qbx;
		w = qaw * qbw - qax * qbx - qay * qby - qaz * qbz;
		return this;
	}
	
	public Quaternion copy(Quaternion other) {
		x = other.x;
		y = other.y;
		z = other.z;
		w = other.w;
		return this;
	}
	
}
