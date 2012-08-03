#version 150
uniform mat4 view;

in vec4 positionWC;
in vec2 gl_PointCoord;

out vec4 depth;

void main(void) {
	vec2 texCoord = gl_PointCoord;
	vec3 n;
	n.xy = texCoord*2.0-1.0;
	float r2 = dot(n.xy, n.xy);
	if (r2 > 1.0) discard; 			// kill pixels outside circle
	n.z = 0.05 * sqrt(1.0-r2);
	
	vec4 g = view * positionWC;
	g = g / g.w;
	vec4 pixelPos = vec4(g.xyz + n*0.5,1.0);
	
	depth = vec4( /*positionWC.xyz,*/ -pixelPos.z );
}