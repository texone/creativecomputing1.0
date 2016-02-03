uniform sampler2D filterTexture;

void main(){
	vec4 filterColor = texture2D(filterTexture,gl_TexCoord[0].st);
	gl_FragColor = vec4(1,1,1,1);// * filterColor;	
}