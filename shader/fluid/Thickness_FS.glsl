#version 150

in vec4 positionWC;
in vec4 normalWC;
in vec4 fragColor;
in vec2 gl_PointCoord;

out vec4 color;

void main(void) {

	vec2 texCoord = gl_PointCoord;
//	vec3 eyeSpacePos : TEXCOORD1,
//	float sphereRadius = 1;

	vec3 n;
	n.xy = texCoord*2.0-1.0;
	float r2 = dot(n.xy, n.xy);
	if (r2 > 1.0) discard; 			// kill pixels outside circle
	n.z = -sqrt(1.0-r2);

	color = vec4(0.1, 0.1, 1, 0.0);

}