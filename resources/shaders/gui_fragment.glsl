#version 140

in vec2 v_textureCoord;

out vec4 out_Color;

uniform sampler2D guiTexture;

void main(void){

	vec4 textureColor = texture(guiTexture, v_textureCoord);
	out_Color = textureColor;
	out_Color.a = 0.5;

}