#version 150 core

uniform sampler2D textureImage;
uniform sampler2D normalTexture;

in vec4 normalWC;
in vec2 fragmentTexCoords;
in vec4 tangentWC;

out vec4 finalColor;

void main(void)
{
	vec4 normal = vec4( vec3(texture(normalTexture, fragmentTexCoords)),0);
	
	
	finalColor = texture(textureImage, fragmentTexCoords);
}