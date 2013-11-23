#version 150 core

uniform vec4 color;
uniform vec3 attenuation;

in vec2 pass_Displacement;

void main(void) {
	float dist = length(pass_Displacement);
	gl_FragColor = vec4(color.rgb * color.a / (attenuation.x * dist * dist + attenuation.y * dist + attenuation.z), 1);
}
