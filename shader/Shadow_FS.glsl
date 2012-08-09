#version 330

/**
 * @brief Calculate lightning with Blinn-Phong model and atmospheric lightning as ambient.
 *
 * @author vbruder
 * @author kseidel
 */

uniform sampler2D worldTex;
uniform sampler2D shadowCoordsTex;
uniform sampler2D shadowTex;

uniform vec3 sunDir;

in vec2 texCoord;

out vec4 enlightenedColor;


void main(void)
{
	vec4 positionWC = texture(worldTex, texCoord);
		
		
	//shadow
	vec4 shadowCoord = texture(shadowCoordsTex, texCoord);
	float shadow = texture(shadowTex, shadowCoord.xy / shadowCoord.w).w;
		
	float dist = distance(10.0 * sunDir, positionWC.xyz);
		
	if(shadow < dist)
	{
		enlightenedColor = vec4(0,0,0,0);
	}
	else
	{
		enlightenedColor = vec4(1,1,1,1);

	}
}

