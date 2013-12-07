#version 150

uniform sampler2D texture_diffuse;
uniform vec2 offset, size;

in vec2 pass_ST;

out vec4 out_Color;

void main(void) { 
   out_Color = texture(texture_diffuse, pass_ST * size + offset);
   // gl_FragColor = vec4(1, 0, 0, 1);
   // out_Color = vec4(1, 0, 0, 1);
}