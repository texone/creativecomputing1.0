#ifndef DISTANCE_FIELD
#define DISTANCE_FIELD

#include "MathHelper.fx"
#include "Materials.fx"

//////////////////////////////////////////////////////////////////////////
// DistanceAtPoint
//////////////////////////////////////////////////////////////////////////
float DistanceAtPoint ( float3 P, float Time, out int materialID )
{
	float dist = 0.0f;
	float tmpDist = 0.0f;
	
	// floor
	dist = P.y +3.0f;
	materialID = MATERIAL_FLOOR;
	
	// warp field infintely in xz plane
	P.x = ( abs ( P.x ) %30.0f -15.0f ) *sign ( P.x );
	P.z = ( abs ( P.z ) %30.0f -15.0f ) *sign ( P.z );
	
	float t = exp ( -P.y *0.1f );
	float s = 1.0f +t *5.0f;
//	dist = min ( dist, DistanceToAABB ( float3 ( 0, 10, 0 ), float3 ( s, 20, s ), RotateAroundY ( P, PI2 *Time *0.2f +P.y *0.5f *( 1.0f -pow ( t, 0.1f ) ) ) ) );
//	dist = min ( dist, DistanceToCylinder ( float3 ( 0.5f, 0, 0 ), s *0.1f, 20.0f, RotateAroundY ( P, ( Time *PI2 *0.2f +P.y *0.5f ) ) ) );
	
	// square
	const float height = 4.5f +4.0f *sin ( Time *PI2 *0.2f );
	tmpDist = DistanceToAABB ( float3 ( 0, height -3.0f, 0 ), float3 ( 7.5f, height, 7.5f ), P );
	if ( tmpDist < dist ) { materialID = MATERIAL_BOX; dist = tmpDist; }
	
	// large sphere
	tmpDist = DistanceToSphere ( float3 ( +15, -2.0f, +15.0f ), 4.0f, P );
	if ( tmpDist < dist ) { materialID = MATERIAL_BLOB; dist = tmpDist; }
	tmpDist = DistanceToSphere ( float3 ( -15, -2.0f, +15.0f ), 4.0f, P );
	if ( tmpDist < dist ) { materialID = MATERIAL_BLOB; dist = tmpDist; }
	tmpDist = DistanceToSphere ( float3 ( -15, -2.0f, -15.0f ), 4.0f, P );
	if ( tmpDist < dist ) { materialID = MATERIAL_BLOB; dist = tmpDist; }
	tmpDist = DistanceToSphere ( float3 ( +15, -2.0f, -15.0f ), 4.0f, P );
	if ( tmpDist < dist ) { materialID = MATERIAL_BLOB; dist = tmpDist; }
	
	return dist;
}

//////////////////////////////////////////////////////////////////////////
// NormalAtPoint
//////////////////////////////////////////////////////////////////////////
float3 NormalAtPoint ( float3 P, float Time )
{
	float3 n = (float3)0;
	float3 offset = float3 ( EPSILON, 0, 0 );
	int materialID;
	
	n.x = DistanceAtPoint ( P +offset.xyz, Time, materialID ) -DistanceAtPoint ( P -offset.xyz, Time, materialID );
	n.y = DistanceAtPoint ( P +offset.yxz, Time, materialID ) -DistanceAtPoint ( P -offset.yxz, Time, materialID );
	n.z = DistanceAtPoint ( P +offset.yzx, Time, materialID ) -DistanceAtPoint ( P -offset.yzx, Time, materialID );
	
	return normalize ( n );
}

//////////////////////////////////////////////////////////////////////////
// AOAtPoint
//////////////////////////////////////////////////////////////////////////
float AOAtPoint ( float3 P, float3 N, float Time )
{
	float total = 0.0f;
	float weigth = 0.5f;
	int materialID;
	
	[unroll]
	for ( int i = 0; i < 5; ++i )
	{
		float delta = ( i +1 ) *( i +1 ) *EPSILON *12.0f;
		total += weigth *( delta -DistanceAtPoint ( P +N *delta, Time, materialID ) );
		weigth *= 0.5f;
	}
	
	return 1.0f -saturate ( total );
}

//////////////////////////////////////////////////////////////////////////
// SSSAtPoint
//////////////////////////////////////////////////////////////////////////
float SSSAtPoint ( float3 P, float3 LookDirection, float Time )
{
	float total = 0.0f;
	float weigth = 0.5f;
	int materialID;
	
	[unroll]
	for ( int i = 0; i < 5; ++i )
	{
		float delta = pow ( i +1, 2.5f ) *EPSILON *12.0f;
		total += -weigth *min ( 0, DistanceAtPoint ( P +LookDirection *delta, Time, materialID ) );
		weigth *= 0.5f;
	}
	
	return saturate ( total );
}

#endif // DISTANCE_FIELD