#version 150

uniform mat4 viewProj;
uniform mat4 model;
uniform mat4 modelIT;
uniform mat4 shadowMatrix;

in vec3 positionMC;
in vec3 normalMC;
in vec4 vertexColor;

out vec4 positionWC;
out vec4 normalWC;
out vec4 color1;
out vec4 shadowCoordWC;

void main(void) { 
   positionWC = model * vec4(positionMC, 1.0);
   gl_Position = viewProj * positionWC;
   normalWC = modelIT * vec4(normalMC, 0.0);
   color1 = vertexColor;
   shadowCoordWC = shadowMatrix * vec4(positionMC, 1.0);
}