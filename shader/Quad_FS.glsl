#version 150 core

uniform sampler2D leftTopImage;
uniform sampler2D rightTopImage;
uniform sampler2D leftDownImage;
uniform sampler2D rightDownImage;

in vec2 texCoord;

out vec4 color;

void main(void)
{
    vec2 tc;
    if(texCoord.x < 0.5)
    {
        if(texCoord.y > 0.5)
        {   
            tc = vec2(texCoord.x * 2, texCoord.y * 2);
            color = ((textureOffset(leftTopImage, tc, ivec2(1, 0)) +
            textureOffset(leftTopImage, tc, ivec2(1, 1)) +
            textureOffset(leftTopImage, tc, ivec2(0,1))) /3);
        }
        else
        {
            tc = vec2(texCoord.x * 2, (texCoord.y - 0.5) * 2);
            color = ((textureOffset(leftDownImage, tc, ivec2(1, 0)) +
            textureOffset(leftDownImage, tc, ivec2(1, 1)) +
            textureOffset(leftDownImage, tc, ivec2(0,1))) /3);
        }
    }
    else
    {
        if(texCoord.y > 0.5)
        {   
            tc = vec2((texCoord.x - 0.5) * 2, texCoord.y * 2);
            color = ((textureOffset(rightTopImage, tc, ivec2(1, 0)) +
            textureOffset(rightTopImage, tc, ivec2(1, 1)) +
            textureOffset(rightTopImage, tc, ivec2(0,1))) /3);
        }
        else
        {
            tc = vec2((texCoord.x - 0.5) * 2, (texCoord.y - 0.5) * 2);
            color = ((textureOffset(rightDownImage, tc, ivec2(1, 0)) +
            textureOffset(rightDownImage, tc, ivec2(1, 1)) +
            textureOffset(rightDownImage, tc, ivec2(0,1))) /3);
        }
    }
}