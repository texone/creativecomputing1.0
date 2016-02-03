#version 120

uniform sampler2DRect data;
uniform sampler2DRect lookUp;

void main() {
	vec4 ids1 = texture2DRect(lookUp, gl_TexCoord[0].xy);
	gl_FragColor = texture2DRect(data,ids1.xy);
	gl_FragColor.a = 1;
}
