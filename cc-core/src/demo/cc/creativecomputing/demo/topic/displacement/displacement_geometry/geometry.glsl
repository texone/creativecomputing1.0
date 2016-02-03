#version 120 
#extension GL_EXT_geometry_shader4 : enable

vec4 bezier(vec4 a, vec4 b, vec4 c, vec4 d, float t){
	float t1 = 1.0f - t;
	return a * t1 * t1 * t1 + 3 * b * t * t1 * t1 + 3 * c * t * t * t1 + d * t * t * t;
}

vec4 catmul(vec4 a, vec4 b, vec4 c, vec4 d, float t){
	return 
		((-t+2)*t-1)*t/2 * a +
		(((3*t-5)*t)*t+2)/2 * b + 
		((-3*t+4)*t+1)*t/2 * c +
		((t-1)*t*t)/2 * d;
}

uniform float strokeWeight = 5;

void emitStripCoords(vec4 theVertex, vec2 theNormal, float theScale){
	gl_TexCoord[0] = vec4(0,-1,0,0);
	gl_Position = gl_ModelViewProjectionMatrix * theVertex + vec4(theNormal *  strokeWeight * theScale, 0, 0);
	EmitVertex();
	
	gl_TexCoord[0] = vec4(0,1,0,0);
	gl_Position = gl_ModelViewProjectionMatrix * theVertex + vec4(theNormal * -strokeWeight * theScale, 0, 0);
	EmitVertex();
}

uniform float resolution = 10;

void main(void){

	for(int i=0; i< gl_VerticesIn; i+=4){
		
		vec4 v1 = gl_PositionIn[i];
		vec4 v2 = gl_PositionIn[i + 1];
		vec4 v3 = gl_PositionIn[i + 2];
		vec4 v4 = gl_PositionIn[i + 3];
		
		
		vec4 lastVertex = v2;
		vec2 normal = normalize(v3.yx - v1.yx) * vec2(1,-1);
		
		emitStripCoords(v2, normal,1);
	
		for(int j = 1; j < resolution;j++){
			vec4 currentVertex = catmul(v1, v2, v3, v4, j / resolution);
			normal = normalize(currentVertex.yx - lastVertex.yx) * vec2(1,-1);
			
			emitStripCoords(currentVertex, normal,1);
			lastVertex = currentVertex;
		}
		
		
		normal = normalize(v4.yx - v2.yx) * vec2(1,-1);
		
		emitStripCoords(v3, normal,1);
		/*
		lastVertex = v3;
		vec4 currentVertex = catmul(v2, v3, v4, v5, 1 / resolution);
		normal = normalize(currentVertex.yx - lastVertex.yx) * vec2(1,-1);
			
		emitStripCoords(currentVertex, normal,1);
			*/
		
	}
	
	EndPrimitive();
}