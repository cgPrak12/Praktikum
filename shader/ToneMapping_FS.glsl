#version 330

/**
 * @brief Performes tone mapping with adaptive exposure levels.
 *
 * @author vbruder
 * @author kseidel
 */
 
uniform sampler2D lightedTex;
uniform float exposure;
uniform vec2 tc_offset[25];

in vec2 texCoord;

out vec4 toneMappedColor;

void main(void)
{
	vec4 hdrSample[25];
	for(int i = 0; i < 25; ++i) {
		hdrSample[i] = texture(lightedTex, texCoord.st + tc_offset[i]);
	}
	
	vec4 vColor = hdrSample[12];
	vec4 kernelcolor = (
		( 1.0 * ( hdrSample[0] + hdrSample[4] + hdrSample[20] + hdrSample[24])) +
		( 4.0 * ( hdrSample[1] + hdrSample[3] + hdrSample[5] + hdrSample[9] + hdrSample[15] + hdrSample[19] + hdrSample[21] + hdrSample[23])) +
		( 7.0 * ( hdrSample[2] + hdrSample[10] + hdrSample[14] + hdrSample[22])) +
		(16.0 * ( hdrSample[6] + hdrSample[8] + hdrSample[16] + hdrSample[18])) +
		(26.0 * ( hdrSample[7] + hdrSample[11] + hdrSample[13] + hdrSample[17])) +
		(41.0 * hdrSample[12])
		) / 273.0;
		
		float kernelLuminance = dot(kernelcolor.rgb, vec3(0.3, 0.59, 0.11));
		
		float newExposure = kernelLuminance * exposure;
		newExposure = clamp(newExposure, 0.2f, 20.0f);
		
		toneMappedColor = 1.0 - exp2(-vColor * newExposure);
		toneMappedColor.a = 1.0;
		
		
	
	
	//debugging
	//toneMappedColor = color;
}