#version 120

uniform sampler2D depthData;
uniform vec2 depthDimension;

uniform vec2 scale;
uniform vec2 center;
uniform float res;

vec3 depthToWorld(vec2 lookUP, float depthValue) {
	vec3 result = vec3(0);
	
	lookUP.y = 1 - lookUP.y;
	lookUP *= depthDimension;
	lookUP *= res;
	
	float depth =  depthValue * 10000.0;
	result.xy = (lookUP - center) * depth * 1.0 / scale;
	result.z = depth;
	return result;
}

void main(){
	float depth = texture2D(depthData, gl_Vertex.xy).x;
	vec4 position = vec4(depthToWorld(gl_Vertex.xy, depth), gl_Vertex.w);
	//vec4 position = vec4(gl_Vertex.xy * 400.0, 0, gl_Vertex.w);
	gl_Position = gl_ModelViewProjectionMatrix * position;
	gl_TexCoord[0] = gl_Vertex.xyzw;
}