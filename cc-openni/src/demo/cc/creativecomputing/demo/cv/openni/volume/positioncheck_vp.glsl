#version 120

uniform float z;

uniform vec3 volumePosition;
uniform vec3 volumeScale;

uniform vec3 checkPosition;
uniform float colorScale;

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
	gl_TexCoord[0] = gl_MultiTexCoord0;
	vec3 volumeWorldPosition = vec3(gl_Vertex.xyz) * volumeScale + volumePosition;
	
	vec3 texCoord = worldToTexture(volumeWorldPosition);
	
	float depth = texture2D(depthData, texCoord.xy).x;
	vec3 texPosition = depthToWorld(texCoord.xy,depth);
	
	gl_Position = gl_ModelViewProjectionMatrix * vec4(texPosition,1.0);
}