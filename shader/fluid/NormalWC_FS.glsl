#version 330

uniform sampler2D depthTex;
uniform float texSize;
uniform vec3 camPos;
uniform mat4 invView;

in vec2 texCoord;
out vec4 color;

float offset = 1.0/texSize;

void main(void) {
	float radius = 100.0 / (1 + length(texture(depthTex, texCoord).xyz - camPos));
	color = vec4(radius/100.0);//texture(depthTex, texCoord).wwww;
}