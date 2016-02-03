uniform sampler2D iChannel0;
uniform float iGlobalTime;
uniform vec2 iChannelResolution;
uniform vec2 iResolution;
uniform vec4 iMouse;


// Created by inigo quilez - iq/2013
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.

float noise(in vec3 x){
    vec3 p = floor(x);
    vec3 f = fract(x);
	f = f * f * (3.0 - 2.0 * f);
	
	vec2 uv = (p.xy + vec2(37.0, 17.0) * p.z) + f.xy;
	vec2 rg = texture2D( iChannel0, (uv+ 0.5) / 256.0).yx;
	return mix( rg.x, rg.y, f.z );
}

uniform float gain;
uniform float lacunarity;

float octavedNoise(int octaves, vec3 position){
	float f = 0.0;
	float scale = 1.0;
	vec3 q = position - vec3(1.0, 0.0, 0.0) * iGlobalTime;
	
	for(int octave = 0; octave < octaves; octave++){
		scale *= gain;
    	f  += scale * noise( q ); 
   		q = q * lacunarity - vec3(1.0, 0.0, 0.0) * iGlobalTime;
	}
	
	return f;
}

uniform int octaves;

vec4 map( vec3 p ){
	float density = 0.2 - p.y;

    // invert space	
	//p = -7.0 * p / dot(p,p);

    // twist space	
	//float co = cos(den - 0.25 * iGlobalTime);
	//float si = sin(den - 0.25 * iGlobalTime);
	//p.xz = mat2(co,-si,si,co)*p.xz;

    // smoke	
	float f = octavedNoise(octaves,p);
	
	

	density = clamp(density + 4.0 * f, 0.0, 1.0 );
	
	vec3 color = mix( vec3(1.0,0.9,0.8), vec3(0.4,0.15,0.1), density );// + 0.05 * sin(p);
	
	return vec4(density);//vec4( color, density );
}

uniform float alphaReduction;
uniform float marchStep;

vec3 raymarch( in vec3 rayOrigin, in vec3 rayDirection ){
	vec4 sum = vec4( 0.0 );

	float t = 0.0;

    // dithering	
	t += marchStep * texture2D( iChannel0, gl_FragCoord.xy / iChannelResolution.x).x;
	
	for( int i=0; i<100; i++ ){
		if( sum.a > 0.99 ) continue;
		
		vec3 pos = rayOrigin + t * rayDirection;
		vec4 color = map( pos );
		
		color.xyz *= mix( 3.1 * vec3(1.0, 0.5, 0.05), vec3(0.48, 0.53, 0.5), clamp( (pos.y-0.2) / 2.0, 0.0, 1.0 ) );
		
		color.a *= alphaReduction;
		color.rgb *= color.a;

		sum = sum + color*(1.0 - sum.a);	

		t += marchStep;
	}

	return clamp( sum.xyz, 0.0, 1.0 );
}

uniform vec3 origin;
uniform float originDistance;
uniform float cr;

void main(void){
	vec2 q = gl_FragCoord.xy / iResolution.xy;
    vec2 p = -1.0 + 2.0 * q;
    p.x *= iResolution.x/ iResolution.y;
	
    // camera
    vec3 rayOrigin = originDistance * normalize(vec3(origin));
	
	// build ray
    vec3 ww = normalize( - rayOrigin);
    vec3 uu = normalize(cross( vec3(sin(cr), cos(cr), 0.0), ww ));
    vec3 vv = normalize(cross(ww,uu));
    vec3 rayDirection = normalize(p.x * uu + p.y * vv + 2.0 * ww);
	
    // raymarch	
	vec3 color = raymarch( rayOrigin, rayDirection );
	
	// contrast and vignetting	
	//color = color * 0.5 + 0.5 * color * color * (3.0 - 2.0 * color);
	//color *= 0.25 + 0.75 * pow(16.0 * q.x * q.y * (1.0 - q.x) * (1.0 - q.y), 0.1);
	
    gl_FragColor = vec4( color, 1.0 );
   //gl_FragColor = vec4(rayDirection,1.0);
}
