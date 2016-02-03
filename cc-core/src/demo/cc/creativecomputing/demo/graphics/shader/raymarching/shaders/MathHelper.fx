#ifndef MATH_HELPER
#define MATH_HELPER

const float EPSILON = 0.01f;
const float PI = 3.1415926535f;
const float PI2 = 3.1415926535f *2.0f;

/*
*	DistanceToSphere
*/
float DistanceToSphere ( float3 center, float radius, float3 P )
{
	return length ( center -P ) -radius;
}

/*
*	DistanceToAABB
*/
float DistanceToAABB ( float3 center, float3 extents, float3 P )
{
	float3 delta = abs ( center -P ) -extents;
	return max ( delta.x, max ( delta.y, delta.z ) );
}

/*
*	DistanceToCylinder
*/
float DistanceToCylinder ( float3 center, float radius, float height, float3 P )
{
	return max ( length ( center.xz -P.xz ) -radius, abs ( center.y -P.y ) -height *0.5f );
}

/*
*	RotateAroundY
*/
float3 RotateAroundY ( float3 P, float a )
{
	float sinA, cosA;
	sincos ( a, sinA, cosA );
	
	float3 Q;
	Q.x = P.x *cosA +P.z *sinA;
	Q.y = P.y;
	Q.z = P.z *cosA -P.x *sinA;
	
	return Q;
}

#endif // MATH_HELPER
