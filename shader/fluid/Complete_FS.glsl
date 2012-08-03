#version 330

uniform sampler2D thickness;
uniform sampler2D depth;
uniform sampler2D light;
// add more textures

in vec2 texCoord;

out vec4 color;

void main(void)
{
	// add other textures and weigh them
    vec4 color1 = texture(thickness, texCoord);
    vec4 color2 = vec4(texture(depth, texCoord).w);
    vec4 color3 = texture(light, texCoord);
    color = color1 + color2 + color3;
    color /= 3.0;
}