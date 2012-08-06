#version 150

in vec2 texCoords;
//in float factor;

uniform sampler2D thickness;
uniform sampler2D depth;
//uniform int width;
//uniform int height;

out vec4 color;

void main(void) {

	int width = 800;
	int height = 800;

	
	float factor = 0.5 - (clamp(texture(depth,texCoords).w,0.1,0.9)/4);


color = /* ( texture( thickness, texCoords-vec2(12.0/800.0,0) )
		 + texture( thickness, texCoords-vec2(11.0/800.0,0) )
		 + texture( thickness, texCoords-vec2(10.0/800.0,0) )
		 + texture( thickness, texCoords-vec2(9.0/800.0,0) )
		 + texture( thickness, texCoords-vec2(8.0/800.0,0) )
		 + texture( thickness, texCoords-vec2(7.0/800.0,0) )
		 + texture( thickness, texCoords-vec2(6.0/800.0,0) )
		 + texture( thickness, texCoords-vec2(5.0/800.0,0) ) 
		 +*/ texture( thickness, texCoords-vec2(factor*10.0*4.0/800.0,0) ) * 66/4070.0
		 + texture( thickness, texCoords-vec2(factor*10.0*3.0/800.0,0) ) * 220/4070.0
		 + texture( thickness, texCoords-vec2(factor*10.0*2.0/800.0,0) ) * 495/4070.0
		 + texture( thickness, texCoords-vec2(factor*10.0*1.0/800.0,0) ) * 792/4070.0
		 
		 + texture( thickness, texCoords ) * 924.0/4070.0
		 
		 + texture( thickness, texCoords+vec2(factor*10.0*1.0/800.0,0) ) * 792/4070.0
		 + texture( thickness, texCoords+vec2(factor*10.0*2.0/800.0,0) ) * 495/4070.0
		 + texture( thickness, texCoords+vec2(factor*10.0*3.0/800.0,0) ) * 220/4070.0
		 + texture( thickness, texCoords+vec2(factor*10.0*4.0/800.0,0) ) * 66/4070.0;
		/* + texture( thickness, texCoords+vec2(5.0/800.0,0) )
		 + texture( thickness, texCoords+vec2(6.0/800.0,0) )
		 + texture( thickness, texCoords+vec2(7.0/800.0,0) )
		 + texture( thickness, texCoords+vec2(8.0/800.0,0) )
		 + texture( thickness, texCoords+vec2(9.0/800.0,0) )
		 + texture( thickness, texCoords+vec2(10.0/800.0,0) )
		 + texture( thickness, texCoords+vec2(11.0/800.0,0) )
		 + texture( thickness, texCoords+vec2(12.0/800.0,0) )
		 ) / 25;
*/

} 
