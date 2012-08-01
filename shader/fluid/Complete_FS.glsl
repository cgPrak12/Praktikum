#version 330

uniform sampler2D thickness;
// add more textures

in vec2 texCoord;

out vec4 color;

void main(void)
{
	// add other textures and weigh them
    color = texture(thickness, texCoord);
}