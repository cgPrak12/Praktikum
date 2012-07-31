#version 150 core

uniform mat4 model;
uniform mat4 viewProj;

in vec3 positionMc;

out vec3 fs_in_color;

void main(void) {
    gl_Position = viewProj * model * vec4(positionMc, 1);
	fs_in_color = sin(positionMc);
}