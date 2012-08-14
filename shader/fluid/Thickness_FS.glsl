#version 150

in vec2 gl_PointCoord;

out vec4 color;

void main(void) {

	vec2 texCoord = gl_PointCoord;

	vec3 n;
	n.xy = texCoord * 2.0 - 1.0;
	float r2 = dot(n.xy, n.xy);
	if (r2 > 1.0) discard; 			// kill pixels outside circle
	n.z = sqrt(1.0-r2) * 0.1; // (1-r2) * 0.05;

	color = vec4(0.0, 0.7*n.z, n.z, 0.0);
//	color = vec4(n.z, n.z, n.z, 1);
//	color = vec4(0.0, 0.0, 0.5, 0.0);
}