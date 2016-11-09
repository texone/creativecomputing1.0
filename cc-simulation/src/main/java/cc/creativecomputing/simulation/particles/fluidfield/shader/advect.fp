/**
 * This program performs a semi-lagrangian advection of a passive field by 
 * a moving velocity field.  It works by tracing backwards from each fragment
 * along the velocity field, and moving the passive value at its destination
 * forward to the starting point.  It performs bilinear interpolation at the 
 * destination to get a smooth resulting field.
 */
 
#include "shader/bilerp.fp"
 
uniform float timeStep;

// mass dissipation constant.
uniform float dissipation;

// 1 / grid scale. 
uniform float rdx;

// 
uniform float darkening;

// the velocity field.    
uniform samplerRECT velocityTexture;

// the field to be advected.       
uniform samplerRECT targetTexture;

/**
 * These methods perform texture lookups at the four nearest neighbors of the 
 * position s and bilinearly interpolate them.
 */ 

float4 f4texRECTbilerp(samplerRECT tex, float2 s){
  float4 st;
  st.xy = floor(s - 0.5) + 0.5;
  st.zw = st.xy + 1;
  
  float2 t = s - st.xy; //interpolating factors 
    
  float4 tex11 = f4texRECT(tex, st.xy);
  float4 tex21 = f4texRECT(tex, st.zy);
  float4 tex12 = f4texRECT(tex, st.xw);
  float4 tex22 = f4texRECT(tex, st.zw);

  // bilinear interpolation
  return lerp(lerp(tex11, tex21, t.x), lerp(tex12, tex22, t.x), t.y);
}

void main(
	in float2 coords : WPOS,  
	out float4 xNew : COLOR
){
  
	// Trace backwards along trajectory (determined by current velocity)
	// distance = rate * time, but since the grid might not be unit-scale,
	// we need to also scale by the grid cell size.
	float2 pos = coords - timeStep * rdx * f2texRECT(velocityTexture, coords);

	// Example:
	//    the "particle" followed a trajectory and has landed like this:
	//
	//   (x1,y2)----(x2,y2)    (xN,yN)
	//      |          |    /----/  (trajectory: (xN,yN) = start, x = end)
	//      |          |---/
	//      |      /--/|    ^
	//      |  pos/    |     \_ v.xy (the velocity)
	//      |          |
	//      |          |
	//   (x1,y1)----(x2,y1)
	//
	// x1, y1, x2, and y2 are the coordinates of the 4 nearest grid points
	// around the destination.  We compute these using offsets and the floor 
	// operator.  The "-0.5" and +0.5 used below are due to the fact that
	// the centers of texels in a TEXTURE_RECTANGLE_NV are at 0.5, 1.5, 2.5, 
	// etc.

	// The function f4texRECTbilerp computes the above 4 points and interpolates 
	// a value from texture lookups at each point.Rendering this value will 
	// effectively place the interpolated value back at the starting point 
	// of the advection.
	 
	// So that we can have dissipating scalar fields (like smoke), we
	// multiply the interpolated value by a [0, 1] dissipation scalar 
	// (1 = lasts forever, 0 = instantly dissipates.  At high frame rates, 
	// useful values are in [0.99, 1].
	
	xNew = dissipation * f4texRECTbilerp(targetTexture, pos) - darkening * timeStep;
	if(darkening > 0)xNew = saturate(xNew);
} 