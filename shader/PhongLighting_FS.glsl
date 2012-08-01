#version 330

/**
 * @brief Calculate lightning with Blinn-Phong and atmospheric lightning as ambient part.
 *
 * @author vbruder
 */

const vec4 upColor = vec4(1.0, 1.0, 0.9, 1.0);
const vec4 downColor = vec4(0.0, 0.3, 0.0, 1.0);

uniform sampler2D worldTex;
uniform sampler2D normalTex;
uniform sampler2D diffuseTex;

uniform vec3 camPos;

uniform vec3 sunDir;

in vec2 texCoord;

out vec4 enlightenedColor;

const float k_a = 0.5; // 0.05;
const float k_spec = 0.3; // 0.3;
const float k_dif = 0.5; // 0.06;
const float es = 16.0;
const float sunIntensity = 1.0;

//const vec3 sunDir = vec3(1.0, 0.0, 0.0);

/**F
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
	vec3 halfvec = normalize(-view + sunDir);
	
	//ambient
	finalColor += (k_a * c_a);
	
	//diffuse
	finalColor += sunIntensity * k_dif * c_d * max(dot(sunDir, normal), 0.0);
		
	//specular
	//finalColor += sunIntensity * k_spec * c_s * max(pow(dot(halfvec, normal), (es + 4)), 0.0);
	
    return (finalColor);
}


void main(void)
{
	vec3 normal = texture(normalTex, texCoord).xyz;	
	
	if(length(normal) < 0.1)
	{
		enlightenedColor = vec4(0.9,0.95,1.0,1);
	}
	else
	{
		// float strength = 0.5 + 0.5 * dot(normal, vec3(0.0, 1.0, 0.0));
		// vec4 enlightenedColor = mix(downColor, upColor, strength);
		
		normal = normalize(normal);
		vec3 positionWC = texture(worldTex, texCoord).xyz;
		
		//ambient light
		float strength = 0.5 + 0.5 * dot(normal, vec3(0,1,0));
		vec4 ambi = mix(downColor, upColor, strength);
		
		//diffuse and specular
		vec3 diff = texture(diffuseTex, texCoord).rgb;
		vec3 spec = vec3(0.8, 0.8, 0.8);	//texture(specularTex, texCoord).rgb;
		
		//blinn-phong calculations
		enlightenedColor = vec4(calcLighting(positionWC, normal, diff, spec, ambi.rgb), 1.0);
	
	}

}