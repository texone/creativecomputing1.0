uniform sampler2D iChannel0;
uniform vec2 iResolution;
uniform float iGlobalTime;

uniform float noiseScale;

float noise(in vec3 x){
    vec3 p = floor(x);
    vec3 f = fract(x);
	f = f * f * (3.0 - 2.0 * f);
	
	vec2 uv = (p.xy + vec2(37.0, 17.0) * p.z) + f.xy;
	vec2 rg = texture2D( iChannel0, (uv+ 0.5) / 256.0, -100.0 ).yx;
	return mix( rg.x, rg.y, f.z );
}

float octavedNoise(in vec3 x){
	float f;
	vec3 q = x                              - vec3(1.0, 0.0, 0.0) * iGlobalTime;;
    f  = 0.50000 * noise( q ); q = q * 2.02 - vec3(1.0, 0.0, 0.0) * iGlobalTime;
    f += 0.25000 * noise( q ); q = q * 2.03 - vec3(1.0, 0.0, 0.0) * iGlobalTime;
    f += 0.12500 * noise( q ); q = q * 2.01 - vec3(1.0, 0.0, 0.0) * iGlobalTime;
    f += 0.06250 * noise( q ); q = q * 2.02 - vec3(1.0, 0.0, 0.0) * iGlobalTime;
    f += 0.03125 * noise( q );
    return f;
}

float raymarch( in vec3 rayOrigin, in vec3 rayDirection ){

	float t = 0.0;

    // dithering	
	//t += 0.05 * texture2D( iChannel0, gl_FragCoord.xy / iChannelResolution.x).x;
	float sum = 0.0;
	for( int i=0; i<100; i++ ){
		if( sum > 0.99 ) continue;
		
		vec3 pos = rayOrigin + t * rayDirection;
		float col = octavedNoise( pos );
		
		//col.xyz *= mix( 3.1 * vec3(1.0, 0.5, 0.05), vec3(0.48, 0.53, 0.5), clamp( (pos.y-0.2) / 2.0, 0.0, 1.0 ) );
		
		//col.a *= 0.6;
		//col.rgb *= col.a;

		sum = sum + col*0.01;	

		t += 0.05;
	}

	return clamp( sum, 0.0, 1.0 );
}

void main(){
	vec2 q = gl_FragCoord.xy / iResolution.xy;
    vec2 p = -1.0 + 2.0 * q;
    p.x *= iResolution.x/ iResolution.y;
    
   // float col = octavedNoise(vec3(p * noiseScale, 0.0));
    float col = raymarch(vec3(p * noiseScale, 0.0), vec3(0.0,0.0,1.0));
    
	gl_FragColor = vec4(col, col, 0.0, 1.0);
}