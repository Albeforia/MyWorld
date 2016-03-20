package renderers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import maths.Matrix4f;
import maths.Vector3f;
import maths.Vector4f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ShaderProgram {
	
	private static final String SHADER_SOURCE_ROOT = "resources/shaders/";
	
	private final int id;
	private final String type;
	
	private int vertexShaderID;
	private int fragmentShaderID;
	
	public Map<String, Integer> attributeLocations;
	public Map<String, Integer> uniformLocations;
	
	public ShaderProgram(String type) {
		this.type = type;
		attributeLocations = new HashMap<String, Integer>();
		uniformLocations = new HashMap<String, Integer>();
		vertexShaderID = loadShader(GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(GL20.GL_FRAGMENT_SHADER);
		loadNames();
		id = GL20.glCreateProgram();
		GL20.glAttachShader(id, vertexShaderID);
		GL20.glAttachShader(id, fragmentShaderID);
		GL20.glLinkProgram(id);
		GL20.glValidateProgram(id);
		cacheAttributeLocations();
		cacheUniformLocations();
	}
	
	public void uploadUniformData(String name, Matrix4f value) {
		if (!uniformLocations.containsKey(name)) {
			System.err.println("Uniform variable " + name + " does not exist.");
			return;
		}
		int location = uniformLocations.get(name);
		if (location < 0) {
			System.err.println("Location of " + name + " is not valid.");
			return;
		}
		GL20.glUniformMatrix4(location, false, value.getBuffer());
	}
	
	public void uploadUniformData(String name, Vector3f value) {
		if (!uniformLocations.containsKey(name)) {
			System.err.println("Uniform variable " + name + " does not exist.");
			return;
		}
		int location = uniformLocations.get(name);
		if (location < 0) {
			System.err.println("Location of " + name + " is not valid.");
			return;
		}
		GL20.glUniform3(location, value.getBuffer());
	}
	
	public void uploadUniformData(String name, Vector4f value) {
		if (!uniformLocations.containsKey(name)) {
			System.err.println("Uniform variable " + name + " does not exist.");
			return;
		}
		int location = uniformLocations.get(name);
		if (location < 0) {
			System.err.println("Location of " + name + " is not valid.");
			return;
		}
		GL20.glUniform4(location, value.getBuffer());
	}
	
	public void uploadUniformData(String name, int value) {
		if (!uniformLocations.containsKey(name)) {
			System.err.println("Uniform variable " + name + " does not exist.");
			return;
		}
		int location = uniformLocations.get(name);
		if (location < 0) {
			System.err.println("Location of " + name + " is not valid.");
			return;
		}
		GL20.glUniform1i(location, value);
	}
	
	public void uploadUniformData(String name, float value) {
		if (!uniformLocations.containsKey(name)) {
			System.err.println("Uniform variable " + name + " does not exist.");
			return;
		}
		int location = uniformLocations.get(name);
		if (location < 0) {
			System.err.println("Location of " + name + " is not valid.");
			return;
		}
		GL20.glUniform1f(location, value);
	}
	
	public void enableAttributes() {
		for (Integer location : attributeLocations.values()) {
			if (location >= 0) {
				GL20.glEnableVertexAttribArray(location);
			}
		}
	}
	
	public void disableAttributes() {
		for (Integer location : attributeLocations.values()) {
			if (location >= 0) {
				GL20.glDisableVertexAttribArray(location);
			}
		}
	}
	
	public void start() {
		GL20.glUseProgram(id);
	}
	
	public void cleanUp() {
		GL20.glUseProgram(0);
		GL20.glDetachShader(id, vertexShaderID);
		GL20.glDetachShader(id, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		GL20.glDeleteProgram(id);
	}
	
	public int getAttributeLocation(String name) {
		return attributeLocations.get(name);
	}
	
	public int getUniformLocation(String name) {
		return uniformLocations.get(name);
	}
	
	private void cacheAttributeLocations() {
		for (String name : attributeLocations.keySet()) {
			attributeLocations.put(name, GL20.glGetAttribLocation(id, name));
		}
	}
	
	private void cacheUniformLocations() {
		for (String name : uniformLocations.keySet()) {
			uniformLocations.put(name, GL20.glGetUniformLocation(id, name));
		}
	}
	
	private int loadShader(int shaderType) {
		String file = type + (shaderType == GL20.GL_VERTEX_SHADER ? "_vertex" : "_fragment");
		StringBuilder shaderSource = new StringBuilder();
		try (FileReader fr = new FileReader(SHADER_SOURCE_ROOT + file + ".glsl");
				BufferedReader reader = new BufferedReader(fr)) {
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		int shaderID = GL20.glCreateShader(shaderType);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
			throw new RuntimeException(GL20.glGetShaderInfoLog(shaderID, 1024));
		}
		return shaderID;
	}
	
	private void loadNames() {
		try (FileReader fr = new FileReader(SHADER_SOURCE_ROOT + type + ".attributes");
				BufferedReader reader = new BufferedReader(fr)) {
			String line;
			while ((line = reader.readLine()) != null) {
				attributeLocations.put(line, -1);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		try (FileReader fr = new FileReader(SHADER_SOURCE_ROOT + type + ".uniforms");
				BufferedReader reader = new BufferedReader(fr)) {
			String line;
			while ((line = reader.readLine()) != null) {
				uniformLocations.put(line, -1);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
