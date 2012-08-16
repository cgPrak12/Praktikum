#version 150

in vec4 positionWC;

uniform samplerCube cubemap;
uniform mat4 view;

out vec4 color;

void main(void) {
	color = texture(cubemap, (positionWC+vec4(0.5,0,0.5,0)).xyz);

}