#version 150

in vec2 positionMC;

uniform mat4 viewProj;
uniform vec3 camera;

out vec2 texCoords;

void main(void) {
	gl_Position = vec4(positionMC, 0.0, 1.0);
	texCoords = vec2(0.5,0.5) + 0.5 * positionMC;
}