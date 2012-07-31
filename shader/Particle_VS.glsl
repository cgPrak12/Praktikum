#version 150 core

uniform mat4 viewProj;

in vec4 positionMC;

void main(void)
{
    gl_Position = viewProj * vec4(positionMC.xyz, 1);
}