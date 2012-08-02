#version 150

in vec2 texCoords;

uniform sampler2D scene;


out vec4 color;

void main(void) {

color =  ( texture( scene, texCoords-vec2(0, 4.0*4.0/800.0) ) * 0.05
		 + texture( scene, texCoords-vec2(0, 4.0*3.0/800.0) ) * 0.09
		 + texture( scene, texCoords-vec2(0, 4.0*2.0/800.0) ) * 0.12
		 + texture( scene, texCoords-vec2(0, 4.0*1.0/800.0) ) * 0.15
		 
		 + texture( scene, texCoords ) * 0.16
		 
		 + texture( scene, texCoords+vec2(0, 4.0*1.0/800.0) ) * 0.15
		 + texture( scene, texCoords+vec2(0, 4.0*2.0/800.0) ) * 0.12
		 + texture( scene, texCoords+vec2(0, 4.0*3.0/800.0) ) * 0.09
		 + texture( scene, texCoords+vec2(0, 4.0*4.0/800.0) ) * 0.05
		 ) ;
		
} 
