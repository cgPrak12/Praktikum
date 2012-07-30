#version 150 core

in vec3 fs_in_color;

void main(void) {
    finalColor = vec4(fs_in_color, 1);
}