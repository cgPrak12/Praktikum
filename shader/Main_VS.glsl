#version 150

uniform mat4 viewProj;
uniform mat4 model;
uniform mat4 modelIT;
uniform mat4 view;
uniform vec3 camPos;
uniform float camFar;


in vec3 positionMC;
in vec3 normalMC;
in vec4 vertexColor;
in vec3 tangentMC;
in vec2 texCoords;

out vec4 positionWC;
out vec4 normalWC;
out vec4 color1;
out vec4 tangentWC;
out vec2 fragmentTexCoords;
out float depth;

void main(void) { 
   positionWC = model * vec4(positionMC, 1.0);
   depth = (view*positionWC).z/camFar * (-1); 
   gl_Position = viewProj * positionWC;
   normalWC  = modelIT * vec4(normalMC, 0.0);
   color1 = vertexColor;
   fragmentTexCoords = texCoords;
   positionWC.w = length(positionWC.xyz - camPos);
   tangentWC = modelIT * vec4(tangentMC, 0);
}