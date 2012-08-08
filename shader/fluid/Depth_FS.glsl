#version 150
uniform mat4 view;
uniform vec3 camPos;
uniform float viewDistance;

in vec4 positionWC;
in vec2 gl_PointCoord;

out vec4 depth;

void main(void) {

	vec2 texCoord = vec2(gl_PointCoord.x, 1-gl_PointCoord.y);

	vec3 n;
	n.xy = texCoord * 2.0 - 1.0;
	float r2 = dot(n.xy, n.xy);
	if (r2 > 1.0) discard; 			// kill pixels outside circle

	n.z = 0.15 * sqrt(1.0 - r2);
	vec4 pos = (view * positionWC);
	vec4 pixelPos = vec4(pos.xyz + n, 1.0);

	// original
	depth = vec4(pixelPos.xyz, length(pixelPos.xyz) / viewDistance); 
	
	// position in xyz, "easy" depth in w !
	//depth = vec4(positionWC.xyz, -pixelPos.z);
	
	// debug (depth in xyzw, -pixelPos.z in xyzw)
	//depth = vec4(distance(-pixelPos.xyz, camPos) / viewDistance);
	//depth = vec4(-pixelPos.z);
}