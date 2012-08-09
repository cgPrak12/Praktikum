#version 150 core

in vec2 texCoords;

uniform sampler2D normalTex;
uniform sampler2D depthTex;
uniform samplerCube cubeMap;
uniform sampler2D lightingTex;
uniform sampler2D plane;
uniform sampler2D thicknessTex;
uniform mat4 view;

out vec4 color;

void main(void) {
		
	float inciMedia = 1.00029;
	float transMedia = 1.333;

	float theta_t = dot(positionC, reflectedC);
	float theta_i = dot(reflectedC, positionC);
	
	float parallelLight = ( transMedia * theta_i - inciMedia * theta_t )
					    / ( transMedia * theta_i + inciMedia * theta_t );
				  
	float perpendLight = ( inciMedia * theta_i - transMedia * theta_t )
					   / ( inciMedia * theta_i + transMedia * theta_t );
					   
	float unpolarizedLight = 1/2 * ( (parallelLight*parallelLight) + (perpendLight*perpendLight));

	color = vec4(unpolarizedLight);





}