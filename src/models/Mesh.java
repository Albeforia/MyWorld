package models;

import helpers.AABB;
import helpers.Sphere;

public class Mesh {
	
	private String name;
	
	private int vaoID;
	private float[] vertices;
	private int numVertices;
	
	private AABB aabb;
	private Sphere boundingSphere;
	
	public Mesh(int vaoID, float[] vertices, int numVertices) {
		this.vaoID = vaoID;
		this.vertices = vertices;
		this.numVertices = numVertices;
	}
	
	public void computeAABB() {
		if (aabb == null) {
			aabb = new AABB().setFromPoints(vertices);
		}
	}
	
	public void computeBoundingSphere() {
		if (boundingSphere == null) {
			boundingSphere = new Sphere().setFromPoints(vertices, aabb.getCenter());
		}
	}
	
	public AABB getAABB() {
		return aabb;
	}
	
	public Sphere getBoundingSphere() {
		return boundingSphere;
	}
	
	public int getVAO() {
		return vaoID;
	}
	
	public int getNumVertices() {
		return numVertices;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
