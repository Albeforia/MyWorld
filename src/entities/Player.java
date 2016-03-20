package entities;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import models.Mesh;

import org.lwjgl.input.Keyboard;

import com.bulletphysics.linearmath.Transform;

public class Player extends Entity {
	
	public static final float MOVE_SPEED = .04f;
	public static final float TURN_SPEED = .004f;
	public static final float JUMP_POWER = 36f;
	
	private float currentSpeed;
	private float currentTurnSpeed;
	private float currentJumpSpeed;
	private boolean inAir;
	
	public Player(Mesh mesh, float mass) {
		super(mesh, 1, mass);
	}
	
	public void move(long delta) {
		input();
		rotation.y += currentTurnSpeed * delta;
		if (rotation.y > Math.PI) {
			rotation.y -= Math.PI * 2;
		}
		if (rotation.y < -Math.PI) {
			rotation.y += Math.PI * 2;
		}
		quaternion.setFromEuler(rotation);
		float distance = currentSpeed * delta;
		float dx = (float)(distance * Math.sin(rotation.y));
		float dz = (float)(distance * Math.cos(rotation.y));
		position.x += dx;
		position.z += dz;
		//
		rigidBody.activate(true);
//		rigidBody.applyCentralForce(new javax.vecmath.Vector3f(dx * 50, 0, dz * 50));
//		currentJumpSpeed -= 0.01 * delta;
//		position.y += currentJumpSpeed;
//		float terrainHeight = terrain.getTerrainHeight(position.x, position.z);
//		if (position.y < terrainHeight) {
//			currentJumpSpeed = 0;
//			position.y = terrainHeight;
//			inAir = false;
//		}
		//
		float halfY = .5f * (aabb.max.y - aabb.min.y);
		Vector3f newPosition = new Vector3f(position.x, position.y + halfY, position.z);
		Quat4f newQuaternion = new Quat4f(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
		Transform transform = new Transform(new Matrix4f(newQuaternion, newPosition, 1));
		rigidBody.setWorldTransform(transform);
		updateAABB();
	}
	
	private void input() {
		boolean moveForward = Keyboard.isKeyDown(Keyboard.KEY_W);
		boolean moveBackward = Keyboard.isKeyDown(Keyboard.KEY_S);
		boolean turnLeft = Keyboard.isKeyDown(Keyboard.KEY_A);
		boolean turnRight = Keyboard.isKeyDown(Keyboard.KEY_D);
		boolean jump = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
		if (moveForward) {
			currentSpeed = MOVE_SPEED;
		}
		else if (moveBackward) {
			currentSpeed = -MOVE_SPEED;
		}
		else {
			currentSpeed = 0;
		}
		if (turnLeft) {
			currentTurnSpeed = TURN_SPEED;
		}
		else if (turnRight) {
			currentTurnSpeed = -TURN_SPEED;
		}
		else {
			currentTurnSpeed = 0;
		}
		if (velocity.y == 0 && jump) {
			rigidBody.setLinearVelocity(new Vector3f(0, JUMP_POWER, 0));
//			currentJumpSpeed = JUMP_POWER;
//			inAir = true;
		}
	}
	
}
