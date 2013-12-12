#version 150

in vec4 in_Position;
in vec2 in_ST;

out vec2 pass_ST;

void main(void) {
    gl_Position = in_Position;
    pass_ST = in_ST;
}