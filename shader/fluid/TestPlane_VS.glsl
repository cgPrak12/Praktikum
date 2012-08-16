#version 330

in vec3 positionMC;

uniform mat4 viewProj;

out vec4 positionWC;

void main(void)
{
    positionWC = vec4(positionMC, 1.0);
    gl_Position = viewProj * vec4(positionMC, 1.0);
}