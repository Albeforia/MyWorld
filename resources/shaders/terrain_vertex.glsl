#version 400 core

in vec3 position;
in vec2 textureCoord;
in vec3 normal;

out vec2 v_textureCoord;
out vec3 v_surfaceNormal;
out vec3 v_lightDirections[4];
out float v_visibility;
out vec3 v_cameraDirection;

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform vec3 lightPositions[4];

uniform vec4 plane;

const float density = 0.004;
const float gradient = 4.0;

void main() {

	vec4 worldPosition = modelMatrix * vec4(position, 1.0);
	vec4 positionToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionToCam;
	
	gl_ClipDistance[0] = dot(worldPosition, plane);
	
	v_textureCoord = textureCoord;
	v_surfaceNormal = (modelMatrix * vec4(normal, 0.0)).xyz;
	
	for (int i = 0; i < 4; ++i) {
		v_lightDirections[i] = lightPositions[i] - worldPosition.xyz;
	}
	
	v_cameraDirection = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz
							- worldPosition.xyz;
	
	float distance = length(positionToCam.xyz);
	v_visibility = exp(-pow((distance * density),gradient));
	v_visibility = clamp(v_visibility, 0.0, 1.0);
	
}