#version 150

in vec3 positionMC;
in vec3 normalMC;
in vec4 vertexColor;

uniform mat4 viewProj;
uniform vec3 camPos;

out vec4 positionWC;
out vec4 normalWC;
out vec4 fragColor;

void main(void) {
	positionWC = vec4(positionMC, 1.0f);
	gl_Position = viewProj * positionWC;
	normalWC = vec4(normalMC, 0.0f);
	fragColor = vertexColor;
	positionWC.w = length(positionWC.xyz - camPos);
}