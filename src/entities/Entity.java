package entities;

import helpers.AABB;
import helpers.Sphere;
import maths.Euler;
import maths.Matrix4f;
import maths.Quaternion;
import maths.Vector3f;
import models.Mesh;
import models.MeshLoader;
import worlds.World;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

public class Entity {
	
	private static int entityIDCount = 0;
	
	protected final int id;
	
	protected Mesh mesh;
	protected int texture;
	protected Vector3f position;
	protected Euler rotation;
	protected Quaternion quaternion;
	protected Vector3f scale;
	protected Matrix4f matrix;
	
	protected AABB aabb;
	protected Sphere boundingSphere;
	
	protected RigidBody rigidBody;
	protected float mass;
	protected javax.vecmath.Vector3f velocity;
	
	public boolean isMouseHovering;
	
	public Entity(Mesh mesh, float scale, float mass) {
		id = entityIDCount++;
		this.mesh = mesh;
		texture = -1;
		aabb = new AABB();
		boundingSphere = new Sphere();
		this.mass = mass;
		position = new Vector3f();
		rotation = new Euler();
		quaternion = new Quaternion();
		this.scale = new Vector3f(1, 1, 1);
		setScale(scale);
		matrix = new Matrix4f();
		velocity = new javax.vecmath.Vector3f();
	}
	
	public int getTexture() {
		return texture;
	}
	
	public void setTexture(int texture) {
		this.texture = texture;
	}
	
	public void setTexture(String file, String format, MeshLoader loader) {
		this.texture = loader.loadTexture(file, format);
	}
	
	public void updateMatrix() {
		matrix.compose(position, quaternion, scale);
	}
	
	public void updatePhysics(World world) {
		// static object or has no rigidbody
		if (mass <= 0) return;
		// rigidbody fix
		rigidBody.getLinearVelocity(velocity);
		javax.vecmath.Vector3f origin = rigidBody.getWorldTransform(new Transform()).origin;
		float halfY = 0.5f * (aabb.max.y - aabb.min.y);
		float footY = origin.y - halfY;
		float terrainHeight = world.getTerrainHeight(origin.x, origin.z);
		if (footY < terrainHeight) {
			footY = terrainHeight;
			velocity.y = 0;
		}
//		System.out.println(velocity);
		rigidBody.setLinearVelocity(new javax.vecmath.Vector3f(0, velocity.y, 0));
		// update rendering position
		position.set(origin.x, footY, origin.z);
		// update rigidbody
		javax.vecmath.Vector3f newPosition = new javax.vecmath.Vector3f(position.x, position.y + halfY, position.z);
		javax.vecmath.Quat4f newQuaternion = new javax.vecmath.Quat4f(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
		Transform transform = new Transform(new javax.vecmath.Matrix4f(newQuaternion, newPosition, 1));
		rigidBody.setWorldTransform(transform);
	}
	
	public Matrix4f getMatrix() {
		return matrix;
	}
	
	public Mesh getMesh() {
		return mesh;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public Entity setPosition(float x, float y, float z) {
		position.set(x, y, z);
		updateAABB();
		return this;
	}
	
	public Euler getRotation() {
		return rotation;
	}
	
	public Vector3f getScale() {
		return scale;
	}
	
	public Entity setScale(float s) {
		scale.set(s, s, s);
		updateAABB();
		return this;
	}
	
	public RigidBody getRigidBody() {
		return rigidBody;
	}
	
	public void setRigidBody(RigidBody rigidBody) {
		this.rigidBody = rigidBody;
	}
	
	public void updateAABB() {
		aabb.min.copy(mesh.getAABB().min);
		aabb.max.copy(mesh.getAABB().max);
		aabb.min.applyMatrix4(Matrix4f.scale(scale.x, scale.y, scale.z)).applyMatrix4(
				Matrix4f.translate(position.x, position.y, position.z));
		aabb.max.applyMatrix4(Matrix4f.scale(scale.x, scale.y, scale.z)).applyMatrix4(
				Matrix4f.translate(position.x, position.y, position.z));
		boundingSphere.center.copy(aabb.getCenter());
		boundingSphere.radius = getBoundingSphereRadius();
	}
	
	public AABB getAABB() {
		return aabb;
	}
	
	public Sphere getBoundingSphere() {
		return boundingSphere;
	}
	
	public float getBoundingSphereRadius() {
		return mesh.getBoundingSphere().radius * scale.x;
	}
	
	public float getMass() {
		return mass;
	}
	
}
