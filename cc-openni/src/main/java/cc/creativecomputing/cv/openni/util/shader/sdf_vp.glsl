uniform float z;

void main(){
	gl_TexCoord[0] = gl_MultiTexCoord0;
	//density = density * 0.1 + prevDensity * 0.9;
	gl_Position = ftransform();
}