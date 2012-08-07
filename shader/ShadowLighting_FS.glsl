#version 330

/**
 * @brief Calculate lightning with Blinn-Phong model and atmospheric lightning as ambient.
 *
 * @author vbruder
 * @author kseidel
 */

const vec4 upColor = vec4(1.0, 1.0, 0.9, 1.0);
const vec4 downColor = vec4(0.0, 0.0, 0.0, 1.0);

uniform sampler2D normalTex;
uniform sampler2D worldTex;
uniform sampler2D diffuseTex;
uniform sampler2D shadowCoordsTex;
uniform sampler2D shadowTex;

uniform vec3 camPos;
uniform vec3 sunDir;

in vec2 texCoord;

out vec4 enlightenedColor;

const float k_a = 0.5; // 0.05;
const float k_spec = 0.3; // 0.3;
const float k_dif = 0.5; // 0.06;
const float es = 16.0;
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
		vec4 positionWC = texture(worldTex, texCoord);
		
		//ambient light
		float strength = 0.5 + 0.5 * dot(normal, vec3(0,1,0));
		vec4 ambi = mix(downColor, upColor, strength);
		
		//diffuse and specular
		vec3 diff = texture(diffuseTex, texCoord).rgb;
		vec3 spec = vec3(0.8, 0.8, 0.8);	//texture(specularTex, texCoord).rgb;
		
		//shadow
		vec4 shadowCoord = texture(shadowCoordsTex, texCoord);
		float shadow = texture(shadowTex, shadowCoord.xy / shadowCoord.w).w;
		
		float dist = distance(10.0 * sunDir, positionWC.xyz);
		if(shadow < dist)
		{
			enlightenedColor = vec4(0);
		}
		else
		{
			enlightenedColor = vec4(calcLighting(positionWC.xyz, normal, diff, spec, ambi.rgb), 1.0);
		}
		
		//blinn-phong calculations
		
	
		// vec4 s = texture(shadowCoordsTex, texCoord);
		// s /= s.w;
		// s.z = 0;
		// enlightenedColor = texture(shadowTex, s.xy);
	}
}


