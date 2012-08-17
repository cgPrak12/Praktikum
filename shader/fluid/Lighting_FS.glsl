#version 150

in vec2 texCoords;

uniform mat4 view; 
uniform sampler2D depthTex; 
uniform sampler2D depthTexLQ;
uniform sampler2D depth2Tex;
uniform sampler2D depth3Tex;
uniform sampler2D normalTex;
uniform sampler2D normalTexLQ;
uniform sampler2D normal2Tex;
uniform sampler2D thicknessTex;
uniform sampler2D thicknessTexLQ;
uniform samplerCube cubeMap;
uniform sampler2D terrain;
uniform sampler2D skyTex;
uniform sampler2D skyBoxTex;
uniform sampler2D cutDepthTex;
uniform vec3 lightPosW;
uniform mat4 iView;

out vec3 color;

const float pi = 3.14159265358979;
const vec3 c_s = vec3(1.0, 1.0, 1.0);  // spekulare Farbe
//const vec3 c_a = vec3(1.0, 1.0, 1.0);  // ambiente Farbe
// material eigenschaften
const float k_a    = 0.05;
const float k_dif  = 0.6;
const float k_spec = 0.3;
const float es     = 16.0;



void main(void) {
	
// ***************************	
// Tiefentest

	vec4 terrainColor = texture(terrain,texCoords);
	vec3 skyColor = texture(skyBoxTex, texCoords).xyz * clamp((lightPosW.y+2.0)/10.0, 0.35, 1.0);
	vec3 background = (1-sign(terrainColor.w)) * skyColor + terrainColor.xyz;
	float cutDepth =texture(cutDepthTex,texCoords).w;

	if((terrainColor.w < cutDepth && terrainColor.w!=0) || cutDepth == 0) { 		
		color = background;
	}else{
	if(cutDepth > 0) {
	

// ***************************	
// Interpolation

	float depth = texture2D(depthTex, texCoords).w * 50;// + texture2D(depthTexLQ, texCoords).w * 0.5;
	
//	vec4 depthInt = texture2D(depthTex, texCoords);
	vec4 normalInt = texture2D(normalTex, texCoords);
//	vec4 thicknessInt = texture2D(thicknessTex, texCoords);

	vec4 depthInt = depth*texture2D(depthTex, texCoords)+ (1-depth)*texture2D(depthTexLQ, texCoords);
//	vec4 normalInt = depth*texture2D(normalTex, texCoords)+ (1-depth)*texture2D(normalTexLQ, texCoords);
	vec4 thicknessInt = depth*texture2D(thicknessTex, texCoords)+ (1-depth)*texture2D(thicknessTexLQ, texCoords);

	vec4 normalInt2 = depth*2 * texture(normal2Tex, texCoords) + (1-depth*2) * texture(normalTex, texCoords);
	
	vec3 position = depthInt.xyz;
	vec3 normal = normalize(normalInt.xyz);
	float thickness = clamp(thicknessInt.z, 0.0, 0.8);
	
// ***************************	
// Color due to absorption
	
	float thickness1 = thickness;//pow(thickness, 0.5);
	
	vec4 darkBlue  = vec4(0.2, 0.3, 0.8, 0.0);
	vec4 lightBlue = vec4(0.1, 0.7, 1.0, 0.0);
	
	vec4 color1 = (1.0-thickness1)*lightBlue + thickness1*darkBlue;
	vec3 c_d = vec3(color1.x, color1.y, color1.z);
	vec3 c_a = vec3(color1.x, color1.y, color1.z);
	
// ***************************	
// Phong-Lighting

	vec3 lightPos = (view * vec4(lightPosW, 1.0) ).xyz;		
	vec3 pos2eye = normalize(-position);	
	vec3 light2pos = normalize(position - lightPos);
	vec3 reflected = normalize(reflect(light2pos, normal));
	
	vec3 phongDiff = c_a * k_a
				   + c_d * k_dif * max(0.7, dot(-light2pos, normal)) * clamp((lightPosW.y+2.0)/10.0, 0.35, 1.0);
	vec3 phongSpec = c_s * k_spec * max(0, pow(dot(reflected, pos2eye), es));
	vec3 phong = phongDiff + phongSpec;

// ***************************			  
// CubeMap

	vec3 position2 = texture(depth2Tex, texCoords).xyz;
	vec3 normal2 = normalInt2.xyz;
	vec3 reflectPos = normalize(reflect(position, normal));
	vec3 reflectedW = normalize((iView * vec4(reflectPos, 0.0)).xyz);
	vec3 cubeColor = texture(cubeMap, reflectedW).xyz;
	

// ***************************
// SphereColor

	float atanYX;
	atanYX = atan(reflectedW.z / reflectedW.x);
	if(reflectedW.x < 0) {
		atanYX += sign(reflectedW.z) * pi;
		if(reflectedW.z == 0) atanYX += pi;
	}
	if(reflectedW.x == 0) atanYX = sign(reflectedW.z) * pi / 2.0;
	
	vec2 sphereCoords;
	sphereCoords.s = atanYX / (pi * 2.0);
	sphereCoords.t = acos(reflectedW.y / length(reflectedW)) / pi;

	vec3 sphereColor = texture(skyTex, sphereCoords).xyz * clamp((lightPosW.y+2.0)/10.0, 0.35, 1.0);

// ***************************
// Final Color

//	color = black*0.5*phongSpec + (1-thickness) * planeColor + thickness * phong + black*0.5 /*pow(thickness,0.02)*/ * mix(phongDiff,cubeColor,cubeColor);//pow(thickness,0.2));// * cubeColor;
//	color = (1-thickness) * planeColor + thickness * phong + pow(thickness, 0.4) * sphereColor;//1.0*mix(phongDiff,cubeColor,cubeColor);//pow(thickness,0.2));// * cubeColor;
	color = (1-thickness) * background + thickness * phong + pow(thickness,0.3) *0.6*sphereColor;
	
	}
	}
}