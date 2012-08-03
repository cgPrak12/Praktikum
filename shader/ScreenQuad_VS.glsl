#version 330
uniform mat4 model;
uniform mat4 viewProj;
uniform mat4 scale;

in vec3 positionMC;
in vec2 vertexTexCoords;

out vec2 texCoord;

void main(void)
{
    gl_Position = viewProj * model * vec4(positionMC, 1);

//    gl_Position = vec4(positionMC, 0.0, 1.0);
    texCoord = vertexTexCoords;
}