#version 150

in vec2 texCoords;

uniform sampler2D thickness;
uniform sampler2D depth;
uniform int width;

out vec4 color;

float widthF = float(width); 

void main(void) {
	float factor = 0.5 - ( clamp(texture(depth, texCoords).w, 0.1, 0.9) /4.0 );

color =    texture( thickness, texCoords-vec2(factor*40.0/widthF, 0) ) *  66.0/4070.0
		 + texture( thickness, texCoords-vec2(factor*30.0/widthF, 0) ) * 220.0/4070.0
		 + texture( thickness, texCoords-vec2(factor*20.0/widthF, 0) ) * 495.0/4070.0
		 + texture( thickness, texCoords-vec2(factor*10.0/widthF, 0) ) * 792.0/4070.0
		 
		 + texture( thickness, texCoords )                             * 924.0/4070.0
		 
		 + texture( thickness, texCoords+vec2(factor*10.0/widthF, 0) ) * 792.0/4070.0
		 + texture( thickness, texCoords+vec2(factor*20.0/widthF, 0) ) * 495.0/4070.0
		 + texture( thickness, texCoords+vec2(factor*30.0/widthF, 0) ) * 220.0/4070.0
		 + texture( thickness, texCoords+vec2(factor*40.0/widthF, 0) ) *  66.0/4070.0;

} 
