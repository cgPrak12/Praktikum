#version 150

in vec2 positionMC;

uniform mat4 viewProj;
uniform sampler2D depth;

out vec2 texCoords;
out float factor;

void main(void) {
	gl_Position = vec4(positionMC, 0.0, 1.0);
	texCoords = vec2(0.5,0.5) + 0.5 * positionMC;
	factor = 1.0 - texture(depth,texCoords).w;
}