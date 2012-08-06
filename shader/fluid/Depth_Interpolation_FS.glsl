#version 150

uniform sampler2D vBlurTexture;
uniform sampler2D low_v_BlurTexture;

in vec2 texCoords;



out vec4 color;

void main(void) {

float h_depth = texture2D(vBlurTexture, texCoords).w;
float l_depth = (texture2D(low_v_BlurTexture, texCoords).w)/2.0;

}


}
