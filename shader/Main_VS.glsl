#version 150

uniform mat4 viewProj;
uniform mat4 model;
uniform mat4 modelIT;
uniform vec3 camPos;

in vec3 positionMC;
in vec3 normalMC;
in vec4 vertexColor;

out vec4 positionWC;
out vec4 normalWC;
out vec4 color1;

void main(void) { 
   positionWC = model * vec4(positionMC, 1.0);
   gl_Position = viewProj * positionWC;
   normalWC = modelIT * vec4(normalMC, 0.0);
   color1 = vertexColor;
   positionWC.w = length(positionWC.xyz - camPos);
}