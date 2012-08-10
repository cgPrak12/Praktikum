#version 150

in vec2 texCoords;

uniform mat4 view; 
uniform sampler2D depthTex;
uniform sampler2D normalTex;
uniform sampler2D thicknessTex;
uniform samplerCube cubeMap;
uniform vec3 lightPosW;

out vec3 color;


const vec3 c_s = vec3(1.0, 1.0, 1.0);  // spekulare Farbe
const vec3 c_a = vec3(1.0, 1.0, 1.0);  // ambiente Farbe
// material eigenschaften
const float k_a    = 0.05;
const float k_dif  = 0.6;
const float k_spec = 0.3;
const float es     = 16.0;


void main(void) {
	
	if(texture(thicknessTex, texCoords).z == 0) discard;
	
	vec3 position = texture(depthTex, texCoords).xyz;
	vec3 normal = normalize(texture(normalTex, texCoords).xyz);
	float thickness = texture(thicknessTex, texCoords).z;
	
// ***************************	
// Color due to absorption
	
	float thickness1 = pow(thickness, 0.5);
	
	vec4 darkBlue  = vec4(0.0, 0.1, 0.5, 0.0);
	vec4 lightBlue = vec4(0.0, 0.7, 1.0, 0.0);
	
	vec4 color1 = (1.0-thickness1)*lightBlue + thickness1*darkBlue;
	vec3 c_d = vec3(0.0, max(0.1, color1.y), max(0.5, color1.z));
	
// ***************************	
// Phong-Lighting

	vec3 lightPos = (view * vec4(lightPosW, 1.0) ).xyz;		
	vec3 pos2eye = normalize(-position);	
	vec3 light2pos = normalize(position - lightPos);
	vec3 reflected = normalize(reflect(light2pos, normal));
	
	vec3 phong = c_a * k_a
			   + c_d * k_dif * max(0, dot(-light2pos, normal))
			   + c_s * k_spec * max(0, pow(dot(reflected, pos2eye), es));

// ***************************			  
// CubeMap

	vec3 reflectPos = normalize(reflect(position, normal));
	vec3 reflectedW = normalize((inverse(view) * vec4(reflectPos, 0.0)).xyz);
	vec3 cubeColor = texture(cubeMap, reflectedW).xyz;
	
	color = cubeColor;//normalize(normal);
	return;

// ***************************			  
// Fresnel

	float rNull = (1.0003-1.3333)/(1.0003+1.3333);//0.5 * ((1.0003-1.3333)/(1.0003+1.3333) + (1.3333-1.0003)/(1.3333+1.0003));
	float fresnel = rNull + ((1.0 - rNull) * pow(1.0 - dot(pos2eye, normal), 2.0));  //normal, vec3(0.0, 0.0, 1.0) ),5.0));


//	color = vec3(fresnel);
	color = cubeColor;
//	color = vec3(1 - dot(pos2eye, normal));
//	color = phong + fresnel * cubeColor;

//	color = phong;

	vec3 waterColor =  0.3 * fresnel * cubeColor + 0.7 * phong;
	
//	color = fresnel * cubeColor;




}