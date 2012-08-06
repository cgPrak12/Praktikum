#version 150 core

uniform sampler2D elevation;
in vec3 color;
in vec2 posMC;
out vec4 finalColor;


void main(void) {
//    finalColor = vec4(texture(elevation,posMC).x,texture(elevation,posMC).x,texture(elevation,posMC).x,1);
    finalColor = vec4(color, 1);
}