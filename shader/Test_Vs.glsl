#version 150 core

uniform mat4 model, viewProj, translation, scale, viewTrans;
uniform sampler2D elevation, high;
uniform float worldSize;

in vec2 positionMC, vertexTexCoords;
in vec3 vertexColor;

out vec3 positionWC;
out vec3 color;
out vec2 tex;


void main(void) {
    
    
	
	
	

	vec4 pos = scale * translation *   vec4(positionMC.x, 0, positionMC.y,1);
	tex = pos.xz / worldSize * 10 + 0.5f;
	float height = 50*texture(elevation, tex).x;
    gl_Position = viewProj * model  * vec4(pos.x,height,pos.z,1);

	color = vertexColor;
	//positionWC = texPos;
	
}    
