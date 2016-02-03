#include "MathHelper.fx"
#include "DistanceField.fx"
#include "Materials.fx"
#include "Rendering.fx"

// constant buffers
cbuffer setOnce
{
	float FOVlength;
	float ScreenRatio;
};
cbuffer setEachFrame
{
	float3 Position;
	float3 DirectionX;
	float3 DirectionY;
	float3 DirectionZ;
	float Time;
};
cbuffer setOnceInAWhile
{
	float3 SunDirection;
};

// VS_INPUT
struct VS_INPUT
{
    float2 Pos	: POSITION;
	float2 Tex	: TEXCOORD;
};

// PS_INPUT
struct PS_INPUT
{
    float4 Pos	: SV_POSITION;
	float2 Tex	: TEXCOORD;
};

/*
*	VertexShader
*/
PS_INPUT vertexShader ( VS_INPUT input )
{
    PS_INPUT output = (PS_INPUT)0;
	
    output.Pos = float4 ( input.Pos.xy, 0, 1 );
	output.Tex = input.Tex;
    
    return output;
}

/*
*	PixelShader
*/
float4 pixelShader ( PS_INPUT input) : SV_Target
{
	// setup ray
	float3 rayDir = float3 ( 0, 0, 0 );
	rayDir += DirectionX *( input.Tex.x *+2.0f -1.0f );
	rayDir += DirectionY *( input.Tex.y *-2.0f +1.0f ) *ScreenRatio;
	rayDir += DirectionZ *FOVlength;
	
	float3 rayPosition = Position +rayDir;
	rayDir = normalize ( rayDir );
	
	// colors
	const float3 skyColor = float3 ( 176, 215, 225 ) /255.0f;
	const float3 sunColor = float3 ( 1.0f, 0.9f, 0.7f );
	const float3 vignetteColor = float3 ( 189, 108, 181 ) /255.0f;
	
	const float3 blobColor = float3 ( 34, 177, 76 ) /255.0f;
	const float3 floorColor = float3 ( 245, 198, 35 ) /255.0f;
	const float3 boxColor = float3 ( 245, 198, 35 ) /255.0f;
	const float3 boxSSSColor = float3 ( 34, 177, 76 ) /255.0f;
	
	// march
	float3 color = skyColor;
	int materialID = -1;
	float totalDistance = 0.0f;
	int totalIterations = 0;
	while ( totalDistance < 500.0f && ++totalIterations < 150 )
	{
		float distance = DistanceAtPoint ( rayPosition, Time, materialID );
		if ( distance < EPSILON *0.5f )
		{
			float3 N = NormalAtPoint ( rayPosition, Time );
			float3 L = SunDirection;
			float3 V = normalize ( Position -rayPosition );
			float3 H = normalize ( V +L );
			
			if ( materialID == MATERIAL_FLOOR )
				color = floorColor;
			if ( materialID == MATERIAL_BLOB )
				color = blobColor;
			if ( materialID == MATERIAL_BOX )
				color = boxColor;
			
			// calculate lighting
			float diffuseTerm = saturate ( dot ( N, L ) );
			color = lerp ( color, sunColor, diffuseTerm );
			
			float aoTerm = AOAtPoint ( rayPosition, N, Time );
			color = lerp ( float3 ( 0, 0, 0 ), color, aoTerm );
			
			if ( materialID == MATERIAL_BOX )
			{
				float sssTerm = SSSAtPoint ( rayPosition, rayDir, Time );
				color = lerp ( color, boxSSSColor, sssTerm );
			}
			
			float specularTerm = saturate ( pow ( saturate ( dot ( N, H ) ), 512 ) );
			color += sunColor *specularTerm *aoTerm;
			
			break;
		}
		
		// march on
		rayPosition += rayDir *distance;
		totalDistance += distance;
	}
	
	// fog
	color = ApplyFog ( color, rayPosition -Position, SunDirection );
	
	// color correction
	color = lerp ( color, sqrt ( color ), 0.7f ) *float3 ( 1.0f, 1.2f, 1.0f );
	
	// vignette
	float vignette = pow ( saturate ( length ( input.Tex -float2 ( 0.5, 0.5f ) ) *1.6f ), 2.0f ) *0.4f;
	color = lerp ( color, vignetteColor, vignette );
	
	return float4 ( color, 1.0f );
}

/*
*	Technique Standard
*/
technique10 Standard
{
    pass Main
    {
        SetVertexShader ( CompileShader ( vs_4_0, vertexShader () ) );
        SetGeometryShader ( NULL );
        SetPixelShader  ( CompileShader ( ps_4_0, pixelShader  () ) );
    }
}