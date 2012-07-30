#version 150 core

uniform sampler2D textureImage;
uniform sampler2D normalTexture;

in vec3 normalWC;
in vec2 fragmentTexCoords;

out vec4 finalColor;

void main(void)
{
	finalColor =vec4(1);
	//finalColor = texture(textureImage, fragmentTexCoords);
}