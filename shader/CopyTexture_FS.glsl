#version 330

uniform sampler2D image;

in vec2 fragmentTexCoords;

out vec4 fragColor;

void main(void)
{
    fragColor = texture(image, fragmentTexCoords);
}