uniform float saturation;

uniform sampler2D textureSampler0;
uniform sampler2D textureSampler1;
uniform sampler2D modSampler;

varying vec3 halfVector;

void main(){
	vec4 color0 = texture2D(textureSampler0, vec2(gl_TexCoord[0].s, 1.0 - gl_TexCoord[0].t));
	vec4 color1 = texture2D(textureSampler1, vec2(gl_TexCoord[0].s, 1.0 - gl_TexCoord[0].t));
	vec4 color = mix(color0, color1, gl_Color.a);
	color = mix(vec4(0.0,0.0,0.0,1.0), vec4(1.0,1.0,1.0,1.0), gl_Color.a);
	vec4 color2 = texture2D(modSampler, gl_TexCoord[1].st);
	//color.xyz *= (gl_Color.b * 2.0 - 1.0) * saturation + 1.0;
	color.xyz *= (color2.r * 2.0 - 1.0) * saturation + 1.0;
	//color.a = 0.99;//gl_Color.a;
	gl_FragData[0] = color;// * gl_Color.g;
}