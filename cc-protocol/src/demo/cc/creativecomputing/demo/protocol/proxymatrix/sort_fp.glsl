#version 120

uniform sampler2DRect surface;
uniform sampler2DRect inforce;

void main() {

	vec4 ids1 = texture2DRect(surface, gl_TexCoord[0].xy);
	vec4 ids2 = texture2DRect(inforce, gl_TexCoord[0].xy);

	gl_FragColor = ids1;
}