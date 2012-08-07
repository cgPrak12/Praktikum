#version 150 core

in vec2 texCoords;

uniform sampler2D depthTex;
uniform sampler2D normalTex;
uniform sampler2D thicknessTex;
uniform sampler2D colorTex;
uniform vec3 camPos;
uniform mat4 view; 

out vec4 finalColor;

//const vec3 c_d = vec3(0.0, 0.5, 0.8);  // diffuse Farbe
const vec3 c_s = vec3(1.0, 1.0, 1.0);  // spekulare Farbe
const vec3 c_a = vec3(1.0, 1.0, 1.0);  // ambiente Farbe

//const vec3 lightPos = vec3(0.0, 5.0, 0.0);
const vec3 lightColor = vec3(1.0, 1.0, 1.0);

// material eigenschaften
const float k_a    =  0.05;
const float k_diff =  0.6;
const float k_spec =  0.3;
const float es     = 16.0;


void main(void)
{
//	if(texture(depthTex,texCoords).w == 1) discard;
	
	float depth = texture(depthTex, texCoords).w;
//	vec3 position = vec3(texCoords*2-1,depth);
	vec3 position = ( view * texture(depthTex, texCoords)).xyz;
	
	vec3 normal = normalize( ( texture(normalTex,texCoords)).xyz );
//	vec3 normal = normalize( texture(normalTex,texCoords).xyz );
//	vec3 normal = normalize( ( inverse(view) * texture(normalTex,texCoords)).xyz );

	vec3 lightPos = ( view * vec4(0.0, -5.0, 0.0, 1.0)).xyz;	
	
//	vec3 c_d = (1.0-(texture(thicknessTex,texCoords)).z) * vec3(0.0, 0.5, 0.8);  // diffuse Farbe
	vec3 c_d = texture(colorTex,texCoords).xyz;  // diffuse Farbe
	
    vec3 color = c_a * k_a;
//    vec3 newCamPos = vec3(view * vec4(camPos,1));
    vec3 pos2eye = normalize(-position);

    vec3 intensity = vec3(1, 1, 1);//plIntensity(position, lightPos);
    vec3 light2pos = normalize(position - lightPos);
    vec3 reflected = reflect(light2pos, normal);
        
    color += c_d * k_diff * intensity * max(0, dot(-light2pos, normal));             // diffuse
    color += c_s * k_spec * intensity * max(0, pow(dot(reflected, pos2eye), es));    // specular		

	
	finalColor = vec4(color, 1.0);
//	finalColor = texture(depthTex, texCoords);//normalize( ( texture(normalTex,texCoords)) );
//	finalColor = vec4(texture(depthTex, texCoords).w);
//	finalColor = vec4(normal,0);
}