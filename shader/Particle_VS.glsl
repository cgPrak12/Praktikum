#version 150 core

uniform mat4 viewProj;
uniform vec3 eye;
in vec4 positionMC;
out float lifetime;

void main(void)
{
    gl_Position = viewProj * vec4(positionMC.xyz, 1);
    lifetime = positionMC.w;
    gl_PointSize = 5.0 /(1.0 + distance(eye, positionMC.xyz));   
}