#version 330

uniform sampler2D worldTexture;
uniform vec3 lightPosition;
in vec2 texCoord;

out vec4 finalColor;
 
void main() {
	int Samples = 128;
	float Intensity = 0.125; 
	float Decay = 0.96875;
	vec2 Direction = vec2(0.5) - texCoord;
	Direction/=Samples;
	vec3 color = texture2D(worldTexture, texCoord).rgb;
	vec2 texCoord_temp = texCoord;
	
	for(int Sample = 0; Sample < Samples; Sample++){
		color+= texture2D(worldTexture, texCoord_temp).rgb * Intensity;
		Intensity *= Decay;
		texCoord_temp+= Direction;
	}
	finalColor = vec4(color, 1.0);
}