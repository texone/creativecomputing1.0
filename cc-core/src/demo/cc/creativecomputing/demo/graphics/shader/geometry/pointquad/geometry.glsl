#version 120 
#extension GL_EXT_geometry_shader4 : enable



void main(void){

	for(int i=0; i< gl_VerticesIn; i++){
		vec4 v1 = gl_PositionIn[i];
		gl_Position = v1 + vec4(10,10,0,0);
		//gl_TexCoord[0] = vec4(0,0,0,0);
		EmitVertex();
		gl_Position = v1 + vec4(10,-10,0,0);
		//gl_TexCoord[0] = vec4(0,0,0,0);
		EmitVertex();
		gl_Position = v1 + vec4(-10,10,0,0);
		//gl_TexCoord[0] = vec4(0,0,0,0);
		EmitVertex();
		gl_Position = v1 + vec4(-10,-10,0,0);
		//gl_TexCoord[0] = vec4(0,0,0,0);
		EmitVertex();
		
		EndPrimitive();
	}
	
}