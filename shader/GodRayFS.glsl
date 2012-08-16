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
	vec2 lightTexPos =((lightpos/lightpos.w).xy*0.5) + vec2(0.5);

	float cosa  =  pow(length(dot(lightpos.xyz, vec3(0, 0, 1))) / (length(lightpos.xyz)), 3);

 	vec2 deltaTexCoord = (texCoords - lightTexPos);  
  	deltaTexCoord *= (1.0f / num_samples) * density;  
   	vec3 color = texture(diffuseTexture,texCoords).rgb;  
	
	vec3 current, sample;
	
   	for (int i = 0; i < num_samples; i++)  
  	{  
  		current = texture(diffuseTexture, texCoords).rgb;
	    texCoords -= deltaTexCoord;
	    sample = current * (vec3(1)- texture(skyTexture, texCoords).rgb);
    	sample *= illuminationDecay * weight;  
	    color += sample;  
    	illuminationDecay *= Decay;  
    	
	}
	vec3 mixColor = texture(diffuseTexture,texCoord).xyz;
	finalColor = vec4( mix(mixColor, color * Exposure , cosa) , 1);
	
	finalColor = finalColor * step(0, lightPosition.y) + (1-step(0, lightPosition.y)) * vec4( mix(mixColor, color * 0.2 , cosa) , 1);

}  
