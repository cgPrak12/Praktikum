#version 150 core

uniform mat4 model;
uniform mat4 viewProj;
uniform mat4 scale;

in vec3 positionMC;


void main(void) {
    gl_Position = viewProj * model * scale * vec4(positionMC, 1);
}