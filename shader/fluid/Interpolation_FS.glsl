#version 150

uniform sampler2D highTex;
uniform sampler2D lowTex;
uniform sampler2D depthTex;

in vec2 texCoord;

out vec4 color;

void main(void) {

float depth = texture2D(depthTex, texCoord).w;  // Sascha meinte, evtl. muss die Tiefe anders skaliert werden ~Basti

color = mix(texture2D(highTex, texCoord), texture2D(lowTex, texCoord), depth);

}