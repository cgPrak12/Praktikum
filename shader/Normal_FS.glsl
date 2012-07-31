#version 150 core

uniform sampler2D textureImage;
uniform sampler2D normalTexture;
uniform sampler2D diffuseTexture;
uniform sampler2D specularTexture;
uniform vec3 lightPosition1;
uniform vec3 eyePosition;

in vec2 fragmentTexCoords;
in vec4 tangentWC;
in vec4 normalWC;
in vec3 positionWC;

out vec4 finalColor;

const float k_d = 5;
const vec3 maxIntensity  = vec3(1.0, 1.0, 0.9);
const float k_a = 0.2;
const float k_s = 0.001;
const float es = 10.0;

vec3 getDiffuse(vec3 pos, vec3 normal, vec3 c_d, vec3 maxIntensity, vec3 lightPos)
{
	vec3 light2pos = pos - lightPos;
	vec3 intensity = maxIntensity / (dot(light2pos, light2pos));
	return intensity * k_d * c_d * max(dot(-normalize(light2pos), normal), 0);
}

vec3 getSpecular(vec3 pos, vec3 normal, vec3 c_s, vec3 maxIntensity, vec3 lightPos, vec3 eye, float reduction)
{
	vec3 light2pos = pos - lightPos;
	vec3 pos2eye   = normalize(eye - pos);
	vec3 reflected = reflect(normalize(light2pos), normal);
	vec3 intensity = maxIntensity / (dot(light2pos, light2pos));
	return intensity * k_s * c_s * max(pow(dot(pos2eye, reflected), reduction), 0);
}

void main(void)
{
	vec3 normal    = vec3( normalize(normalWC));
	vec3 tangent   = vec3( normalize(tangentWC));
	vec3 binormal  = cross(tangent,normal);
	
	vec3 mapNormal = texture(normalTexture, fragmentTexCoords).rgb;
	normal = mapNormal.z*normal + mapNormal.y * binormal + mapNormal.x* tangent;
	
	vec3 c_d =  texture(diffuseTexture,  fragmentTexCoords).rgb;
	vec3 c_a =  texture(textureImage,    fragmentTexCoords).rgb;
	vec3 c_s =  texture(specularTexture, fragmentTexCoords).rgb;
	
	vec3 color  = c_a*k_a; 
	color      += getDiffuse (positionWC, normal, c_d, maxIntensity, lightPosition1);
	color      += getSpecular(positionWC, normal, c_s, maxIntensity, lightPosition1, eyePosition, es);
	finalColor = vec4(color, 1);
}