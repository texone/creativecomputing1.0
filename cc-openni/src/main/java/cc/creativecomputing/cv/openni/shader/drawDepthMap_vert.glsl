#version 120

uniform sampler2D depthData;
uniform vec2 depthDimension;

uniform float depthLod;

uniform vec2 scale;
uniform vec2 center;
uniform float res;



vec3 depthToWorld(vec2 lookUP, float depthValue) {
	vec3 result = vec3(0);
	lookUP.y = 1 - lookUP.y;
	lookUP *= depthDimension;
	lookUP *= res;
	
	float depth =  depthValue * 10000.0;
	result.xy = (lookUP - center) * depth / scale;
	result.z = depth;
	return result;
}

vec3 worldToTexture(vec3 worldPos){
	vec2 tex = worldPos.xy;
	float depth = worldPos.z;
	tex = tex / depth * scale;
	tex += center;
	tex /= res;
	tex /= depthDimension;
	tex.y = 1 - tex.y;
	
	return vec3(tex, depth / 10000.0);
}

void main(){
	float depth = texture2DLod(depthData, gl_Vertex.xy,depthLod).x;
	vec3 world = depthToWorld(gl_Vertex.xy, depth);
	vec3 tex = worldToTexture(world);
	vec4 position = vec4(world, gl_Vertex.w);
	//vec4 position = vec4(gl_Vertex.xy * 4.0, depth * 10000.0, gl_Vertex.w);
	gl_Position = gl_ModelViewProjectionMatrix * position;
	gl_FrontColor = gl_Color;
	gl_TexCoord[0] = gl_Vertex.xyzw;
}