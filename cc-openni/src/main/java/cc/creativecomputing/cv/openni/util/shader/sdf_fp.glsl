#version 120

uniform float z;

uniform vec3 volumePosition;
uniform vec3 volumeScale;

uniform float colorScale;

uniform sampler2D depthData;
uniform sampler3D cubeSampler;
uniform vec2 depthDimension;

uniform vec2 scale;
uniform vec2 center;
uniform float res;
uniform float smoothing;

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
	//tex.y = 1 - tex.y;
	
	return vec3(tex, depth / 10000.0);
}

void main(){
	vec3 texIndex = vec3(gl_TexCoord[0].xy,z);
	vec3 volumeWorldPosition = vec3(texIndex) * volumeScale + volumePosition;
	float volumeDistance = length(volumeWorldPosition);
	vec3 texCoord = worldToTexture(volumeWorldPosition);
	
	float alpha = 1.0;
	
	float depth = texture2D(depthData, texCoord.xy).x;
	vec3 texPosition = depthToWorld(texCoord.xy,depth);
	float texDistance = length(texPosition);
	
	float density = (texDistance - volumeDistance) * colorScale / 1000;
	float prevDensity = texture3D(cubeSampler,texIndex).r;
	
	if(density == 0.0){
		density = prevDensity;
	} else {
		density = density * (1 - smoothing) + prevDensity * smoothing;
	}
	
	if(texCoord.x > 1.0 || texCoord.x < 0.0 || texCoord.y > 1.0 || texCoord.y < 0.0){
		density = 0.0;
	}
	
	gl_FragColor = vec4(density, density, density, 1.0);
	
}