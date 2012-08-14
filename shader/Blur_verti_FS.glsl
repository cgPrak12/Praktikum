#version 150 core

uniform sampler2D RTBlurH; // this should hold the texture rendered by the horizontal blur pass
in vec2 texCoord;
out vec4 finalColor;

const float blurSize = 1.0/512.0;
 
void main(void)
{
   vec4 sum = vec4(0.0);
 
   // blur in y (vertical)
   // take nine samples, with the distance blurSize between them
   sum += texture(RTBlurH, vec2(texCoord.x, texCoord.y - 4.0*blurSize)) * 0.05;
   sum += texture(RTBlurH, vec2(texCoord.x, texCoord.y - 3.0*blurSize)) * 0.09;
   sum += texture(RTBlurH, vec2(texCoord.x, texCoord.y - 2.0*blurSize)) * 0.12;
   sum += texture(RTBlurH, vec2(texCoord.x, texCoord.y - blurSize)) * 0.15;
   sum += texture(RTBlurH, vec2(texCoord.x, texCoord.y)) * 0.16;
   sum += texture(RTBlurH, vec2(texCoord.x, texCoord.y + blurSize)) * 0.15;
   sum += texture(RTBlurH, vec2(texCoord.x, texCoord.y + 2.0*blurSize)) * 0.12;
   sum += texture(RTBlurH, vec2(texCoord.x, texCoord.y + 3.0*blurSize)) * 0.09;
   sum += texture(RTBlurH, vec2(texCoord.x, texCoord.y + 4.0*blurSize)) * 0.05;
 
   finalColor = sum;
}

//http://www.gamerendering.com/2008/10/11/gaussian-blur-filter-shader/