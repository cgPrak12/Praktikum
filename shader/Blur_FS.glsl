#version 150 core

/**
 * @brief Blurs a texture. Mixed are 25 texels per texel.
 *
 * @author vbruder
 * @author kseidel
 */
 
uniform sampler2D colorTex;
uniform vec2 tc_offset[25];

in vec2 texCoord;

out vec4 bluredColor;

void main(void)
{
	vec4 sample[25];
	for(int i = 0; i < 25; ++i) {
		sample[i] = textureOffset(colorTex, texCoord.st, ivec2(tc_offset[i]));
	}
	bluredColor = (
		( 1.0 * ( sample[0] +  sample[4] + sample[20] + sample[24])) +
		( 4.0 * ( sample[1] +  sample[3] +  sample[5] +  sample[9] + sample[15] + sample[19] + sample[21] + sample[23])) +
		( 7.0 * ( sample[2] + sample[10] + sample[14] + sample[22])) +
		(16.0 * ( sample[6] +  sample[8] + sample[16] + sample[18])) +
		(26.0 * ( sample[7] + sample[11] + sample[13] + sample[17])) +
		(41.0 *  sample[12])
		) / 273.0;
}