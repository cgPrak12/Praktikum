#version 150 core

in vec2 texCoords;

uniform sampler2D lightingTex;
uniform sampler2D plane;
uniform sampler2D thicknessTex;

out vec4 color;

void main(void) {
		
//	float inciMedia = 1.00029;
//	float transMedia = 1.333;

//	float theta_t = dot(positionC, reflectedC);
//	float theta_i = dot(reflectedC, positionC);
	
//	float parallelLight = ( transMedia * theta_i - inciMedia * theta_t )
//					    / ( transMedia * theta_i + inciMedia * theta_t );
				  
//	float perpendLight = ( inciMedia * theta_i - transMedia * theta_t )
//					   / ( inciMedia * theta_i + transMedia * theta_t );
					   
//	float unpolarizedLight = 1/2 * ( (parallelLight*parallelLight) + (perpendLight*perpendLight));

//	color = vec4(unpolarizedLight);


	float thickness = texture(thicknessTex, texCoords).z;

	vec4 waterColor = texture(lightingTex, texCoords);
	
	vec4 planeColor = texture(plane, texCoords);

	color = min(1.0, thickness+0.3) * waterColor + max(0.0, (1.0-thickness-0.3)) * planeColor;


}