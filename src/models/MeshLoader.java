package models;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import renderers.ShaderProgram;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class MeshLoader {
	
	private List<Integer> vaoIDs;
	private List<Integer> vboIDs;
	private List<Integer> textureIDs;
	
	public MeshLoader() {
		vaoIDs = new ArrayList<Integer>();
		vboIDs = new ArrayList<Integer>();
		textureIDs = new ArrayList<Integer>();
	}
	
	public Mesh load(float[] positions, float[] textureCoords, float[] normals, int[] indices,
						ShaderProgram program) {
		int id = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(id);
		vaoIDs.add(id);
		uploadAttributeData("position", positions, 3, program);
		uploadAttributeData("textureCoord", textureCoords, 2, program);
		uploadAttributeData("normal", normals, 3, program);
		uploadIndicesData(indices);
		// unbind vao
		GL30.glBindVertexArray(0);
		return new Mesh(id, positions, indices.length);
	}
	
	public Mesh load(float[] positions, int dimentions, ShaderProgram program) {
		int id = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(id);
		vaoIDs.add(id);
		uploadAttributeData("position", positions, dimentions, program);
		// unbind vao
		GL30.glBindVertexArray(0);
		return new Mesh(id, positions, positions.length / dimentions);
	}
	
	public int loadTexture(String file, String format) {
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture(format, new FileInputStream("resources/textures/" + file + "." + format));
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		int id = texture.getTextureID();
		textureIDs.add(id);
		return id;
	}
	
	public int loadCubeMap(String[] textureFiles) {
		int id = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, id);
		for (int i = 0; i < textureFiles.length; ++i) {
			TextureData data = decodeTextureFile("resources/textures/skybox/" + textureFiles[i] + ".png");
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0,
					GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		textureIDs.add(id);
		return id;
	}
	
	private TextureData decodeTextureFile(String fileName) {
		int width = 0;
		int height = 0;
		ByteBuffer buffer = null;
		try {
			FileInputStream in = new FileInputStream(fileName);
			PNGDecoder decoder = new PNGDecoder(in);
			width = decoder.getWidth();
			height = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(buffer, width * 4, Format.RGBA);
			buffer.flip();
			in.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return new TextureData(buffer, width, height);
	}
	
	public void cleanUp() {
		// TODO unboxing cost
		vaoIDs.forEach(id -> GL30.glDeleteVertexArrays(id));
		vboIDs.forEach(id -> GL15.glDeleteBuffers(id));
		textureIDs.forEach(id -> GL11.glDeleteTextures(id));
	}
	
	private void uploadAttributeData(String name, float[] data, int coordSize, ShaderProgram program) {
		int location = program.getAttributeLocation(name);
		if (location < 0) {
			System.err.println("Location of " + name + " is not valid.");
			return;
		}
		int id = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
		vboIDs.add(id);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length).put(data);
		buffer.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(location, coordSize, GL11.GL_FLOAT, false, 0, 0);
		// unbind vbo
//		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private void uploadIndicesData(int[] data) {
		int id = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, id);
		vboIDs.add(id);
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length).put(data);
		buffer.flip();
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		// unbind vbo
//		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
}
