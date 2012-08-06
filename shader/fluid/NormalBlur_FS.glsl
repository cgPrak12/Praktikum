#version 150

in vec2 texCoords;

uniform sampler2D normalTex;
//uniform sampler2D depth;

out vec4 color;

void main(void) {

	int width = 800;
	int height = 800;
	
//	float factor = 0.5 - (clamp(texture(depth,texCoords).w,0.1,0.9)/4);


color = ( texture( normalTex, texCoords - vec2(0.0, 5.0/800.0) )
		+ texture( normalTex, texCoords - vec2(0.0, 4.0/800.0) )
		+ texture( normalTex, texCoords - vec2(0.0, 3.0/800.0) )
		+ texture( normalTex, texCoords - vec2(0.0, 2.0/800.0) )
		+ texture( normalTex, texCoords - vec2(0.0, 1.0/800.0) )
		+ texture( normalTex, texCoords )
		+ texture( normalTex, texCoords + vec2(0.0, 1.0/800.0) )
		+ texture( normalTex, texCoords + vec2(0.0, 2.0/800.0) )
		+ texture( normalTex, texCoords + vec2(0.0, 3.0/800.0) )
		+ texture( normalTex, texCoords + vec2(0.0, 4.0/800.0) )
		+ texture( normalTex, texCoords + vec2(0.0, 5.0/800.0) )
		) / 7;

} 