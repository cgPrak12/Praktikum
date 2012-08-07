#version 150 core

uniform sampler2D shadowImage;

in vec2 texCoord;

out vec4 color;

void main(void)
{
	vec4 shadow = texture(shadowImage, texCoord);

	color = vec4(shadow.w, shadow.w, shadow.w, shadow.w);
}