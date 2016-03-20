#version 400 core

in vec2 position;

out vec4 v_clipSpace;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

void main(void) {

	v_clipSpace = projectionMatrix * viewMatrix * modelMatrix
					* vec4(position.x, 0, position.y, 1.0);
	gl_Position = v_clipSpace;
 
}