#version 150 core

uniform sampler2D elevation;
in vec3 color;
in vec3 positionWC;
in vec2 tex;

out vec4 finalColor;


void main(void) {
    finalColor = vec4(texture(elevation,tex).r, texture(elevation,tex).r, texture(elevation,tex).r,1);
  // finalColor = vec4(color, 1);
}