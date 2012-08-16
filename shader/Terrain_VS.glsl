#version 150 core

uniform mat4 viewProj;
uniform mat4 model;
uniform mat4 modelIT;
uniform float param;
uniform float generalScale;

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
	tex = pos.xz / textureSize(elevation,0)/generalScale + 0.5f;
	height = heightScale * texture(elevation, tex).r;
	normalWC = texture(elevation, tex).gba;
	materialV = texture(materials, tex).r;
	
	vec3 c1 = cross(normalWC, vec3(0.0, 0.0, 1.0)); 
	tangent = normalize(c1);
	
	binormal = cross(tangent, normalWC); 
	binormal = normalize(binormal);

	gl_Position = viewProj * model * vec4(pos.x, height, pos.z, 1.0);
}