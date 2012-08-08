#version 150

uniform sampler2D highTex;
uniform sampler2D lowTex;

in vec2 texCoord;

out vec4 color;

void main(void) {

float depth = texture2D(highTex, texCoord).w;

color = mix(texture2D(lowTex, texCoord), texture2D(highTex, texCoord), depth);

}