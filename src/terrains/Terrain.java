package terrains;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import maths.Matrix4f;
import maths.Vector2f;
import maths.Vector3f;
import models.Mesh;
import models.MeshLoader;
import renderers.ShaderProgram;

public class Terrain {
	
	private static final float SIZE = 800;
	private static final float MAX_HEIGHT = 40;
	private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;
	
	private float x;
	private float z;
	private Matrix4f matrix;
	
	private Mesh mesh;
	
	private int backgroundTexture;
	private int rTexture;
	private int gTexture;
	private int bTexture;
	private int blendMap;
	
	private float[][] heights;
	
	public Terrain(int gridX, int gridZ, MeshLoader loader, String heightMap, ShaderProgram program) {
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		this.matrix = Matrix4f.translate(x, 0, z);
		this.mesh = generateTerrain(loader, heightMap, program);
		backgroundTexture = loader.loadTexture("grassy", "png");
		rTexture = loader.loadTexture("dirt", "png");
		gTexture = loader.loadTexture("pinkFlowers", "png");
		bTexture = loader.loadTexture("path", "png");
		blendMap = loader.loadTexture("blendMap", "png");
	}
	
	public Mesh getMesh() {
		return mesh;
	}
	
	public Matrix4f getMatrix() {
		return matrix;
	}
	
	public float getTerrainHeight(float worldX, float worldZ) {
		float terrainX = worldX - this.x;
		float terrainZ = worldZ - this.z;
		float gridSize = 1f * SIZE / (heights.length - 1);
		int gridX = (int)Math.floor(terrainX / gridSize);
		int gridZ = (int)Math.floor(terrainZ / gridSize);
		if (gridX < 0 || gridX >= heights.length - 1 || gridZ < 0 || gridZ >= heights.length - 1) {
			return 0;
		}
		float x = (terrainX % gridSize) / gridSize;
		float z = (terrainZ % gridSize) / gridSize;
		float answer;
		if (x <= (1 - z)) {
			answer = barryCentric(new Vector3f(0, heights[gridX][gridZ], 0), new Vector3f(1,
					heights[gridX + 1][gridZ], 0), new Vector3f(0,
					heights[gridX][gridZ + 1], 1), new Vector2f(x, z));
		}
		else {
			answer = barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(1,
					heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
					heights[gridX][gridZ + 1], 1), new Vector2f(x, z));
		}
		return answer;
	}
	
	private Mesh generateTerrain(MeshLoader loader, String heightMap, ShaderProgram program) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("resources/misc/" + heightMap + ".png"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		int NUM_VERTEX = image.getHeight();
		int count = NUM_VERTEX * NUM_VERTEX;
		heights = new float[NUM_VERTEX][NUM_VERTEX];
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[6 * (NUM_VERTEX - 1) * (NUM_VERTEX - 1)];
		int vertexPointer = 0;
		for (int i = 0; i < NUM_VERTEX; i++) {
			for (int j = 0; j < NUM_VERTEX; j++) {
				vertices[vertexPointer * 3] = (float)j / ((float)NUM_VERTEX - 1) * SIZE;
				float height = getHeight(j, i, image);
				heights[j][i] = height;
				vertices[vertexPointer * 3 + 1] = height;
				vertices[vertexPointer * 3 + 2] = (float)i / ((float)NUM_VERTEX - 1) * SIZE;
				Vector3f normal = calculateNormal(j, i, image);
				normals[vertexPointer * 3] = normal.x;
				normals[vertexPointer * 3 + 1] = normal.y;
				normals[vertexPointer * 3 + 2] = normal.z;
				textureCoords[vertexPointer * 2] = (float)j / ((float)NUM_VERTEX - 1);
				textureCoords[vertexPointer * 2 + 1] = (float)i / ((float)NUM_VERTEX - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for (int gz = 0; gz < NUM_VERTEX - 1; gz++) {
			for (int gx = 0; gx < NUM_VERTEX - 1; gx++) {
				int topLeft = (gz * NUM_VERTEX) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * NUM_VERTEX) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.load(vertices, textureCoords, normals, indices, program);
	}
	
	private float getHeight(int x, int z, BufferedImage image) {
		if (x < 0 || x >= image.getHeight() || z < 0 || z >= image.getHeight()) {
			return 0;
		}
		float height = image.getRGB(x, z);
		height += MAX_PIXEL_COLOR / 2f;
		height /= MAX_PIXEL_COLOR / 2f;
		height *= MAX_HEIGHT;
		return height;
	}
	
	private Vector3f calculateNormal(int x, int z, BufferedImage image) {
		float heightL = getHeight(x - 1, z, image);
		float heightR = getHeight(x + 1, z, image);
		float heightD = getHeight(x, z - 1, image);
		float heightU = getHeight(x, z + 1, image);
		Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU).normalize();
		return normal;
	}
	
	private float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
	
	public int getBackgroundTexture() {
		return backgroundTexture;
	}
	
	public int getrTexture() {
		return rTexture;
	}
	
	public int getgTexture() {
		return gTexture;
	}
	
	public int getbTexture() {
		return bTexture;
	}
	
	public int getBlendMap() {
		return blendMap;
	}
	
}
