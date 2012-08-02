#version 150 core

in vec2 texCoords;

vec3 lightDir = vec3(0.5, -1.0, 0.0);
uniform sampler2D depthTex;
uniform sampler2D normalTex;
uniform vec3 camPos; 

	// szenenspezifische eigenschaften
	uniform vec3 eyePosition;   // position der kamera

out vec4 finalColor;

vec3 c_d = vec3(0.0, 0.5, 0.8);  // diffuse Farbe
vec3 c_s = vec3(1.0, 1.0, 1.0);  // spekulare Farbe
vec3 c_a = vec3(1.0, 1.0, 1.0);  // ambiente Farbe

vec3 lightColor = vec3(1.0, 1.0, 1.0);

// material eigenschaften
float k_a = 0.05;
float k_diff = 0.6;
float k_spec = 0.3;
float es = 16.0;
float depth = texture(depthTex, texCoords).x;
vec3 position = vec3(texCoords,depth);



/**
 * Berechnet die Intensitaet einer Punktlichtquelle.
 * @param p Position des beleuchteten Punktes
 * @param p_p Position der Punktlichtquelle
 * @param I_p_max Maximale Intensitaet der Lichtquelle im Punkt p_p
 * @return Intensitaet der Lichtquelle im Punkt p
 */
vec3 plIntensity()
{
//    float depth = texture(depthTex, texCoords).x;
    float factor = 1.0 / dot(position-camPos, position-camPos);
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
vec3 calcLighting()
{
	vec3 normal = normalize( texture(normalTex,texCoords).xyz );
	
	
    vec3 color = c_a * k_a;
    vec3 pos2eye = normalize(camPos-position);

    vec3 intensity = plIntensity();
    vec3 light2pos = normalize(position-camPos);
    vec3 reflected = reflect(light2pos, normal);
        
    color += c_d * k_diff * intensity * max(0, dot(-light2pos, normal));             // diffuse
    color += c_s * k_spec * intensity * max(0, pow(dot(reflected, pos2eye), es));   // specular

    return color;
}

void main(void)
{

//	vec3 normal = normalize( texture(normalTex,texCoords).xyz );
	
	finalColor = vec4(calcLighting(), 1.0);
}