#version 150 core

const vec3 c_a = vec3(1.0, 1.0, 1.0);

const vec3 c_s = vec3(1.0, 1.0, 1.0);
const float k_a = 0.05;
const float k_d = 0.6;
const float k_s = 0.3;
const float es = 32.0;

uniform vec3 eyePosition;
uniform sampler2D diffuseTex;
uniform sampler2D bumpTex;
uniform sampler2D normalTex;
uniform vec3 plPosition1;
uniform vec3 plPosition2;

const vec3 maxIntensity1 = vec3(0.5, 1.0, 1.0);
const vec3 maxIntensity2 = vec3(1.0, 1.0, 0.5);

in vec3 positionWC;
in vec3 normalWC;
in vec3 tangentWC;
in vec2 fragmentTexCoords;

out vec4 finalColor;

vec3 getDiffuse(vec3 pos, vec3 normal, vec3 c_d, vec3 maxIntensity, vec3 lightPos)
{
	vec3 light2pos = pos - lightPos;
	vec3 intensity = maxIntensity / (dot(light2pos, light2pos));
	
	return intensity * k_d * c_d * max(dot(-normalize(light2pos), normal), 0);
}

vec3 getSpecular(vec3 pos, vec3 normal, vec3 c_s, vec3 maxIntensity, vec3 lightPos)
{
	vec3 light2pos = pos - lightPos;
	vec3 pos2eye = normalize(eyePosition - pos);
	vec3 reflected = reflect(normalize(light2pos), normal);
	vec3 intensity = maxIntensity / (dot(light2pos, light2pos));
	
	return intensity * k_s * c_s * max(pow(dot(pos2eye, reflected), es), 0);
}

void main(void)
{
	vec3 normal = normalize(normalWC);
	vec3 tangent = normalize(tangentWC);
	vec3 binormal = cross(normal, tangent);
	
	vec3 sampledNormal = 2.0 * texture(normalTex, fragmentTexCoords).rgb - vec3(1.0);
	
	normal = sampledNormal.x * tangent + sampledNormal.y * binormal + sampledNormal.z * normal;
	

	float height = texture(bumpTex, fragmentTexCoords).r;
	vec3 c_d = texture(diffuseTex, fragmentTexCoords).rgb;
	
	vec3 pos = positionWC + 0.2 * height * normalWC;
	
	vec3 color = k_a * c_a;
	color += getDiffuse(pos, normal, c_d, maxIntensity1, plPosition1);
	color += getDiffuse(pos, normal, c_d, maxIntensity2, plPosition2);
	color += getSpecular(pos, normal, c_s, maxIntensity1, plPosition1);
	color += getSpecular(pos, normal, c_s, maxIntensity2, plPosition2);
	
	finalColor = vec4(color, 1.0);
}