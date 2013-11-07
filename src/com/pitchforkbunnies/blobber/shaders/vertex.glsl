#version 150 core

uniform mat4 transform;

in vec4 in_Position;
in vec2 in_ST;

out vec2 pass_ST;

void main(void) {
    gl_Position = in_Position * transform;
    pass_ST = in_ST;
}