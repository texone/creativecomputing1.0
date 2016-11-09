/**
 * This program computes the divergence of the specified vector field 
 * "velocity". The divergence is defined as 
 *
 *  "grad dot v" = partial(v.x)/partial(x) + partial(v.y)/partial(y),
 *
 * and it represents the quantity of "stuff" flowing in and out of a parcel of
 * fluid.  Incompressible fluids must be divergence-free.  In other words 
 * this quantity must be zero everywhere.  
 */ 

// 0.5 / gridscale
uniform half halfrdx;

// vector field       
uniform samplerRECT w;             
        
void main(
	in half2 coords : WPOS,  // grid coordinates
	out half4 div : COLOR // divergence (output)
){
	half4 vL = h4texRECT(w,coords + float2(-1,  0));
	half4 vR = h4texRECT(w,coords + float2( 1,  0));
	half4 vB = h4texRECT(w,coords + float2( 0, -1));
	half4 vT = h4texRECT(w,coords + float2( 0,  1));
	div = halfrdx * (vR.x - vL.x + vT.y - vB.y);
} 