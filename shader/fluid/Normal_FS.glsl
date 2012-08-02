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
	if(abs(ddx.z) > abs(ddx2.z)) ddx = ddx2;
	
	vec3 ddy = eyePos(vec2(0, offset))- eye;
	vec3 ddy2 = eye - eyePos(vec2(0, -offset));
	if(abs(ddy2.z) < abs(ddy.z)) ddy = ddy2;
	
	vec3 normal = cross(ddx, ddy);
	color = vec4(normalize(normal),0);
}

vec3 eyePos(vec2 offset) {
	// something with depth and texCoords, has to be improved!
	float depth = texture(depthTex, texCoord+offset).x;
	return vec3(texCoord.x, texCoord.y, depth);
}