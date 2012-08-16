#version 150

in vec2 texCoords;

uniform sampler2D terrain;
uniform sampler2D depthTex;
uniform sampler2D skyTex;

out vec3 color;

void main(void) {

	vec4 terrainColor = texture(terrain, texCoords);
	float depth = texture(depthTex, texCoords).w;
	color = texture(skyTex, texCoords).xyz;
	if(terrainColor.w > depth) {
		color = vec3(terrainColor);
	}
	if(depth > 0) {
		color = vec3(0,0,1);
	}
//	color = texture(depthTex, texCoords).xyz;

//	color = texture(terrain, texCoords).xyz + vec3(0,0,texture(depthTex, texCoords).w*100); 
}