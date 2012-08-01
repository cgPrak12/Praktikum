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
	vec4 texturColor =texture2D(worldTexture, texCoord);
	vec3 color = vec3(0.0, 0.0, 0.0); 
	if (texturColor.w > 3)
	{
		vec2 actTexCoord = texCoord - float(int(gaussRadius/2)) * (texturColor.w - 3.0) * deltaBlur;
		for (int i=0; i<gaussRadius; ++i) { 
			color += gaussFilter[i] * texture2D(worldTexture, actTexCoord).xyz;
			actTexCoord += (texturColor.w - 3.0) * deltaBlur;
		}
	}
	else
	{
		color = texturColor.xyz;
	}
	finalColor = vec4(color,1.0);
}