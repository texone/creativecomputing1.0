uniform sampler3D cubeSampler;

void main(){
	gl_TexCoord[0] = gl_MultiTexCoord0;
	
	vec4 color = texture3D(cubeSampler, gl_MultiTexCoord0.xyz);
	if(gl_MultiTexCoord0.w == 1.0){
		vec3 myPosition = gl_Vertex.xyz;
		myPosition.xyz += normalize(color.xyz) * 30.0 + vec3(1.0,1.0,1.0);
		gl_Position = gl_ModelViewProjectionMatrix * vec4(myPosition,1.0);
	} else {
		gl_Position = ftransform();
	}
}