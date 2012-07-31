#version 330

uniform sampler2D worldTexture;
uniform float deltaBlur;
in vec2 texCoord;

out vec4 finalColor;
const int gaussRadius = 11;
const float gaussFilter[gaussRadius] = float[gaussRadius](
	0.0402,0.0623,0.0877,0.1120,0.1297,0.1362,0.1297,0.1120,0.0877,0.0623,0.0402
);
 
void main() {
	vec2 actTexCoord = texCoord - float(int(gaussRadius/2)) * deltaBlur;
	vec3 color = vec3(0.0, 0.0, 0.0); 
	for (int i=0; i<gaussRadius; ++i) { 
		color += gaussFilter[i] * texture2D(worldTexture, actTexCoord).xyz;
		actTexCoord += deltaBlur;
	}
	finalColor = vec4(color,1.0);
}