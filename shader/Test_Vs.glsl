#version 150 core

uniform mat4 model, viewProj, translation, scale, viewTrans;
uniform sampler2D elevation;
uniform float worldSize;

in vec2 positionMC, vertexTexCoords;
in vec3 vertexColor;
out vec3 positionWC;
out vec3 color;



void main(void) {
	vec3 posWC = mat3(model) * (/*(0.5 + */vec3(positionMC.x, 0, positionMC.y)/worldSize);
	//float height = texture(elevation, p).x;
	vec4 pos = scale * translation *   vec4(positionMC.x,  /*height*/ 0, positionMC.y,1);
    gl_Position = viewProj * model  * pos;
	color = vertexColor;
	positionWC = posWC;
}    
