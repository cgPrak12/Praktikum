#version 150 core

uniform sampler2D elevation;
in vec3 color;
in vec3 positionWC;
out vec4 finalColor;


void main(void) {
	vec2 texCoords = vec2(positionWC.x,positionWC.z);
    finalColor = vec4(texture(elevation,texCoords).x,texture(elevation,texCoords).x,texture(elevation,texCoords).x,1);
   // finalColor = vec4(color, 1);
}