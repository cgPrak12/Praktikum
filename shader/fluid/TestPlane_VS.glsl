#version 150

uniform mat4 viewProj;

in vec3 positionMC;

out vec4 positionWC;

void main(void) { 
   gl_Position =  viewProj * vec4(positionMC, 1.0);
}