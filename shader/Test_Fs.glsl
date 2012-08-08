#version 150 core

uniform sampler2D elevation;
uniform sampler2D coloration;
in vec2 tex;

out vec4 finalColor;


void main(void) {
	finalColor = vec4(texture(coloration,tex).rgb, 1);
}