package renderers;

import entities.Entity;
import guis.BasicGui;
import helpers.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lights.Light;
import maths.Matrix4f;
import maths.Plane;
import maths.Vector3f;
import maths.Vector4f;
import models.Mesh;
import models.MeshLoader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import terrains.Terrain;
import waters.WaterFrameBuffers;
import waters.WaterTile;
import cameras.PerspectiveCamera;

public class WorldRenderer {
	
	public static final int MAX_LIGHTS = 4;
	
	private List<BasicGui> guis;
	
	private ShaderProgram currentProgram;
	private Renderer entityRenderer;
	private Renderer terrainRenderer;
	private Renderer skyboxRenderer;
	private Renderer waterRenderer;
	private Renderer guiRenderer;
	
	private Color fogColor;
	private Vector3f fixedFogColor;
	
	private Matrix4f skyboxView;
	private float skyboxRotation;
	
	public WorldRenderer(MeshLoader loader) {
		fogColor = new Color(0x667788);
		//
		guis = new ArrayList<BasicGui>();
		//
		entityRenderer = new EntityRenderer(new ShaderProgram("entity"));
		terrainRenderer = new TerrainRenderer(new ShaderProgram("terrain"));
		skyboxRenderer = new SkyboxRenderer(new ShaderProgram("skybox"), loader);
		waterRenderer = new WaterRenderer(new ShaderProgram("water"), loader);
		guiRenderer = new GuiRenderer(new ShaderProgram("gui"), loader);
		//
		skyboxView = new Matrix4f();
		skyboxRotation = 0;
		//
		GL11.glClearColor(fogColor.r, fogColor.g, fogColor.b, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
	}
	
	public void renderScene(PerspectiveCamera camera, Map<Mesh, List<Entity>> entityGruops, Terrain terrain,
							List<Light> lights, Plane clipPlane) {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		//
		fixedFogColor = fogColor.toVector();
		fixedFogColor.scale(((SkyboxRenderer)skyboxRenderer).getLightFactor());
		//
		currentProgram = entityRenderer.getProgram();
		currentProgram.start();
		uploadUniforms(camera, lights, clipPlane);
		for (Mesh mesh : entityGruops.keySet()) {
			List<Entity> entities = entityGruops.get(mesh);
//			System.out.println(mesh.getName() + ": " + entities.size());
			GL30.glBindVertexArray(mesh.getVAO());
			currentProgram.enableAttributes();
			((EntityRenderer)entityRenderer).render(entities);
			currentProgram.disableAttributes();
			GL30.glBindVertexArray(0);
		}
		//
		currentProgram = terrainRenderer.getProgram();
		currentProgram.start();
		uploadUniforms(camera, lights, clipPlane);
		GL30.glBindVertexArray(terrain.getMesh().getVAO());
		currentProgram.enableAttributes();
		((TerrainRenderer)terrainRenderer).render(terrain);
		currentProgram.disableAttributes();
		GL30.glBindVertexArray(0);
		//
		currentProgram = skyboxRenderer.getProgram();
		currentProgram.start();
		currentProgram.uploadUniformData("projectionMatrix", camera.getProjectionMatrix());
		skyboxView.copy(camera.getViewMatrix());
		skyboxView.m03 = 0;
		skyboxView.m13 = -40;
		skyboxView.m23 = 0;
		skyboxRotation += 0.0006f;
		if (skyboxRotation > Math.PI * 2) {
			skyboxRotation -= Math.PI * 2;
		}
		currentProgram.uploadUniformData("viewMatrix", skyboxView.multiply(Matrix4f.rotate(skyboxRotation, 0, 1, 0)));
		((SkyboxRenderer)skyboxRenderer).render(fixedFogColor);
	}
	
	public void renderWater(PerspectiveCamera camera, List<WaterTile> waterTiles, WaterFrameBuffers fbos) {
		currentProgram = waterRenderer.getProgram();
		currentProgram.start();
		currentProgram.uploadUniformData("projectionMatrix", camera.getProjectionMatrix());
		currentProgram.uploadUniformData("viewMatrix", camera.getViewMatrix());
		((WaterRenderer)waterRenderer).render(waterTiles, fbos);
	}
	
	public void renderGui() {
		currentProgram = guiRenderer.getProgram();
		currentProgram.start();
		((GuiRenderer)guiRenderer).render(guis);
	}
	
	public void addGui(BasicGui gui) {
		guis.add(gui);
	}
	
	public EntityRenderer getEntityRenderer() {
		return (EntityRenderer)entityRenderer;
	}
	
	public TerrainRenderer getTerrainRenderer() {
		return (TerrainRenderer)terrainRenderer;
	}
	
	public SkyboxRenderer getSkyboxRenderer() {
		return (SkyboxRenderer)skyboxRenderer;
	}
	
	public void cleanUp() {
		entityRenderer.cleanUp();
		terrainRenderer.cleanUp();
		skyboxRenderer.cleanUp();
		waterRenderer.cleanUp();
		guiRenderer.cleanUp();
	}
	
	private void uploadUniforms(PerspectiveCamera camera, List<Light> lights, Plane clipPlane) {
		currentProgram.uploadUniformData("skyColor", fixedFogColor);
		currentProgram.uploadUniformData("projectionMatrix", camera.getProjectionMatrix());
		currentProgram.uploadUniformData("viewMatrix", camera.getViewMatrix());
		currentProgram.uploadUniformData("plane",
				new Vector4f(clipPlane.normal.x, clipPlane.normal.y, clipPlane.normal.z, clipPlane.constant));
		for (int i = 0; i < MAX_LIGHTS; ++i) {
			if (i < lights.size()) {
				currentProgram.uploadUniformData("lightPositions[" + i + "]", lights.get(i)
						.getPosition());
				currentProgram.uploadUniformData("lightColors[" + i + "]", lights.get(i).getColor());
				currentProgram.uploadUniformData("attenuations[" + i + "]", lights.get(i)
						.getAttenuation());
			}
			else {
				currentProgram.uploadUniformData("lightPositions[" + i + "]", new Vector3f());
				currentProgram.uploadUniformData("lightColors[" + i + "]", new Vector3f());
				currentProgram.uploadUniformData("attenuations[" + i + "]", new Vector3f(1, 0, 0));
			}
		}
	}
	
}
