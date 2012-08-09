#version 150

uniform mat4 viewProj;
uniform vec3 camPos;

in vec3 positionMC;

out vec4 positionWC;
out float pointSize;

void main(void) {
	positionWC = vec4(positionMC,1);

	gl_Position = viewProj * positionWC;
	pointSize = 100.0 / (1 + length(positionMC - camPos)); // evtl. wie in Thickness? Basti
	gl_PointSize = pointSize;
}