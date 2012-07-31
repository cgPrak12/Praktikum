#version 150 core

uniform mat4 model;
uniform mat4 viewProj;

in vec2 positionMC;

out vec3 fs_in_color;

void main(void) {
	fs_in_color = vec3(1,0,0);
    gl_Position = viewProj * model * vec4(positionMc,0, 1);

}