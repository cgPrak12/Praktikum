#version 330
uniform mat4 viewProj;
uniform mat4 model;
uniform mat4 modelIT;
uniform mat4 scale;
uniform mat4 translate;

in vec3 positionMC;
in vec3 normalMC;
in vec2 vertexTexCoords;

out vec4 positionWC;
out vec4 normalWC;
out vec2 fragmentTexCoords;

void main(void)
{
    vec4 world = model * vec4(positionMC, 1.0);
    positionWC = vec4(world.xyz / world.w, 1.0);
    normalWC = (modelIT * vec4(normalMC, 0.0));
    fragmentTexCoords = vertexTexCoords;
    gl_Position = viewProj * translate * scale * world;
}