#version 120 
#extension GL_EXT_geometry_shader4 : enable

uniform float thickness;

varying out vec3 normal;

void main(void){

	for(int i=0; i< gl_VerticesIn; i+=3){
		vec4 v1 = gl_PositionIn[i];
		vec4 v2 = gl_PositionIn[i + 1];
		vec4 v3 = gl_PositionIn[i + 2];
		
		vec3 normalTop = normalize(cross(v1.xyz - v2.xyz, v3.xyz - v2.xyz));
		vec3 normal1 = normalize(cross(v2.xyz - v1.xyz, normalTop));
		vec3 normal2 = normalize(cross(v3.xyz - v2.xyz, normalTop));
		vec3 normal3 = normalize(cross(v1.xyz - v3.xyz, normalTop));
		
		vec3 top1 = v1.xyz + normalTop * thickness;
		vec3 top2 = v2.xyz + normalTop * thickness;
		vec3 top3 = v3.xyz + normalTop * thickness;
	
		vec3 bottom1 = v1.xyz - normalTop * thickness;
		vec3 bottom2 = v2.xyz - normalTop * thickness;
		vec3 bottom3 = v3.xyz - normalTop * thickness;
		
		gl_FrontColor = vec4(1.0,1.0,1.0,1.0);
		
		normal = normalTop;
		gl_Position = gl_ModelViewProjectionMatrix * vec4(top1,1);
		EmitVertex();
		gl_Position = gl_ModelViewProjectionMatrix * vec4(top2,1);
		EmitVertex();
		gl_Position = gl_ModelViewProjectionMatrix * vec4(top3,1);
		EmitVertex();
		EndPrimitive();
		
		normal = -normalTop;
		gl_Position = gl_ModelViewProjectionMatrix * vec4(bottom1,1);
		EmitVertex();
		gl_Position = gl_ModelViewProjectionMatrix * vec4(bottom2,1);
		EmitVertex();
		gl_Position = gl_ModelViewProjectionMatrix * vec4(bottom3,1);
		EmitVertex();
		EndPrimitive();
		
		normal = normal1;
		gl_Position = gl_ModelViewProjectionMatrix * vec4(bottom1,1);
		EmitVertex();
		gl_Position = gl_ModelViewProjectionMatrix * vec4(bottom2,1);
		EmitVertex();
		gl_Position = gl_ModelViewProjectionMatrix * vec4(top1,1);
		EmitVertex();
		gl_Position = gl_ModelViewProjectionMatrix * vec4(top2,1);
		EmitVertex();
		EndPrimitive();
		
		normal = normal2;
		gl_Position = gl_ModelViewProjectionMatrix * vec4(bottom2,1);
		EmitVertex();
		gl_Position = gl_ModelViewProjectionMatrix * vec4(bottom3,1);
		EmitVertex();
		gl_Position = gl_ModelViewProjectionMatrix * vec4(top2,1);
		EmitVertex();
		gl_Position = gl_ModelViewProjectionMatrix * vec4(top3,1);
		EmitVertex();
		EndPrimitive();
		
		normal = normal3;
		gl_Position = gl_ModelViewProjectionMatrix * vec4(bottom3,1);
		EmitVertex();
		gl_Position = gl_ModelViewProjectionMatrix * vec4(bottom1,1);
		EmitVertex();
		gl_Position = gl_ModelViewProjectionMatrix * vec4(top3,1);
		EmitVertex();
		gl_Position = gl_ModelViewProjectionMatrix * vec4(top1,1);
		EmitVertex();
		EndPrimitive();
	}
}