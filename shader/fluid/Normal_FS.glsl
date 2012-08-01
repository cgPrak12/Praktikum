#version 330

uniform sampler2D depthTex;
uniform float texSize;

in vec2 texCoord;
out vec4 color;

float offset = 10.0/texSize;

void main(void)
{
	color = texture(depthTex, texCoord);	
}