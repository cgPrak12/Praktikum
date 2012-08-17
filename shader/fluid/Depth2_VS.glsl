#version 150

uniform mat4 viewProj;
uniform vec3 camPos;
uniform float size;

in vec4 positionMC;

out vec4 positionWC;
out float pointSize;
out float lifetime;

void main(void) {
	positionWC = vec4(positionMC.xyz,1);
	lifetime = positionMC.w;

	gl_Position = viewProj * positionWC;

	pointSize = 200 / ( pow(length(positionMC.xyz - camPos), 1.0))*1/size;
	gl_PointSize = pointSize;
}