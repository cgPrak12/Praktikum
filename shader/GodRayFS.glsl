#version 330

uniform sampler2D worldTexture;
uniform sampler2D diffuseTexture;
uniform vec3 lightPosition;
uniform mat4 viewProj;
uniform mat4 model;
in vec2 texCoord;

out vec4 finalColor;

const float num_samples       = 100;
const float density           = 0.8;
const float weight            = 0.9;
const float Decay             = 0.5;
const float Exposure          = 0.5;
const vec4  backgroundColor	  = vec4(0,0,0,1);

void main() 
{  
	float illuminationDecay = 1;
	vec2 texCoords = texCoord;
	vec4 lightpos = viewProj * model* vec4(lightPosition,1);
 	vec2 deltaTexCoord = (texCoords - (((lightpos/lightpos.w).xy*0.5)+vec2(0.5)));  
 	
  	deltaTexCoord *= 1.0f / num_samples * density;  
   	vec3 color = texture(diffuseTexture,texCoords).rgb;  
	if(length(color) < 0.01)
	{
		finalColor = backgroundColor + texCoords.y*vec4(0.0,0.04,0.4,0.0) + texCoords.x*vec4(0.2,0.0,0.0,0.0);
		return;
	}
    
   	for (int i = 0; i < num_samples; i++)  
  	{  
  		vec3 current = texture(diffuseTexture,texCoords).rgb;
  		if(length(current) < 0.01)
  		{
  			current = vec3(0.9,0.95,1.0);
  		}
  			
	    texCoords -= deltaTexCoord;  
	    vec3 sample = current * (vec3(1)- texture(worldTexture, texCoords).rgb);   
    	sample *= illuminationDecay * weight;  
	    color += sample;  
    	illuminationDecay *= Decay;  
  }  
  finalColor = vec4(color * Exposure, 1);  
}  