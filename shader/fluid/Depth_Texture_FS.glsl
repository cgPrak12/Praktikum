#version 150
uniform mat4 view;
uniform mat4 proj;
uniform samplerCube cubemapTex;

in vec4 positionWC;
in vec4 normalWC;
in vec4 fragColor;
in vec2 gl_PointCoord;
in float pointSize;
in vec4 positionMC1;

out vec4 depth;

void main(void) {

	vec2 texCoord = vec2(gl_PointCoord.x, 1-gl_PointCoord.y);
//	vec3 eyeSpacePos : TEXCOORD1,
//	float sphereRadius = 1;

	vec3 n;
	n.xy = texCoord*2.0-1.0;
	float r2 = dot(n.xy, n.xy);
	if (r2 > 1.0) discard; 			// kill pixels outside circle
	n.z = 0.05 * sqrt(1.0-r2);
	vec4 pos = (view * positionWC);
	pos = pos/pos.w;
	vec4 pixelPos = vec4(pos.xyz+n*0.5, 1.0);
	vec4 positionWC1 = view * positionWC;
	
	// position in xyz, depth in w !
	depth = vec4(positionWC.xyz,-pixelPos.z);//vec4(-pixelPos.x, pixelPos.y, pixelPos.z, pixelPos.z);
//	depth = texture(cubemapTex, positionWC.xyz);
//	depth = vec4(1);
}