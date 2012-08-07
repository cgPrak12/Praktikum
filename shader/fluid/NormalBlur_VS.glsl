#version 330

in vec2 positionMC;
out vec2 texCoord;

void main(void)
{
    gl_Position = vec4(positionMC, 0.0, 1.0);
    texCoord = vec2(0.5 + 0.5 * positionMC);
}