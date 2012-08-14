#version 150

uniform vec3 camPos;

in vec4 positionWC;
in vec4 normalWC;
in vec4 color1;
in vec4 shadowCoordWC;

out vec4 position;
out vec4 normal;
out vec4 color;
out vec4 shadowCoord;

void main(void) {
   position.xyz = positionWC.xyz / positionWC.w;
   position.w = distance(position.xyz, camPos);
   normal = normalWC;
   color = color1;
   shadowCoord = shadowCoordWC;
}