#version 150 core

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

in vec3 positionMC;
in vec2 texCoords;
in vec3 normalMC;
in vec3 tangentMC;

out vec4 normalWC;
out vec2 fragmentTexCoords;
out vec3 positionWC;
out vec4 tangentWC;

void main(void)
{
	vec4 positionWC_t = model * vec4(positionMC, 1.0);
	positionWC = positionWC_t.xyz / positionWC_t.w;
	gl_Position = projection * view * positionWC_t;
	
	normalWC = model*vec4(normalMC,0);
	tangentWC = model*vec4(tangentMC,0);
	fragmentTexCoords = texCoords;
}