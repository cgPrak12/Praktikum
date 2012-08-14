#version 150
uniform mat4 view;
uniform vec3 camPos;
uniform float viewDistance;


in vec4 positionWC;
in vec2 gl_PointCoord;
in float pointSize;

out vec4 depth;
out vec4 depth2;

void main(void) {

	vec2 texCoord = vec2(gl_PointCoord.x, 1-gl_PointCoord.y);

	vec3 n;
	n.xy = texCoord * 2.0 - 1.0;
	float r2 = dot(n.xy, n.xy);
	if (r2 > 1.0) discard; 			// kill pixels outside circle


	n.z = -dot(n.xy, n.xy);
	vec4 pos = (view * positionWC);
	vec3 pixelPos = pos.xyz + n;
	depth = vec4(pixelPos, length(pixelPos) / viewDistance);
	
	float scale = pointSize * 0.5 / 200.0;
	vec3 pixelPos2 = pos.xyz + scale*n;
	depth2 = vec4(pixelPos2, length(pixelPos2) / viewDistance);
}