#version 150 core

in vec3 fs_in_color;

out vec4 finalColor;

void main(void) {
    finalColor = vec4(fs_in_color, 1);
}