#version 150

in vec2 texCoords;

uniform mat4 view; 
uniform sampler2D depthTex; 
uniform sampler2D depthTexLQ;
uniform sampler2D depth2Tex;
uniform sampler2D normalTex;
uniform sampler2D normalTexLQ;
uniform sampler2D normal2Tex;
uniform sampler2D thicknessTexNB;
uniform sampler2D thicknessTex;
uniform sampler2D thicknessTexLQ;
uniform samplerCube cubeMap;
uniform sampler2D plane;
uniform vec3 lightPosW;
uniform mat4 iView;
uniform vec3 eye;

out vec3 color;


const vec3 c_s = vec3(1.0, 1.0, 1.0);  // spekulare Farbe
//const vec3 c_a = vec3(1.0, 1.0, 1.0);  // ambiente Farbe
// material eigenschaften
const float k_a    = 0.05;
const float k_dif  = 0.6;
const float k_spec = 0.3;
const float es     = 16.0;


void main(void) {
	
// ***************************	
// Interpolation

	float depth = texture2D(depthTex, texCoords).w + texture2D(depthTexLQ, texCoords).w * 0.5;
	vec4 depthInt = /*texture2D(depthTex, texCoords);/*/depth*texture2D(depthTex, texCoords)+ (1-depth)*texture2D(depthTexLQ, texCoords);
	vec4 normalInt = /*texture2D(normalTex, texCoords);/*/depth*texture2D(normalTex, texCoords)+ (1-depth)*texture2D(normalTexLQ, texCoords);
	vec4 thicknessInt = /*texture2D(thicknessTex, texCoords);/*/depth*texture2D(thicknessTex, texCoords)+ (1-depth)*texture2D(thicknessTexLQ, texCoords);

	float black = 1;
//	if(texture(thicknessTexNB, texCoords).z == 0) black = 0;
	
	vec3 position = depthInt.xyz;
	vec3 normal = normalize(normalInt.xyz);
	float thickness = clamp(thicknessInt.z, 0.0, 1.0);
	
// ***************************	
// Color due to absorption
	
	float thickness1 = thickness;//pow(thickness, 0.5);
	
	vec4 darkBlue  = vec4(0.2, 0.3, 0.8, 0.0);
	vec4 lightBlue = vec4(0.1, 0.7, 1.0, 0.0);
//	vec4 darkBlue  = vec4(0.3, 0.5, 0.8, 0.0);
//	vec4 lightBlue = vec4(0.4, 0.8, 0.9, 0.0);
	
	vec4 color1 = (1.0-thickness1)*lightBlue + thickness1*darkBlue;
	vec3 c_d = vec3(color1.x, color1.y, color1.z);
	vec3 c_a = vec3(color1.x, color1.y, color1.z);
	
// ***************************	
// Phong-Lighting

	vec3 lightPos = (view * vec4(lightPosW, 1.0) ).xyz;		
	vec3 pos2eye = normalize(-position);	
	vec3 light2pos = normalize(position - lightPos);
	vec3 reflected = normalize(reflect(light2pos, normal));
	
	vec3 phong = c_a * k_a
			   + c_d * k_dif * max(0.7, dot(-light2pos, normal))
			   + c_s * k_spec * max(0, pow(dot(reflected, pos2eye), es));

// ***************************			  
// CubeMap

	vec3 position2 = texture(depth2Tex, texCoords).xyz;
	vec3 normal2 = texture(normal2Tex, texCoords).xyz;
	vec3 reflectPos = normalize(reflect(position, normal));
	vec3 reflectedW = normalize((iView * vec4(reflectPos, 0.0)).xyz);
	vec3 cubeColor = texture(cubeMap, reflectedW).xyz;
	

// ***************************			  
// Fresnel

	float rNull = (1.0003-1.3333)/(1.0003+1.3333);//0.5 * ((1.0003-1.3333)/(1.0003+1.3333) + (1.3333-1.0003)/(1.3333+1.0003));
	float fresnel = rNull + ((1.0 - rNull) * pow(1.0 - dot(pos2eye, normal), 2.0));  //normal, vec3(0.0, 0.0, 1.0) ),5.0));


//	color = vec3(fresnel);
//	color = cubeColor;
//	color = vec3(1 - dot(pos2eye, normal)) * cubeColor;
//	color = phong + fresnel * cubeColor;

//	color = phong;
//	color = normal;
	vec3 waterColor =  0.5*(1.0 - max(0, dot(pos2eye, normal)))  +  phong;
//	color = waterColor;
//	color = fresnel * cubeColor;
	mat4 iv = inverse(view);
	vec4 wPos = vec4(-position, 1);
	vec4 wN = vec4(normal, 0);
	
//	color = vec3(thickness) * phong + 0.3*cubeColor;//waterColor;

	vec3 planeColor = texture(plane, texCoords).xyz;

	color = 0 * black * 0.2*(1.0 - max(0, dot(pos2eye, normal))) + (1-thickness) * planeColor + thickness * black * phong + thickness * mix(phong, cubeColor, cubeColor);//thickness * 0.5 * black * vec3(pow(cubeColor.x,2), pow(cubeColor.y,2), pow(cubeColor.z,2)); 

}