#ifndef RENDERING
#define RENDERING

//////////////////////////////////////////////////////////////////////////
// ApplyFog
//////////////////////////////////////////////////////////////////////////
float3 ApplyFog ( float3 currentColor, float3 ray, float3 sunDirection )
{
	float rayLength = length ( ray );
	ray = ray /rayLength;
	
	float fogAmount = 1.0f -exp ( -rayLength *0.008f );
	float sunAmount = pow ( max ( dot ( ray, sunDirection ), 0.0f ), 8.0f );
	
	float3 fogColor = lerp ( float3 ( 0.5f, 0.6f, 0.7f ), float3 ( 1.0f, 0.9f, 0.7f ), sunAmount );
	return lerp ( currentColor, fogColor, fogAmount );
}

#endif // RENDERING