#version 150

in vec2 texCoords;

uniform sampler2D thickness;
uniform sampler2D depth;
uniform int height;

out vec4 color;

float heightF = float(height);

void main(void) {

	float factor = 0.5 - ( clamp(texture(depth, texCoords).w, 0.1, 0.9) /4.0 );

	color =
		   texture( thickness, texCoords-vec2(0, factor*40.0*4.0/heightF) ) * 0.05
		 + texture( thickness, texCoords-vec2(0, factor*30.0*3.0/heightF) ) * 0.09
		 + texture( thickness, texCoords-vec2(0, factor*20.0*2.0/heightF) ) * 0.12
		 + texture( thickness, texCoords-vec2(0, factor*10.0*1.0/heightF) ) * 0.15
		 
		 + texture( thickness, texCoords )                                  * 0.16
		 
		 + texture( thickness, texCoords+vec2(0, factor*10.0*1.0/heightF) ) * 0.15
		 + texture( thickness, texCoords+vec2(0, factor*20.0*2.0/heightF) ) * 0.12
		 + texture( thickness, texCoords+vec2(0, factor*30.0*3.0/heightF) ) * 0.09
		 + texture( thickness, texCoords+vec2(0, factor*40.0*4.0/heightF) ) * 0.05;
		
} 
