
uniform float time;
uniform vec2 resolution;

 
float hash( float n ) { return fract(sin(n)*43758.5453123); }

float noise( in vec3 x )
{
    vec3 p = floor(x);
    vec3 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
	
    float n = p.x + p.y * 157.0 + 113.0 * p.z;
    return mix(mix(mix( hash(n+  0.0), hash(n+  1.0),f.x),
                   mix( hash(n+157.0), hash(n+158.0),f.x),f.y),
               mix(mix( hash(n+113.0), hash(n+114.0),f.x),
                   mix( hash(n+270.0), hash(n+271.0),f.x),f.y),f.z);
}

uniform int octaves;
uniform float gain;
uniform float lacunarity;

uniform float speedGain;
uniform vec3 noiseMovement;

float octavedNoise(in vec3 p){
	float result = 0.0;
	float myFallOff = gain;
	float mySpeedFallOff = speedGain;
	float myAmp = 0.0;
	
	vec3 q = p - noiseMovement * mySpeedFallOff * time;
	for(int i = 0; i < octaves; i++){
		myAmp += myFallOff;
		result += myFallOff * noise( q ); 
		q = q * lacunarity - noiseMovement * time * mySpeedFallOff;
		myFallOff *= gain;
		mySpeedFallOff *= speedGain;
	}
	
	return result / myAmp;
}

uniform float densityStart;
uniform float densityNoiseAmp;
uniform float densitySinusColorMod;

uniform float maskBlend;

float density( vec3 p ){
	float result = densityStart + p.z;

	float noiseValue = octavedNoise(p);



	result = clamp(result + densityNoiseAmp * noiseValue, 0.0, 1.0 );
	
	return result;
}

uniform float marchStepSize;
uniform int marchSteps;

uniform float densityScale;

uniform vec4 densityColor0;
uniform float densityColor0Amp;
uniform vec4 densityColor1;
uniform float densityColor1Amp;

uniform vec4 backColor;

uniform float densityShape;

vec4 raymarch( in vec3 rayOrigin, in vec3 rayDirection){
	vec4 sum = vec4( 0.0 );

	float t = 0.0;

    // dithering	
	t += marchStepSize;
	
	for( int i = 0; i < marchSteps; i++ ){
		if( sum.a > 0.99 ) continue;
		
		vec3 pos = rayOrigin + t * rayDirection;
		float densityValue = density(pos);
		float colorValue = pow(densityValue,densityShape);
		
		vec3 col = mix(densityColor0Amp * densityColor0.rgb, densityColor1Amp * densityColor1.rgb, colorValue ) + densitySinusColorMod * sin(pos);
		float a = mix(densityColor0.a, densityColor1.a, densityValue );
		
		densityValue *= densityScale;
		col.rgb *= densityValue;
		
		sum = sum + vec4(col, a * densityValue) * (1.0 - sum.a);	

		t += marchStepSize;
	}

	return clamp( sum.rgba, 0.0, 1.0 );
}

void main(void){
	vec2 q = gl_FragCoord.xy / resolution.xy;
    vec2 p = -1.0 + 2.0 * q;
    p.x *= resolution.x/ resolution.y;
    
	
    // raymarch	
	vec4 color = raymarch( vec3(p,0.0), vec3(0.0,0.0,-1.0) );
	
	float n = noise(vec3(p * 20.0,0.0));
	
    gl_FragColor = color;
}
