#version 150 core

uniform sampler2D normalTexture;
uniform sampler2D noiseTexture;
uniform sampler2D diffuseTexture;

//nur zu testzwecken !!!!
	uniform float printNormal;
//

const float totStrength = 1;
const float strength = 0.3;
const float offset = 18.0;
const float falloff = 0.000002;
const float rad = 0.006;

const float SAMPLES = 10;
const float invSamples = 1.0/10.0;
in vec2 texCoord;
in vec3 positionWC;

out vec4 finalColor;


void main(void)
{
	const vec3 pSphere[10] = vec3[](vec3(-0.010735935, 0.01647018, 0.0062425877),
									vec3(-0.06533369, 0.3647007, -0.13746321),
									vec3(-0.6539235, -0.016726388, -0.53000957),
									vec3(0.40958285, 0.0052428036, -0.5591124),
									vec3(-0.1465366, 0.09899267, 0.15571679),
									vec3(-0.44122112, -0.5458797, 0.04912532),
									vec3(0.03755566, -0.10961345, -0.33040273),
									vec3(0.019100213, 0.29652783, 0.066237666),
									vec3(0.8765323, 0.011236004, 0.28265962),
									vec3(0.29264435, -0.40794238, 0.15964167));

	// grab a normal for reflecting the sample rays later on
	vec3 fres = normalize((texture(noiseTexture,texCoord*offset).xyz*2.0)	- vec3(1.0));
	vec4 currentPixelSample = texture(normalTexture,texCoord);
	float currentPixelDepth = currentPixelSample.a;
	//don't compare to background
	if (currentPixelDepth > 0.7)
		{
			finalColor = texture(diffuseTexture, texCoord);
			return;
		}
    vec3 ep = vec3(texCoord, currentPixelDepth);
    // get the normal of current fragment
    vec3 norm = currentPixelSample.xyz;
 
    float bl = 0.0;
    // adjust for the depth ( not shure if this is good..)
    float radD = rad/currentPixelDepth;
 
    vec3 ray, se, occNorm;
    float occluderDepth, depthDifference, normDiff;
 
    for(int i=0; i < SAMPLES;i++)
    {
		// get a vector (randomized inside of a sphere with radius 1.0) from a texture and reflect it
		ray = radD*reflect(pSphere[i],fres);
 
		// if the ray is outside the hemisphere then change direction
		se = ep + sign(dot(ray,norm) )*ray;
 
		// get the depth of the occluder fragment
		vec4 occluderFragment = texture(normalTexture,se.xy);
 
		// get the normal of the occluder fragment
		occNorm = occluderFragment.xyz;
 
		// if depthDifference is negative = occluder is behind current fragment
		depthDifference = currentPixelDepth-occluderFragment.a;
 
		// calculate the difference between the normals as a weight
 
		normDiff = (1.0-dot(occNorm,norm));
		// the falloff equation, starts at falloff and is kind of 1/x^2 falling
		bl += normDiff*(1.0-smoothstep(falloff,strength,depthDifference));
		
	}
 
    // output the result
	float ao = totStrength*bl*invSamples;
	finalColor = vec4(ao);
	if (printNormal ==1.0)
	{
		finalColor = ao * texture(diffuseTexture,texCoord);
	}

}
