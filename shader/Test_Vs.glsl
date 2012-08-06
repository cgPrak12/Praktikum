#version 150 core

uniform mat4 model, viewProj, translation, scale, viewTrans;
uniform sampler2D elevation;
uniform float worldSize;

in vec2 positionMC, vertexTexCoords;
in vec3 vertexColor;

out vec3 positionWC;
out vec3 color;
out vec2 tex;




void main(void) {
    
    vec3 posiWC = mat3(model) * vec3(positionMC.x,0, positionMC.y);
	
	
	//float height = texture(elevation, p).x;
	vec4 pos = scale * translation *   vec4(positionMC.x, 0, positionMC.y,1);
	tex = pos.xz / worldSize + 0.5f;
    gl_Position = viewProj * model  * pos;
	color = vertexColor;
	//positionWC = texPos;
	
}    
