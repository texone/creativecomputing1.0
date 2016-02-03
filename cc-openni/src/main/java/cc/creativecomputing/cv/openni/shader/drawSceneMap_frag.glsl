#version 120

uniform sampler2D sceneData;

void main(){
	float user = texture2D(sceneData, gl_TexCoord[0].st).x;
	if(user== 0)discard;
	gl_FragColor = gl_Color;
	
}