uniform sampler3D cubeSampler;

void main(){
	vec4 color = texture3D(cubeSampler, gl_TexCoord[0].xyz);
	float density = color.w;
	if(density > 0.0){
		gl_FragColor = vec4(density, 0.0, 0.0, 1.0);
	} else {
		gl_FragColor = vec4(0.0, 0.0, -density, 1.0);
	}
	
	gl_FragColor = vec4(color.xyz / 2.0 + 0.5,1.0);
	
}