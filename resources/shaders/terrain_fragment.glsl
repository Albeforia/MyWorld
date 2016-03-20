#version 400 core

in vec2 v_textureCoord;
in vec3 v_surfaceNormal;
in vec3 v_lightDirections[4];
in float v_visibility;
in vec3 v_cameraDirection;

out vec4 fragColor;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;

uniform vec3 lightColors[4];
uniform vec3 attenuations[4];
uniform vec3 skyColor;

void main() {

	vec3 unitNormal = normalize(v_surfaceNormal);
	
	vec3 unitCameraDirection = normalize(v_cameraDirection);

	vec4 blendMapColor = texture(blendMap, v_textureCoord);
	float backTextureAmount = 1 - (blendMapColor.r + blendMapColor.g + blendMapColor.b);
	vec2 tiledCoord = v_textureCoord * 40.0;
	vec4 backgroundTextureColor = texture(backgroundTexture, tiledCoord) * backTextureAmount;
	vec4 rTextureColor = texture(rTexture, tiledCoord) * blendMapColor.r;
	vec4 gTextureColor = texture(gTexture, tiledCoord) * blendMapColor.g;
	vec4 bTextureColor = texture(bTexture, tiledCoord) * blendMapColor.b;
	vec4 totalColor = backgroundTextureColor + rTextureColor + gTextureColor + bTextureColor;
	
	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);
	
	for (int i = 0; i < 4; ++i) {
		float distance = length(v_lightDirections[i]);
		float attFactor = attenuations[i].x +
			attenuations[i].y * distance +
			attenuations[i].z * distance * distance;
		vec3 unitLightDirection = normalize(v_lightDirections[i]);
		float nDotl = dot(unitNormal, unitLightDirection);
		float brightness = max(nDotl, 0.0);
		totalDiffuse = totalDiffuse +
				(brightness * lightColors[i]) / attFactor;
	}
	
	totalDiffuse = max(totalDiffuse, 0.2);
	
	fragColor = vec4(totalDiffuse, 1.0) * totalColor;
			
	fragColor = mix(vec4(skyColor, 1.0), fragColor, v_visibility);
	
}