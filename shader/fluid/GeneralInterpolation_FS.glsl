#version 150
in vec2 texCoord;

uniform sampler2D depthTex;
uniform sampler2D texHQ;
uniform sampler2D texLQ;

out vec4 color;

void main(void) {
	float p = texture(depthTex, texCoord).w;
	color = texture(texHQ, texCoord) * (1-p) 
		  + texture(texLQ, texCoord) * p;
} 