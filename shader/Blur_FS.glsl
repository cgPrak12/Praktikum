#version 330

uniform sampler2D worldTexture;
uniform sampler2D diffuseTexture;
uniform float deltaBlur;
in vec2 texCoord;

out vec4 finalColor;
const int gaussRadius = 11;
const float gaussFilter[gaussRadius] = float[gaussRadius](
	0.0402,0.0623,0.0877,0.1120,0.1297,0.1362,0.1297,0.1120,0.0877,0.0623,0.0402
);
 
void main() {
	vec4 texturColor =texture2D(diffuseTexture, texCoord);
	vec3 color = vec3(0.0, 0.0, 0.0); 
	float eyeDist = texture2D(worldTexture, texCoord).w;
	if (eyeDist > 3)
	{
		vec2 actTexCoord = texCoord - float(int(gaussRadius/2)) * (eyeDist - 3.0) * deltaBlur;
		for (int i=0; i<gaussRadius; ++i) { 
			color += gaussFilter[i] * texture2D(diffuseTexture, actTexCoord).xyz;
			actTexCoord += (eyeDist - 3.0) * deltaBlur;
		}
	}
	else
	{
		color = texturColor.xyz;
	}
	finalColor = vec4(color,1.0);
}