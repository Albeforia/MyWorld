package maths;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Vector3f {
	
	public float x;
	public float y;
	public float z;
	
	public Vector3f() {
		this.x = 0f;
		this.y = 0f;
		this.z = 0f;
	}
	
	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public float lengthSquared() {
		return x * x + y * y + z * z;
	}
	
	public float length() {
		return (float)Math.sqrt(lengthSquared());
	}
	
	public Vector3f normalize() {
		return divide(length());
	}
	
	public Vector3f add(Vector3f other) {
		x += other.x;
		y += other.y;
		z += other.z;
		return this;
	}
	
	public Vector3f negate() {
		return scale(-1f);
	}
	
	public Vector3f sub(Vector3f other) {
		x -= other.x;
		y -= other.y;
		z -= other.z;
		return this;
	}
	
	public Vector3f scale(float scalar) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
		return this;
	}
	
	public Vector3f divide(float scalar) {
		return scale(1f / scalar);
	}
	
	public float dot(Vector3f other) {
		return this.x * other.x + this.y * other.y + this.z * other.z;
	}
	
	public Vector3f cross(Vector3f other) {
		float x = this.y * other.z - this.z * other.y;
		float y = this.z * other.x - this.x * other.z;
		float z = this.x * other.y - this.y * other.x;
		return new Vector3f(x, y, z);
	}
	
	public FloatBuffer getBuffer() {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
		buffer.put(x).put(y).put(z);
		buffer.flip();
		return buffer;
	}
	
	public Vector3f set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	public Vector3f setFromMatrixPosition(Matrix4f m) {
		this.x = m.m03;
		this.y = m.m13;
		this.z = m.m23;
		return this;
	}
	
	public Vector3f min(Vector3f v) {
		if (this.x > v.x) {
			this.x = v.x;
		}
		if (this.y > v.y) {
			this.y = v.y;
		}
		if (this.z > v.z) {
			this.z = v.z;
		}
		return this;
	}
	
	public Vector3f max(Vector3f v) {
		if (this.x < v.x) {
			this.x = v.x;
		}
		if (this.y < v.y) {
			this.y = v.y;
		}
		if (this.z < v.z) {
			this.z = v.z;
		}
		return this;
	}
	
	public float distanceTo(Vector3f v) {
		return (float)Math.sqrt(this.distanceToSquared(v));
	}
	
	public float distanceToSquared(Vector3f v) {
		float dx = this.x - v.x;
		float dy = this.y - v.y;
		float dz = this.z - v.z;
		return dx * dx + dy * dy + dz * dz;
	}
	
	public Vector3f applyMatrix4(Matrix4f m) {
		float x = this.x, y = this.y, z = this.z;
		this.x = m.m00 * x + m.m01 * y + m.m02 * z + m.m03;
		this.y = m.m10 * x + m.m11 * y + m.m12 * z + m.m13;
		this.z = m.m20 * x + m.m21 * y + m.m22 * z + m.m23;
		return this;
	}
	
	public Vector3f subVectors(Vector3f a, Vector3f b) {
		x = a.x - b.x;
		y = a.y - b.y;
		z = a.z - b.z;
		return this;
	}
	
	public Vector3f crossVectors(Vector3f a, Vector3f b) {
		float ax = a.x, ay = a.y, az = a.z;
		float bx = b.x, by = b.y, bz = b.z;
		x = ay * bz - az * by;
		y = az * bx - ax * bz;
		z = ax * by - ay * bx;
		return this;
	}
	
	public Vector3f applyProjection(Matrix4f m) {
		float x = this.x, y = this.y, z = this.z;
		float d = 1 / (m.m30 * x + m.m31 * y + m.m32 * z + m.m33); // perspective divide
		this.x = (m.m00 * x + m.m01 * y + m.m02 * z + m.m03) * d;
		this.y = (m.m10 * x + m.m11 * y + m.m12 * z + m.m13) * d;
		this.z = (m.m20 * x + m.m21 * y + m.m22 * z + m.m23) * d;
		return this;
	}
	
	public Vector3f applyQuaternion(Quaternion q) {
		float qx = q.x;
		float qy = q.y;
		float qz = q.z;
		float qw = q.w;
		// calculate quat * vector
		float ix = qw * x + qy * z - qz * y;
		float iy = qw * y + qz * x - qx * z;
		float iz = qw * z + qx * y - qy * x;
		float iw = -qx * x - qy * y - qz * z;
		// calculate result * inverse quat
		x = ix * qw + iw * -qx + iy * -qz - iz * -qy;
		y = iy * qw + iw * -qy + iz * -qx - ix * -qz;
		z = iz * qw + iw * -qz + ix * -qy - iy * -qx;
		return this;
	}
	
	public Vector3f clone() {
		return new Vector3f(x, y, z);
	}
	
	public Vector3f copy(Vector3f other) {
		x = other.x;
		y = other.y;
		z = other.z;
		return this;
	}
	
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
}
