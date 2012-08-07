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
	
	vec3 position1 = normalize(texture(depthTex, texCoords).xyz);
	vec3 position = vec3(view * vec4(position1, 1.0));
	vec3 normal = normalize(texture(normalTex, texCoords).xyz);
//	vec3 normal = normalize(vec3(inverse(view) * normal1));
	
    vec3 pos2eye = normalize(-position);

    vec3 light2pos = normalize(position);
    vec3 reflected1 = reflect(position, normal);
	vec3 reflected = vec3(normalize(inverse(view) * vec4(reflected1, 0.0)));
	
	vec4 cubeColor = texture(cubeMap, reflected);// * 12.0 * pow(dot(reflected, pos2eye),1);
	
	vec4 planeColor = texture(plane, texCoords);
	
	float thickness = texture(thicknessTex, texCoords).z;
	
	vec4 waterColor =  0.2 * cubeColor + 0.7 * texture(lightingTex, texCoords);
	
	float rNull = 1.3333;//(1.0003-1.3333)/(1.0003+1.3333);
	float fresnel = rNull + ((1.0 - rNull) * pow(1.0 - dot(reflected, position), 5.0));  //normal, vec3(0.0, 0.0, 1.0) ),5.0));
	
	color = min(1.0, thickness+0.3) * waterColor + max(0.0, (1.0-thickness-0.3)) * planeColor;

//	color = vec4(vec3(fresnel), 0.0); // * cubeColor;

//	color = cubeColor;
}