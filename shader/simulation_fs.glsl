#version 330

in vec3 coords;
in vec3 normal;
out vec4 fragColor;

uniform sampler2D normalTex;
uniform sampler2D heightTex;

void main(void)
{
    float h = coords.y+0.4;
//    vec3 color = texture(normalTex, vec2(coords.x,coords.z)).xyz;
//    fragColor = vec4(0.5 + 0.5*normalize(color),1.0);
    vec3 color = texture(heightTex, vec2(coords.x,coords.z)).xyz + vec3(0.4);
    fragColor = vec4(color,1.0);
}