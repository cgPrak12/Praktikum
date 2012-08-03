#version 150

uniform mat4 sun_viewProj;
uniform mat4 sun_model;
uniform mat4 sun_modelIT;
uniform vec4 sun_Position;


in vec3 positionMC;
in vec3 normalMC;
in vec4 vertexColor;

in vec2 texCoords;

out vec4 positionWC;
out vec4 normalWC;
out vec4 color;
out vec2 sun_fragmentTexCoords;

void main(void) { 
   
   positionWC = sun_model * vec4(positionMC, 1.0);
   gl_Position = sun_viewProj * positionWC;
   normalWC  = sun_modelIT * vec4(normalMC, 0.0);
   color = vertexColor;
   sun_fragmentTexCoords = texCoords;
   
}