#version 150


uniform mat4 view;
uniform mat4 proj;
uniform vec3 camPos;

in vec3 positionMC;

out vec4 positionWC;
out float pointSize;
out vec4 positionMC1;

void main(void) {
	positionWC = vec4(positionMC,1);
	positionMC1 = view * vec4(positionMC,1);
	gl_Position = proj * view * positionWC;
	gl_PointSize = 100.0 / (1 + length(positionMC - camPos));
	pointSize = gl_PointSize;
}