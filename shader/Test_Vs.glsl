#version 150 core

uniform mat4 model;
uniform mat4 viewProj;

<<<<<<< HEAD
in vec2 positionMC;
=======
in vec2 positionMc;
>>>>>>> 94b290f0312cb0bc1cd2d37852840969820493da

out vec3 fs_in_color;

void main(void) {
<<<<<<< HEAD
	fs_in_color = vec3(1,0,0);
    gl_Position = viewProj * model * vec4(positionMc,0, 1);

=======
    gl_Position = viewProj * model * vec4(positionMc.x,0,positionMc.y, 1);
	fs_in_color = vec3(sin(positionMc),1);
>>>>>>> 94b290f0312cb0bc1cd2d37852840969820493da
}