
uniform sampler2D depthTexture;
uniform samplerCube cubeMap;
uniform vec2 resolution;

uniform vec2 scale;
uniform vec2 center;
uniform float res;

uniform float refraction;

vec3 depthToWorld(vec2 lookUP, float depthValue) {
	vec3 result = vec3(0.0);
	lookUP.y = 1.0 - lookUP.y;
	lookUP *= resolution;
	lookUP *= res;
	
	float depth =  depthValue * 10000.0;
	result.xy = (lookUP - center) * depth / scale;
	result.z = depth;
	return result;
}

vec3 world(vec2 lookUP){
	float depth = texture2D(depthTexture, vec2(lookUP.x, 1.0 - lookUP.y)).x;
	return depthToWorld(lookUP, depth);
}

uniform float normalRadius;
uniform float minDepth;
uniform float maxDepth;

uniform vec3 glassChromaticDispertion;
uniform float glassBias;
uniform float glassScale;
uniform float glassPower;

void main(){
	vec2 q = gl_FragCoord.xy / resolution;
	vec2 add = 1.0 / resolution * normalRadius;
	vec3 left = world(q - vec2(add.x,0.0));
	vec3 right = world(q + vec2(add.x,0.0));
	vec3 top = world(q - vec2(0.0,add.y));
	vec3 bottom = world(q + vec2(0.0,add.y));
	
	vec3 pos = world(q);
	vec3 leftRight = normalize(right - left);
	vec3 topBottom = normalize(bottom - top);
	vec3 normal = cross(leftRight, topBottom);//(cross(leftRight, topBottom) + 1.0) / 2.0;
	
	float depth = texture2D(depthTexture, vec2(q.x, 1.0 - q.y)).x;
	if(depth < minDepth || depth > maxDepth)discard;
	
	vec3 uEyePosition = vec3(0.0,0.0,-000.0);
	vec3 incident = normalize(pos - uEyePosition);
	
	vec3 t = reflect(incident, normal);	
	vec3 tr = refract(incident, normal, glassChromaticDispertion.x);
	vec3 tg = refract(incident, normal, glassChromaticDispertion.y);
	vec3 tb = refract(incident, normal, glassChromaticDispertion.z);
	
	// bias, scale, 1, power
	float rfac = glassBias + glassScale * pow(1.0 + dot(incident, normal), glassPower);
	
	vec4 ref = textureCube(cubeMap, t);

	vec4 ret = vec4(1);
	ret.r = textureCube(cubeMap, tr).r;
	ret.g = textureCube(cubeMap, tg).g;
	ret.b = textureCube(cubeMap, tb).b;
	
	gl_FragColor = ret * rfac + ref * (1.0 - rfac);
	
	//gl_FragColor = textureCube(background,normal);
}