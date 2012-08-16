#version 150

uniform sampler2D colorTex;
in vec4 positionWC;

out vec4 color;

void main(void) {
	vec2 texCoord = vec2(0.5)+0.5*positionWC.xz;
	color = texture(colorTex, texCoord);
}