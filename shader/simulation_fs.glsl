#version 330

in vec3 coords;
in vec3 normal;
out vec4 fragColor;

void main(void)
{
    float h = coords.y+0.4;
    fragColor = vec4(0.5+0.5*normalize(normal),1.0);

}