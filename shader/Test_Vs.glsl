#version 150 core

uniform mat4 model, viewProj, translation, scale, viewTrans;
uniform sampler2D elevation;
uniform float worldSize;
uniform float heightScale;

in vec2 positionMC, vertexTexCoords;
in vec3 vertexColor;

out vec3 positionWC;
out vec2 tex;
out vec3 color;

void main(void) {
   	vec4 pos = scale * translation *vec4(positionMC.x, 0, positionMC.y,1);
	tex = pos.xz / worldSize + 0.5f;
	
	float height = heightScale * texture(elevation, tex).x;
    gl_Position = viewProj *model * vec4(pos.x,height,pos.z,1);

	color = vertexColor;
	//positionWC = texPos;
	
}    
