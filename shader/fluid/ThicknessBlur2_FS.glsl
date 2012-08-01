#version 150

in vec2 texCoords;

uniform sampler2D thickness;


out vec4 color;

void main(void) {

color =  (/* texture( thickness, texCoords-vec2(0, 12/800) )
		 + texture( thickness, texCoords-vec2(0, 11/800) )
		 + texture( thickness, texCoords-vec2(0, 10/800) )
		 + texture( thickness, texCoords-vec2(0, 9/800) )
		 + texture( thickness, texCoords-vec2(0, 8/800) )
		 + texture( thickness, texCoords-vec2(0, 7/800) )
		 + texture( thickness, texCoords-vec2(0, 6/800) )
		 + texture( thickness, texCoords-vec2(0, 5/800) ) */
		  texture( thickness, texCoords-vec2(0, 4.0*4.0/800.0) ) * 0.05
		 + texture( thickness, texCoords-vec2(0, 4.0*3.0/800.0) ) * 0.09
		 + texture( thickness, texCoords-vec2(0, 4.0*2.0/800.0) ) * 0.12
		 + texture( thickness, texCoords-vec2(0, 4.0*1.0/800.0) ) * 0.15
		 
		 + texture( thickness, texCoords ) * 0.16
		 
		 + texture( thickness, texCoords+vec2(0, 4.0*1.0/800.0) ) * 0.15
		 + texture( thickness, texCoords+vec2(0, 4.0*2.0/800.0) ) * 0.12
		 + texture( thickness, texCoords+vec2(0, 4.0*3.0/800.0) ) * 0.09
		 + texture( thickness, texCoords+vec2(0, 4.0*4.0/800.0) ) * 0.05
		/* + texture( thickness, texCoords+vec2(0, 5/800) )
		 + texture( thickness, texCoords+vec2(0, 6/800) )
		 + texture( thickness, texCoords+vec2(0, 7/800) )
		 + texture( thickness, texCoords+vec2(0, 8/800) )
		 + texture( thickness, texCoords+vec2(0, 9/800) )
		 + texture( thickness, texCoords+vec2(0, 10/800) )
		 + texture( thickness, texCoords+vec2(0, 11/800) )
		 + texture( thickness, texCoords+vec2(0, 12/800) )*/
		 ) ;
		
} 
