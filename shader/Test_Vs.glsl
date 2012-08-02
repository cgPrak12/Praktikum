#version 150 core

uniform mat4 model, viewProj, translation, scale, viewTrans;
uniform sampler2D elevation;

in vec2 positionMC, vertexTexCoords;
out vec2 positionC, texCoords;
//out vec3 fs_in_color;

void main(void) {
	float height = texture(elevation, positionMC/1000).x;
	//vec3 pos = vec3(positionMC.x, height, positionMC.y);
	vec4 pos = scale * translation *   vec4(positionMC.x, 0 , positionMC.y,1);
    gl_Position = viewProj * model  * vec4(pos.x ,0,pos.z ,1);
	texCoords = positionMC/1000;
	positionC = positionMC;
}    
