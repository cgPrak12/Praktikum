#version 150

in vec4 positionMC;

uniform mat4 viewProj;
uniform vec3 camera;
uniform float size;

out float lifetime;

void main(void) {
    lifetime = positionMC.w;
	gl_Position = viewProj * vec4(positionMC.xyz, 1.0f);
	gl_PointSize = 50 / ( pow(length(positionMC.xyz - camera), 1.0))*1/size;
//	gl_PointSize = 100 / (1 + pow(length(positionMC.xyz - camera), 1.2))*1/size;
}