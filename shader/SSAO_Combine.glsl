#version 150 core

uniform sampler2D ssaoTexture;
uniform sampler2D diffuseTexture;

in vec2 texCoord;

out vec4 finalColor;

void main(void)
{
	finalColor = texture( diffuseTexture, texCoord) - texture(ssaoTexture, texCoord);	
}