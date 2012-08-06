#version 330

uniform sampler2D depthTex;
uniform float texSize;
uniform vec3 camPos;

in vec2 texCoord;
out vec4 color;

float offset = 1.0/texSize;

vec3 eyePos(vec2 offset) {
	// something with depth and texCoords, has to be improved!
	vec2 newCoord = texCoord.xy+offset;
	float depth = texture(depthTex, newCoord).w;
	return vec3(newCoord, depth);
}

void main(void) {
	float depth = texture(depthTex, texCoord).w;
//	if(depth <= 0) discard;
	vec3 eye = eyePos(vec2(0));
	
	vec3 ddx = eyePos(vec2(offset,0)) - eye;
	vec3 ddx2 = eye - eyePos(vec2(-offset,0));
	if(abs(ddx.z) > abs(ddx2.z)) ddx = ddx2;
	
	vec3 ddy = eyePos(vec2(0, offset))- eye;
	vec3 ddy2 = eye - eyePos(vec2(0, -offset));
	if(abs(ddy2.z) < abs(ddy.z)) ddy = ddy2;
	
	vec3 normal = -cross(ddx, ddy);
	color = normalize(vec4(normal, 0));
 }
