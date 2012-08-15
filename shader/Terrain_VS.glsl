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
	vec4 next = scale * translation * vec4(positionMC.x+1/worldSize, 0, positionMC.y+1/worldSize, 1);
	tex = pos.xz / worldSize + 0.5f;
	vec2 texNext = next.xz / worldSize + 0.5f;
	float dx, dz;
	height = heightScale * texture(elevation, tex).r;
	float heightNext = heightScale * texture(elevation, texNext).r;
	normalWC = texture(elevation, tex).gba;
	materialV = texture(materials, tex).r;
	

	tangent = vec3(next.x-pos.x, next.y - pos.y, 0);
	binormal = vec3(next.xyz-pos.xyz);
	
	gl_Position = viewProj * model * vec4(pos.x, height, pos.z, 1.0);
}