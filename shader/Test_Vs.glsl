uniform mat4 model;
uniform mat4 viewProj;

in vec3 vs_in_pos;

out vec3 fs_in_color;

void main(void) {
    gl_Position = viewProj * model * vec4(vs_in_pos, 1);
	fs_in_color = sin(vs_in_pos);
}