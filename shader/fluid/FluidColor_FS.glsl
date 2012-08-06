#version 150 core

in vec2 texCoords;

uniform sampler2D thicknessTex;

out vec4 color;


void main(void) {

	if(texture(thicknessTex,texCoords).z == 0) discard;
	
	float thickness = pow(texture(thicknessTex,texCoords).z,0.5);
	
	vec4 darkBlue = vec4(0.0, 0.1, 0.5, 0.0);
	vec4 lightBlue = vec4(0.0, 0.7, 1.0, 0.0);
	
	vec4 color1 = (1.0-thickness)*lightBlue + thickness*darkBlue;
	color = vec4(0.0, max(0.1, color1.y), max(0.5, color1.z), 0.0);

}