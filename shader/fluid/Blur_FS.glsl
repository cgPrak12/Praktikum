#version 150
const int SAMPLES = 13;
const float FACTORS[SAMPLES] = float[](
	0.000244141, 0.002929688, 0.016113281, 0.053710938, 0.120849609, 0.193359375, 
	0.225585936, 0.193359375, 0.120849609, 0.053710938, 0.016113281, 0.002929688, 
	0.000244141
);

uniform sampler2D tex;
uniform sampler2D depth;
uniform float offsetValue = 1.0f; // how far to go?
uniform float dir = 1.0f; 		  // 1 = horizontal, 0 = vertical

in vec2 texCoord;

out vec4 color;

void main(void) {
	float offset = offsetValue / ( dir * textureSize(tex, 0).x + (1 - dir) * textureSize(tex, 0).y );
	
	float newRelVecCoord = -(float(SAMPLES) / 2.0f - 0.5f) * offset;
	vec2 newTC;
	
	vec4 sumNewColor = vec4(0.0f);
	
	for(int i = 0; i < SAMPLES; i++) {
		newTC = texCoord + vec2(dir * newRelVecCoord, (1 - dir) * newRelVecCoord);
		if(texture(depth, newTC).w == 0.0) newTC = texCoord;
				
		sumNewColor += texture(tex, newTC) * FACTORS[i];
		
		newRelVecCoord += offset;
	}
	
	color = sumNewColor;
}