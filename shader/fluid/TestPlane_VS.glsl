#version 150

uniform mat4 viewProj;

in vec3 positionMC;

out vec4 positionWC;

void main(void) { 
   positionWC = vec4(positionMC, 1.0);
   gl_Position =  viewProj * vec4(positionMC, 1.0);
}