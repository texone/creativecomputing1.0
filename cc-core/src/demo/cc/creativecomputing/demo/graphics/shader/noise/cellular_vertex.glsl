

#version 120

varying vec3 vTexCoord3D;

void main(void) {
	gl_TexCoord[0] = gl_MultiTexCoord0 * 16.0;
	vTexCoord3D = gl_Vertex.xyz * 0.08;
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
