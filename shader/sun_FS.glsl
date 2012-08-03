#version 150 core

uniform sampler2D sunTexture;

in vec2 sun_fragmentTexCoords;

out vec4 sunColor;


void main(void){ 

	
  sunColor = texture(sunTexture, sun_fragmentTexCoords);

}

	
