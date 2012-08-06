#version 150 core

uniform mat4 model, viewProj, translation, scale, viewTrans;
uniform sampler2D elevation;

in vec2 positionMC, vertexTexCoords;
in vec3 vertexColor;
out vec2 posMC;
out vec3 color;



void main(void) {
//	vec2 p = positionMC/100;
//	float height = texture(elevation, p);
	vec4 pos = scale * translation *   vec4(positionMC.x, /* height */ 0 , positionMC.y,1);
    gl_Position = viewProj * model  * pos;
	color = vertexColor;
//	posMC = p;
}    
