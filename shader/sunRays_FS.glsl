#version 330

uniform sampler2D sunTexture;
in vec4 positionWC;
in vec4 normalWC;
in vec4 color;
in vec2 sun_fragmentTexCoords;

out vec4 finalColor;
 
void main() {

	int Samples = 128;
	float Intensity = 0.125; 
	float Decay = 0.96875;
	vec2 Direction = vec2(0.5) - sun_fragmentTexCoords;
	Direction/=Samples;
	vec3 color = texture2D(sunTexture, sun_fragmentTexCoords).rgb;
	vec2 sun_fragmentTexCoords_temp = sun_fragmentTexCoords;
	
	for(int Sample = 0; Sample < Samples; Sample++){
		color+= texture2D(sunTexture, sun_fragmentTexCoords_temp).rgb * Intensity;
		Intensity *= Decay;
		sun_fragmentTexCoords_temp+= Direction;
	}
	finalColor = vec4(color, 1.0);
}