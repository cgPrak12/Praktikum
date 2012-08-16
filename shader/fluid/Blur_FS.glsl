#version 330

in vec2 texCoord;

uniform sampler2D tex;
uniform int dir; 		  // 1 = horizontal, 0 = vertical

out vec4 color;

void main(void) {
	vec4 sumNewColor = vec4(0.0f);
	vec2 stepSize = vec2( sign(dir)   * 1.0 / textureSize(tex, 0).x, 
					      sign(1-dir) * 1.0 / textureSize(tex, 0).y);
	
	sumNewColor += 0.0000000076834112 * (texture(tex, texCoord - (15.0 + 0.030303)*stepSize) + texture(tex, texCoord + (15.0 + 0.030303)*stepSize));
    sumNewColor += 0.0000012703239918 * (texture(tex, texCoord - (13.0 + 0.090909)*stepSize) + texture(tex, texCoord + (13.0 + 0.090909)*stepSize));
    sumNewColor += 0.0000552590936422 * (texture(tex, texCoord - (11.0 + 0.151515)*stepSize) + texture(tex, texCoord + (11.0 + 0.151515)*stepSize));
    sumNewColor += 0.0009946636855602 * (texture(tex, texCoord - ( 9.0 + 0.212121)*stepSize) + texture(tex, texCoord + ( 9.0 + 0.212121)*stepSize));
    sumNewColor += 0.0089796027168632 * (texture(tex, texCoord - ( 7.0 + 0.272727)*stepSize) + texture(tex, texCoord + ( 7.0 + 0.272727)*stepSize));
    sumNewColor += 0.0450612790882587 * (texture(tex, texCoord - ( 5.0 + 0.333333)*stepSize) + texture(tex, texCoord + ( 5.0 + 0.333333)*stepSize));
    sumNewColor += 0.1334507111459971 * (texture(tex, texCoord - ( 3.0 + 0.393939)*stepSize) + texture(tex, texCoord + ( 3.0 + 0.393939)*stepSize));
    sumNewColor += 0.2414822392165661 * (texture(tex, texCoord - ( 1.0 + 0.454545)*stepSize) + texture(tex, texCoord + ( 1.0 + 0.454545)*stepSize));
    sumNewColor += 0.1399499340914190 *  texture(tex, texCoord);
	
	color = sumNewColor;
}