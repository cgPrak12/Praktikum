#version 330

uniform sampler2D depthTex;
// add more textures

in vec2 texCoord;

out vec4 color;

void main(void)
{
	float offset = 1/800;
    vec4 tangent1 = texture(depthTex, texCoord+vec2(0,offset));
    vec4 tangent2 = texture(depthTex, texCoord+vec2(offset,0));
    //color = vec4(tangent1.x);//vec4(cross(tangent1.xyz, tangent2.xyz), 1);
    color = vec4(texCoord.x, texCoord.y-1, 0, 0);
}