#version 150

uniform sampler2D depthTex;

in vec2 texCoords;

out vec4 color;

void main(void) {

float depth = texture2D(depthTex, texCoords).w;
float size = textureSize(depthTex, 0).y;
float sum = 0;
float i = 0;
for(float j=-5;j<=5;j++){
	float sample= texture2D(depthTex, texCoords + j*vec2(0, 1.0/size)).w;
	
	//spatial domain
	float r = j;
	float w = pow(2.72,-r*r);
	
	//range domain
	float r2=(sample-depth)*3.0;                      
	float g=pow(2.72,-r2*r2);
	
	sum+=sample*g*0.7;//*w*g;
	i+=w*g;
}

if(i>0.0){
sum/=i;
}
color =vec4(texture2D(depthTex, texCoords).xyz,sum);
color=vec4(sum);
} 
