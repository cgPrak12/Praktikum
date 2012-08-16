#version 150 core

uniform mat4 viewProj;
uniform vec3 eye;
in vec4 positionMC;
out float lifetime;

void main(void)
{
    gl_Position = viewProj * vec4(positionMC.xyz, 1);
    lifetime = positionMC.w;
    float d = distance(eye, positionMC.xyz);
    gl_PointSize = 20.0 / (1.0 + d*d);
}