#version 150
uniform mat4 view;
uniform vec3 camPos;
uniform float viewDistance;


in vec4 positionWC;
in vec2 gl_PointCoord;
in float pointSize;
in float lifetime;

out vec4 depth;
out vec4 depth2;
out vec4 depth3;

void main(void) {

	if (lifetime <= 0) {
		discard;
	}
	
	vec2 texCoord = vec2(gl_PointCoord.x, 1-gl_PointCoord.y);

	vec3 n;
	n.xy = texCoord * 2.0 - 1.0;
	float r2 = dot(n.xy, n.xy);
	if (r2 > 1.0) discard;//{ 			// kill pixels outside circle
//		depth = vec4(0,0,0,1);
//		depth2 = vec4(0,0,0,1);
//		depth3 = vec4(0,0,0,1);
//		return;
//	}
	float black = 1;
//	if(r2 > 0.5) black = 0;

//	n.z = 0.15 * sqrt(1.0 - r2);
	n.z = -dot(n.xy, n.xy);
	vec4 pos = (view * positionWC);

	vec3 pixelPos = pos.xyz + n;
	depth = vec4(pixelPos, length(pixelPos) / viewDistance);

	float scale = pointSize * 0.5 / 100.0;
	vec3 pixelPos2 = pos.xyz + scale*n;
//	depth2 = vec4(pixelPos2, pixelPos.z);
	depth2 = vec4(pixelPos2, length(pixelPos2) / viewDistance);
	
//	depth3 = vec4(pos.xyz, length(pos.xyz) / viewDistance);
	depth3 = vec4(pos.xyz, length(pos.xyz) / viewDistance);
}