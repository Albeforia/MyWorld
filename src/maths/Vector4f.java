package maths;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Vector4f {
	
	public float x;
	public float y;
	public float z;
	public float w;
	
	public Vector4f() {
		this.x = 0f;
		this.y = 0f;
		this.z = 0f;
		this.w = 0f;
	}
	
	public Vector4f(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public float lengthSquared() {
		return x * x + y * y + z * z + w * w;
	}
	
	public float length() {
		return (float)Math.sqrt(lengthSquared());
	}
	
	public Vector4f normalize() {
		float length = length();
		return divide(length);
	}
	
	public Vector4f add(Vector4f other) {
		float x = this.x + other.x;
		float y = this.y + other.y;
		float z = this.z + other.z;
		float w = this.w + other.w;
		return new Vector4f(x, y, z, w);
	}
	
	public Vector4f negate() {
		return scale(-1f);
	}
	
	public Vector4f subtract(Vector4f other) {
		return this.add(other.negate());
	}
	
	public Vector4f scale(float scalar) {
		float x = this.x * scalar;
		float y = this.y * scalar;
		float z = this.z * scalar;
		float w = this.w * scalar;
		return new Vector4f(x, y, z, w);
	}
	
	public Vector4f divide(float scalar) {
		return scale(1f / scalar);
	}
	
	public float dot(Vector4f other) {
		return this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w;
	}
	
	public Vector4f lerp(Vector4f other, float alpha) {
		return this.scale(1f - alpha).add(other.scale(alpha));
	}
	
	public FloatBuffer getBuffer() {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(x).put(y).put(z).put(w);
		buffer.flip();
		return buffer;
	}
	
	public Vector4f applyMatrix4(Matrix4f m) {
		Vector4f result = new Vector4f();
		result.x = m.m00 * x + m.m01 * y + m.m02 * z + m.m03 * w;
		result.y = m.m10 * x + m.m11 * y + m.m12 * z + m.m13 * w;
		result.z = m.m20 * x + m.m21 * y + m.m22 * z + m.m23 * w;
		result.w = m.m30 * x + m.m31 * y + m.m32 * z + m.m33 * w;
		return result;
	}
	
}
