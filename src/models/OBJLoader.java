package models;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import maths.Vector2f;
import maths.Vector3f;
import renderers.ShaderProgram;

public class OBJLoader {
	
	public static Mesh load(String file, MeshLoader loader, ShaderProgram program) {
		FileReader fr = null;
		try {
			fr = new FileReader("resources/models/" + file + ".obj");
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(fr);
		String line;
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		float[] verticesArray = null;
		float[] texturesArray = null;
		float[] normalsArray = null;
		int[] indicesArray = null;
		try {
			while (true) {
				line = reader.readLine();
				String[] currentLine = line.split(" ");
				if (line.startsWith("v ")) {
					Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),
							Float.parseFloat(currentLine[2]),
							Float.parseFloat(currentLine[3]));
					vertices.add(vertex);
				}
				else if (line.startsWith("vn ")) {
					Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]),
							Float.parseFloat(currentLine[2]),
							Float.parseFloat(currentLine[3]));
					normals.add(normal);
				}
				else if (line.startsWith("vt ")) {
					Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]),
							Float.parseFloat(currentLine[2]));
					textures.add(texture);
				}
				else if (line.startsWith("f ")) {
					normalsArray = new float[vertices.size() * 3];
					texturesArray = new float[vertices.size() * 2];
					break;
				}
			}
			while (line != null) {
				String[] currentLine = line.split(" ");
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");
				processVertex(vertex1, indices, textures, normals, texturesArray, normalsArray);
				processVertex(vertex2, indices, textures, normals, texturesArray, normalsArray);
				processVertex(vertex3, indices, textures, normals, texturesArray, normalsArray);
				line = reader.readLine();
			}
			reader.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		verticesArray = new float[vertices.size() * 3];
		indicesArray = new int[indices.size()];
		int vertexIndex = 0;
		for (Vector3f vertex : vertices) {
			verticesArray[vertexIndex++] = vertex.x;
			verticesArray[vertexIndex++] = vertex.y;
			verticesArray[vertexIndex++] = vertex.z;
		}
		for (int i = 0; i < indices.size(); ++i) {
			indicesArray[i] = indices.get(i);
		}
		return loader.load(verticesArray, texturesArray, normalsArray, indicesArray, program);
	}
	
	private static void processVertex(String[] vertexData,
										List<Integer> indices, List<Vector2f> textures, List<Vector3f> normals,
										float[] texturesArray, float[] normalsArray) {
		int currentVertexIndex = Integer.parseInt(vertexData[0]) - 1;
		indices.add(currentVertexIndex);
		Vector2f currentTexture = textures.get(Integer.parseInt(vertexData[1]) - 1);
		texturesArray[currentVertexIndex * 2] = currentTexture.x;
		texturesArray[currentVertexIndex * 2 + 1] = 1 - currentTexture.y;
		Vector3f currentNormal = normals.get(Integer.parseInt(vertexData[2]) - 1);
		normalsArray[currentVertexIndex * 3] = currentNormal.x;
		normalsArray[currentVertexIndex * 3 + 1] = currentNormal.y;
		normalsArray[currentVertexIndex * 3 + 2] = currentNormal.z;
	}
	
}
