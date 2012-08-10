#version 330
const int SAMPLES = 13;
const float FACTORS[SAMPLES] = float[](
	0.000244141, 0.002929688, 0.016113281, 0.053710938, 0.120849609, 0.193359375, 
	0.225585936, 0.193359375, 0.120849609, 0.053710938, 0.016113281, 0.002929688, 
	0.000244141
);

uniform sampler2D skip;
uniform sampler2D depth;
uniform sampler2D normal;
uniform sampler2D thickness;
uniform float offsetValue = 1.0f; // how far to go?
uniform float dir = 1.0f; 		  // 1 = horizontal, 0 = vertical

in vec2 texCoord;

out vec4 depthBlur; // layout(location) needs(!) #version 330
out vec4 normalBlur;
out vec4 thicknessBlur;

void main(void) {
	float offset = offsetValue / ( dir * textureSize(skip, 0).x + (1 - dir) * textureSize(skip, 0).y );
	
	float newRelVecCoord = -(float(SAMPLES) / 2.0f - 0.5f) * offset;
	vec2 newTC;
	
	vec4 sumNewDepth = vec4(0.0f);
	vec4 sumNewNormal = vec4(0.0f);
	vec4 sumNewThickness = vec4(0.0f);
	
	for(int i = 0; i < SAMPLES; i++) {
		newTC = texCoord + vec2(dir * newRelVecCoord, (1 - dir) * newRelVecCoord);
		if(texture(skip, newTC).w == 0.0) newTC = texCoord;
				
		sumNewDepth += texture(depth, newTC) * FACTORS[i];
		sumNewNormal += texture(normal, newTC) * FACTORS[i];
		sumNewThickness += texture(thickness, newTC) * FACTORS[i];
		
		newRelVecCoord += offset;
	}
	
	depthBlur = vec4(1,0,0,0);//sumNewDepth;
	normalBlur = vec4(0,1,0,0);//sumNewNormal;
	thicknessBlur = vec4(0,0,1,0);//sumNewThickness;
}