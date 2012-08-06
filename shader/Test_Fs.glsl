#version 150 core

uniform sampler2D elevation;
in vec3 color;
out vec4 finalColor;


void main(void) {
    finalColor = vec4(color,1);
}