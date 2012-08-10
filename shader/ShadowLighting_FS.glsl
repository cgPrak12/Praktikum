#version 330

/**
 * @brief Calculate lightning with Blinn-Phong model and atmospheric lightning as ambient.
 *
 * @author vbruder
 * @author kseidel
 */

const vec4 upColor = vec4(1.0, 1.0, 0.95, 1.0);
const vec4 downColor = vec4(0.0, 0.0, 0.0, 1.0);

uniform sampler2D normalTex;
uniform sampler2D worldTex;
uniform sampler2D diffuseTex;
uniform sampler2D specularTex;
uniform sampler2D shadowTex;
// uniform sampler2D shadowCoordsTex;

uniform mat4 lView;
uniform mat4 lProj;

uniform vec3 camPos;
uniform vec3 sunDir;

in vec2 texCoord;

out vec4 enlightenedColor;

//lighting constants
const float k_a = 0.05; 
const float k_spec = 0.5; 
const float k_dif = 0.7; 
const float es = 5.0;
const float sunIntensity = 1.0;

/**
 * Calculate lightning with Blinn-Phong.
 * @param pos position of the point in world coords
 * @param normal normel of the point in world coords
 * @param c_d Diffuse color of the point
 * @param c_s specular color of the point
 * @param c_a ambient color
 * @return enlightened color of the point
 */
vec3 calcLighting(vec3 pos, vec3 normal, vec3 c_d, vec3 c_s, vec3 c_a)
{
	vec3 finalColor = vec3(0.0, 0.0, 0.0);
	vec3 view = normalize(camPos - pos);
	vec3 halfvec = normalize(-view + normalize(sunDir));
	
	//ambient
	finalColor += (k_a * c_a);
	
	//diffuse
	finalColor += sunIntensity * k_dif * c_d * max(dot(normalize(camPos - pos), normalize(normal)), 0.0);
	
	//specular
	if (c_s != 0.0)
	{
		finalColor += sunIntensity * k_spec * c_s * max(pow(dot(halfvec, normal), es), 0.0);
	}
	
    return (finalColor);
}


void main(void)
{
	vec3 normal = texture(normalTex, texCoord).xyz;	
		
	vec4 positionWC = texture(worldTex, texCoord);
		
	//ambient light
	float strength = 0.5 + 0.5 * dot(normal, sunDir);
	vec4 ambi = mix(downColor, upColor, strength);
		
	//diffuse and specular
	vec3 diff = texture(diffuseTex, texCoord).rgb;
	vec3 spec = texture(specularTex, texCoord).rgb;
		
	// *** shadow ***
	vec4 shadowCoord = lProj * lView * vec4(positionWC.xyz, 1);
	shadowCoord /= shadowCoord.w;

	float sum = 0; 
	int count = 0;

	for (int i = -2; i < 3; i++) 
	{
		for (int j = -2; j < 3; j++)
		{
			sum += textureOffset(shadowTex, 0.5 + 0.5*shadowCoord.xy, ivec2(i,j)).w;
			count++;
		}
	}
	float shadow = sum / count;
	
	float dist = distance(positionWC.xyz, sunDir);
		
	if(shadow < dist)
	{
		enlightenedColor = vec4(calcLighting(positionWC.xyz, normalize(normal), 0.3*diff, vec3(0), ambi.rgb), 1.0);
	}
	else
	{
		enlightenedColor = vec4(calcLighting(positionWC.xyz, normal, diff, spec, ambi.rgb), 1.0);
	}
}


