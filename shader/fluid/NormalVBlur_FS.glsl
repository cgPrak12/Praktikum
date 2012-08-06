#version 330
float factors[] = float[](0.015625, 0.09375, 0.234375, 0.3125, 0.234375, 0.09375, 0.015625);

uniform sampler2D normalTex;
uniform float texSize;
uniform float offsetValue;

in vec2 texCoord;
out vec4 color;

int count = factors.length;
float offset = offsetValue/texSize;
float newRelVecCoord = - (float(count) / 2.0f - 0.5f) * offset;
vec3 sumNewNormal = vec3(0.0f);
vec2 newTC;

void main(void) {
	for(int i = 0; i < count; i++) {
		newTC = texCoord+vec2(0.0f, newRelVecCoord);
		sumNewNormal += texture(normalTex, newTC).xyz * factors[i];
		newRelVecCoord += offset;
	}
	color = vec4(sumNewNormal, 0.0f);
}