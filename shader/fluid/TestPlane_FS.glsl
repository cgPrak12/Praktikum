#version 150

uniform sampler2D colorTex;
in vec2 texCoord;

out vec4 color;

void main(void) {

	color = texture(colorTex, texCoord);
}