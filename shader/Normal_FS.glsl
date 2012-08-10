#version 150 core

uniform sampler2D normalTexture;
uniform sampler2D diffuseTexture;
uniform sampler2D specularTexture;
uniform sampler2D skyTexture;
uniform sampler2D bumpTexture;
uniform vec3 lightPosition1;
uniform vec3 eyePosition;

const float k_d = 2000;
const vec3 maxIntensity  = vec3(1.0, 1.0, 0.9);
const float k_a = 0.3;
const float k_s = 0.01;
const float es = 8.0;

in vec2 texCoord;
in vec3 positionWC;

out vec4 finalColor;



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
	vec3 normal = texture(normalTexture, texCoord).rgb;
	
	vec3 c_d =  texture(diffuseTexture,  texCoord).rgb;
	vec3 c_a =  c_d;
	vec3 c_s =  texture(specularTexture, texCoord).rgb;
	
	vec3 position = positionWC + 0.9 * texture(bumpTexture, texCoord).r * normal;
	
	vec3 color  = c_a*k_a; 
		 color += getDiffuse (position, normal, c_d, maxIntensity, lightPosition1);
		 color += getSpecular(position, normal, c_s, maxIntensity, lightPosition1, eyePosition, es);
		 
	float skyDraw = sign(texture(skyTexture, texCoord).x);
	
	finalColor =  vec4(skyDraw*color,0.0);
	finalColor +=  vec4(( (1-skyDraw) * c_d) ,0.0 ) ;
	
}