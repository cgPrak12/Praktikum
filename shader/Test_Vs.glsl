#version 150 core

uniform mat4 model;
uniform mat4 viewProj;

in vec2 positionMc;

out vec3 fs_in_color;

void main(void) {
    gl_Position = viewProj * model * vec4(positionMc.x,0,positionMc.y, 1);
	fs_in_color = vec3(sin(positionMc),1);
}