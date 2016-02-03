
uniform vec2 resolution;


uniform float marchStepSize;
uniform int marchSteps;

uniform sampler3D depthVolume;

uniform vec3 voxelOffset;
uniform vec3 voxelScale;

vec3 voxelCoords(vec3 position) {
	return (position - voxelOffset) / voxelScale;
}

vec3 raymarch( in vec3 rayOrigin, in vec3 rayDirection){
	vec4 sum = vec4( 0.0 );

	float t = 0.0;
	float lastDepth = 1.0;

    // dithering	
	
	for( int i = 0; i < 64; i++ ){
		
		vec3 pos = rayOrigin + t * rayDirection;
		float densityValue = 0.0;//density(pos);
		
		float depth = texture3D(depthVolume, voxelCoords(pos)).x;
		if(depth >= 0.0 && lastDepth < 0.0)return pos;
		//if()

		t += 1.0 / 64.0;
	}

	return vec3(0.0);
	//return clamp( sum.aaa, 0.0, 1.0 );
}

uniform vec3 rayOrigin;

uniform vec3 cameraPosition;

uniform float depth;

void main(void){
	vec2 q = gl_FragCoord.xy / resolution.xy;
    vec2 p = -1.0 + 2.0 * q;
    //p *= 0.5;
    p.x *= resolution.x/ resolution.y;
    
	
    // camera
    //vec3 rayOrigin = 5.0 * normalize(vec3(1.0, 1.5, 0.0));
	
	// build ray
    vec3 ww = normalize(  - rayOrigin);
    vec3 uu = normalize(cross( vec3(0.0,1.0,0.0), ww ));
    vec3 vv = normalize(cross(ww,uu));
    //vec3 rayDirection = normalize( p.x * uu + p.y * vv + 2.0 * ww );
	
    // raymarch	
	//vec3 col = raymarch( rayOrigin, rayDirection);
	vec3 rayDirection = normalize(vec3(p,0.0) - cameraPosition);
	vec3 color = raymarch( vec3(p,0.0), vec3(0.0,0.0,1.0) );
	
	// contrast and vignetting	
	//col = col * 0.5 + 0.5 * col * col * (3.0 - 2.0 * col);
	//col *= 0.25 + 0.75 * pow( 16.0 * q.x * q.y * (1.0 - q.x) * (1.0 - q.y), 0.1 );
	
    gl_FragColor = vec4( color, 1.0 );
    
    float depthVal =  texture3D(depthVolume, vec3(q,depth)).x;
    
    if(depthVal > 0.0)gl_FragColor = vec4( depthVal,0.0,0.0, 1.0 );
    else gl_FragColor = vec4( 0.0,0.0, -depthVal,1.0 );
}
