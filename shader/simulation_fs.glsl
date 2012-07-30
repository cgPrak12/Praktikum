#version 330

in vec3 coords;
out vec4 fragColor;

void main(void)
{
    float h = coords.y+0.4;
    fragColor = vec4(h,h,h,1.0);

}