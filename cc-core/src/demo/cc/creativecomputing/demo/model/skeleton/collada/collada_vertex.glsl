void main(){
	gl_Position = ftransform();
	gl_FrontColor = vec4((gl_MultiTexCoord1.xyz) / 10.0, 1.0);//vec4(1.0);////
}