#version 330

in vec3 coords;
in vec3 normal;
out vec4 fragColor;

uniform sampler2D normalTex;

void main(void)
{
    float h = coords.y+0.4;
    vec3 color = texture(normalTex, vec2(coords.x,coords.z)).xyz;
    fragColor = vec4(0.5 + 0.5*normalize(color),1.0);
}