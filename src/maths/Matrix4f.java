package maths;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Matrix4f {
	
	private FloatBuffer buffer;
	private boolean bufferNeedsUpdate;
	
	public float m00, m01, m02, m03;
	public float m10, m11, m12, m13;
	public float m20, m21, m22, m23;
	public float m30, m31, m32, m33;
	
	public Matrix4f() {
		setIdentity();
		buffer = BufferUtils.createFloatBuffer(16);
	}
	
	public void updateBuffer() {
		buffer.clear();
		buffer.put(m00).put(m10).put(m20).put(m30);
		buffer.put(m01).put(m11).put(m21).put(m31);
		buffer.put(m02).put(m12).put(m22).put(m32);
		buffer.put(m03).put(m13).put(m23).put(m33);
		buffer.flip();
		bufferNeedsUpdate = false;
	}
	
	public FloatBuffer getBuffer() {
		if (bufferNeedsUpdate) {
			updateBuffer();
		}
		return buffer;
	}
	
	public Matrix4f setIdentity() {
		m00 = 1f;
		m11 = 1f;
		m22 = 1f;
		m33 = 1f;
		m01 = 0f;
		m02 = 0f;
		m03 = 0f;
		m10 = 0f;
		m12 = 0f;
		m13 = 0f;
		m20 = 0f;
		m21 = 0f;
		m23 = 0f;
		m30 = 0f;
		m31 = 0f;
		m32 = 0f;
		bufferNeedsUpdate = true;
		return this;
	}
	
	public static Matrix4f translate(float x, float y, float z) {
		Matrix4f _translate = new Matrix4f();
		_translate.m03 = x;
		_translate.m13 = y;
		_translate.m23 = z;
		return _translate;
	}
	
	public static Matrix4f rotate(float angle, float x, float y, float z) {
		Matrix4f _rotate = new Matrix4f();
		float c = (float)Math.cos(angle);
		float s = (float)Math.sin(angle);
		_rotate.m00 = x * x * (1f - c) + c;
		_rotate.m10 = y * x * (1f - c) + z * s;
		_rotate.m20 = x * z * (1f - c) - y * s;
		_rotate.m01 = x * y * (1f - c) - z * s;
		_rotate.m11 = y * y * (1f - c) + c;
		_rotate.m21 = y * z * (1f - c) + x * s;
		_rotate.m02 = x * z * (1f - c) + y * s;
		_rotate.m12 = y * z * (1f - c) - x * s;
		_rotate.m22 = z * z * (1f - c) + c;
		return _rotate;
	}
	
	public static Matrix4f scale(float x, float y, float z) {
		Matrix4f _scale = new Matrix4f();
		_scale.m00 = x;
		_scale.m11 = y;
		_scale.m22 = z;
		return _scale;
	}
	
	public Matrix4f multiply(Matrix4f other) {
		Matrix4f result = new Matrix4f();
		result.m00 = this.m00 * other.m00 + this.m01 * other.m10 + this.m02 * other.m20 + this.m03 * other.m30;
		result.m10 = this.m10 * other.m00 + this.m11 * other.m10 + this.m12 * other.m20 + this.m13 * other.m30;
		result.m20 = this.m20 * other.m00 + this.m21 * other.m10 + this.m22 * other.m20 + this.m23 * other.m30;
		result.m30 = this.m30 * other.m00 + this.m31 * other.m10 + this.m32 * other.m20 + this.m33 * other.m30;
		result.m01 = this.m00 * other.m01 + this.m01 * other.m11 + this.m02 * other.m21 + this.m03 * other.m31;
		result.m11 = this.m10 * other.m01 + this.m11 * other.m11 + this.m12 * other.m21 + this.m13 * other.m31;
		result.m21 = this.m20 * other.m01 + this.m21 * other.m11 + this.m22 * other.m21 + this.m23 * other.m31;
		result.m31 = this.m30 * other.m01 + this.m31 * other.m11 + this.m32 * other.m21 + this.m33 * other.m31;
		result.m02 = this.m00 * other.m02 + this.m01 * other.m12 + this.m02 * other.m22 + this.m03 * other.m32;
		result.m12 = this.m10 * other.m02 + this.m11 * other.m12 + this.m12 * other.m22 + this.m13 * other.m32;
		result.m22 = this.m20 * other.m02 + this.m21 * other.m12 + this.m22 * other.m22 + this.m23 * other.m32;
		result.m32 = this.m30 * other.m02 + this.m31 * other.m12 + this.m32 * other.m22 + this.m33 * other.m32;
		result.m03 = this.m00 * other.m03 + this.m01 * other.m13 + this.m02 * other.m23 + this.m03 * other.m33;
		result.m13 = this.m10 * other.m03 + this.m11 * other.m13 + this.m12 * other.m23 + this.m13 * other.m33;
		result.m23 = this.m20 * other.m03 + this.m21 * other.m13 + this.m22 * other.m23 + this.m23 * other.m33;
		result.m33 = this.m30 * other.m03 + this.m31 * other.m13 + this.m32 * other.m23 + this.m33 * other.m33;
		return result;
	}
	
	public Matrix4f multiplyMatrices(Matrix4f a, Matrix4f b) {
		m00 = a.m00 * b.m00 + a.m01 * b.m10 + a.m02 * b.m20 + a.m03 * b.m30;
		m10 = a.m10 * b.m00 + a.m11 * b.m10 + a.m12 * b.m20 + a.m13 * b.m30;
		m20 = a.m20 * b.m00 + a.m21 * b.m10 + a.m22 * b.m20 + a.m23 * b.m30;
		m30 = a.m30 * b.m00 + a.m31 * b.m10 + a.m32 * b.m20 + a.m33 * b.m30;
		m01 = a.m00 * b.m01 + a.m01 * b.m11 + a.m02 * b.m21 + a.m03 * b.m31;
		m11 = a.m10 * b.m01 + a.m11 * b.m11 + a.m12 * b.m21 + a.m13 * b.m31;
		m21 = a.m20 * b.m01 + a.m21 * b.m11 + a.m22 * b.m21 + a.m23 * b.m31;
		m31 = a.m30 * b.m01 + a.m31 * b.m11 + a.m32 * b.m21 + a.m33 * b.m31;
		m02 = a.m00 * b.m02 + a.m01 * b.m12 + a.m02 * b.m22 + a.m03 * b.m32;
		m12 = a.m10 * b.m02 + a.m11 * b.m12 + a.m12 * b.m22 + a.m13 * b.m32;
		m22 = a.m20 * b.m02 + a.m21 * b.m12 + a.m22 * b.m22 + a.m23 * b.m32;
		m32 = a.m30 * b.m02 + a.m31 * b.m12 + a.m32 * b.m22 + a.m33 * b.m32;
		m03 = a.m00 * b.m03 + a.m01 * b.m13 + a.m02 * b.m23 + a.m03 * b.m33;
		m13 = a.m10 * b.m03 + a.m11 * b.m13 + a.m12 * b.m23 + a.m13 * b.m33;
		m23 = a.m20 * b.m03 + a.m21 * b.m13 + a.m22 * b.m23 + a.m23 * b.m33;
		m33 = a.m30 * b.m03 + a.m31 * b.m13 + a.m32 * b.m23 + a.m33 * b.m33;
		bufferNeedsUpdate = true;
		return this;
	}
	
	public Matrix4f multiplyScalar(float scalar) {
		m00 *= scalar;
		m10 *= scalar;
		m20 *= scalar;
		m30 *= scalar;
		m01 *= scalar;
		m11 *= scalar;
		m21 *= scalar;
		m31 *= scalar;
		m02 *= scalar;
		m12 *= scalar;
		m22 *= scalar;
		m32 *= scalar;
		m03 *= scalar;
		m13 *= scalar;
		m23 *= scalar;
		m33 *= scalar;
		bufferNeedsUpdate = true;
		return this;
	}
	
	private Matrix4f makeFrustum(float left, float right, float bottom, float top,
									float near, float far) {
		float a = (right + left) / (right - left);
		float b = (top + bottom) / (top - bottom);
		float c = -(far + near) / (far - near);
		float d = -(2f * far * near) / (far - near);
		m00 = (2f * near) / (right - left);
		m11 = (2f * near) / (top - bottom);
		m02 = a;
		m12 = b;
		m22 = c;
		m32 = -1f;
		m23 = d;
		m33 = 0f;
		bufferNeedsUpdate = true;
		return this;
	}
	
	public Matrix4f makePerspective(float fov, float aspect, float near, float far) {
		float ymax = (float)(near * Math.tan(Math.toRadians(fov * 0.5)));
		float ymin = -ymax;
		float xmin = ymin * aspect;
		float xmax = ymax * aspect;
		return makeFrustum(xmin, xmax, ymin, ymax, near, far);
	}
	
	public static Matrix4f perspective(float fovy, float aspect, float near, float far) {
		Matrix4f perspective = new Matrix4f();
		float f = (float)(1f / Math.tan(Math.toRadians(fovy) / 2f));
		perspective.m00 = f / aspect;
		perspective.m11 = f;
		perspective.m22 = (far + near) / (near - far);
		perspective.m32 = -1f;
		perspective.m23 = (2f * far * near) / (near - far);
		perspective.m33 = 0f;
		return perspective;
	}
	
	public static Matrix4f view(float eyeX,
								float eyeY,
								float eyeZ,
								float centerX,
								float centerY,
								float centerZ,
								float upX,
								float upY,
								float upZ) {
		Matrix4f view = new Matrix4f();
		Vector3f f = new Vector3f(centerX - eyeX, centerY - eyeY, centerZ - eyeZ).normalize();
		Vector3f s = f.cross(new Vector3f(upX, upY, upZ)).normalize();
		Vector3f u = s.cross(f);
		view.m00 = s.x;
		view.m01 = s.y;
		view.m02 = s.z;
		view.m03 = 0;
		view.m10 = u.x;
		view.m11 = u.y;
		view.m12 = u.z;
		view.m13 = 0;
		view.m20 = -f.x;
		view.m21 = -f.y;
		view.m22 = -f.z;
		view.m23 = 0;
		view.m30 = 0;
		view.m31 = 0;
		view.m32 = 0;
		view.m33 = 1;
		return view.multiply(Matrix4f.translate(-eyeX, -eyeY, -eyeZ));
	}
	
	public Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) {
		Vector3f x = new Vector3f();
		Vector3f y = new Vector3f();
		Vector3f z = new Vector3f();
		z.subVectors(eye, target).normalize();
		if (z.length() == 0) {
			z.z = 1;
		}
		x.crossVectors(up, z).normalize();
		if (x.length() == 0) {
			z.x += 0.0001;
			x.crossVectors(up, z).normalize();
		}
		y.crossVectors(z, x);
		m00 = x.x;
		m01 = y.x;
		m02 = z.x;
		m10 = x.y;
		m11 = y.y;
		m12 = z.y;
		m20 = x.z;
		m21 = y.z;
		m22 = z.z;
		bufferNeedsUpdate = true;
		return this;
	}
	
	public float determinant() {
		float value =
				m03 * m12 * m21 * m30 - m02 * m13 * m21 * m30 - m03 * m11 * m22 * m30 + m01 * m13 * m22 * m30 +
						m02 * m11 * m23 * m30 - m01 * m12 * m23 * m30 - m03 * m12 * m20 * m31 + m02 * m13 * m20 * m31 +
						m03 * m10 * m22 * m31 - m00 * m13 * m22 * m31 - m02 * m10 * m23 * m31 + m00 * m12 * m23 * m31 +
						m03 * m11 * m20 * m32 - m01 * m13 * m20 * m32 - m03 * m10 * m21 * m32 + m00 * m13 * m21 * m32 +
						m01 * m10 * m23 * m32 - m00 * m11 * m23 * m32 - m02 * m11 * m20 * m33 + m01 * m12 * m20 * m33 +
						m02 * m10 * m21 * m33 - m00 * m12 * m21 * m33 - m01 * m10 * m22 * m33 + m00 * m11 * m22 * m33;
		return value;
	}
	
	public void compose(Vector3f position, Quaternion quaternion, Vector3f scale) {
		quaternion.setRotationMatrixTo(this);
		float x = scale.x, y = scale.y, z = scale.z;
		m00 *= x;
		m01 *= y;
		m02 *= z;
		m10 *= x;
		m11 *= y;
		m12 *= z;
		m20 *= x;
		m21 *= y;
		m22 *= z;
		m30 *= x;
		m31 *= y;
		m32 *= z;
		m03 = position.x;
		m13 = position.y;
		m23 = position.z;
		bufferNeedsUpdate = true;
	}
	
	public void getInverseTo(Matrix4f m) {
		float det = determinant();
		if (det == 0) {
			m.setIdentity();
		}
		else {
			m.m00 = m12 * m23 * m31 - m13 * m22 * m31 + m13 * m21 * m32 - m11 * m23 * m32 - m12 * m21 * m33 + m11 * m22 * m33;
			m.m01 = m03 * m22 * m31 - m02 * m23 * m31 - m03 * m21 * m32 + m01 * m23 * m32 + m02 * m21 * m33 - m01 * m22 * m33;
			m.m02 = m02 * m13 * m31 - m03 * m12 * m31 + m03 * m11 * m32 - m01 * m13 * m32 - m02 * m11 * m33 + m01 * m12 * m33;
			m.m03 = m03 * m12 * m21 - m02 * m13 * m21 - m03 * m11 * m22 + m01 * m13 * m22 + m02 * m11 * m23 - m01 * m12 * m23;
			m.m10 = m13 * m22 * m30 - m12 * m23 * m30 - m13 * m20 * m32 + m10 * m23 * m32 + m12 * m20 * m33 - m10 * m22 * m33;
			m.m11 = m02 * m23 * m30 - m03 * m22 * m30 + m03 * m20 * m32 - m00 * m23 * m32 - m02 * m20 * m33 + m00 * m22 * m33;
			m.m12 = m03 * m12 * m30 - m02 * m13 * m30 - m03 * m10 * m32 + m00 * m13 * m32 + m02 * m10 * m33 - m00 * m12 * m33;
			m.m13 = m02 * m13 * m20 - m03 * m12 * m20 + m03 * m10 * m22 - m00 * m13 * m22 - m02 * m10 * m23 + m00 * m12 * m23;
			m.m20 = m11 * m23 * m30 - m13 * m21 * m30 + m13 * m20 * m31 - m10 * m23 * m31 - m11 * m20 * m33 + m10 * m21 * m33;
			m.m21 = m03 * m21 * m30 - m01 * m23 * m30 - m03 * m20 * m31 + m00 * m23 * m31 + m01 * m20 * m33 - m00 * m21 * m33;
			m.m22 = m01 * m13 * m30 - m03 * m11 * m30 + m03 * m10 * m31 - m00 * m13 * m31 - m01 * m10 * m33 + m00 * m11 * m33;
			m.m23 = m03 * m11 * m20 - m01 * m13 * m20 - m03 * m10 * m21 + m00 * m13 * m21 + m01 * m10 * m23 - m00 * m11 * m23;
			m.m30 = m12 * m21 * m30 - m11 * m22 * m30 - m12 * m20 * m31 + m10 * m22 * m31 + m11 * m20 * m32 - m10 * m21 * m32;
			m.m31 = m01 * m22 * m30 - m02 * m21 * m30 + m02 * m20 * m31 - m00 * m22 * m31 - m01 * m20 * m32 + m00 * m21 * m32;
			m.m32 = m02 * m11 * m30 - m01 * m12 * m30 - m02 * m10 * m31 + m00 * m12 * m31 + m01 * m10 * m32 - m00 * m11 * m32;
			m.m33 = m01 * m12 * m20 - m02 * m11 * m20 + m02 * m10 * m21 - m00 * m12 * m21 - m01 * m10 * m22 + m00 * m11 * m22;
			m.multiplyScalar(1 / det);
		}
	}
	
	public float getMaxScaleOnAxis() {
		float scaleXSq = m00 * m00 + m10 * m10 + m20 * m20;
		float scaleYSq = m01 * m01 + m11 * m11 + m21 * m21;
		float scaleZSq = m02 * m02 + m12 * m12 + m22 * m22;
		return (float)Math.sqrt(Math.max(scaleXSq, Math.max(scaleYSq, scaleZSq)));
	}
	
	public void applyToVector3Array(float[] array) {
		Vector3f v1 = new Vector3f();
		int offset = 0;
		int length = array.length;
		for (int i = 0, j = offset; i < length; i += 3, j += 3) {
			v1.x = array[j];
			v1.y = array[j + 1];
			v1.z = array[j + 2];
			v1.applyMatrix4(this);
			array[j] = v1.x;
			array[j + 1] = v1.y;
			array[j + 2] = v1.z;
		}
	}
	
	public Matrix4f copy(Matrix4f m) {
		m00 = m.m00;
		m10 = m.m10;
		m20 = m.m20;
		m30 = m.m30;
		m01 = m.m01;
		m11 = m.m11;
		m21 = m.m21;
		m31 = m.m31;
		m02 = m.m02;
		m12 = m.m12;
		m22 = m.m22;
		m32 = m.m32;
		m03 = m.m03;
		m13 = m.m13;
		m23 = m.m23;
		m33 = m.m33;
		bufferNeedsUpdate = true;
		return this;
	}
	
	public String toString() {
		return "" + m00 + "\t" + m01 + "\t" + m02 + "\t" + m03 + "\n"
				+ m10 + "\t" + m11 + "\t" + m12 + "\t" + m13 + "\n"
				+ m20 + "\t" + m21 + "\t" + m22 + "\t" + m23 + "\n"
				+ m30 + "\t" + m31 + "\t" + m32 + "\t" + m33 + "\n";
	}
	
}
