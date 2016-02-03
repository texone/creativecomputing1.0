void main(){
	gl_Position = ftransform();
	gl_FrontColor = vec4((gl_Normal + 1.0) / 2.0, 1.0);//vec4(1.0);////
}