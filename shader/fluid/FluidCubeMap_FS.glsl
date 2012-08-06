#version 150 core

in vec2 texCoords;

uniform sampler2D normalTex;
uniform sampler2D depthTex;
uniform samplerCube cubeMap;
uniform sampler2D lightingTex;
uniform sampler2D plane;
uniform sampler2D thicknessTex;

out vec4 color;

void main(void) {
	
	vec3 position = texture(depthTex, texCoords).xyz;
	vec3 normal = normalize(texture(normalTex, texCoords).xyz);
	
    vec3 pos2eye = normalize(-position);

    vec3 light2pos = normalize(position);
    vec3 reflected = reflect(light2pos, normal);
	
	vec4 cubeColor = texture(cubeMap, reflected);// * 12.0 * pow(dot(reflected, pos2eye),1);
	
	vec4 planeColor = texture(plane, texCoords);
	
	float thickness = texture(thicknessTex, texCoords).z;
	
	vec4 waterColor =  0.2 * cubeColor + 0.7 * texture(lightingTex, texCoords);
	
	color = min(1.0, thickness+0.3) * waterColor + max(0.0, (1.0-thickness-0.3)) * planeColor;

}