#version 150 core

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

uniform sampler2D normalTexture;

in vec3 positionMC;
in vec2 vertexTexCoords;
in vec3 normalMC;
in vec3 tangentMC;

out vec4 tangentWC;
out vec4 normalWC;
out vec2 fragmentTexCoords;

void main(void)
{
	gl_Position =  projection *view * model *vec4(positionMC,1);
	normalWC = model*vec4(normalMC,1);
	tangentWC = model*vec4(tangentMC,1);
	fragmentTexCoords = vertexTexCoords;
}