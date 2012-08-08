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

in vec4 positionWC;
in vec4 normalWC;
in vec2 fragmentTexCoords;

//out vec4 position;
//out vec4 normal;
out vec4 color;

void main(void)
{
    if(k_diss != 1.0 && texture(dissolveTex, fragmentTexCoords).a <= k_diss)
        discard;

    vec3 colorMix = k_a;
    colorMix += k_dif*texture(diffuseTex, fragmentTexCoords).rgb;
    colorMix += k_spec*texture(specularTex, fragmentTexCoords).rgb;

//    position = positionWC;
//    normal = normalWC;
    color = vec4(colorMix, 0.0);
}