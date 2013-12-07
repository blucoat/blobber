#version 150

uniform vec3 attenuation;
uniform vec3 color;

in vec2 pass_Displacement;
in vec2 pass_Normal;
in float pass_Depth;

void main(void) {
	float dist = length(pass_Displacement);
	vec2 toLight = -1 * pass_Displacement / dist;
	float denominator = attenuation.x * dist * dist + attenuation.y * dist + attenuation.z;
	gl_FragColor = vec4(dot(toLight, pass_Normal) * pass_Depth * color / denominator, 1);
}