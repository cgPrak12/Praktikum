#version 150
const int SAMPLES = 7;
const float factors[SAMPLES] = float[]( 
	/* 7*/ 0.015625000, 0.093750000, 0.234375000, 0.312500000, 0.234375000, 0.093750000, 0.015625000
//	/* 9*/ 0.003906250, 0.031250000, 0.109375000, 0.218750000, 0.273437500, 0.218750000, 0.109375000, 0.031250000, 0.003906250
//	/*11*/ 0.000976563, 0.009765625, 0.043945313, 0.117187500, 0.205078125, 0.246093750, 0.205078125, 0.117187500, 0.043945313, 0.009765625, 0.000976563
//	/*13*/ 0.000244141, 0.002929688, 0.016113281, 0.053710938, 0.120849609, 0.193359375, 0.225585938, 0.193359375, 0.120849609, 0.053710938, 0.016113281, 0.002929688, 0.000244141
); 

in vec2 texCoord;

uniform sampler2D tex;
uniform sampler2D depth;
uniform float offsetValue = 1.0f; // how far to go?
uniform float dir = 1.0f; 		  // 1 = horizontal, 0 = vertical

out vec4 color;

void main(void) {
	float offset = offsetValue / ( dir * textureSize(tex, 0).x + (1 - dir) * textureSize(tex, 0).y );
	
	float newRelVecCoord = -(float(SAMPLES) / 2.0f - 0.5f) * offset;
	vec2 newTC;
	
	vec4 sumNewColor = vec4(0.0f);
	
	for(int i = 0; i < SAMPLES; i++) {
		newTC = texCoord + vec2(dir * newRelVecCoord, (1 - dir) * newRelVecCoord);
		if(texture(depth, newTC).w == 0.0) newTC = texCoord;
				
		sumNewColor += texture(tex, newTC) * factors[i];
		
		newRelVecCoord += offset;
	}
	
	color = sumNewColor;
}