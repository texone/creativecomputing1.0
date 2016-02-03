uniform float scale;
uniform float minDepth;

uniform float amount1;
uniform float amount2;
uniform float amount3;

uniform float moveZ;
uniform float moveZ1;
uniform float moveZ2;
uniform float moveZ3;

uniform float ambientRange;
uniform float ambientRangeAmount;

uniform float ambientDepth;
uniform float ambientDepth1;
uniform float ambientDepth2;
uniform float ambientDepth3;

uniform float ambientAmount1;
uniform float ambientAmount2;
uniform float ambientAmount3;

uniform vec3 move;

float ambientFactor(float ambientDepthEnd, float depth, float amount){
	return mix(1.0, smoothstep(ambientDepth,ambientDepthEnd,depth), amount);
}

float smoothStepPow(float edge0, float edge1, float x){
   float t = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
   t = pow(t, 0.3);
    return t * t * (3.0 - 2.0 * t);
}

void main(void) {
	vec2 F1 = cellular((vec3(gl_TexCoord[0].xy, moveZ1 + moveZ) + move)* scale);
	vec2 F2 = cellular((vec3(gl_TexCoord[0].xy, moveZ2 + moveZ) + move) * 2.0 * scale);
	vec2 F3 = cellular((vec3(gl_TexCoord[0].xy, moveZ3 + moveZ) + move) * 4.0 * scale);
	
	float facets1 = (F1.y-F1.x);
	float facets2 = (F2.y-F2.x);
	float facets3 = (F3.y-F3.x);
	/*
	float blendFrac = smoothstep(minDepth, minDepth + 0.25, facets1);
	float r = facets1 * amount1 + facets2 * amount2 * blendFrac + facets3 * amount3 * blendFrac;
	*/
	float r = facets1 * amount1 + facets2 * amount2 + facets3 * amount3;
	
	
	//float facets1 = (F1.y) * amount1;
	//float facets2 = (F2.y) * amount2;
	//float facets3 = (F3.y) * amount3;
	//float dots = smoothstep(0.05, 0.1, F.x);
	//float n = max(facets1 + facets2 + facets3, minDepth);//;// * dots;
	
	float n1 = 
		ambientFactor(ambientDepth1, facets1, ambientAmount1) *
		ambientFactor(ambientDepth2, facets2, ambientAmount2) *
		ambientFactor(ambientDepth3, facets3, ambientAmount3);
		
	if(r <= minDepth)n1 = n1 * 0.1 + 0.9;
	
	float ne1 = (smoothStepPow(minDepth, minDepth + ambientRange, r) - 0.5) * 2.0;
	float ne2 = smoothStepPow(minDepth, minDepth - ambientRange, r);
	float edgeAmbience = max(ne1, ne2);
	edgeAmbience = mix(1.0, edgeAmbience, ambientRangeAmount);
	float n = min(edgeAmbience,n1);
	
	if(r < minDepth){
		r = r * 0.02 + minDepth * 0.98;
	}
	
	gl_FragColor = vec4(r, n, n, 1.0);
}
