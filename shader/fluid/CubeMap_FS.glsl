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
	
	if(texture(depthTex,texCoords).w == 0 && texture(plane, texCoords).x == 0) discard;
	
	vec3 position1 = texture(depthTex, texCoords).xyz;
	vec3 positionW = normalize(position1);
	vec3 positionC = normalize(vec3( view * vec4(position1, 1.0)));
	
	vec4 normal1 = texture(normalTex, texCoords);
	vec3 normalW = normalize(vec3(inverse(view) * normal1));
	vec3 normalC = normalize(texture(normalTex, texCoords).xyz);
	
	vec3 camPosW = (inverse(view) * vec4(0.0)).xyz;
	
    vec3 pos2eye = normalize(-positionC);

    vec3 light2pos = normalize(positionC);
    
    vec3 reflectedC = normalize(reflect(positionC, normalC).xyz);
    vec3 reflectedW = normalize(reflect(positionW, normalW).xyz);
    vec3 reflected1 = reflect(positionC, normalC).xyz;
	vec3 reflectedW1 = vec3(normalize(inverse(view) * vec4(reflected1, 0.0)));
	
	vec4 cubeColor = texture(cubeMap, reflectedW1);// * 12.0 * pow(dot(reflected, pos2eye),1);
	
	vec4 planeColor = texture(plane, texCoords);
	
	float thickness = texture(thicknessTex, texCoords).z;
	

	
	float rNull = 1.333; //0.5 * ((1.0003-1.3333)/(1.0003+1.3333) + (1.3333-1.0003)/(1.3333+1.0003));
	float fres = dot(-positionC, normalC) / (length(-positionC) * length(normalC));
	float fresnel = rNull + ((1.0 - rNull) * pow(1.0 - 0.5*dot(positionC, reflectedC), 5.0));  //normal, vec3(0.0, 0.0, 1.0) ),5.0));
	

	
	float inciMedia = 1.00029;
	float transMedia = 1.333;
	
//	float theta_t = dot(positionW - camPos, normalW);
//	float theta_i = dot(positionW - camPos, normalW);

	float theta_t = dot(positionC, reflectedC);
	float theta_i = dot(reflectedC, positionC);
	
	float parallelLight = ( transMedia * theta_i - inciMedia * theta_t )
					    / ( transMedia * theta_i + inciMedia * theta_t );
				  
	float perpendLight = ( inciMedia * theta_i - transMedia * theta_t )
					   / ( inciMedia * theta_i + transMedia * theta_t );
					   
	float unpolarizedLight = 1/2 * ( (parallelLight*parallelLight) + (perpendLight*perpendLight));

//	color = vec4(0.5 + dot(positionC, normalC));
//	color = vec4(unpolarizedLight);

//	color = vec4(normalW, 0);

	vec4 color1 = vec4(vec3(fresnel), 0.0) * cubeColor;

//	color = cubeColor;

	vec4 waterColor =  (0.2 * fresnel * cubeColor) + 0.7 * texture(lightingTex, texCoords);

	color = min(1.0, thickness+0.3) * waterColor + max(0.0, (1.0-thickness-0.3)) * planeColor;

}