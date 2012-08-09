#version 150
const float factors[91] = float[]( 
	/*07 samples index 00*/ 0.015625000, 0.093750000, 0.234375000, 0.312500000, 0.234375000, 0.093750000, 0.015625000,
	/*09 samples index 07*/ 0.003906250, 0.031250000, 0.109375000, 0.218750000, 0.273437500, 0.218750000, 0.109375000, 0.031250000, 0.003906250,
	/*11 samples index 16*/ 0.000976563, 0.009765625, 0.043945313, 0.117187500, 0.205078125, 0.246093750, 0.205078125, 0.117187500, 0.043945313, 0.009765625, 0.000976563,
	/*13 samples index 27*/ 0.000244141, 0.002929688, 0.016113281, 0.053710938, 0.120849609, 0.193359375, 0.225585938, 0.193359375, 0.120849609, 0.053710938, 0.016113281, 0.002929688, 0.000244141,
	/*15 samples index 40*/ 0.000061035, 0.000854492, 0.005554199, 0.022216797, 0.061096191, 0.122192383, 0.183288574, 0.209472656, 0.183288574, 0.122192383, 0.061096191, 0.022216797, 0.005554199, 0.000854492, 0.000061035,
	/*17 samples index 55*/ 0.000015259, 0.000244141, 0.001831055, 0.008544922, 0.027770996, 0.066650391, 0.122192383, 0.174560547, 0.196380615, 0.174560547, 0.122192383, 0.066650391, 0.027770996, 0.008544922, 0.001831055, 0.000244141, 0.000015259,
	/*19 samples index 72*/ 0.000003815, 0.000068665, 0.000583649, 0.003112793, 0.011672974, 0.032684326, 0.070816040, 0.121398926, 0.166923523, 0.185470581, 0.166923523, 0.121398926, 0.070816040, 0.032684326, 0.011672974, 0.003112793, 0.000583649, 0.000068665, 0.000003815
); 

in vec2 texCoord;

uniform sampler2D tex;
uniform sampler2D depth;
uniform float offsetValue = 1.0f; // how far to go?
uniform float dir = 1.0f; 		  // 1 = horizontal, 0 = vertical
uniform int samples = 13;
uniform int startValue = 27;

out vec4 color;

void main(void) {
	
	float offset = offsetValue / ( dir * textureSize(tex, 0).x + (1 - dir) * textureSize(tex, 0).y );
	
	float newRelVecCoord = -(float(samples) / 2.0f - 0.5f) * offset;
	vec2 newTC;
	
	vec4 sumNewColor = vec4(0.0f);
	
	for(int i = startValue; i < startValue + samples; i++) {
		newTC = texCoord + vec2(dir * newRelVecCoord, (1 - dir) * newRelVecCoord);
		if(texture(depth, newTC).w == 0.0) newTC = texCoord;
				
		sumNewColor += texture(tex, newTC) * factors[i];
		
		newRelVecCoord += offset;
	}
	
	color = sumNewColor;
}