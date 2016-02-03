uniform float3 noiseScale = 1;
uniform float3 noiseOffset = float3(0,0,0);
uniform float noiseAmount;

uniform float fade;
uniform float fadeSize;
	
void main(
	in 		float2 		coords	: WPOS,
	in 		float2 		tex		: TEXCOORD0,
	out 	float4 		output0 : COLOR0
) { 
	float noiseVal = (noise(float3(coords.xy,0) * 0.01 * noiseScale + noiseOffset) + 1)/2;
	float fadeValue = tex.x + noiseVal * noiseAmount;
	fadeValue /= 1 + noiseAmount;
	fade *= 1 + fadeSize;
	float myBrightness = saturate((fadeValue + (fade + fadeSize) - (1 + fadeSize)) / fadeSize) ;
	output0 = float4(myBrightness,myBrightness,myBrightness,1.0);
}     