uniform float z;

uniform vec3 volumePosition;
uniform vec3 volumeScale;

uniform vec3 checkPosition;
uniform float colorScale;

void main(){
	vec3 position = vec3(gl_TexCoord[0].xy,z) * volumeScale + volumePosition;
	float density = distance(checkPosition.x, position.x) * colorScale;
	if(position.x > checkPosition.x){
		gl_FragColor = vec4(density,0.0,0.0,1.0);
	} else {
		gl_FragColor = vec4(0.0,0.0,density,1.0);
	}
	
}