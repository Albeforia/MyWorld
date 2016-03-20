#version 400

in vec3 v_textureCoord;
out vec4 out_Color;

uniform samplerCube cubeMap1;
uniform samplerCube cubeMap2;
uniform float blendFactor;
uniform vec3 fogColor;

const float lowerLimit = 0;
const float upperLimit = 30.0;

void main(void){
	vec4 texture1 = texture(cubeMap1, v_textureCoord);
	vec4 texture2 = texture(cubeMap2, v_textureCoord);
	vec4 finalColor = mix(texture1, texture2, blendFactor);
	float factor = (v_textureCoord.y - lowerLimit) / (upperLimit - lowerLimit);
    factor = clamp(factor, 0.0, 1.0);
    out_Color = mix(vec4(fogColor, 1.0), finalColor, factor);
}