#version 150

uniform mat4 viewProj;
uniform vec3 camPos;
uniform float size;

in vec3 positionMC;

out vec4 positionWC;
out float pointSize;

void main(void) {
	positionWC = vec4(positionMC,1);

	gl_Position = viewProj * positionWC;
//	pointSize =(50/(1 + length(positionMC - camPos))*(1/size));

//	pointSize =(500/(1 + length(positionMC - camPos))*(1/size));
//	pointSize = 1000 / (1 + pow(length(positionMC.xyz - camPos), 1.2))*1/size;
	pointSize = 50 / ( pow(length(positionMC.xyz - camPos), 1.0))*1/size;
	
    
	gl_PointSize = pointSize;
}