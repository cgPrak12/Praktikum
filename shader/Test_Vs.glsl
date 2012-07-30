#version 150 core

uniform mat4 model;
uniform mat4 viewProj;

in vec3 vs_in_pos;

out vec3 fs_in_color;

void main(void) {
	fs_in_color = vec3(1,0,0);
    gl_Position = viewProj * model * vec4(vs_in_pos, 1);

}