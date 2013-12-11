#version 150

uniform vec3 color;
uniform vec3 attenuation;

in vec2 pass_Displacement;

out vec4 out_Color;

void main(void) {
	float dist = length(pass_Displacement);
	out_Color = vec4(color / (attenuation.x * dist * dist + attenuation.y * dist + attenuation.z), 1);
}
