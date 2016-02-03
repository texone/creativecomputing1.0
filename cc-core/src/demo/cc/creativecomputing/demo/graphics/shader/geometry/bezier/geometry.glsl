#version 120 
#extension GL_EXT_geometry_shader4 : enable

vec4 bezier(vec4 a, vec4 b, vec4 c, vec4 d, float t){
	float t1 = 1.0f - t;
	return a * t1 * t1 * t1 + 3 * b * t * t1 * t1 + 3 * c * t * t * t1 + d * t * t * t;
}

void main(void){

	for(int i=0; i< gl_VerticesIn; i+=4){
		vec4 v1 = gl_PositionIn[i];
		vec4 v2 = gl_PositionIn[i + 1];
		vec4 v3 = gl_PositionIn[i + 2];
		vec4 v4 = gl_PositionIn[i + 3];
		
		vec4 lastBezier = bezier(v1, v2, v3, v4, 0);
		vec4 normal;
	
		for(int j = 1; j < 100;j++){
			vec4 myBezier = bezier(v1, v2, v3, v4, j / 99.0);
			normal = normalize(myBezier - lastBezier);
			gl_Position = lastBezier + normal.yxzw * 5;
			EmitVertex();
			gl_Position = lastBezier + normal.yxzw * -5;
			EmitVertex();
			
			lastBezier = myBezier;
		}
			gl_Position = lastBezier + normal.yxzw * 5;
			EmitVertex();
			gl_Position = lastBezier + normal.yxzw * -5;
			EmitVertex();
		
	}
	EndPrimitive();
}