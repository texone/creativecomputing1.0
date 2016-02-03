#include "shader/util/texturenoise.fp"

uniform float noiseScale = 1;
uniform float3 noiseOffset = float3(0,0,0);
	
void main(
	in 		float2 		coords	: WPOS,
	out 	float4 		output0 : COLOR0
) { 
	output0 = (noise(coords * 0.001 * noiseScale + noiseOffset.xy) + 1)/2;
	output0 += (noise(coords * 0.002 * noiseScale - noiseOffset.xy) + 1)/2;
	output0 /= 2;
}     