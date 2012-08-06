#version 150

uniform mat4 view;
uniform mat4 proj;
//uniform mat4 model;
//uniform mat4 modelIT;
//uniform vec3 camPos;

in vec3 positionMC;

out vec4 positionWC;
//out vec2 texCoords;

void main(void) { 
   positionWC = vec4(positionMC, 1.0);
   gl_Position =  proj * view * positionWC;
//   positionWC.w = length(positionWC.xyz - camPos);
   
//	texCoords = vec2(0.5,0.5) + 0.5 * positionMC;
}