#version 150

in vec2 texCoords;

uniform sampler2D scene;


out vec4 color;

void main(void) {

color =  texture( scene, texCoords-vec2(4.0*4.0/800.0,0) ) * 66/4070.0
		 + texture( scene, texCoords-vec2(4.0*3.0/800.0,0) ) * 220/4070.0
		 + texture( scene, texCoords-vec2(4.0*2.0/800.0,0) ) * 495/4070.0
		 + texture( scene, texCoords-vec2(4.0*1.0/800.0,0) ) * 792/4070.0
		 
		 + texture( scene, texCoords ) * 924.0/4070.0
		 
		 + texture( scene, texCoords+vec2(4.0*1.0/800.0,0) ) * 792/4070.0
		 + texture( scene, texCoords+vec2(4.0*2.0/800.0,0) ) * 495/4070.0
		 + texture( scene, texCoords+vec2(4.0*3.0/800.0,0) ) * 220/4070.0
		 + texture( scene, texCoords+vec2(4.0*4.0/800.0,0) ) * 66/4070.0;

} 

