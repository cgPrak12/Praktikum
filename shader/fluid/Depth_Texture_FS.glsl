#version 150
uniform mat4 view;
uniform mat4 proj;
uniform vec3 camPos;
uniform float viewDistance;


in vec4 positionWC;
in vec2 gl_PointCoord;

out vec4 depth;

void main(void) {

	vec2 texCoord = vec2(gl_PointCoord.x,1-gl_PointCoord.y);
//	vec3 eyeSpacePos : TEXCOORD1,
//	float sphereRadius = 1;

	vec3 n;
	n.xy = texCoord*2.0-1.0;
	float r2 = dot(n.xy, n.xy);
	if (r2 > 1.0) discard; 			// kill pixels outside circle
	n.z = sqrt(2.0-r2);
	
	vec4 g = view * positionWC;
	g = g / g.w;
	vec4 pixelPos = vec4(g.xyz + 0.5*n,1.0);
	
	// position in xyz, depth in w !
	depth = vec4(pixelPos.xyz, distance(-pixelPos.xyz, camPos) /viewDistance);
	
}