#version 150

uniform sampler2D scene;


in vec2 texCoords;

out vec4 color;

void main(void) {
vec4 sum = vec4(0.0);
float i = 0.0;
float size = textureSize(scene, 0).x;
float depth = texture2D(scene, texCoords).w;
vec4 sample=texture2D( scene, texCoords ) * 1000.0/4070.0;
float factor=700/4070.0;	

for(float j=-5.0;j<=5.0;j++){
//	float sample= texture2D(scene,texCoords + j*vec2(1.0/size, 0)).w;
	
	sample = sample + texture2D( scene, texCoords + j*vec2(1.0/size , 0.0) ) * vec4(factor)
		           + texture2D( scene, texCoords - j*vec2(1.0/size, 0.0) ) * vec4(factor);
	factor= factor/2.0;	           
		 
		 
	//spatial domain
	float r = j;
	float w =  pow(2.72,-r*r);
	
	//range domain

	vec4 r2=sample-depth;                   
	float g_w= pow(2.72,-r2.w*r2.w);
	float g_x= pow(2.72,-r2.x*r2.x);
	float g_y= pow(2.72,-r2.y*r2.y);
	float g_z= pow(2.72,-r2.z*r2.z);
	
	sum+=(sample*vec4(g_x,g_y,g_z,g_w)*vec4(1.5));
	i+=(w*g_w)*2.0;
}

if(i>0.0){
sum/=i;
}
//color =vec4(texture2D(depthTex, texCoords).xyz,sum);
//color=vec4(sum.w);
color=sum;
} 