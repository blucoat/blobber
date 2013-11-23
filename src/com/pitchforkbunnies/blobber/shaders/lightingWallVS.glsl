#version 150 core

in vec4 in_Position;
in vec2 in_Displacement;
in vec2 in_Normal;
in float in_Depth;

out vec2 pass_Displacement;
out vec2 pass_Normal;
out float pass_Depth;

void main(void) {
	gl_Position = in_Position;
	pass_Displacement = in_Displacement;
	pass_Normal = in_Normal;
	pass_Depth = in_Depth;
}