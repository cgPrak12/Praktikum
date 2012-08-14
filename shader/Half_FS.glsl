#version 150 core

uniform sampler2D leftImage;
uniform sampler2D rightImage;

in vec2 texCoord;

out vec4 finalColor;

void main(void)
{	
	if(texCoord.x < 0.5f)
	{
		finalColor = texture(leftImage, texCoord);
	}
	else
	{
		finalColor = texture(rightImage, texCoord);
	}
	//finalColor = vec4(texCoord.x < 0.5f ? 1 : 0);
}