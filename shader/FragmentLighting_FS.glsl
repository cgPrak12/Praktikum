#version 150 core
// material eigenschaften
//uniform float es;   // parameter

uniform vec3 k_a; //Gewicht für ambiente Farbe
uniform vec3 k_dif; //Gewicht für diffuse Farbe
uniform vec3 k_spec; //Gewicht für spekulare Farbe
uniform float k_diss; //Dissolve factor

uniform sampler2D diffuseTex;           // diffuse farbe
uniform sampler2D dissolveTex;
uniform sampler2D specularTex;          // spekulare farbe

// szenenspezifische eigenschaften
//uniform vec3 eyePosition;   // position der kamera

in vec3 positionWC;
in vec3 normalWC;
in vec2 fragmentTexCoords;

out vec4 fragColor;

void main(void)
{
        if(k_diss != 1.0 && texture(dissolveTex, fragmentTexCoords).a <= k_diss)
            discard;

        vec3 color = k_a;
        color += k_dif*texture(diffuseTex, fragmentTexCoords).rgb;
        color += k_spec*texture(specularTex, fragmentTexCoords).rgb;

	fragColor = vec4(color, 1.0);
}