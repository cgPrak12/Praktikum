#version 150

in vec2 texCoords;

uniform sampler2D thickness;
//uniform int width;
//uniform int height;

out vec4 color;

void main(void) {

	int width = 800;
	int height = 800;

	vec4 texCoo;

color =  ( (texture( thickness, texCoords+vec2(1/800,0) )
		 + texture( thickness, texCoords+vec2(0,1/800) )
		 + texture( thickness, texCoords-vec2(1/800,0) ) 
		 + texture( thickness, texCoords-vec2(0,1/800) )
		  
		 + texture( thickness, texCoords-vec2(1/800,1/800) ) 
		 + texture( thickness, texCoords+vec2(1/800,1/800) ) 
		 + texture( thickness, texCoords+vec2(-1/800,1/800) ) 
		 + texture( thickness, texCoords+vec2(1/800,-1/800) ) 
		 
		 + texture( thickness, texCoords+vec2(0,2/800) ) 
		 + texture( thickness, texCoords+vec2(2/800,0) ) 
		 + texture( thickness, texCoords-vec2(0,2/800) ) 
		 + texture( thickness, texCoords-vec2(2/800,0) )
		  
		 + texture( thickness, texCoords+vec2(2/800,2/800) ) 
		 + texture( thickness, texCoords-vec2(2/800,2/800) ) 
		 + texture( thickness, texCoords+vec2(-2/800,2/800) ) 
		 + texture( thickness, texCoords+vec2(2/800,-2/800) )

		 + texture( thickness, texCoords+vec2(0,3/800) ) 
		 + texture( thickness, texCoords+vec2(3/800,0) ) 
		 + texture( thickness, texCoords-vec2(0,3/800) ) 
		 + texture( thickness, texCoords-vec2(3/800,0) )

		 + texture( thickness, texCoords+vec2(3/800,3/800) ) 
		 + texture( thickness, texCoords-vec2(3/800,3/800) ) 
		 + texture( thickness, texCoords+vec2(-3/800,3/800) ) 
		 + texture( thickness, texCoords+vec2(3/800,-3/800) )		  
		 
		 + texture( thickness, texCoords) ) *100 )
		 /2500;

//	vec4 texColor = texture(thickness, texCoords);
//	vec4 texColor = texture(thickness, texCoords);
//	vec4 texColor = texture(thickness, texCoords);

//	color = texColor;

}