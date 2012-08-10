#version 150 core

uniform sampler2D RTScene; // Textur der Szene die geblurt werden muss
in vec2 texCoord;
out vec4 finalColor;
 
const float blurSize = 1.0/512.0; // I've chosen this size because this will result in that every step will be one pixel wide if the RTScene texture is of size 512x512
 
void main(void)
{
   vec4 sum = vec4(0.0);
 
   // blur in x (horizontal)
   // take nine samples, with the distance blurSize between them
   sum += texture(RTScene, vec2(texCoord.x - 4.0*blurSize, texCoord.y)) * 0.05;
   sum += texture(RTScene, vec2(texCoord.x - 3.0*blurSize, texCoord.y)) * 0.09;
   sum += texture(RTScene, vec2(texCoord.x - 2.0*blurSize, texCoord.y)) * 0.12;
   sum += texture(RTScene, vec2(texCoord.x - blurSize, texCoord.y)) * 0.15;
   sum += texture(RTScene, vec2(texCoord.x, texCoord.y)) * 0.16;
   sum += texture(RTScene, vec2(texCoord.x + blurSize, texCoord.y)) * 0.15;
   sum += texture(RTScene, vec2(texCoord.x + 2.0*blurSize, texCoord.y)) * 0.12;
   sum += texture(RTScene, vec2(texCoord.x + 3.0*blurSize, texCoord.y)) * 0.09;
   sum += texture(RTScene, vec2(texCoord.x + 4.0*blurSize, texCoord.y)) * 0.05;
 
   finalColor = sum;
}

//http://www.gamerendering.com/2008/10/11/gaussian-blur-filter-shader/