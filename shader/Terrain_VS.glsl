#version 150 core

uniform mat4 viewProj;
uniform mat4 model;
uniform mat4 modelIT;
uniform float param;

in vec3 positionMC;
in vec3 normalMC;
in float material;

out float height;
out vec3 normalWC;
out float materialV;

void main(void)
{

	materialV = material;
	height = positionMC.y;
	vec3 pos = positionMC;
	//if (height<0.55) pos.y *= 0.5 + 0.5 * (sin(param + positionMC.x) * cos(0.9 * param + 0.8 * positionMC.z));
	normalWC = (modelIT * vec4(normalMC, 0.0)).xyz;
	normalWC = mat3(modelIT) * normalMC;	
	//if (height<0.55) normalWC.y *= 2 + 0.5 * (sin(param + normalWC.x) * cos(0.9 * param + 0.8 * normalWC.z));
	gl_Position = viewProj * model * vec4(pos, 1.0);
}