#include "shader/util/simplex.fp"

uniform float move = 0;

uniform float baseNoiseScale = 1;
uniform float3 baseNoiseOffset = float3(0,0,0);
uniform float baseNoisePow = 1;
uniform float3 baseDepthMove;
uniform float baseNoiseAmplify = 1;
uniform float baseNoiseShift = 0;

uniform float fractalNoiseScale = 1;
uniform float3 fractalNoiseOffset = float3(0,0,0);
uniform float fractalNoisePow = 1;
uniform float3 fractalDepthMove;

uniform float resultScale = 1;
uniform float resultPow = 1;
uniform float resultShift = 0;

void main(
	in float2 iTexCoord0 : TEXCOORD0,
	in float4 iColor : COLOR0,
	out float4 oColor : COLOR0 
){
	//oColor = float4(depth, depth, depth, 1);
	//oColor.a = iColor.a;
	
	float3 myBaseNoiseMove = baseNoiseOffset + move * baseDepthMove;
	float myNoise = (snoise(float3(iTexCoord0.xy,0) * baseNoiseScale + myBaseNoiseMove) + 1)/2;
	myNoise *= baseNoiseAmplify;
	myNoise -= baseNoiseShift;
	//myNoise = saturate(myNoise);
	myNoise = pow(myNoise,baseNoisePow);
	
	float3 myFractalNoiseMove = fractalNoiseOffset + move * fractalDepthMove;
	float myFractalNoise = (noise(float3(iTexCoord0.xy,0) * fractalNoiseScale + myFractalNoiseMove) + 1)/2;
	myFractalNoise = pow(myFractalNoise,fractalNoisePow);
	
	float myResultNoise = myNoise * myFractalNoise;
	
	myResultNoise *= resultScale;
	myResultNoise -= resultShift;
	myResultNoise = saturate(myResultNoise);
	
	myResultNoise = pow(myResultNoise,resultPow);
	
	
	oColor = lerp(iColor, float4(1, 1, 1, 1), myResultNoise);
	oColor.a = iColor.a;
}