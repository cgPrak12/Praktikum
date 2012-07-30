#version 150 core


uniform mat4 view;
uniform mat4 projection;

in vec3 positionMC;
in vec2 texCoords;

out vec3 normalWC;
out vec2 fragmentTexCoords;

void main(void)
{
	gl_Position = view * projection  * vec4(positionMC,1);
	fragmentTexCoords = texCoords;
}