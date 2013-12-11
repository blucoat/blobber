#version 150

in vec4 in_Position;
in vec2 in_Displacement;

out vec2 pass_Displacement;

void main(void) {
	gl_Position = in_Position;
	pass_Displacement = in_Displacement;
}