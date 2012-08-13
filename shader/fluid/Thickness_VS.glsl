#version 150

in vec3 positionMC;

uniform mat4 viewProj;
uniform vec3 camera;
uniform float size;

void main(void) {
	gl_Position = viewProj * vec4(positionMC, 1.0f);
	gl_PointSize = 50 / ( pow(length(positionMC.xyz - camera), 1.0))*1/size;
}