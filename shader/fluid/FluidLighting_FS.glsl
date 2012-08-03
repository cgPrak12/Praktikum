#version 150 core

in vec2 texCoords;

uniform sampler2D depthTex;
uniform sampler2D normalTex;
uniform vec3 camPos;
uniform mat4 view; 

	// szenenspezifische eigenschaften
	uniform vec3 eyePosition;   // position der kamera

out vec4 finalColor;


const vec3 c_d = vec3(0.0, 0.5, 0.8);  // diffuse Farbe
const vec3 c_s = vec3(1.0, 1.0, 1.0);  // spekulare Farbe
const vec3 c_a = vec3(1.0, 1.0, 1.0);  // ambiente Farbe

//const vec3 lightPos = vec3(0.0, 5.0, 0.0);
const vec3 lightColor = vec3(1.0, 1.0, 1.0);

// material eigenschaften
const float k_a = 0.05;
const float k_diff = 0.6;
const float k_spec = 0.3;
const float es = 16.0;




/**
 * Berechnet die Intensitaet einer Punktlichtquelle.
 * @param p Position des beleuchteten Punktes
 * @param p_p Position der Punktlichtquelle
 * @param I_p_max Maximale Intensitaet der Lichtquelle im Punkt p_p
 * @return Intensitaet der Lichtquelle im Punkt p
 */
vec3 plIntensity(vec3 position, vec3 lightPos)
{
//    float depth = texture(depthTex, texCoords).x;
    float factor = 1.0 / dot(position-lightPos, position-lightPos);
    return factor * lightColor;
}

/**
 * Berechnet die Beleuchtungsfarbe fuer einen Punkt mit allen Lichtquellen.
 * @param pos Position des Punktes in Weltkoordinaten
 * @param normal Normel des Punktes in Weltkoordinaten
 * @param c_d Diffuse Farbe des Punktes
 * @param c_s Spekulare Farbe des Punktes
 * @return Farbe des beleuchteten Punktes
 */
vec3 calcLighting(vec3 position)
{
	vec3 normal = normalize( ( texture(normalTex,texCoords)).xyz );

	vec3 lightPos = (view * vec4(0.0, 5.0, 0.0, 1.0)).xyz;	
	
    vec3 color = c_a * k_a;
    vec3 pos2eye = normalize(position);

    vec3 intensity = vec3(1,1,1);//plIntensity(position, lightPos);
    vec3 light2pos = normalize(position-lightPos);
    vec3 reflected = reflect(light2pos, normal);
        
    color += c_d * k_diff * intensity * max(0, dot(-light2pos, normal));             // diffuse
    color += c_s * k_spec * intensity * max(0, pow(dot(reflected, pos2eye), es));    // specular

    return color;
}

void main(void)
{

//	vec3 normal = normalize( texture(normalTex,texCoords).xyz );

	vec3 normal = normalize( ( inverse(view) * texture(normalTex,texCoords)).xyz );
	
	float depth = texture(depthTex, texCoords).w;
//	vec3 position = vec3(texCoords*2-1,depth);
	vec3 position = ( view * texture(depthTex, texCoords)).xyz;
	
	finalColor = vec4(calcLighting(position), 1.0);
//	finalColor = texture(depthTex, texCoords);//normalize( ( texture(normalTex,texCoords)) );
//	finalColor = vec4(normal,0);
}