#version 150

uniform sampler2D texture_diffuse, lightmap;

in vec2 pass_ST;

out vec4 out_Color;

void main(void) {
	vec4 light = texture(lightmap, vec2(gl_FragCoord.x / 800.0, gl_FragCoord.y / 600.0));
	vec4 color = texture(texture_diffuse, pass_ST);
    out_Color = vec4(color.rgb * light.rgb, color.a);
}