#version 150

in vec4 positionWC;
in vec4 normalWC;
in vec4 color1;

out vec4 position;
out vec4 normal;
out vec4 color;

void main(void) {
   position = positionWC;
   normal = normalWC;
   color = color1;
}