#version 330

uniform mat4 proj;
uniform mat4 view;

in vec3 positionMC;
out vec3 coords;

void main(void)
{
    gl_Position = proj * view * vec4(positionMC, 1.0);
    coords = positionMC;
/*
    gl_Position = vec4(positionMC, 0.0, 1.0);

    texCoord = vec2(0.5, 0.5) + 0.5 * positionMC;
    texCoord.y = 1.0 - texCoord.y;
*/
}