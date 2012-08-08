#version 150

uniform sampler2D normalTexture;
uniform sampler2D specularTexture;
uniform sampler2D textureImage;

in vec4 positionWC;
in vec4 normalWC;
in vec4 color1;
in vec2 fragmentTexCoords;
in vec4 tangentWC;
in float depth;
in vec4 shadowCoordWC;

out vec4 position;
out vec4 normal;
out vec4 color;
out vec3 spec;
out vec3 skyColor;
out vec4 shadowCoord;

void main(void) {
   float laenge = length (normalWC.xyz);
   vec3 normalAbs;
   if (laenge !=0)
   {
   		normalAbs = normalize(vec3(normalWC));
   }
   else
   {
   		normalAbs = vec3(0);
   }
   
   vec3 tangent   = normalize(vec3(tangentWC));
   vec3 binormal  = cross(tangent,normalAbs);
   vec3 mapNormal = 2 * texture(normalTexture, fragmentTexCoords).rgb - vec3(1);
   
   //Tiefeninformation in der W komponente der normale
   normal =vec4(   mapNormal.z * normalAbs 
   				 + mapNormal.y * binormal 
				 + mapNormal.x * tangent , depth);
				 
   //color = color1;
   color = texture(textureImage, fragmentTexCoords);
   position = positionWC;
   spec = texture(specularTexture, fragmentTexCoords).rgb ;
   skyColor = length(normalAbs) * vec3(1);
   shadowCoord = shadowCoordWC;
}