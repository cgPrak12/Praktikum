#version 150 core

uniform mat4 model, viewProj, translation, scale, viewTrans;
uniform sampler2D elevation;

in vec2 positionMC, vertexTexCoords;
in vec3 vertexColor;

//out vec2 positionMC;
out vec3 color;

void main(void) {


 



	vec4 pos = scale * translation *   vec4(positionMC.x, 0 , positionMC.y,1);
    gl_Position = viewProj * model  * vec4(pos.x ,0,pos.z ,1);
	color = vertexColor;
}    
