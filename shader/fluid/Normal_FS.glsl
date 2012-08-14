#version 330

uniform sampler2D depthTex;
uniform sampler2D depth2Tex;
uniform float texSize;

in vec2 texCoord;
out vec4 normal;
out vec4 normal2;

float factor1 = 1.0;
float factor2 = 1.0;

float offset = 1.0 / texSize;

vec3 eyePos(vec2 offset, sampler2D tex) {
	vec2 newCoord = texCoord.xy + offset;
	float depth = texture(tex, newCoord).w;
	return vec3(newCoord, depth);
}
vec4 calculateNormal(sampler2D tex) {
	vec3 eye = eyePos(vec2(0), tex);
	
	vec3 ddx  = eyePos(vec2( offset, 0), tex) - eye;
	vec3 ddx2 = eye - eyePos(vec2(-offset, 0), tex);
	if(abs(ddx.z) > abs(ddx2.z)) ddx = ddx2;
	
	vec3 ddy  = eyePos(vec2(0,  offset), tex) - eye;
	vec3 ddy2 = eye - eyePos(vec2(0, -offset), tex);
	if(abs(ddy2.z) < abs(ddy.z)) ddy = ddy2;
	
	vec3 newNormal = -cross(ddx, ddy);
	newNormal.z = -newNormal.z;
	return normalize(vec4(newNormal, 0));
}

void main(void) {
	if(texture(depthTex, texCoord).w <= 0) factor1 = 0.0;
	if(texture(depth2Tex, texCoord).w <= 0) factor2 = 0.0;

	normal = calculateNormal(depthTex);
	normal2 = calculateNormal(depth2Tex);
 }
