#version 150 core

uniform mat4 viewProj;
uniform mat4 model;

in vec3 positionMC;
in vec3 normalMC;
in vec3 tangentMC;
in vec2 vertexTexCoords;

out vec3 positionWC;
out vec3 normalWC;
out vec3 tangentWC;
out vec2 fragmentTexCoords;

void main(void)
{
	vec4 positionWC_t = model * vec4(positionMC, 1.0);
	positionWC = positionWC_t.xyz / positionWC_t.w;
	gl_Position = viewProj * positionWC_t;
	
	normalWC = (model * vec4(normalMC, 0.0)).xyz;
	tangentWC = (model * vec4(tangentMC, 0.0)).xyz;
	
	fragmentTexCoords = vertexTexCoords;
}