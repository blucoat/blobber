#version 130

uniform vec3 attenuation;
uniform vec3 color;

in vec2 pass_Displacement;
in vec2 pass_Normal;
in float pass_Depth;

out vec4 out_Color;

void main(void) {
	float dist = length(pass_Displacement);
	vec2 toLight = -1 * pass_Displacement / dist;
	float denominator = attenuation.x * dist * dist + attenuation.y * dist + attenuation.z;
	out_Color = vec4(dot(toLight, pass_Normal) * pass_Depth * color / denominator, 1);
	//out_color = vec4(1, 1, 1, 1);
}