#version 150 core

uniform mat4 model;
uniform mat4 viewProj;
uniform mat4 scale;

in vec3 positionMC;
in vec2 vertexTexCoords;

out vec2 fragmentTexCoords;

void main(void) {
    fragmentTexCoords = vertexTexCoords;
    gl_Position = viewProj * model * scale * vec4(positionMC, 1);
}

