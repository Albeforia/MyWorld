package worlds;

import guis.BasicGui;
import helpers.Frustum;
import helpers.MousePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import lights.Light;
import maths.Plane;
import models.Mesh;
import models.MeshLoader;
import models.OBJLoader;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import renderers.WorldRenderer;
import terrains.Terrain;
import waters.WaterFrameBuffers;
import waters.WaterTile;
import cameras.PerspectiveCamera;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestRayResultCallback;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CylinderShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import entities.Entity;
import entities.Lamp;
import entities.Player;

public class World {
	
	public static final int GRAVITY = -100;
	
	private DynamicsWorld dynamicsWorld;
	private CollisionDispatcher collisionDispatcher;
	
	private PerspectiveCamera camera;
	private Frustum frustum;
	
	private MousePicker mousePicker;
	
	private Map<String, Mesh> meshes;
	private Map<String, Integer> textures;
	private Map<RigidBody, Entity> rigidBodies;
	private List<Entity> entities;
	private List<Light> lights;
	private List<WaterTile> waterTiles;
	private Terrain terrain;
	private Player player;
	private Light sun;
	
	private MeshLoader loader;
	private WorldRenderer worldRenderer;
	
	private WaterFrameBuffers fbos;
	
	public World() {
		initPhysics();
		loader = new MeshLoader();
		worldRenderer = new WorldRenderer(loader);
		meshes = new HashMap<String, Mesh>();
		textures = new HashMap<String, Integer>();
		rigidBodies = new HashMap<RigidBody, Entity>();
		entities = new ArrayList<Entity>();
		lights = new ArrayList<Light>();
		waterTiles = new ArrayList<WaterTile>();
		camera = new PerspectiveCamera(60, 1f * Display.getWidth() / Display.getHeight(), 1, 1000);
		frustum = new Frustum();
		setPlayer(400, 40, 400);
		setTerrain("heightmap");
		setSun();
//		setWater();
		mousePicker = new MousePicker(camera);
	}
	
	private void setPlayer(float x, float y, float z) {
		player = new Player(registerMesh("person"), 10f);
		player.setPosition(x, y, z);
		player.setTexture(registerTexture("person"));
		addRigidBody(player, "cylinder");
		entities.add(player);
		camera.bind(player);
	}
	
	private void setTerrain(String heightmap) {
		terrain = new Terrain(0, 0, loader, heightmap, worldRenderer.getTerrainRenderer().getProgram());
	}
	
	private void setSun() {
		sun = new Light(0xaaaaaa, 1);
		addLight(sun, 0, 3000, 3000);
	}
	
	private void setWater() {
		waterTiles.add(new WaterTile(400, 400, 4));
		fbos = new WaterFrameBuffers();
		addGui(fbos.getReflectionTexture(), -0.5f, 0.5f, 0.3f, 0.3f);
		addGui(fbos.getRefractionTexture(), 0.5f, 0.5f, 0.3f, 0.3f);
	}
	
	private void initPhysics() {
		BroadphaseInterface broadphase = new DbvtBroadphase();
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		collisionDispatcher = new CollisionDispatcher(collisionConfiguration);
		ConstraintSolver solver = new SequentialImpulseConstraintSolver();
		dynamicsWorld = new DiscreteDynamicsWorld(collisionDispatcher, broadphase, solver, collisionConfiguration);
		dynamicsWorld.setGravity(new Vector3f(0, GRAVITY, 0));
	}
	
	private Mesh registerMesh(String name) {
		if (!meshes.containsKey(name)) {
			Mesh mesh = OBJLoader.load(name, loader, worldRenderer.getEntityRenderer().getProgram());
			mesh.setName(name);
			mesh.computeAABB();
			mesh.computeBoundingSphere();
			meshes.put(name, mesh);
		}
		return meshes.get(name);
	}
	
	private int registerTexture(String name) {
		if (!textures.containsKey(name)) {
			textures.put(name, loader.loadTexture(name, "png"));
		}
		return textures.get(name);
	}
	
	public Entity addEntity(String meshName, float x, float y, float z, float scale, float mass, String type) {
		Entity entity = new Entity(registerMesh(meshName), scale, mass).setPosition(x, y, z);
		entity.setTexture(registerTexture(meshName));
		if (mass >= 0) {
			addRigidBody(entity, type);
		}
		entities.add(entity);
		return entity;
	}
	
	public Entity addLamp(int color, float intensity, float x, float y, float z) {
		Lamp lamp = new Lamp(registerMesh("lamp"));
		lamp.setPosition(x, y, z);
		lamp.setLight(color, intensity);
		lamp.setTexture(registerTexture("lamp"));
		addRigidBody(lamp, "cylinder");
		entities.add(lamp);
		addLight(lamp.getLight(), lamp.getPosition().x, lamp.getPosition().y + 30, lamp.getPosition().z);
		return lamp;
	}
	
	private void addLight(Light light, float x, float y, float z) {
		light.setPosition(x, y, z);
		lights.add(light);
	}
	
	public void addGui(String file, float x, float y, float sx, float sy) {
		BasicGui gui = new BasicGui(x, y, sx, sy);
		gui.setTexture(file, "png", loader);
		worldRenderer.addGui(gui);
	}
	
	public void addGui(int texture, float x, float y, float sx, float sy) {
		BasicGui gui = new BasicGui(x, y, sx, sy);
		gui.setTexture(texture);
		worldRenderer.addGui(gui);
	}
	
	private void addRigidBody(Entity entity, String type) {
		float mass = entity.getMass();
		float x = entity.getAABB().getCenter().x;
		float y = entity.getAABB().getCenter().y;
		float z = entity.getAABB().getCenter().z;
		float halfX = entity.getAABB().max.x - x;
		float halfY = entity.getAABB().max.y - y;
		float halfZ = entity.getAABB().max.z - z;
		CollisionShape collisionShape = null;
		switch (type) {
			case "box":
				collisionShape = new BoxShape(new Vector3f(halfX + 1, halfY + 1, halfZ + 1));
				break;
			case "cylinder":
				collisionShape = new CylinderShape(new Vector3f(halfX + 1f, halfY, halfZ + 1f));
				break;
			default:
				collisionShape = new CylinderShape(new Vector3f(halfX + 1, halfY + 1, halfZ + 1));
				break;
		}
		MotionState motionState = new DefaultMotionState(new Transform(new Matrix4f(
				new Quat4f(), new Vector3f(x, y, z), 1.0f)));
		Vector3f inertia = new Vector3f(0, 0, 0);
		collisionShape.calculateLocalInertia(mass, inertia);
		RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(mass, motionState, collisionShape, inertia);
		constructionInfo.restitution = 0;
		constructionInfo.friction = 100;
		RigidBody rigidBody = new RigidBody(constructionInfo);
		if (mass > 0) {
			rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
		}
		dynamicsWorld.addRigidBody(rigidBody);
		entity.setRigidBody(rigidBody);
		rigidBodies.put(rigidBody, entity);
	}
	
	public void update(long delta) {
		player.move(delta);
		// update camera and frustum
		camera.update();
		frustum.setFromMatrix(camera.getProjScreenMatrix());
		// physics simulation
		dynamicsWorld.stepSimulation(delta);
		// update entities' rendering position by physics
		rigidBodies.values().forEach(entity -> entity.updatePhysics(this));
		// mouse picking
		mousePicker.update();
		entities.forEach(entity -> entity.isMouseHovering = false);
		Entity hit = null;
		RayResult rayResult = new RayResult();
		if (raycast(new Vector3f(mousePicker.getCurrentRay().x,
				mousePicker.getCurrentRay().y,
				mousePicker.getCurrentRay().z), 80, rayResult)) {
			hit = rigidBodies.get(rayResult.rigidBody);
			hit.isMouseHovering = true;
		}
		while (Mouse.next()) {
			if (Mouse.getEventButtonState()) {
				if (Mouse.getEventButton() == 0) {
					if (hit instanceof Lamp) {
						((Lamp)hit).trigger();
					}
				}
			}
		}
		// update sun light
		sun.setIntensity(worldRenderer.getSkyboxRenderer().getLightFactor() + 0.1f);
	}
	
	public float getTerrainHeight(float worldX, float worldZ) {
		return terrain.getTerrainHeight(worldX, worldZ);
	}
	
	public void render() {
		Map<Mesh, List<Entity>> entityGruops =
				entities.stream().filter(entity -> frustum.intersectsEntity(entity))
						.collect(Collectors.groupingBy(Entity::getMesh));
		// render reflection
//		fbos.bindReflectionFrameBuffer();
//		worldRenderer.renderScene(camera, entityGruops, terrain, lights, new Plane().set(0, 1, 0, -waterTiles.get(0).getHeight()));
		// render refraction
//		fbos.bindRefractionFrameBuffer();
//		float distance = 2f * (camera.getPosition().y - waterTiles.get(0).getHeight());
//		camera.getPosition().y -= distance;
//		camera.invertPitch();
//		worldRenderer.renderScene(camera, entityGruops, terrain, lights, new Plane().set(0, -1, 0, waterTiles.get(0).getHeight()));
//		camera.getPosition().y += distance;
//		camera.invertPitch();
		// render scene
//		fbos.unbindCurrentFrameBuffer();
		worldRenderer.renderScene(camera, entityGruops, terrain, lights, new Plane().set(0, -1, 0, 99999));
//		worldRenderer.renderWater(camera, waterTiles, fbos);
		worldRenderer.renderGui();
	}
	
	public void cleanUp() {
		worldRenderer.cleanUp();
		loader.cleanUp();
	}
	
	private boolean raycast(Vector3f direction, float distance, RayResult output) {
		// get the picking ray from where we clicked
		Vector3f rayFrom = new Vector3f(camera.getPosition().x, camera.getPosition().y, camera.getPosition().z);
		Vector3f rayTo = new Vector3f(rayFrom.x + direction.x * distance,
				rayFrom.y + direction.y * distance,
				rayFrom.z + direction.z * distance);
		
		// create our raycast callback object
		ClosestRayResultCallback rayCallback = new ClosestRayResultCallback(rayFrom, rayTo);
		
		// perform the raycast
		dynamicsWorld.rayTest(rayFrom, rayTo, rayCallback);
		
		// did we hit something?
		if (rayCallback.hasHit()) {
			// if so, get the rigid body we hit
			RigidBody rigidBody = (RigidBody)(rayCallback.collisionObject);
			if (rigidBody == null) {
				return false;
			}
			// prevent us from picking objects like the ground plane
			if (rigidBody.isStaticObject() || rigidBody.isKinematicObject()) {
//				return false;
			}
			// set the result data
			output.rigidBody = rigidBody;
			output.hitPoint.set(rayCallback.hitPointWorld.x, rayCallback.hitPointWorld.y, rayCallback.hitPointWorld.z);
			return true;
		}
		
		// we didn't hit anything
		return false;
	}
	
	private class RayResult {
		
		public RigidBody rigidBody;
		public Vector3f hitPoint = new Vector3f();
	}
	
}
