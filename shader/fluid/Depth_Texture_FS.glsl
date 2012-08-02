#version 150
uniform mat4 view;
uniform mat4 proj;

in vec4 positionWC;
in vec4 normalWC;
in vec4 fragColor;
in vec2 gl_PointCoord;
in float pointSize;
in vec4 positionMC1;

out vec4 depth;

void main(void) {

	vec2 texCoord = gl_PointCoord;
//	vec3 eyeSpacePos : TEXCOORD1,
//	float sphereRadius = 1;

	vec3 n;
	n.xy = texCoord*2.0-1.0;
	float r2 = dot(n.xy, n.xy);
	if (r2 > 1.0) discard; 			// kill pixels outside circle
	n.z = sqrt(1.0-r2);
	
	vec4 g = view * positionWC;
	g = g / g.w;
	vec4 pixelPos = vec4(g.xyz + n*0.5,1.0);
	
	
	float newX = positionMC1.x + n.x * pointSize;
	float newY = positionMC1.y + n.y * pointSize;
	float newZ = positionMC1.z + -n.z * pointSize;
	
	// position in xyz, depth in w !
	depth = vec4( newX, newY, newZ, -pixelPos.z / pixelPos.w );

}