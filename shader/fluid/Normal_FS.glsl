#version 330

uniform sampler2D depthTex;
uniform float texSize;

in vec2 texCoord;
out vec4 color;

float offset = 10.0/texSize;

void main(void)
{
	// get depth
	float depth = texture(depthTex, texCoord).x;
	// paper suggests a maxDepth
	/*
	if(depth > maxDepth) {
		discard; return;
	}
	*/
	vec3 eye = eyePos(vec2(0));
	
	vec3 ddx = eyePos(vec2(offset,0)) - eye;
	vec3 ddx2 = eye - eyePos(vec2(-offset,0));
	
	vec3 ddy = eyePos(vec2(0, offset))- eye;
	vec3 ddy2 = eye - eyePos(vec2(0, -offset));
}

vec3 eyePos(vec2 offset) {
	// something with depth and texCoords, has to be improved!
	float depth = texture(depthTex, texCoord).x;
	return vec3(texCoord.x, texCoord.y, depth);
}