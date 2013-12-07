#version 150

uniform sampler2D texture_diffuse, lightmap;
uniform vec2 offset, size;

in vec2 pass_ST;

void main(void) {
	vec4 light = texture(lightmap, vec2(gl_FragCoord.x / 800.0, gl_FragCoord.y / 600.0)); 
	vec4 color = texture(texture_diffuse, pass_ST * size + offset);
    gl_FragColor = vec4(color.rgb * light.rgb, 1);
    //gl_FragColor = light;
}