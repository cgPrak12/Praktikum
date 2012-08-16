#version 330

in vec3 coords;
in vec3 normal;
in vec3 positionFS;
out vec4 fragColor;

uniform sampler2D normalTex;
uniform sampler2D heightTex;
uniform mat4 view;
uniform float viewDistance;
uniform sampler2D grassTex;
uniform vec3 lightPos;
uniform vec3 camPos;

const vec3 c_s = vec3(1.0, 1.0, 1.0);  // spekulare Farbe
const vec3 c_a = vec3(1.0, 1.0, 1.0);  // ambiente Farbe
// material eigenschaften
const float k_a    = 0.05;
const float k_dif  = 0.8;
const float k_spec = 0.3;
const float es     = 16.0;


vec3 getNormal(vec2 tc)  {
    float dx = 1.0/512.0;
    float dy = 1.0/512.0;
    
    float here = texture(heightTex, tc).x;

    float v01 = texture(heightTex, tc + vec2(-dx, 0.0)).x;
    float v10 = texture(heightTex, tc + vec2(0.0, -dy)).x;
    float v12 = texture(heightTex, tc + vec2(0.0, dy)).x;
    float v21 = texture(heightTex, tc + vec2(+dx, 0.0)).x;
    
    vec3 hvec = vec3(tc.x,here,tc.y);

    vec3 v1 = vec3( -dx,    v01, 0.0)-hvec; 
    vec3 v4 = vec3( 0.0,    v10, -dy)-hvec; 
    vec3 v2 = vec3( 0.0,     v12, dy)-hvec; 
    vec3 v3 = vec3( dx,    v21,  0.0)-hvec; 

    vec3 c1 = cross(v1,v2);
    vec3 c2 = cross(v2,v3);
    vec3 c3 = cross(v3,v4);
    vec3 c4 = cross(v4,v1);

    return normalize(c1+c2+c3+c4);
}

void main(void)
{
    if (positionFS.x < 0 || positionFS.z < 0 || positionFS.x > 1 || positionFS.z > 1)
        discard;

    float h = coords.y+0.4;
    
    vec3 normal = getNormal(vec2(coords.x,coords.z));
    vec3 position = positionFS;
    
// ***************************	
// Phong-Lighting

	vec3 c_d = texture(grassTex, vec2(coords.x,coords.z)).xyz;	
	vec3 pos2eye = normalize(camPos-position);	
	vec3 light2pos = normalize(position - lightPos);
	vec3 reflected = normalize(reflect(light2pos, normal));
	
	vec3 phongDiff = c_a * k_a
				   + c_d * k_dif * max(0.7, dot(-light2pos, normal));
	vec3 phongSpec = c_s * k_spec * max(0, pow(dot(reflected, pos2eye), es));
	vec3 phong = phongDiff + phongSpec;




	vec4 pos = (view * vec4(positionFS,1.0));
	float depth = length(pos.xyz) / viewDistance;	

//    fragColor = vec4(0.5+0.5*getNormal(vec2(coords.x,coords.z)), depth);
//    fragColor = vec4(texture(grassTex, vec2(coords.x,coords.z)).xyz, depth);
	fragColor = vec4(phong, depth);
}