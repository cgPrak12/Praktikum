#version 330
float factors[7] = float[](0.015625, 0.09375, 0.234375, 0.3125, 0.234375, 0.09375, 0.015625);

uniform sampler2D normalTex;
uniform float texSize;

in vec2 texCoord;
out vec4 color;

int count = factors.length;
float offset = 1.0/texSize;
float newRelVecCoord = - (float(count) / 2.0 - 0.5) * offset;  
vec3 sumNewNormal = vec3(0);
vec2 newTC = texCoord;

void main(void) {
	for(int i = 0; i < count; i++) {
		newTC += vec2(newRelVecCoord,0);
		if(newTC.x < 0) {
			newRelVecCoord+=offset;
			continue;
		}
		if(newTC.x > 1) {
			i = count;
			continue;
		}
		sumNewNormal += texture(normalTex, newTC).xyz * factors[i];
		newRelVecCoord += offset;
	}
	color = vec4(sumNewNormal, 0);
}
