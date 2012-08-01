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



color =  ( texture( thickness, texCoords-vec2(12.0/800.0,0) )
		 + texture( thickness, texCoords-vec2(11.0/800.0,0) )
		 + texture( thickness, texCoords-vec2(10.0/800.0,0) )
		 + texture( thickness, texCoords-vec2(9.0/800.0,0) )
		 + texture( thickness, texCoords-vec2(8.0/800.0,0) )
		 + texture( thickness, texCoords-vec2(7.0/800.0,0) )
		 + texture( thickness, texCoords-vec2(6.0/800.0,0) )
		 + texture( thickness, texCoords-vec2(5.0/800.0,0) ) 
		 + texture( thickness, texCoords-vec2(4.0/800.0,0) )
		 + texture( thickness, texCoords-vec2(3.0/800.0,0) )
		 + texture( thickness, texCoords-vec2(2.0/800.0,0) )
		 + texture( thickness, texCoords-vec2(1.0/800.0,0) )
		 
		 + texture( thickness, texCoords )
		 
		 + texture( thickness, texCoords+vec2(1.0/800.0,0) )
		 + texture( thickness, texCoords+vec2(2.0/800.0,0) )
		 + texture( thickness, texCoords+vec2(3.0/800.0,0) )
		 + texture( thickness, texCoords+vec2(4.0/800.0,0) )
		 + texture( thickness, texCoords+vec2(5.0/800.0,0) )
		 + texture( thickness, texCoords+vec2(6.0/800.0,0) )
		 + texture( thickness, texCoords+vec2(7.0/800.0,0) )
		 + texture( thickness, texCoords+vec2(8.0/800.0,0) )
		 + texture( thickness, texCoords+vec2(9.0/800.0,0) )
		 + texture( thickness, texCoords+vec2(10.0/800.0,0) )
		 + texture( thickness, texCoords+vec2(11.0/800.0,0) )
		 + texture( thickness, texCoords+vec2(12.0/800.0,0) )
		 ) / 25;
		  /*
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
*/
//	color = texColor;

} 

/*
uniform sampler2D RTScene; // the texture with the scene you want to blur
in vec2 vTexCoord;
 
float blurSize = 1.0/800.0; // I've chosen this size because this will result in that every step will be one pixel wide if the RTScene texture is of size 512x512
 
void main(void)
{
   vec4 sum = vec4(0.0);
 
   // blur in y (vertical)
   // take nine samples, with the distance blurSize between them
   sum += texture2D(RTScene, vec2(vTexCoord.x - 4.0*blurSize, vTexCoord.y)) * 0.05;
   sum += texture2D(RTScene, vec2(vTexCoord.x - 3.0*blurSize, vTexCoord.y)) * 0.09;
   sum += texture2D(RTScene, vec2(vTexCoord.x - 2.0*blurSize, vTexCoord.y)) * 0.12;
   sum += texture2D(RTScene, vec2(vTexCoord.x - blurSize, vTexCoord.y)) * 0.15;
   sum += texture2D(RTScene, vec2(vTexCoord.x, vTexCoord.y)) * 0.16;
   sum += texture2D(RTScene, vec2(vTexCoord.x + blurSize, vTexCoord.y)) * 0.15;
   sum += texture2D(RTScene, vec2(vTexCoord.x + 2.0*blurSize, vTexCoord.y)) * 0.12;
   sum += texture2D(RTScene, vec2(vTexCoord.x + 3.0*blurSize, vTexCoord.y)) * 0.09;
   sum += texture2D(RTScene, vec2(vTexCoord.x + 4.0*blurSize, vTexCoord.y)) * 0.05;
 
   color = sum;
   
  }*/