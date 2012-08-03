#version 150 core

/**
 * @brief Adds a bloom effect to the scene.
 *		  Uses the original image as well as a bright image
 *        and 4 blured images with different stages.
 *
 * @author vbruder
 * @author kseidel
 */

uniform sampler2D origImage;
uniform sampler2D brightImage;
uniform sampler2D blur1;
uniform sampler2D blur2;
uniform sampler2D blur3;
uniform sampler2D blur4;

// uniform float exposure;
uniform float bloomLevel;

in vec2 texCoord;

out vec4 bloomColor;
//out bloomBright;

void main(void)
{
	vec4 baseImage = texture(origImage, texCoord);
	vec4 brightPass = texture(brightImage, texCoord);
	vec4 blurColor1 = texture(blur1, texCoord);
	vec4 blurColor2 = texture(blur2, texCoord);
	vec4 blurColor3 = texture(blur3, texCoord);
	vec4 blurColor4 = texture(blur4, texCoord);
	
	vec4 bloom = brightPass + blurColor1 + blurColor2 + blurColor3 + blurColor4;
	vec4 color;
	color = baseImage + (bloomLevel * bloom);
	bloomColor = color;
	//bloomColor = baseImage;
	bloomColor.a = 1f;
	// color = 1.0 - exp2(-color * exposure);
	// bloomColor = color;
	// bloomColor.a = 1.0f;
}