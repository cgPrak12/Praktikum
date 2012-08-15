#version 150 core

uniform mat4 viewProj;
uniform mat4 model;
uniform mat4 modelIT;
uniform float param;
uniform float worldSize;
uniform mat4 translation;
uniform mat4 scale;
uniform float heightScale;
uniform sampler2D elevation;
uniform sampler2D materials;
in vec3 positionMC;



out float height;
out vec3 normalWC;
out vec2 tex;
out float materialV;
out vec3 tangent;
out vec3 binormal;


void main(void)
{
	vec4 pos = scale * translation * vec4(positionMC.x, 0, positionMC.y, 1);
	tex = pos.xz / worldSize + 0.5f;
	float dx, dz;
	dx = pos.x/worldSize;
	dz = pos.z/worldSize;
	height = heightScale * texture(elevation, tex).r;
	normalWC = texture(elevation, tex).gba;
	materialV = texture(materials, tex).r;
	

	tangent = vec3(pos.x + 1/worldSize, texture(elevation, vec2(tex.x+dx,tex.y+dz)).x - texture(elevation,tex).x, 0.0);
	binormal = vec3(0.0, texture(elevation, vec2(tex.x+dx,tex.y+dz)).x - texture(elevation,tex).x, tex.y + 1/worldSize);
	
	gl_Position = viewProj * model * vec4(pos.x, height, pos.z, 1.0);
}