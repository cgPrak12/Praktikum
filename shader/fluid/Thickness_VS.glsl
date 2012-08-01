#version 150

in vec3 positionMC;

uniform mat4 viewProj;
uniform vec3 camera;

void main(void) {
	vec4 positionWC;
	positionWC = viewProj * vec4(positionMC, 1.0f);
	gl_Position = positionWC;
	positionWC.w = length(positionMC.xyz - camera);
	gl_PointSize = 100 / (1 + pow(positionWC.w, 1.2));
}