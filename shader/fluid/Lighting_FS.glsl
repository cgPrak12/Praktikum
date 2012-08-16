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
uniform sampler2D thicknessTexNB;
uniform sampler2D thicknessTex;
uniform sampler2D thicknessTexLQ;
uniform samplerCube cubeMap;
uniform sampler2D plane;
uniform sampler2D skyTex;
uniform sampler2D skyBoxTex;
uniform vec3 lightPosW;
uniform mat4 iView;
uniform vec3 eye;
uniform sampler2D finalTex;

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
// Interpolation

	vec4 planeColor = texture(plane,texCoords);
	vec3 background = (1-sign(planeColor.w)) * texture(skyBoxTex, texCoords).xyz + planeColor.xyz;

	if((texture(plane,texCoords).w < texture(finalTex,texCoords).w && texture(plane,texCoords).w!=0) || (texture(finalTex,texCoords).w==0)) { 		
		color = background;
	}else{if(texture(finalTex,texCoords).w > 0) {
	


	float depth = texture2D(depthTex, texCoords).w * 10;// + texture2D(depthTexLQ, texCoords).w * 0.5;
	
//	vec4 depthInt = texture2D(depthTex, texCoords);
	vec4 normalInt = texture2D(normalTex, texCoords);
//	vec4 thicknessInt = texture2D(thicknessTex, texCoords);

	vec4 depthInt = depth*texture2D(depthTex, texCoords)+ (1-depth)*texture2D(depthTexLQ, texCoords);
//	vec4 normalInt = depth*texture2D(normalTex, texCoords)+ (1-depth)*texture2D(normalTexLQ, texCoords);
	vec4 thicknessInt = depth*texture2D(thicknessTex, texCoords)+ (1-depth)*texture2D(thicknessTexLQ, texCoords);

	vec4 normalInt2 = depth*2 * texture(normal2Tex, texCoords) + (1-depth*2) * texture(normalTex, texCoords);

	float black = 1;
//	if(texture(normalTex, texCoords).z == 0) black = 0;
//	black = sign(texture(thicknessTexNB, texCoords).z);
	
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
				   + c_d * k_dif * max(0.7, dot(-light2pos, normal));
	vec3 phongSpec = c_s * k_spec * max(0, pow(dot(reflected, pos2eye), es));
	vec3 phong = phongDiff + phongSpec;

// ***************************			  
// CubeMap

	vec3 position2 = texture(depth2Tex, texCoords).xyz;
	vec3 normal2 = normalInt2.xyz;//texture(normal2Tex, texCoords).xyz;
	vec3 reflectPos = normalize(reflect(position, normal));
	vec3 reflectedW = normalize((iView * vec4(reflectPos, 0.0)).xyz);
	vec3 cubeColor = texture(cubeMap, reflectedW).xyz;
	

// ***************************			  
// Fresnel

	float rNull = (1.0003-1.3333)/(1.0003+1.3333);//0.5 * ((1.0003-1.3333)/(1.0003+1.3333) + (1.3333-1.0003)/(1.3333+1.0003));
	float fresnel = rNull + ((1.0 - rNull) * pow(1.0 - dot(pos2eye, normal), 2.0));  //normal, vec3(0.0, 0.0, 1.0) ),5.0));
	
	

// ***************************
// SphereColor

//	vec3 normalW = normalize(vec3(iView * vec4(normal,0)));
//	vec3 positionW = normalize(vec3(iView * vec4(position,0))-eye);
//	vec3 reflectedW = normalize(reflect(positionW, normalW));

	float atanYX;
	atanYX = atan(reflectedW.z / reflectedW.x);
	if(reflectedW.x < 0) {
		atanYX += sign(reflectedW.z) * pi;
		if(reflectedW.z == 0) atanYX += pi;
	}
	if(reflectedW.x == 0) atanYX = sign(reflectedW.z) * pi / 2.0;
	
	vec2 sphereCoords;
	sphereCoords.x = atanYX / (pi * 2.0);
	sphereCoords.y = acos(reflectedW.y / length(reflectedW)) / pi;

	vec3 sphereColor = black*texture(skyTex, sphereCoords).xyz;

	/*
	float phi = acos( dot(reflectedW.xz, vec2(0,1)) / length(reflectedW.xz) );
	if(reflectedW.x < 0.0) phi = 180.0 - phi;
	if(reflectedW.x == 0.0) {
		if(sign(reflectedW.z) > 0.0) phi = pi;
		if(sign(reflectedW.z) < 0.0) phi = 0.0;
	}
	float theta = 180 - acos( dot(reflectedW.xyz, vec3(0,1,0)) );
	
	vec2 sphereCoords;
	sphereCoords.s = phi / (2.0*pi);
	sphereCoords.t = theta / pi;
	
	vec3 sphereColor = black*texture(skyTex, sphereCoords).xyz;
*/
//	color = reflectedW;//vec3(sphereCoords, 0);
//	color = sphereColor;




//	color = vec3(fresnel);
//	color = reflectedW;
	//color = texture(cubeMap, vec3(texCoords, 0)).xyz;
	
	vec3 pos2eye2 = normalize(-position2);
	fresnel = (1 - dot(-pos2eye2, -normal2));// * cubeColor;
//	color = phong + fresnel * cubeColor;

//	color = phong;
//	color = vec3(depth);//normal;
//	vec3 waterColor =  0.5*(1.0 - max(0, dot(pos2eye, normal)))  +  phong;
//	color = waterColor;
//	cubeColor = fresnel * cubeColor;
	mat4 iv = inverse(view);
	vec4 wPos = vec4(-position, 1);
	vec4 wN = vec4(normal, 0);
	
//	color = vec3(thickness) * phong + 0.3*cubeColor;//waterColor;

	vec3 planeColor = texture(plane, texCoords).xyz;
//	color = vec3(texture(plane, texCoords).w) + vec3(depth);
//	color = vec3(thickness);
//	color = cubeColor;//clamp(thickness*10,0.0,1.0) * cubeColor +phongDiff;

// 0 * black * 0.2*(1.0 - max(0, dot(pos2eye, normal))) + 

//	color = (1-thickness) * planeColor + pow(thickness,0.25) * black * phongDiff + phongSpec +	
//	clamp(thickness*1000,0.0,1.0)*0.5 * cubeColor;//mix(phong, cubeColor, cubeColor);
//	thickness * 0.5 * black * vec3(pow(cubeColor.x,2), pow(cubeColor.y,2), pow(cubeColor.z,2)); 

//	color = black*0.5*phongSpec + (1-thickness) * planeColor + thickness * phong + black*0.5 /*pow(thickness,0.02)*/ * mix(phongDiff,cubeColor,cubeColor);//pow(thickness,0.2));// * cubeColor;
//	color = (1-thickness) * planeColor + black*thickness * phong + black*pow(thickness, 0.4) * sphereColor;//1.0*mix(phongDiff,cubeColor,cubeColor);//pow(thickness,0.2));// * cubeColor;
	color = (1-thickness) * background + thickness * phong + pow(thickness,0.3) *0.6*sphereColor;
//	color = phongSpec;
//	color = vec3(depth);
	}}
}