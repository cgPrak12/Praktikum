#version 150

const vec4 lp = vec4(0, 50, 0, 1);

uniform mat4 view;
uniform sampler2D normalL;
uniform sampler2D worldDepthPosL;
uniform sampler2D thicknessL;

uniform sampler2D normalH;
uniform sampler2D worldDepthPosH;
uniform sampler2D thicknessH;

uniform samplerCube cubeMap;
uniform sampler2D env;

in vec2 texCoords;

out vec4 color;

vec3 getInterpol(sampler2D texL, sampler2D texH, float t) {
      vec4 s0 = texture(texL, texCoords);
      vec4 s1 = texture(texH, texCoords);
      return mix(s0, s1, t).xyz;
}

float getFresnel(float n0, float n1, vec3 v, vec3 n) {
      float r = (n0 - n1) / (n0 + n1);
      r = r * r;
      float d = 1 - max(0, dot(n, v));
      return d;//r + (1 - r)  * d;
}

void main(void) {

        float depth = (texture(worldDepthPosH, texCoords).w + texture(worldDepthPosL, texCoords).w) * 0.5;
        
        vec3 normal = normalize(getInterpol(normalL, normalH, depth));
        vec3 pos = getInterpol(worldDepthPosL, worldDepthPosH, depth);
        float t = getInterpol(thicknessL, thicknessH, depth).z;
        vec4 envColor = texture(env, texCoords);
        
      vec4 lightPos = view * lp;
        vec3 lightReflect = normalize(reflect(pos - lightPos.xyz, normal));
        vec4 eyeReflect = normalize(inverse(view) * vec4(reflect(pos, normal),0));
        vec4 fluidColor0 = vec4(1,1,1,1);
        vec4 fluidColor1 = vec4(0,0,0.8,0);

        if(envColor.w != 0 && envColor.w < depth) {
                //        t = 0;
        }
        vec4 reflectColor = texture(cubeMap, eyeReflect.xyz);
        vec4 fluidSpec = vec4(0.2,0.2,0.2,0) * pow(max(dot(lightReflect, normalize(-pos)), 0), 16);
        vec4 fluidDiff = fluidColor1 * max(0.2, dot(normal, normalize(lightPos.xyz - pos)));
        
        vec4 fluidColor = fluidDiff + mix(fluidDiff, reflectColor, reflectColor) + fluidSpec;
        //vec4 fluidColor = fluidDiff + reflectColor * getFresnel(1, 1.396, normalize(-pos), normal) + fluidSpec;

//	color = vec4(t);
      color = mix(envColor, mix(fluidColor0, fluidColor, t), clamp(t, 0, 0.8));
}
