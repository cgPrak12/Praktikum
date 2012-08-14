#version 150 core

/**
 * @brief Calculates bright data of a scene.
 *
 * @author vbruder
 * @author kseidel
 */

uniform sampler2D colorTex;
uniform vec4 colorFactor;

in vec2 texCoord;

out vec4 brightness;

void main(void)
{
	const float bloomLimit = 1.0;
	
	vec4 color = colorFactor * texture(colorTex, texCoord);
	color.a = 1.0;
	
	vec3 brightColor = max(color.rgb - vec3(bloomLimit), vec3(0.0));
	float bright = dot(brightColor, vec3(1.0));
	bright = smoothstep(0.0, 0.5, bright);
	brightness.rgb = mix(vec3(0.0), colorFactor.rgb, bright).rgb;
	brightness.a = 1.0;
}
