#version 130

uniform sampler2D texture_diffuse, lightmap;
uniform vec2 resolution;

in vec2 pass_ST;

out vec4 out_Color;

void main(void) {
	vec4 light = texture(lightmap, vec2(gl_FragCoord.x / resolution.x, gl_FragCoord.y / resolution.y));
	vec4 color = texture(texture_diffuse, pass_ST);
    out_Color = vec4(color.rgb * light.rgb, color.a);
}
