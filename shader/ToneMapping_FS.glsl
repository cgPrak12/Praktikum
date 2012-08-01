#version 330

/**
 * @brief Performes tone mapping.
 *
 * @author vbruder
 */
 
uniform sampler2D diffuseTex;
uniform float exposure;

in vec2 texCoord;

out vec4 toneMappedColor;

void main(void)
{
	vec4 color = texture(diffuseTex, texCoord);
	
	toneMappedColor = 1.0 - exp2(-color * exposure);
	toneMappedColor.a = 1.0;
}