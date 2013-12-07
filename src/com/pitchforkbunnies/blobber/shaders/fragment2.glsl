#version 150

uniform sampler2D texture_diffuse;
uniform vec2 offset, size;

in vec2 pass_ST;

void main(void) { 
    gl_FragColor = texture(texture_diffuse, pass_ST * size + offset);
}