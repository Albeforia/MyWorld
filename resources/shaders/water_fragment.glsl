#version 400 core

out vec4 out_Color;

in vec4 v_clipSpace;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;

void main() {

	vec2 ndc = (v_clipSpace.xy / v_clipSpace.w) * 0.5 + 0.5;
	vec2 reflectionTexCoord = vec2(ndc.x, 1 - ndc.y);
	vec2 refractionTexCoord = vec2(ndc.x, ndc.y);

	vec4 reflectionColor = texture(reflectionTexture, reflectionTexCoord);
	vec4 refractionColor = texture(refractionTexture, refractionTexCoord);

	out_Color = mix(reflectionColor, refractionColor, 0.5);

}