#version 150 core

uniform mat4 model;
uniform mat4 viewProj;
uniform mat4 translation;
//uniform sampler2D elevation;

in vec2 positionMC;
//in vec2 vertexTexCoords;
//out vec2 texCoords;
//out vec3 fs_in_color;

void main(void) {
	//float height = texture(elevation, vertexTexCoords).x;
	//vec3 pos = vec3(positionMC.x, height, positionMC.y);
    gl_Position = viewProj * model * translation *2 *vec4(positionMC.x, 0, positionMC.y, 1);
//	texCoords = vertexTexCoords;
}    
