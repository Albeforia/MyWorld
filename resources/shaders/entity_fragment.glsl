#version 400 core

in vec2 v_textureCoord;
in vec3 v_surfaceNormal;
in vec3 v_lightDirections[4];
in float v_visibility;

out vec4 fragColor;

uniform sampler2D textureSampler;

uniform vec3 lightColors[4];
uniform vec3 attenuations[4];
uniform vec3 skyColor;

uniform float isMouseHovering;

void main() {
	vec3 unitNormal = normalize(v_surfaceNormal);
	
	vec3 totalDiffuse = vec3(0.0);
	
	for (int i = 0; i < 4; ++i) {
		float distance = length(v_lightDirections[i]);
		float attFactor = attenuations[i].x +
			attenuations[i].y * distance +
			attenuations[i].z * distance * distance;
		vec3 unitLightDirection = normalize(v_lightDirections[i]);
		float nDotl = dot(unitNormal, unitLightDirection);
		float brightness = max(0.0, nDotl);
		totalDiffuse += (brightness * lightColors[i]) / attFactor;
	}
	totalDiffuse = max(vec3(0.2, 0.2, 0.2), totalDiffuse);
	
	vec4 textureColor = texture(textureSampler, v_textureCoord);
	if (textureColor.a < 0.5) {
		discard;
	}
	
	fragColor = vec4(totalDiffuse, 1.0) * textureColor;
	fragColor = mix(vec4(skyColor, 1.0), fragColor, v_visibility);
	
	if (isMouseHovering > 0) {
		fragColor *= 2;
	}
	
}