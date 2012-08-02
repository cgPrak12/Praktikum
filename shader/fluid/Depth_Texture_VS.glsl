#version 150
uniform mat4 viewProj;
uniform vec3 camPos;

in vec3 positionMC;

out vec4 positionWC;

void main(void) {
	positionWC = vec4(positionMC,1);
	gl_Position = viewProj * positionWC;
	gl_PointSize = 100.0 / (1 + length(positionMC - camPos));
}