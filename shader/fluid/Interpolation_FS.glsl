#version 150

uniform sampler2D highTex;
uniform sampler2D lowTex;

in vec2 texCoords;

out vec4 color;

void main(void) {

float depth = texture2D(highTex, texCoords).w;

color = mix(texture2D(lowTex, texCoords), texture2D(highTex, texCoords), depth);

}