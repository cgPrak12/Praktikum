#version 330

uniform sampler2D diffuseTexture;
uniform sampler2D skyTexture;
uniform vec3 lightPosition;
uniform mat4 viewProj;

in vec2 texCoord;

out vec4 finalColor;

const float num_samples       = 64;
const float density           = 0.8;
const float weight            = 0.05;
const float Decay             = 1;
const float Exposure          = 0.6;

void main() 
{  
	float illuminationDecay = 0.5;
	vec2 texCoords = texCoord;
	vec4 lightpos    = viewProj *vec4(lightPosition,1);
	vec2 lightTexPos =((lightpos/lightpos.w).xy*0.5)+vec2(0.5);

	float cosa  =  pow(length(dot(lightpos.xyz,vec3(0,0,1)))/ (length(lightpos.xyz)), 3);
	if( (lightpos.z < 0) || (lightPosition.y < 0))
	{
		finalColor = texture(diffuseTexture,texCoords); 
		return;
	}
 	vec2 deltaTexCoord = (texCoords - lightTexPos);  
  	deltaTexCoord *= (1.0f / num_samples) * density;  
   	vec3 color = texture(diffuseTexture,texCoords).rgb;  
	
   	for (int i = 0; i < num_samples; i++)  
  	{  
  		vec3 current = texture(diffuseTexture, texCoords).rgb;
	    texCoords -= deltaTexCoord;
	    vec3 sample = current * (vec3(1)- texture(skyTexture, texCoords).rgb);
    	sample *= illuminationDecay * weight;  
	    color += sample;  
    	illuminationDecay *= Decay;  
    	
  }  
  finalColor = vec4( mix(texture(diffuseTexture,texCoord).xyz, color * Exposure , cosa) , 1);  
}  
