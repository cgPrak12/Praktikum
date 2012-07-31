#version 150 core

uniform sampler2D elevation;
in vec2 texCoords;
out vec4 finalColor;

void main(void) {
    finalColor = vec4(texture(elevation, texCoords).x,texture(elevation, texCoords).x,texture(elevation, texCoords).x,1);
}