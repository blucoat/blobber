#version 130

uniform sampler2D texture_diffuse;

in vec2 pass_ST;

out vec4 out_Color;

void main(void) { 
   out_Color = texture(texture_diffuse, pass_ST);
}