#version 150 core

uniform mat4 model;
uniform mat4 viewProj;
uniform mat4 translation;
uniform mat4 scale;
uniform mat4 viewTrans;
uniform sampler2D elevation;

in vec2 positionMC;
out vec2 positionC;
in vec2 vertexTexCoords;
out vec2 texCoords;
//out vec3 fs_in_color;

void main(void) {
	float height = texture(elevation, positionMC/1000).x;
	//vec3 pos = vec3(positionMC.x, height, positionMC.y);
	vec4 pos = scale * translation *   vec4(positionMC.x, 0 , positionMC.y,1);
    gl_Position = viewProj * model  * vec4(pos.x ,0,pos.z ,1);
	texCoords = positionMC/1000;
	positionC = positionMC;
}    
