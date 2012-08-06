#version 150

uniform sampler2D colorTex;
//in vec2 texCoords;
in vec4 positionWC;

out vec4 color;

void main(void) {

	vec2 texCoords = vec2(0.5+0.5*positionWC.x,0.5+0.5*positionWC.z);//    vec2(((positionWC.x+1)/2)/1000, ((positionWC.z+1)/2)/1000);
	color = /*positionWC;//vec4(1,1,1,0);//*/texture(colorTex, texCoords);
}