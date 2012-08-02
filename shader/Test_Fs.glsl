#version 150 core

uniform sampler2D elevation;
in vec2 texCoords;
out vec4 finalColor;
in vec2 positionC;

void main(void) {
    finalColor = vec4(1,1,1,1);
}